package com.example.cloudsweather.logic.Network

import android.app.appsearch.SearchResult
import com.example.cloudsweather.logic.Model.RealtimeResponse
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Query
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.RuntimeException
import java.net.HttpURLConnection
import java.net.Inet4Address
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object CloudsWeatherNetwork {
    //统一的网络数据访问源入口
    private val placeService = ServiceCreator.create<PlaceService>()

    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query).await()

    private val weatherService = ServiceCreator.create<WeatherService>()

    suspend fun getDailyWeather(lng:String,lat:String) = weatherService.getDailyWeather(lng,lat).await()

    suspend fun getRealtimeWeather(lng: String,lat: String) = weatherService.getRealtimeWeather(lng, lat).await()

    private suspend fun <T> Call<T>.await():T{
        return suspendCoroutine { continuation -> enqueue(object :Callback<T>{
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val body = response.body()
                if (body != null){
                    continuation.resume(body)
                }else{
                    continuation.resumeWithException(RuntimeException("response body is null"))
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                continuation.resumeWithException(t)
            }

        }) }
    }

}