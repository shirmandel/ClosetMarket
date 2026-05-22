package com.example.closetmarket.ui.profile

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.closetmarket.model.User
import com.example.closetmarket.repository.ImageRepository
import com.example.closetmarket.repository.UserRepository

class EditProfileViewModel : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _saveSuccess = MutableLiveData(false)
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun getUser(): User? = UserRepository.getCurrentUser()

    fun saveProfile(name: String, imageBitmap: Bitmap?) {
        _isLoading.value = true
        if (imageBitmap != null) {
            ImageRepository.uploadImage(imageBitmap, "profiles") { imageUrl ->
                UserRepository.updateProfile(name, imageUrl) { success ->
                    _isLoading.value = false
                    if (success) {
                        _saveSuccess.value = true
                    } else {
                        _errorMessage.value = "Failed to update profile"
                    }
                }
            }
        } else {
            UserRepository.updateProfile(name, null) { success ->
                _isLoading.value = false
                if (success) {
                    _saveSuccess.value = true
                } else {
                    _errorMessage.value = "Failed to update profile"
                }
            }
        }
    }
}

