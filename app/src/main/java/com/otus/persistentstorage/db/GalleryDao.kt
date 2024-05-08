package com.otus.persistentstorage.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GalleryDao {
    @Query("SELECT * FROM photos")
    fun getPhotos(): List<PhotoItemEntity>

    @Insert
    fun insertPhotos(vararg items: PhotoItemEntity)

    @Query("DELETE FROM photos")
    fun deletePhotos()
}