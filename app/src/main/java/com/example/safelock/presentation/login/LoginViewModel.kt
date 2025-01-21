package com.example.safelock.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safelock.data.repository.SignUpLoginRepository
import com.example.safelock.utils.ApiResponse
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: SignUpLoginRepository
) : ViewModel() {
    private val _loginState = MutableStateFlow<ApiResponse<FirebaseUser>>(ApiResponse.Idle)
    val loginState: StateFlow<ApiResponse<FirebaseUser>> = _loginState

    fun login(email: String, password: String) {
        _loginState.value = ApiResponse.Loading
        viewModelScope.launch {
            repository.signInWithEmailPassword(email, password).collect { response ->
                _loginState.value = response
            }
        }
    }

    fun clearFailureState() {
        _loginState.value = ApiResponse.Idle // Reset to Idle after failure
    }

    fun clearSuccessState() {
        _loginState.value = ApiResponse.Idle
    }

    fun clearLoadingState() {
        _loginState.value = ApiResponse.Idle
    }

    fun saveUserEmail(key: String, value: String) {
        repository.saveUserEmail(key, value)
    }

    fun saveUserPassword(key: String, value: String) {
        repository.saveUserPassword(key, value)
    }

    fun getUserEmail(key: String): String? {
        return repository.getUserEmail(key)
    }

    fun getUserPassword(key: String): String? {
        return repository.getUserPassword(key)
    }


}