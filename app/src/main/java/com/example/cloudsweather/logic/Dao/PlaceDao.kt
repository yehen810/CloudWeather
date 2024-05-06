package com.example.cloudsweather.logic.Dao

import android.content.Context
import androidx.core.content.edit
import com.example.cloudsweather.logic.Model.Place
import com.example.cloudsweather.utils.CloudsWeatherApplication
import com.google.gson.Gson

object PlaceDao {
    fun savePlace(place:Place){
        sharedPreferences().edit {
            //通过Gson将place对象转成json字符串
            putString("place",Gson().toJson(place))
        }
    }

    fun getSavedPlace():Place{
        val placeJson = sharedPreferences().getString("place","")
        //通过Gson将JSON字符串解析成Place对象并返回
        return Gson().fromJson(placeJson,Place::class.java)
    }

    fun isPlaceSaved() = sharedPreferences().contains("place")

    private fun sharedPreferences() =
        CloudsWeatherApplication.context.getSharedPreferences("cloud_weather", Context.MODE_PRIVATE)

}