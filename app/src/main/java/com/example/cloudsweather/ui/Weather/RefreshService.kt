package com.example.cloudsweather.ui.Weather

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.example.cloudsweather.logic.Repository
import com.example.cloudsweather.utils.LogUtil
import kotlinx.coroutines.Runnable

class RefreshService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    private var intervalMillis: Long = 0

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
        runnable = Runnable {

            val refresh = Repository.getSavedRefresh()
            intervalMillis = refresh.intervalMillis

            //发送广播给WeatherViewModel中的广播接收者
            val intent = Intent("refreshWeather").apply {
                setPackage(packageName)
                putExtra("location_lng",refresh.lng)
                putExtra("location_lat",refresh.lat)
                putExtra("intervalMillis",refresh.intervalMillis)
            }
            sendBroadcast(intent)

            // 调度下一个执行时间，实现每隔 intervalMillis 豪秒执行一次
            handler.postDelayed(runnable,intervalMillis)
            LogUtil.d("服务","服务已创建,当前任务重复间隔为$intervalMillis 毫秒")
        }
        //将 runnable 对象立即放入消息队列，而不需要延迟执行
        handler.post(runnable)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LogUtil.d("服务","服务已开启,当前任务重复间隔为$intervalMillis 毫秒")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.d("服务","服务已关闭")
        //从消息队列中移除指定的 runnable 对象，以防止它被执行或延迟执行
        // 移除任务，避免内存泄漏
        handler.removeCallbacks(runnable)
    }
}