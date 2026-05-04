package com.example.closetmarket.repository

import android.net.Uri
import android.util.Log
import com.example.closetmarket.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlin.apply
import kotlin.run

object UserRepository {
    private const val TAG = "UserRepository"

    private val auth = FirebaseAuth.getInstance()

    fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return User(
            uid = firebaseUser.uid,
            displayName = firebaseUser.displayName ?: "",
            email = firebaseUser.email ?: "",
            profileImageUrl = firebaseUser.photoUrl?.toString() ?: ""
        )
    }

    fun isLoggedIn(): Boolean = auth.currentUser != null

    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                callback(true, null)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Login failed: ${e.message}")
                callback(false, e.message)
            }
    }

    fun register(email: String, password: String, name: String, callback: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val firebaseUser = result.user
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                firebaseUser?.updateProfile(profileUpdates)
                    ?.addOnSuccessListener {
                        callback(true, null)
                    }
                    ?.addOnFailureListener {
                        callback(true, null) // Auth succeeded, profile update is minor
                    }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Register failed: ${e.message}")
                callback(false, e.message)
            }
    }

    fun logout() {
        auth.signOut()
    }

    fun updateProfile(name: String, imageUrl: String?, callback: (Boolean) -> Unit) {
        val user = auth.currentUser ?: run {
            callback(false)
            return
        }

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .apply {
                if (imageUrl != null) {
                    setPhotoUri(Uri.parse(imageUrl))
                }
            }
            .build()

        user.updateProfile(profileUpdates)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Profile update failed: ${e.message}")
                callback(false)
            }
    }
}
