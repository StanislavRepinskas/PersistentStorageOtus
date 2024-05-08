package com.otus.persistentstorage.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        PhotoItemEntity::class
    ],
    version = 1
)
abstract class GalleryDatabase : RoomDatabase() {
    abstract val dao: GalleryDao
}

private lateinit var INSTANCE: GalleryDatabase

fun getGalleryDatabase(context: Context): GalleryDatabase {
    synchronized(GalleryDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                GalleryDatabase::class.java,
                "gallery_database"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}