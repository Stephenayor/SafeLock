package com.example.safelock.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safelock.data.repository.SignUpLoginRepository
import com.example.safelock.utils.ApiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: SignUpLoginRepository
) : ViewModel() {
    private val _signUpState = MutableStateFlow<ApiResponse<Unit>>(ApiResponse.Idle)
    val signUpState: StateFlow<ApiResponse<Unit>> = _signUpState

    fun signUp(email: String, password: String) {
        _signUpState.value = ApiResponse.Loading
        viewModelScope.launch {
            repository.signUpWithEmailPassword(email, password).collect { response ->
                _signUpState.value = response
            }
        }
    }

    fun clearFailureState() {
        _signUpState.value = ApiResponse.Idle // Reset to Idle after failure
    }

    fun clearSuccessState() {
        _signUpState.value = ApiResponse.Idle
    }

    fun clearLoadingState() {
        _signUpState.value = ApiResponse.Idle
    }
}
