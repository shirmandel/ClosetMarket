package com.example.closetmarket.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.closetmarket.base.MyApplication
import com.example.closetmarket.db.AppLocalDb
import kotlin.let
import kotlin.run
import com.example.closetmarket.model.ClothingItem

object ClothingItemRepository {
    private const val TAG = "ClothingItemRepo"
    private const val PREFS_NAME = "closetmarket_prefs"
    private const val MOCK_SEEDED_KEY = "mock_seeded"

    private val localDb = AppLocalDb.db.clothingItemDao()

    val allItems: LiveData<List<ClothingItem>> = localDb.getAllItems()

    fun getItemsByUser(userId: String): LiveData<List<ClothingItem>> {
        return localDb.getItemsByUser(userId)
    }

    fun getWishlistedItems(): LiveData<List<ClothingItem>> {
        return localDb.getWishlistedItems()
    }

    fun refreshItems(callback: (() -> Unit)? = null) {
        val context = MyApplication.Globals.appContext ?: run {
            callback?.invoke()
            return
        }
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val alreadySeeded = prefs.getBoolean(MOCK_SEEDED_KEY, false)

        if (!alreadySeeded) {
            // Seed mock data on first launch
            MyApplication.Globals.executorService.execute {
                val existing = localDb.getAllItemsSync()
                if (existing.isEmpty()) {
                    seedMockData()
                }
                prefs.edit().putBoolean(MOCK_SEEDED_KEY, true).apply()
                MyApplication.Globals.mainHandler.post {
                    callback?.invoke()
                }
            }
        } else {
            callback?.invoke()
        }
    }

    fun addItem(item: ClothingItem, callback: (Boolean) -> Unit) {
        MyApplication.Globals.executorService.execute {
            localDb.insert(item.copy(lastUpdated = System.currentTimeMillis() / 1000))
            MyApplication.Globals.mainHandler.post {
                callback(true)
            }
        }
    }

    fun updateItem(item: ClothingItem, callback: (Boolean) -> Unit) {
        MyApplication.Globals.executorService.execute {
            localDb.insert(item.copy(lastUpdated = System.currentTimeMillis() / 1000))
            MyApplication.Globals.mainHandler.post {
                callback(true)
            }
        }
    }

    fun deleteItem(itemId: String, callback: (Boolean) -> Unit) {
        MyApplication.Globals.executorService.execute {
            localDb.deleteById(itemId)
            MyApplication.Globals.mainHandler.post {
                callback(true)
            }
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

    private fun seedMockData() {
        val mockItems = listOf(
            ClothingItem(
                id = "1", title = "Vintage Floral Dress",
                description = "Beautiful vintage floral dress, perfect for summer. Size M, cotton material, lightly worn.",
                imageUrl = "https://www.svgrepo.com/show/508699/landscape-placeholder.svg",
                category = "dresses", condition = "used", price = "50",
                city = "Tel Aviv", street = "Rothschild Blvd",
                userId = "mock_user_1", userName = "Sarah Cohen",
                uploadDate = "Dec 28", lastUpdated = System.currentTimeMillis() / 1000
            ),
            ClothingItem(
                id = "2", title = "Denim Jacket",
                description = "Classic denim jacket, barely worn. Size L, perfect condition with no damage.",
                imageUrl = "https://www.svgrepo.com/show/508699/landscape-placeholder.svg",
                category = "jackets", condition = "like-new", price = "80",
                city = "Jerusalem", street = "King George St",
                userId = "mock_user_2", userName = "David Levi",
                uploadDate = "Dec 27", lastUpdated = System.currentTimeMillis() / 1000 - 100
            ),
            ClothingItem(
                id = "3", title = "White Canvas Sneakers",
                description = "Clean white sneakers, size 42. Worn only a few times, in great condition.",
                imageUrl = "https://www.svgrepo.com/show/508699/landscape-placeholder.svg",
                category = "shoes", condition = "like-new", price = "60",
                city = "Haifa", street = "Ben Gurion Ave",
                userId = "mock_user_3", userName = "Maya Israeli",
                uploadDate = "Dec 26", lastUpdated = System.currentTimeMillis() / 1000 - 200
            ),
            ClothingItem(
                id = "4", title = "Casual T-Shirt Bundle",
                description = "Set of 3 basic t-shirts in black, white, and gray. Size S. All in good condition.",
                imageUrl = "https://www.svgrepo.com/show/508699/landscape-placeholder.svg",
                category = "tops", condition = "used", price = "free",
                city = "Tel Aviv", street = "Dizengoff St",
                userId = "mock_user_2", userName = "David Levi",
                uploadDate = "Dec 25", lastUpdated = System.currentTimeMillis() / 1000 - 300
            ),
            ClothingItem(
                id = "5", title = "Winter Coat",
                description = "Warm winter coat with hood. Size M. Perfect for cold weather, barely used.",
                imageUrl = "https://www.svgrepo.com/show/508699/landscape-placeholder.svg",
                category = "jackets", condition = "like-new", price = "120",
                city = "Rishon LeZion", street = "Herzl St",
                userId = "mock_user_1", userName = "Sarah Cohen",
                uploadDate = "Dec 24", lastUpdated = System.currentTimeMillis() / 1000 - 400
            ),
            ClothingItem(
                id = "6", title = "Summer Shorts",
                description = "Light blue denim shorts, perfect for hot days. Size 28, good condition.",
                imageUrl = "https://www.svgrepo.com/show/508699/landscape-placeholder.svg",
                category = "bottoms", condition = "used", price = "30",
                city = "Netanya", street = "Herzl St",
                userId = "mock_user_3", userName = "Maya Israeli",
                uploadDate = "Dec 23", lastUpdated = System.currentTimeMillis() / 1000 - 500
            ),
            ClothingItem(
                id = "7", title = "Leather Handbag",
                description = "Elegant brown leather handbag. Good quality, has some wear but still looks great.",
                imageUrl = "https://www.svgrepo.com/show/508699/landscape-placeholder.svg",
                category = "accessories", condition = "used", price = "45",
                city = "Tel Aviv", street = "Allenby St",
                userId = "mock_user_2", userName = "David Levi",
                uploadDate = "Dec 22", lastUpdated = System.currentTimeMillis() / 1000 - 600
            ),
            ClothingItem(
                id = "8", title = "Brand New Sneakers",
                description = "Never worn! Brand new athletic sneakers, size 40. Bought wrong size.",
                imageUrl = "https://www.svgrepo.com/show/508699/landscape-placeholder.svg",
                category = "shoes", condition = "new", price = "90",
                city = "Beer Sheva", street = "Rager Blvd",
                userId = "mock_user_1", userName = "Sarah Cohen",
                uploadDate = "Dec 21", lastUpdated = System.currentTimeMillis() / 1000 - 700
            ),
            ClothingItem(
                id = "9", title = "Striped Summer Top",
                description = "Light and breezy striped top, perfect for summer. Size S, excellent condition.",
                imageUrl = "https://www.svgrepo.com/show/508699/landscape-placeholder.svg",
                category = "tops", condition = "like-new", price = "35",
                city = "Tel Aviv", street = "Ibn Gabirol St",
                userId = "mock_user_1", userName = "Sarah Cohen",
                uploadDate = "Dec 20", lastUpdated = System.currentTimeMillis() / 1000 - 800
            )
        )
        for (item in mockItems) {
            localDb.insert(item)
        }
        Log.d(TAG, "Mock data seeded: ${mockItems.size} items")
    }
}
