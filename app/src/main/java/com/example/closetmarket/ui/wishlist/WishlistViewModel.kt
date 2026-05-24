package com.example.closetmarket.ui.wishlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.closetmarket.model.ClothingItem
import com.example.closetmarket.repository.ClothingItemRepository

class WishlistViewModel : ViewModel() {

    val wishlistedItems: LiveData<List<ClothingItem>> = ClothingItemRepository.getWishlistedItems()

    fun toggleWishlist(itemId: String) {
        ClothingItemRepository.toggleWishlist(itemId)
    }
}

