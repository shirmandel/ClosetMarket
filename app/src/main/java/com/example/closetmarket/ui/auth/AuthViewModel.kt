package com.example.closetmarket.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.closetmarket.repository.UserRepository

class AuthViewModel : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _authSuccess = MutableLiveData(false)
    val authSuccess: LiveData<Boolean> = _authSuccess

    fun login(email: String, password: String) {
        _isLoading.value = true
        _errorMessage.value = null
        UserRepository.login(email, password) { success, error ->
            _isLoading.value = false
            if (success) {
                _authSuccess.value = true
            } else {
                _errorMessage.value = error ?: "Login failed"
            }
        }
    }

    fun register(email: String, password: String, name: String) {
        _isLoading.value = true
        _errorMessage.value = null
        UserRepository.register(email, password, name) { success, error ->
            _isLoading.value = false
            if (success) {
                _authSuccess.value = true
            } else {
                _errorMessage.value = error ?: "Registration failed"
            }
        }
    }
}

