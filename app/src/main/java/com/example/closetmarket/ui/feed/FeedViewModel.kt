package com.example.closetmarket.ui.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.closetmarket.model.ClothingItem

class FeedViewModel : ViewModel() {

    private val _items = MutableLiveData<List<ClothingItem>>(mockItems())
    val items: LiveData<List<ClothingItem>> = _items

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun refreshData() {
        _isLoading.value = true
        _items.value = mockItems()
        _isLoading.value = false
    }

    fun toggleWishlist(itemId: String) {
        val current = _items.value ?: return
        _items.value = current.map { item ->
            if (item.id == itemId) item.copy(isWishlisted = !item.isWishlisted) else item
        }
    }

    private fun mockItems(): List<ClothingItem> = listOf(
        ClothingItem(
            id = "1",
            title = "Vintage Denim Jacket",
            description = "Classic blue denim jacket in great condition.",
            imageUrl = "",
            category = "jackets",
            condition = "like-new",
            price = "45",
            city = "Tel Aviv",
            street = "Dizengoff",
            userId = "u1",
            userName = "Alice",
            uploadDate = "2026-05-01",
            isWishlisted = false,
            lastUpdated = System.currentTimeMillis()
        ),
        ClothingItem(
            id = "2",
            title = "Black Summer Dress",
            description = "Lightweight black dress, perfect for summer.",
            imageUrl = "",
            category = "dresses",
            condition = "new",
            price = "60",
            city = "Haifa",
            street = "Herzl",
            userId = "u2",
            userName = "Bella",
            uploadDate = "2026-05-03",
            isWishlisted = true,
            lastUpdated = System.currentTimeMillis()
        ),
        ClothingItem(
            id = "3",
            title = "Running Sneakers",
            description = "Comfortable running shoes, lightly used.",
            imageUrl = "",
            category = "shoes",
            condition = "good",
            price = "free",
            city = "Jerusalem",
            street = "Jaffa",
            userId = "u3",
            userName = "Chen",
            uploadDate = "2026-04-28",
            isWishlisted = false,
            lastUpdated = System.currentTimeMillis()
        ),
        ClothingItem(
            id = "4",
            title = "Wool Sweater",
            description = "Warm wool sweater, gently worn.",
            imageUrl = "",
            category = "tops",
            condition = "good",
            price = "30",
            city = "Be'er Sheva",
            street = "Ringelblum",
            userId = "u4",
            userName = "Dana",
            uploadDate = "2026-04-20",
            isWishlisted = false,
            lastUpdated = System.currentTimeMillis()
        )
    )
}
