package com.example.cloudsweather.utils

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.example.cloudsweather.showToast
import com.example.cloudsweather.utils.ActivityCollector
import kotlin.properties.Delegates

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        Log.d("BaseActivity",javaClass.simpleName)
        ActivityCollector.addActivity(this)

    }

    

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }

    var exitTime :Long = 0L
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK && event?.action == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis() - exitTime) > 2000){
                "再按一次退出CloudsWeather".showToast()
                exitTime = System.currentTimeMillis()
            }else{
                ActivityCollector.finshAllActivity()
                System.exit(0)
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}