package com.example.cloudsweather.logic.Network

import com.example.cloudsweather.utils.CloudsWeatherApplication
import com.example.cloudsweather.logic.Model.DailyResponse
import com.example.cloudsweather.logic.Model.RealtimeResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface WeatherService {
    //获取实时的天气信息
    @GET("v2.5/${CloudsWeatherApplication.TOKEN}/{lng},{lat}/realtime.json")
    fun getRealtimeWeather(@Path("lng")lng:String,@Path("lat")lat:String):Call<RealtimeResponse>

    //获取未来的天气信息
    @GET("v2.5/${CloudsWeatherApplication.TOKEN}/{lng},{lat}/daily.json")
    fun getDailyWeather(@Path("lng") lng:String,@Path("lat") lat:String):Call<DailyResponse>

}