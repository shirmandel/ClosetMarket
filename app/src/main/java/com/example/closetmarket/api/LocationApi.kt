package com.example.closetmarket.api

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

data class LocationAddress(
    @SerializedName("road") val road: String?,
    @SerializedName("city") val city: String?,
    @SerializedName("town") val town: String?,
    @SerializedName("village") val village: String?,
    @SerializedName("suburb") val suburb: String?,
    @SerializedName("state") val state: String?,
    @SerializedName("country") val country: String?
) {
    fun getCityName(): String = city ?: town ?: village ?: suburb ?: ""
}

data class LocationResult(
    @SerializedName("display_name") val displayName: String,
    @SerializedName("lat") val lat: String,
    @SerializedName("lon") val lon: String,
    @SerializedName("address") val address: LocationAddress?
)

interface LocationApi {
    @GET("search")
    fun searchLocation(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("addressdetails") addressDetails: Int = 1,
        @Query("accept-language") language: String = "en",
        @Query("limit") limit: Int = 5,
        @Query("countrycodes") countryCodes: String = "il"
    ): Call<List<LocationResult>>

    @GET("search")
    fun searchCity(
        @Query("city") city: String,
        @Query("format") format: String = "json",
        @Query("addressdetails") addressDetails: Int = 1,
        @Query("accept-language") language: String = "en",
        @Query("limit") limit: Int = 10,
        @Query("countrycodes") countryCodes: String = "il",
        @Query("featuretype") featureType: String = "settlement"
    ): Call<List<LocationResult>>

    @GET("search")
    fun getAllCities(
        @Query("q") query: String = "Israel",
        @Query("format") format: String = "json",
        @Query("addressdetails") addressDetails: Int = 1,
        @Query("accept-language") language: String = "en",
        @Query("limit") limit: Int = 50,
        @Query("countrycodes") countryCodes: String = "il",
        @Query("featuretype") featureType: String = "city"
    ): Call<List<LocationResult>>
}



