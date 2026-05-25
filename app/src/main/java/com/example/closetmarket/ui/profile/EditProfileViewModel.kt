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
                if (imageUrl != null) {
                    UserRepository.updateProfile(name, imageUrl) { success ->
                        _isLoading.postValue(false)
                        if (success) _saveSuccess.postValue(true)
                        else _errorMessage.postValue("Failed to update profile")
                    }
                } else {
                    _isLoading.postValue(false)
                    _errorMessage.postValue("Failed to upload image")
                }
            }
        } else {
            UserRepository.updateProfile(name, null) { success ->
                _isLoading.postValue(false)
                if (success) _saveSuccess.postValue(true)
                else _errorMessage.postValue("Failed to update profile")
            }
        }
    }
}
