package com.example.closetmarket.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.closetmarket.base.MyApplication
import com.example.closetmarket.model.ClothingItem
import kotlin.jvm.java

@Database(entities = [ClothingItem::class], version = 1)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun clothingItemDao(): ClothingItemDao
}

object AppLocalDb {
    val db: AppLocalDbRepository by lazy {
        val context = MyApplication.Globals.appContext
            ?: throw kotlin.IllegalStateException("Application context not available")
        Room.databaseBuilder(
            context,
            AppLocalDbRepository::class.java,
            "closetmarket.db"
        ).fallbackToDestructiveMigration(true)
            .build()
    }
}

