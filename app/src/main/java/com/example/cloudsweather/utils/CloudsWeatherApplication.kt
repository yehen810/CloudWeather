package com.example.cloudsweather.utils


import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class CloudsWeatherApplication : Application() {
    companion object{
        //Context设置成静态变量很容易会产生内存泄漏的问题
        //注解用于忽略警告
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        //彩云天气的令牌值
        const val  TOKEN = "hoRuuPGIK9zrHIQc"
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }


}