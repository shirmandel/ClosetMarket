package com.example.closetmarket.api

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class CitiesRequest(
    @SerializedName("country") val country: String = "Israel"
)

data class CitiesResponse(
    @SerializedName("error") val error: Boolean,
    @SerializedName("msg") val msg: String,
    @SerializedName("data") val data: List<String>
)

interface CitiesApi {
    @POST("v0.1/countries/cities")
    fun getCities(@Body request: CitiesRequest = CitiesRequest()): Call<CitiesResponse>
}

