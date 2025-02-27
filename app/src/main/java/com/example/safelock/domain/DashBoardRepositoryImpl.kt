package com.example.safelock.domain

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.safelock.data.repository.DashBoardRepository
import com.example.safelock.data.repository.model.MediaData
import com.example.safelock.utils.ApiResponse
import com.example.safelock.utils.AppConstants
import com.example.safelock.utils.AppConstants.SAFELOCK_MEDIA_DATA
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class DashBoardRepositoryImpl @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val firebaseFireStore: FirebaseFirestore,
    @ApplicationContext private val context: Context,
): DashBoardRepository {

    override suspend fun uploadImageToCloud(imageUri: Uri): Flow<ApiResponse<Uri>> = flow{
        emit(ApiResponse.Loading)
        val firebaseStorageReference = firebaseStorage.reference
        val imageStorageReference = firebaseStorageReference.child("SafeLockImages/${UUID.randomUUID()}.jpg")
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

    override suspend fun uploadVideoToCloud(videoUri: Uri): Flow<ApiResponse<Uri>> = flow{
        emit(ApiResponse.Loading)
        val firebaseStorageReference = firebaseStorage.reference
        val videoStorageReference = firebaseStorageReference.child("SafeLockVideos/${UUID.randomUUID()}.mp4")
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
    ): Flow<ApiResponse<Boolean>> = flow{
        emit(ApiResponse.Loading)
        try {
            val dataDetails = HashMap<String, Any>()
            dataDetails["dataImage"] = imageDownloadUrl
            dataDetails["dataTitle"] = mediaDataTitle
            firebaseFireStore.collection(SAFELOCK_MEDIA_DATA).document()
                .set(dataDetails)
                .await()
            emit(ApiResponse.Success(true))
            Log.d("UploadFirestore", "Successfully uploaded")
        } catch (e: Exception) {
            emit(ApiResponse.Failure(e))
        }
    }



    override suspend fun getMediaItems(): Flow<ApiResponse<List<MediaData>>> = callbackFlow {
        send(ApiResponse.Loading)

        val listenerRegistration = firebaseFireStore.collection(SAFELOCK_MEDIA_DATA)
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    trySend(ApiResponse.Failure(error)).isSuccess
                    return@addSnapshotListener
                }

                if (querySnapshot != null && !querySnapshot.isEmpty) {
                    val mediaItems = querySnapshot.documents.map { document ->
                        val dataImage = document.getString("dataImage") ?: ""
                        val dataTitle = document.getString("dataTitle") ?: ""
                        MediaData(
                            dataImage = dataImage,
                            dataTitle = dataTitle
                        )
                    }
                    trySend(ApiResponse.Success(mediaItems)).isSuccess
                } else {
                    trySend(ApiResponse.Success(emptyList())).isSuccess
                }
            }

        // Ensure proper cleanup when the flow collector stops collecting
        awaitClose {
            listenerRegistration.remove()
        }
    }


    //    override suspend fun getMediaItems(): Flow<ApiResponse<List<MediaData>>> = flow {
//        emit(ApiResponse.Loading)
//        try {
//            var items : List<MediaData> = mutableListOf()
//            // Retrieve data from the FireStore collection
//            val querySnapshot = firebaseFireStore.collection(SAFELOCK_MEDIA_DATA)
//                .get()
//                .await()
//
//            // Convert documents to a list of MediaData
//            val mediaItems = querySnapshot.documents.map { document ->
//                val dataImage = document.getString("dataImage") ?: ""
//                val dataTitle = document.getString("dataTitle") ?: ""
//                MediaData(
//                    dataImage = dataImage,
//                    dataTitle = dataTitle
//                )
//            }
//
//            emit(ApiResponse.Success(mediaItems))
//
////            firebaseFireStore.collection(SAFELOCK_MEDIA_DATA)
////                .addSnapshotListener { querySnapshot, error ->
////
////                    if (querySnapshot != null && !querySnapshot.isEmpty) {
////                        val mediaItems = querySnapshot.documents.map { document ->
////                            val dataImage = document.getString("dataImage") ?: ""
////                            val dataTitle = document.getString("dataTitle") ?: ""
////                            MediaData(
////                                dataImage = dataImage,
////                                dataTitle = dataTitle
////                            )
////                        }
////                       items = mediaItems
////                    }
////                }
////            emit(ApiResponse.Success(items))
//        } catch (e: Exception) {
//            emit(ApiResponse.Failure(e))
//        }
//    }


}