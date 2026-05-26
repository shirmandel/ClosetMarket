package com.example.closetmarket.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.closetmarket.base.MyApplication
import com.example.closetmarket.db.AppLocalDb
import com.example.closetmarket.model.ClothingItem
import com.google.firebase.firestore.FirebaseFirestore

object ClothingItemRepository {
    private const val TAG = "ClothingItemRepo"
    private const val COLLECTION = "clothingItems"

    private val localDb = AppLocalDb.db.clothingItemDao()
    private val firestore = FirebaseFirestore.getInstance()

    val allItems: LiveData<List<ClothingItem>> = localDb.getAllItems()

    fun getItemsByUser(userId: String): LiveData<List<ClothingItem>> {
        return localDb.getItemsByUser(userId)
    }

    fun getWishlistedItems(): LiveData<List<ClothingItem>> {
        return localDb.getWishlistedItems()
    }

    fun refreshItems(callback: (() -> Unit)? = null) {
        firestore.collection(COLLECTION)
            .get()
            .addOnSuccessListener { snapshot ->
                MyApplication.Globals.executorService.execute {
                    val remoteIds = HashSet<String>()
                    for (doc in snapshot.documents) {
                        val item = doc.toObject(ClothingItem::class.java)
                        if (item != null && item.id.isNotEmpty()) {

                            val existing = localDb.getItemById(item.id)
                            val merged = if (existing != null)
                                item.copy(isWishlisted = existing.isWishlisted)
                            else item
                            localDb.insert(merged)
                            remoteIds.add(item.id)
                        }
                    }

                    val localItems = localDb.getAllItemsSync()
                    var pruned = 0
                    for (local in localItems) {
                        if (local.id !in remoteIds) {
                            localDb.deleteById(local.id)
                            pruned++
                        }
                    }
                    Log.d(TAG, "Synced ${snapshot.size()} items; pruned $pruned stale")
                    MyApplication.Globals.mainHandler.post { callback?.invoke() }
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to sync from Firestore", e)
                callback?.invoke()
            }
    }

    fun addItem(item: ClothingItem, callback: (Boolean) -> Unit) {
        val itemWithTimestamp = item.copy(lastUpdated = System.currentTimeMillis() / 1000)

        firestore.collection(COLLECTION)
            .document(item.id)
            .set(itemToMap(itemWithTimestamp))
            .addOnSuccessListener {
                MyApplication.Globals.executorService.execute {
                    localDb.insert(itemWithTimestamp)
                    MyApplication.Globals.mainHandler.post {
                        callback(true)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to add item to Firestore", e)
                callback(false)
            }
    }

    fun updateItem(item: ClothingItem, callback: (Boolean) -> Unit) {
        val itemWithTimestamp = item.copy(lastUpdated = System.currentTimeMillis() / 1000)

        firestore.collection(COLLECTION)
            .document(item.id)
            .set(itemToMap(itemWithTimestamp))
            .addOnSuccessListener {
                MyApplication.Globals.executorService.execute {
                    localDb.insert(itemWithTimestamp)
                    MyApplication.Globals.mainHandler.post {
                        callback(true)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to update item in Firestore", e)
                callback(false)
            }
    }

    fun deleteItem(itemId: String, callback: (Boolean) -> Unit) {
        firestore.collection(COLLECTION)
            .document(itemId)
            .delete()
            .addOnSuccessListener {
                MyApplication.Globals.executorService.execute {
                    localDb.deleteById(itemId)
                    MyApplication.Globals.mainHandler.post {
                        callback(true)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to delete item from Firestore", e)
                callback(false)
            }
    }

    fun toggleWishlist(itemId: String) {
        MyApplication.Globals.executorService.execute {
            val item = localDb.getItemById(itemId)
            item?.let {
                localDb.insert(it.copy(isWishlisted = !it.isWishlisted))
            }
        }
    }

    fun getItemById(itemId: String, callback: (ClothingItem?) -> Unit) {
        MyApplication.Globals.executorService.execute {
            val item = localDb.getItemById(itemId)
            MyApplication.Globals.mainHandler.post {
                callback(item)
            }
        }
    }

    private fun itemToMap(item: ClothingItem): Map<String, Any?> = mapOf(
        "id" to item.id,
        "title" to item.title,
        "description" to item.description,
        "imageUrl" to item.imageUrl,
        "category" to item.category,
        "condition" to item.condition,
        "price" to item.price,
        "city" to item.city,
        "street" to item.street,
        "userId" to item.userId,
        "userName" to item.userName,
        "userEmail" to item.userEmail,
        "uploadDate" to item.uploadDate,
        "lastUpdated" to item.lastUpdated
    )
}
