package com.example.closetmarket.repository

import android.graphics.Bitmap
import android.util.Log

object ImageRepository {
    private const val TAG = "ImageRepository"

    fun uploadImage(bitmap: Bitmap, folder: String, callback: (String?) -> Unit) {
        // Mock: return a placeholder URL instead of uploading to Firebase Storage
        // When you set up Firebase Storage later, replace this with real upload logic
        Log.d(TAG, "Mock image upload for folder: $folder (Storage not configured)")
        callback("https://www.svgrepo.com/show/508699/landscape-placeholder.svg")
    }
}
