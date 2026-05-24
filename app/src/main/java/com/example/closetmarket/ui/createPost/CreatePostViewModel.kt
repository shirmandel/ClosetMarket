package com.example.closetmarket.ui.createPost

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.closetmarket.model.ClothingItem
import com.example.closetmarket.repository.ClothingItemRepository
import com.example.closetmarket.repository.ImageRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class CreatePostViewModel : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _postSuccess = MutableLiveData(false)
    val postSuccess: LiveData<Boolean> = _postSuccess

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _currentItem = MutableLiveData<ClothingItem?>()
    val currentItem: LiveData<ClothingItem?> = _currentItem

    fun loadItem(itemId: String) {
        ClothingItemRepository.getItemById(itemId) { item ->
            _currentItem.value = item
        }
    }

    fun submitPost(
        title: String,
        description: String,
        category: String,
        condition: String,
        price: String,
        isFree: Boolean,
        city: String,
        street: String,
        imageBitmap: Bitmap?,
        existingImageUrl: String?,
        userId: String,
        userName: String,
        editItemId: String?
    ) {
        _isLoading.value = true
        _errorMessage.value = null

        val itemId = editItemId ?: UUID.randomUUID().toString()
        val dateFormat = SimpleDateFormat("MMM dd", Locale.US)
        val uploadDate = dateFormat.format(Date())
        val finalPrice = if (isFree) "free" else price

        if (imageBitmap != null) {
            // Upload image first
            ImageRepository.uploadImage(imageBitmap, "items") { imageUrl ->
                if (imageUrl != null) {
                    saveItem(itemId, title, description, category, condition, finalPrice,
                        city, street, imageUrl, userId, userName, uploadDate, editItemId != null)
                } else {
                    _isLoading.postValue(false)
                    _errorMessage.postValue("Failed to upload image")
                }
            }
        } else {
            val imageUrl = existingImageUrl ?: ""
            saveItem(itemId, title, description, category, condition, finalPrice,
                city, street, imageUrl, userId, userName, uploadDate, editItemId != null)
        }
    }

    private fun saveItem(
        id: String, title: String, description: String,
        category: String, condition: String, price: String,
        city: String, street: String, imageUrl: String,
        userId: String, userName: String, uploadDate: String,
        isEdit: Boolean
    ) {
        val item = ClothingItem(
            id = id,
            title = title,
            description = description,
            imageUrl = imageUrl,
            category = category,
            condition = condition,
            price = price,
            city = city,
            street = street,
            userId = userId,
            userName = userName,
            uploadDate = uploadDate
        )

        val callback: (Boolean) -> Unit = { success ->
            _isLoading.postValue(false)
            if (success) {
                _postSuccess.postValue(true)
            } else {
                _errorMessage.postValue("Failed to save item")
            }
        }

        if (isEdit) {
            ClothingItemRepository.updateItem(item, callback)
        } else {
            ClothingItemRepository.addItem(item, callback)
        }
    }
}

