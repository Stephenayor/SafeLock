package com.example.safelock.presentation.securemedia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safelock.data.repository.DashBoardRepository
import com.example.safelock.data.repository.ScreenUsageRepository
import com.example.safelock.data.repository.SecuredMediaRepository
import com.example.safelock.utils.ApiResponse
import com.example.safelock.utils.base.BaseViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecuredMediaViewModel @Inject constructor(
    private val securedMediaRepository: SecuredMediaRepository,
    private val analyticss: FirebaseAnalytics,
    private val screenUsageRepository: ScreenUsageRepository,
    private val dashBoardRepository: DashBoardRepository
) : BaseViewModel(analyticss, screenUsageRepository, dashBoardRepository){

    private val _deleteMediaDataState = MutableStateFlow<ApiResponse<Boolean>>(ApiResponse.Idle)
    val deleteMediaDataState: StateFlow<ApiResponse<Boolean>> = _deleteMediaDataState

    init {

    }

    fun deleteMediaByTitle(mediaTitle: String){
        _deleteMediaDataState.value = ApiResponse.Loading
        viewModelScope.launch {
            securedMediaRepository.deleteImageByTitle(mediaTitle).collect{response ->
                _deleteMediaDataState.value = response
            }
        }
    }
}