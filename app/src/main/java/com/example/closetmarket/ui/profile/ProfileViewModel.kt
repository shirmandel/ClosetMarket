package com.example.closetmarket.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.closetmarket.model.User
import com.example.closetmarket.repository.ClothingItemRepository
import com.example.closetmarket.repository.UserRepository
import com.example.closetmarket.model.ClothingItem

class ProfileViewModel : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private var _userItems: LiveData<List<ClothingItem>>? = null

    fun loadUser() {
        _user.value = UserRepository.getCurrentUser()
    }

    fun getUserItems(): LiveData<List<ClothingItem>> {
        val userId = UserRepository.getCurrentUser()?.uid ?: ""
        if (_userItems == null) {
            _userItems = ClothingItemRepository.getItemsByUser(userId)
        }
        return _userItems!!
    }

    fun logout() {
        UserRepository.logout()
    }
}

