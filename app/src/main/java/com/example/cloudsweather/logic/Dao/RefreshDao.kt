package com.example.cloudsweather.logic.Dao

import android.content.Context
import androidx.core.content.edit
import com.example.cloudsweather.logic.Model.Refresh
import com.example.cloudsweather.utils.CloudsWeatherApplication
import com.google.gson.Gson

object RefreshDao {
    fun saveRefresh(refresh: Refresh){
        sharedPreferences().edit {
            putString("refresh",Gson().toJson(refresh))
        }
    }

    fun getSavedRefresh():Refresh{
        val refreshJson = sharedPreferences().getString("refresh","")
        return Gson().fromJson(refreshJson,Refresh::class.java)
    }

    fun isRefreshSaved() = sharedPreferences().contains("refresh")

    private fun sharedPreferences() = CloudsWeatherApplication.context.getSharedPreferences("refresh",Context.MODE_PRIVATE)
}