package com.example.closetmarket.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.closetmarket.model.ClothingItem
import com.example.closetmarket.repository.ClothingItemRepository

class ItemDetailsViewModel : ViewModel() {

    private val _item = MutableLiveData<ClothingItem?>()
    val item: LiveData<ClothingItem?> = _item

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _deleteSuccess = MutableLiveData(false)
    val deleteSuccess: LiveData<Boolean> = _deleteSuccess

    fun loadItem(itemId: String) {
        _isLoading.value = true
        ClothingItemRepository.getItemById(itemId) { item ->
            _item.value = item
            _isLoading.value = false
        }
    }

    fun deleteItem(itemId: String) {
        _isLoading.value = true
        ClothingItemRepository.deleteItem(itemId) { success ->
            _isLoading.value = false
            _deleteSuccess.value = success
        }
    }

    fun toggleWishlist(itemId: String) {
        ClothingItemRepository.toggleWishlist(itemId)
    }
}
