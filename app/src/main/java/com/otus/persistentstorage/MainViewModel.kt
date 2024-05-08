package com.otus.persistentstorage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.otus.persistentstorage.db.PhotoItemEntity
import com.otus.persistentstorage.db.getGalleryDatabase
import com.otus.persistentstorage.network.getUnsplashApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val context: Context) : ViewModel() {

    private val Context.store: DataStore<Preferences> by preferencesDataStore(name = "main")
    private val galleryDatabase = getGalleryDatabase(context).dao
    private val unsplashApi = getUnsplashApi()

    private val _showProgressFlow = MutableStateFlow<Boolean>(true)
    val showProgressFlow = _showProgressFlow.asStateFlow()

    private val _galleryItemsFlow = MutableStateFlow<List<GalleryItem>>(emptyList())
    val galleryItemsFlow = _galleryItemsFlow.asStateFlow()

    init {
        update()
    }

    fun update(isForce: Boolean = false) {
        _showProgressFlow.value = true
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()

            val lastNetworkLoadTime: Long = if (!isForce) {
                context.store.data.firstOrNull()?.get(LAST_NETWORK_LOAD_KEY) ?: 0L
            } else 0L

            if (isForce || currentTime - lastNetworkLoadTime >= NETWORK_LOAD_THRESHOLD) {
                println("DBG: load from network")

                _galleryItemsFlow.value = withContext(Dispatchers.IO) {
                    val photos = unsplashApi.getPhotos().map {
                        GalleryItem(
                            id = it.id,
                            imageUrl = it.urls.small
                        )
                    }

                    val dbItems: List<PhotoItemEntity> = photos.map {
                        PhotoItemEntity(
                            id = it.id,
                            imageUrl = it.imageUrl
                        )
                    }

                    galleryDatabase.deletePhotos()
                    galleryDatabase.insertPhotos(*dbItems.toTypedArray())

                    context.store.edit {
                        it[LAST_NETWORK_LOAD_KEY] = currentTime
                    }

                    photos
                }
            } else {
                println("DBG: load from db")
                _galleryItemsFlow.value = withContext(Dispatchers.IO) {
                    galleryDatabase.getPhotos()
                }.map {
                    GalleryItem(
                        id = it.id,
                        imageUrl = it.imageUrl
                    )
                }
            }

            _showProgressFlow.value = false
        }
    }

    class Factory(private val context: Context) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(context) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

    companion object {
        private const val NETWORK_LOAD_THRESHOLD = 60_000 * 60
        private val LAST_NETWORK_LOAD_KEY = longPreferencesKey("LAST_NETWORK_LOAD_KEY")
    }
}