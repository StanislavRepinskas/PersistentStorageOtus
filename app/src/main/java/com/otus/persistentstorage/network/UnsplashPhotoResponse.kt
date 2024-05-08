package com.otus.persistentstorage.network

import com.google.gson.annotations.SerializedName

data class UnsplashPhotoResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("urls")
    val urls: UnsplashPhotoUrlsResponse
)

data class UnsplashPhotoUrlsResponse(
    @SerializedName("small")
    val small: String
)
