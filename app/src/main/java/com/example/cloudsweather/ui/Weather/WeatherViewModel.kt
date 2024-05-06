package com.example.cloudsweather.ui.Weather

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.cloudsweather.logic.Model.Refresh
import com.example.cloudsweather.logic.Model.Location
import com.example.cloudsweather.logic.Repository
import com.example.cloudsweather.showToast
import com.example.cloudsweather.startService
import com.example.cloudsweather.stopService
import com.example.cloudsweather.utils.CloudsWeatherApplication
import com.example.cloudsweather.utils.LogUtil

class WeatherViewModel:ViewModel() {
    private val locationLiveData = MutableLiveData<Location>()

    var locationLng=""
    var locationLat=""
    var placeName=""

    var title = ""
    var text = ""
    var icon = 0

    var checkedItem = 1;

    var context = CloudsWeatherApplication.context

    val weatherLiveData = Transformations.switchMap(locationLiveData){
        location -> Repository.refreshWeather(location.lng,location.lat)
    }

    fun refreshWeather(lng:String,lat:String){
        locationLiveData.value = Location(lng, lat)
    }

    private val broadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if (it.action == "refreshWeather"){
                    val lng = intent.getStringExtra("location_lng") ?: ""
                    val lat = intent.getStringExtra("location_lat") ?: ""
                    val intervalMillis = intent.getLongExtra("intervalMillis",0L)

                    locationLiveData.value = Location(lng, lat)

                    "天气信息已经刷新".showToast()
                    LogUtil.d("WeatherViewModel","数据已经刷新")
                    LogUtil.d("WeatherViewModel",intervalMillis.toString())
                }
            }
        }
    }

    fun registerReceiver(){
        val intentFilter = IntentFilter("refreshWeather")
        context.registerReceiver(broadcastReceiver,intentFilter)
    }

    fun unregisterReceiver(){
        context.unregisterReceiver(broadcastReceiver)
    }

    fun startService(lng:String,lat:String,intervalMillis:Long =  1 * 60 * 1000L){
        val refresh =Refresh(lng,lat,intervalMillis)
        Repository.saveRefresh(refresh)
        startService<RefreshService>(context)

    }

    fun stopService(){
       stopService<RefreshService>(context)
    }

}