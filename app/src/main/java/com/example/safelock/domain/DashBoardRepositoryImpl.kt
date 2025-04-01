package com.example.safelock.domain

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.safelock.data.repository.DashBoardRepository
import com.example.safelock.data.repository.database.dao.SaveImageDao
import com.example.safelock.data.repository.database.entity.SaveImageEntity
import com.example.safelock.data.repository.model.MediaData
import com.example.safelock.utils.ApiResponse
import com.example.safelock.utils.AppConstants
import com.example.safelock.utils.AppConstants.SAFELOCK_MEDIA_DATA
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class DashBoardRepositoryImpl @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val firebaseFireStore: FirebaseFirestore,
    @ApplicationContext private val context: Context,
    private val saveImageDao: SaveImageDao
) : DashBoardRepository {
    private lateinit var firebaseAuth: FirebaseAuth

    override suspend fun uploadImageToCloud(imageUri: Uri): Flow<ApiResponse<Uri>> = flow {
        emit(ApiResponse.Loading)
        val firebaseStorageReference = firebaseStorage.reference
        val imageStorageReference =
            firebaseStorageReference.child("SafeLockImages/${UUID.randomUUID()}.jpg")
        try {
            val imageDownloadUrl = imageStorageReference.child(AppConstants.SAFELOCK_MEDIA_IMAGES)
                .putFile(imageUri).await()
                .storage.downloadUrl.await()
            Log.d("image upload", "Upload successful")
            emit(ApiResponse.Success(imageDownloadUrl))
        } catch (e: Exception) {
            emit(ApiResponse.Failure(e))
        }
    }

    override suspend fun uploadVideoToCloud(videoUri: Uri): Flow<ApiResponse<Uri>> = flow {
        emit(ApiResponse.Loading)
        val firebaseStorageReference = firebaseStorage.reference
        val videoStorageReference =
            firebaseStorageReference.child("SafeLockVideos/${UUID.randomUUID()}.mp4")
        try {
            val videoFileUrl = videoStorageReference.child(AppConstants.SAFELOCK_MEDIA_VIDEOS)
                .putFile(videoUri).await()
                .storage.downloadUrl.await()
            Log.d("video upload", "video Upload successful")
            emit(ApiResponse.Success(videoFileUrl))
        } catch (e: Exception) {
            emit(ApiResponse.Failure(e))
        }
    }

    override suspend fun uploadMediaDataToFireStore(
        imageDownloadUrl: Uri,
        mediaDataTitle: String
    ): Flow<ApiResponse<Boolean>> = flow {
        emit(ApiResponse.Loading)
        firebaseAuth = FirebaseAuth.getInstance()
        try {
            val currentUserId = firebaseAuth.currentUser?.uid.orEmpty()
            if (currentUserId.isEmpty()) {
                emit(ApiResponse.Failure(Exception("User not logged in")))
                return@flow
            }
            val dataDetails = hashMapOf(
                "dataImage" to imageDownloadUrl.toString(),
                "dataTitle" to mediaDataTitle
            )
            firebaseFireStore.collection(SAFELOCK_MEDIA_DATA)
                .document(currentUserId)
                .collection("media")
                .add(dataDetails)
                .await()

            emit(ApiResponse.Success(true))
            Log.d("UploadFirestore", "Successfully uploaded for user: $currentUserId")
        } catch (e: Exception) {
            emit(ApiResponse.Failure(e))
        }
    }


    override suspend fun getMediaItems(): Flow<ApiResponse<List<MediaData>>> = callbackFlow {
        send(ApiResponse.Loading)

        firebaseAuth = FirebaseAuth.getInstance()
        val currentUserId = firebaseAuth.currentUser?.uid.orEmpty()
        if (currentUserId.isEmpty()) {
            trySend(ApiResponse.Failure(Exception("User not logged in")))
            close()
            return@callbackFlow
        }

        val listenerRegistration = firebaseFireStore.collection(SAFELOCK_MEDIA_DATA)
            .document(currentUserId)
            .collection("media")
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    trySend(ApiResponse.Failure(error)).isSuccess
                    return@addSnapshotListener
                }

                if (querySnapshot != null && !querySnapshot.isEmpty) {
                    val mediaItems = querySnapshot.documents.map { document ->
                        val dataImage = document.getString("dataImage") ?: ""
                        val dataTitle = document.getString("dataTitle") ?: ""
                        MediaData(dataImage, dataTitle)
                    }
                    trySend(ApiResponse.Success(mediaItems)).isSuccess
                } else {
                    trySend(ApiResponse.Success(emptyList())).isSuccess
                }
            }

        awaitClose { listenerRegistration.remove() }
    }


    override suspend fun saveImagesInDB(imageUrl: String, imageTitle: String, isVideo: Boolean) {
        withContext(Dispatchers.IO) {
            val saveImageEntity = SaveImageEntity(imageUrl, imageTitle, isVideo)
            saveImageDao.saveImages(saveImageEntity)
        }
    }


    override suspend fun getSavedImages(): Flow<ApiResponse<List<SaveImageEntity>>> = callbackFlow {
        send(ApiResponse.Loading)
        val job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val savedImages = saveImageDao.getSavedImages()
                trySend(ApiResponse.Success(savedImages))
            } catch (exception: Exception) {
                trySend(ApiResponse.Failure(exception))
            }
        }

        awaitClose { job.cancel() }
    }


}