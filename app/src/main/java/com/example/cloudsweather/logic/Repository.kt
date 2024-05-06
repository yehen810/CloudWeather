package com.example.cloudsweather.logic

import androidx.lifecycle.liveData
import com.example.cloudsweather.logic.Dao.RefreshDao
import com.example.cloudsweather.logic.Dao.PlaceDao
import com.example.cloudsweather.logic.Model.Refresh
import com.example.cloudsweather.logic.Model.Place
import com.example.cloudsweather.logic.Model.Weather
import com.example.cloudsweather.logic.Network.CloudsWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

object Repository {

    fun savePlace(place:Place) = PlaceDao.savePlace(place)

    fun getSavedPlace() = PlaceDao.getSavedPlace()

    fun isPlaceSaved() = PlaceDao.isPlaceSaved()

    fun searchPlaces(query:String) = fire(Dispatchers.IO){
            val placeResponse = CloudsWeatherNetwork.searchPlaces(query)
            if(placeResponse.status == "ok"){
                val places = placeResponse.places
                //包装获得的城市数据列表
                Result.success(places)
            }else{
                Result.failure(java.lang.RuntimeException("response status is ${placeResponse.status}"))
            }
    }


    fun refreshWeather(lng:String,lat:String) = fire(Dispatchers.IO) {
                coroutineScope {
                    val deferredRealtime = async {
                        CloudsWeatherNetwork.getRealtimeWeather(lng, lat)
                    }
                    val defferedDaily = async {
                        CloudsWeatherNetwork.getDailyWeather(lng,lat)
                    }
                    //并发执行
                    val realtimeResponse = deferredRealtime.await()
                    val dailyResponse = defferedDaily.await()
                    if(realtimeResponse.status == "ok" && dailyResponse.status == "ok"){
                        val realtime = realtimeResponse.result.realtime
                        val daily = dailyResponse.result.daily
                        val weather = Weather(realtime,daily)
                        Result.success(weather)
                    }else{
                        Result.failure((RuntimeException("realtime response status is ${realtimeResponse.status}"
                                +"daily response status is ${dailyResponse.status}")))
                    }
                }
    }

    //封装try-catch处理的高阶函数
    private fun <T>fire(context:CoroutineContext,block:suspend ()->Result<T>) = liveData<Result<T>>(context){
        val result = try{
            block()
        }catch (e:Exception){
            Result.failure<T>(e)
        }
        emit(result)
    }

    fun saveRefresh(refresh:Refresh) = RefreshDao.saveRefresh(refresh)

    fun getSavedRefresh() = RefreshDao.getSavedRefresh()

    fun isRefreshSaved() = RefreshDao.isRefreshSaved()
}