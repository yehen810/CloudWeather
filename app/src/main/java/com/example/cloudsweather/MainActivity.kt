package com.example.cloudsweather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.cloudsweather.utils.BaseActivity

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}