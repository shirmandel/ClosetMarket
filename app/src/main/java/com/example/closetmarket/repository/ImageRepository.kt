package com.example.closetmarket.repository

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.UUID

object ImageRepository {
    private const val TAG = "ImageRepository"
    private val storage = FirebaseStorage.getInstance()

    fun uploadImage(bitmap: Bitmap, folder: String, callback: (String?) -> Unit) {
        val ref = storage.reference.child("$folder/${UUID.randomUUID()}.jpg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        val data = baos.toByteArray()

        ref.putBytes(data)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    Log.d(TAG, "Image uploaded: $uri")
                    callback(uri.toString())
                }.addOnFailureListener { e ->
                    Log.e(TAG, "Failed to get download URL", e)
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to upload image", e)
                callback(null)
            }
    }
}
