package com.example.closetmarket.ui.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.closetmarket.repository.ClothingItemRepository
import com.example.closetmarket.model.ClothingItem

class FeedViewModel : ViewModel() {

    val items: LiveData<List<ClothingItem>> = ClothingItemRepository.allItems
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun refreshData() {
        _isLoading.value = true
        ClothingItemRepository.refreshItems {
            _isLoading.value = false
        }
    }

    fun toggleWishlist(itemId: String) {
        ClothingItemRepository.toggleWishlist(itemId)
    }

}
