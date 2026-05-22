package com.example.closetmarket.repository

import android.util.Log
import com.example.closetmarket.api.CitiesResponse
import com.example.closetmarket.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object LocationRepository {
    private const val TAG = "LocationRepository"

    private var cachedCities: List<String> = emptyList()

    fun loadAllCities(callback: (List<String>) -> Unit) {
        if (cachedCities.isNotEmpty()) {
            callback(cachedCities)
            return
        }

        RetrofitClient.citiesApi.getCities().enqueue(
            object : Callback<CitiesResponse> {
                override fun onResponse(
                    call: Call<CitiesResponse>,
                    response: Response<CitiesResponse>
                ) {
                    if (response.isSuccessful && response.body()?.error == false) {
                        cachedCities = response.body()!!.data.sorted()
                        Log.d(TAG, "Loaded ${cachedCities.size} cities from CountriesNow API")
                        callback(cachedCities)
                    } else {
                        Log.e(TAG, "Failed to load cities: ${response.code()}")
                        callback(emptyList())
                    }
                }

                override fun onFailure(call: Call<CitiesResponse>, t: Throwable) {
                    Log.e(TAG, "Network error loading cities", t)
                    callback(emptyList())
                }
            }
        )
    }
}
