package com.example.cloudsweather.ui.Weather

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkManager
import com.example.cloudsweather.R
import com.example.cloudsweather.logic.Model.Location
import com.example.cloudsweather.logic.Model.Weather
import com.example.cloudsweather.logic.Model.getSky
import com.example.cloudsweather.showToast
import com.example.cloudsweather.utils.BaseActivity
import com.example.cloudsweather.utils.LogUtil
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.forecast.*
import kotlinx.android.synthetic.main.life_index.*
import kotlinx.android.synthetic.main.now.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class WeatherActivity : BaseActivity() {
    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        val decorView = window.decorView
        //改变系统的UI显示，这里表示Activity的布局会显示在状态栏上面
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        //将状态栏设置为透明色
        window.statusBarColor = Color.TRANSPARENT


        navBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener{
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {
                //滑动菜单隐藏时，要隐藏搜索时显示的输入法
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
            }

            override fun onDrawerStateChanged(newState: Int) {}

        })

        //初始操作赋值
        if(viewModel.locationLng.isEmpty()){
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }

        if(viewModel.locationLat.isEmpty()){
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if(viewModel.placeName.isEmpty()){
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }

        LogUtil.d("location_lng",viewModel.locationLng)
        LogUtil.d("location_lat",viewModel.locationLat)

        viewModel.weatherLiveData.observe(this, Observer { result ->
            val weather=result.getOrNull()
            if(weather!=null){
                showWeatherInfo(weather)
            }else{
                "无法成功获取天气信息".showToast()
                result.exceptionOrNull()?.printStackTrace()
            }
            swipeRefresh.isRefreshing = false
        })

        //设置下拉刷新进度条的颜色
        swipeRefresh.setColorSchemeResources(R.color.black)
        swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }
        refreshWeather()

        //悬浮按钮点击事件
        refreshFab.setOnClickListener {
            refreshDialog()
        }

        //注册广播接收者
        viewModel.registerReceiver()
        //默认的后台服务是一分钟更新一次数据
        viewModel.startService(lng = viewModel.locationLng, lat = viewModel.locationLat)
    }

    fun refreshWeather(){
        viewModel.refreshWeather(viewModel.locationLng,viewModel.locationLat)
        swipeRefresh.isRefreshing = true
    }

    private fun showWeatherInfo(weather: Weather) {
        placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily

        LogUtil.e("daily",daily.toString())

        //填充now.xml布局中的数据
        val currentTempText = "${realtime.temperature.toInt()}°C"

        currentTemp.text = currentTempText
        currentSky.text = getSky(realtime.skycon).info

        val currentPM25Text = "空气指数${realtime.airQuality.aqi.chn.toInt()}"
        currentAQI.text = currentPM25Text
        LogUtil.e("温度",realtime.temperature.toString())
        LogUtil.e("空气品质",realtime.airQuality.aqi.chn.toString())

        nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)

        //填充forecast.xml布局中的数据
        forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for(i in 0 until days){

            val skycon = daily.skycon[i]

            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false)

            val dateInfo = view.findViewById(R.id.dateInfo) as TextView
            val skyIcon = view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo = view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo = view.findViewById(R.id.temperatureInfo) as TextView

            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.date)

            /*LogUtil.e("时间",skycon.date.toString())*/

            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()}~${temperature.max.toInt()}°C"
            temperatureInfo.text = tempText
            forecastLayout.addView(view)
        }

        //填充life_index.xml布局中的数据
        //生活指数服务器会返回许多天的数据，但界面上只需当天的数据，故取下标0的数据即可
        val lifeIndex = daily.lifeIndex
        coldRiskText.text = lifeIndex.coldRisk[0].desc
        dressingText.text = lifeIndex.dressing[0].desc
        ultravioletText.text = lifeIndex.ultraviolet[0].desc
        carWashingText.text = lifeIndex.carWashing[0].desc
        weatherLayout.visibility = View.VISIBLE


        viewModel.title = "${viewModel.placeName} ${getSky(realtime.skycon).info}"
        viewModel.text = "现在温度${currentTempText},${currentPM25Text}"
        viewModel.icon = getSky(realtime.skycon).icon
    }

    fun refreshDialog() {
         val dialog = AlertDialog.Builder(this).apply {
             setTitle("设置天气更新频率")
             setIcon(R.mipmap.ic_launcher)
             setCancelable(false)
             setSingleChoiceItems(arrayOf("30秒", "1分钟", "2分钟", "5分钟", "10分钟"), viewModel.checkedItem)
             { dialog, which ->
                 viewModel.checkedItem = which
                 val intervalMillis = when(which){
                     0 -> 1 * 30 * 1000L   //30秒
                     1 -> 1 * 60 * 1000L   //1分钟
                     2 -> 2 * 60 * 1000L   //2分钟
                     3 -> 5 * 60 * 1000L   //5分钟
                     4 -> 10 * 60 * 1000L   //10分钟
                     else -> 1 * 60 * 1000L
                 }
                 viewModel.stopService()
                 viewModel.startService(viewModel.locationLng,viewModel.locationLat,intervalMillis)
                 dialog.dismiss()
             }
             create()
         }
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        //销毁时取消注册广播接收者和停止后台服务
        viewModel.unregisterReceiver()
        viewModel.stopService()
    }

}