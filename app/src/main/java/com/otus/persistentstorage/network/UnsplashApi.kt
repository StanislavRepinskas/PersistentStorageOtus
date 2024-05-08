package com.otus.persistentstorage.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface UnsplashApi {
    @GET("photos")
    suspend fun getPhotos(): List<UnsplashPhotoResponse>
}

fun getUnsplashApi(): UnsplashApi {
    val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    val client = OkHttpClient().newBuilder()
        .addInterceptor { interceptorChain ->
            val request = interceptorChain.request().newBuilder()
                .header("Authorization", "Client-ID LG0qW0osr-hp_8NN_6iDpbznZN5ojIkT59KPZQ3hv2c")
                .build()
            interceptorChain.proceed(request)
        }
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.unsplash.com//")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    return retrofit.create(UnsplashApi::class.java)
}