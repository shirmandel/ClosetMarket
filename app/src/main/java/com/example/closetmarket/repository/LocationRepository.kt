package com.example.closetmarket.repository

import android.util.Log
import com.example.closetmarket.api.CitiesResponse
import com.example.closetmarket.api.LocationResult
import com.example.closetmarket.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object LocationRepository {
    private const val TAG = "LocationRepository"

    // Cached list of all city names loaded from API
    private var cachedCities: List<String> = emptyList()

    /**
     * Loads all Israeli cities from CountriesNow API once,
     * caches them, and returns via callback.
     */
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

    fun searchCity(query: String, callback: (List<LocationResult>) -> Unit) {
        RetrofitClient.locationApi.searchCity(query).enqueue(
            object : Callback<List<LocationResult>> {
                override fun onResponse(
                    call: Call<List<LocationResult>>,
                    response: Response<List<LocationResult>>
                ) {
                    if (response.isSuccessful) {
                        val results = response.body() ?: emptyList()
                        Log.d(TAG, "Found ${results.size} cities for '$query'")
                        callback(results)
                    } else {
                        Log.e(TAG, "City API error: ${response.code()}")
                        callback(emptyList())
                    }
                }

                override fun onFailure(call: Call<List<LocationResult>>, t: Throwable) {
                    Log.e(TAG, "City network error", t)
                    callback(emptyList())
                }
            }
        )
    }

    fun searchStreet(query: String, callback: (List<LocationResult>) -> Unit) {
        RetrofitClient.locationApi.searchLocation(query).enqueue(
            object : Callback<List<LocationResult>> {
                override fun onResponse(
                    call: Call<List<LocationResult>>,
                    response: Response<List<LocationResult>>
                ) {
                    if (response.isSuccessful) {
                        val results = response.body() ?: emptyList()
                        Log.d(TAG, "Found ${results.size} results for '$query'")
                        callback(results)
                    } else {
                        Log.e(TAG, "API error: ${response.code()}")
                        callback(emptyList())
                    }
                }

                override fun onFailure(call: Call<List<LocationResult>>, t: Throwable) {
                    Log.e(TAG, "Network error", t)
                    callback(emptyList())
                }
            }
        )
    }
}

