package com.example.closetmarket.model

data class ClothingItem(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val category: String = "",
    val condition: String = "",
    val price: String = "",
    val city: String = "",
    val street: String = "",
    val userId: String = "",
    val userName: String = "",
    val uploadDate: String = "",
    val isWishlisted: Boolean = false,
    val lastUpdated: Long? = null
)
