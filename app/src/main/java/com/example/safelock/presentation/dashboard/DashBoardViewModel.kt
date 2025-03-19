package com.example.safelock.presentation.dashboard

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safelock.data.repository.DashBoardRepository
import com.example.safelock.data.repository.model.MediaData
import com.example.safelock.utils.ApiResponse
import com.example.safelock.utils.Firebase.EventTracker
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashBoardViewModel @Inject constructor(
   private val dashBoardRepository: DashBoardRepository,
    private val firebaseAnalytics: FirebaseAnalytics
): ViewModel(){
    private val _uploadImageState = MutableStateFlow<ApiResponse<Uri>>(ApiResponse.Idle)
    val uploadImage: StateFlow<ApiResponse<Uri>> = _uploadImageState

    private val _uploadMediaDataState = MutableStateFlow<ApiResponse<Boolean>>(ApiResponse.Idle)
    val uploadMediaDataState: StateFlow<ApiResponse<Boolean>> = _uploadMediaDataState

    private val _getAllMediaItems = MutableStateFlow<ApiResponse<List<MediaData>>>(ApiResponse.Idle)
    val getAllMediaItems: StateFlow<ApiResponse<List<MediaData>>> = _getAllMediaItems

    private val _mediaTitle = MutableStateFlow<String?>(null)
    val mediaTitle: StateFlow<String?> = _mediaTitle

    init {
        getAllMediaItems()
    }

    fun clearLoadingState() {
        _uploadMediaDataState.value = ApiResponse.Idle
        _uploadImageState.value = ApiResponse.Idle
    }



    fun uploadImageToCloud(imageUri: Uri){
        _uploadImageState.value = ApiResponse.Loading
        viewModelScope.launch {
            dashBoardRepository.uploadImageToCloud(imageUri).collect { response ->
                _uploadImageState.value = response
            }
        }
    }

    fun uploadVideoToCloud(videoUri: Uri){
        _uploadImageState.value = ApiResponse.Loading
        viewModelScope.launch {
            dashBoardRepository.uploadVideoToCloud(videoUri).collect { response ->
                _uploadImageState.value = response
            }
        }
    }

    fun uploadMediaDataToFireStoreDatabase(cloudImageUri: Uri, mediaTitle: String){
        _uploadMediaDataState.value = ApiResponse.Loading
        Log.d("mediaTitle inside viewmodel", _mediaTitle.value.toString())
        viewModelScope.launch {
            dashBoardRepository.uploadMediaDataToFireStore(cloudImageUri,
                _mediaTitle.value!!).collect { response ->
                _uploadMediaDataState.value = response
            }
        }
    }

    fun getAllMediaItems(){
        _getAllMediaItems.value = ApiResponse.Loading
        viewModelScope.launch {
            dashBoardRepository.getMediaItems().collect { response ->
                _getAllMediaItems.value = response
            }
        }
    }

    fun setMediaTitle(title: String) {
        _mediaTitle.value = title
    }

    fun logDashBoardScreenEvents(screenName: String, screenClass: String) {
//        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, null)
       val bundle =  EventTracker.trackEvent(screenName, screenClass)
        firebaseAnalytics.logEvent("dashboard_feature_usage", bundle)
    }
}