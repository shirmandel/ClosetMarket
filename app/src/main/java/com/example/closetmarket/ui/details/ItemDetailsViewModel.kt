package com.example.closetmarket.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.closetmarket.model.ClothingItem

class ItemDetailsViewModel : ViewModel() {

    private val _item = MutableLiveData<ClothingItem?>()
    val item: LiveData<ClothingItem?> = _item

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _deleteSuccess = MutableLiveData(false)
    val deleteSuccess: LiveData<Boolean> = _deleteSuccess

    fun loadItem(itemId: String) {

    }

    fun deleteItem(itemId: String) {

    }

    fun toggleWishlist(itemId: String) {
    }
}

