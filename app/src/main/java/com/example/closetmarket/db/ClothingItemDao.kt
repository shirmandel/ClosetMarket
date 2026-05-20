package com.example.closetmarket.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.closetmarket.model.ClothingItem

@Dao
interface ClothingItemDao {

    @Query("SELECT * FROM clothing_items ORDER BY lastUpdated DESC")
    fun getAllItems(): LiveData<List<ClothingItem>>

    @Query("SELECT * FROM clothing_items ORDER BY lastUpdated DESC")
    fun getAllItemsSync(): List<ClothingItem>

    @Query("SELECT * FROM clothing_items WHERE userId = :userId ORDER BY lastUpdated DESC")
    fun getItemsByUser(userId: String): LiveData<List<ClothingItem>>

    @Query("SELECT * FROM clothing_items WHERE isWishlisted = 1 ORDER BY lastUpdated DESC")
    fun getWishlistedItems(): LiveData<List<ClothingItem>>

    @Query("SELECT * FROM clothing_items WHERE id = :id")
    fun getItemById(id: String): ClothingItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(items: List<ClothingItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: ClothingItem)

    @Update
    fun updateItem(item: ClothingItem)

    @Delete
    fun delete(item: ClothingItem)

    @Query("DELETE FROM clothing_items WHERE id = :id")
    fun deleteById(id: String)

    @Query("DELETE FROM clothing_items")
    fun deleteAll()

    @Query("SELECT * FROM clothing_items WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY lastUpdated DESC")
    fun searchItems(query: String): List<ClothingItem>
}
