package com.example.closetmarket.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Nominatim — for street/location search
    private const val NOMINATIM_BASE_URL = "https://nominatim.openstreetmap.org/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("User-Agent", "ClosetMarket/1.0")
                .build()
            chain.proceed(request)
        })
        .build()

    private val nominatimRetrofit = Retrofit.Builder()
        .baseUrl(NOMINATIM_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val locationApi: LocationApi = nominatimRetrofit.create(LocationApi::class.java)

    // CountriesNow — for loading all cities in Israel
    private const val COUNTRIES_NOW_BASE_URL = "https://countriesnow.space/api/"

    private val countriesNowRetrofit = Retrofit.Builder()
        .baseUrl(COUNTRIES_NOW_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val citiesApi: CitiesApi = countriesNowRetrofit.create(CitiesApi::class.java)
}


