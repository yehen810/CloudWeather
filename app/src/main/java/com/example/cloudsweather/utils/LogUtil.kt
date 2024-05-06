package com.example.cloudsweather.utils

import android.util.Log

//日志工具
object LogUtil {
    private const val VERBOSE = 1

    private const val DEBUG = 2

    private const val INFO = 3

    private const val WARN =4

    private const val ERROR = 5

    private const val NOTHING = 6

    /*private var level = VERBOSE*/
    private var level = NOTHING     //确保项目完成后不再打印日志

    fun v(tag:String,msg:String){
        if(level <= VERBOSE){
            Log.v(tag,msg)
        }
    }

    fun d(tag: String,msg: String){
        if(level <= DEBUG){
            Log.d(tag, msg)
        }
    }

    fun i(tag: String,msg: String){
        if(level <= INFO){
            Log.i(tag, msg)
        }
    }

    fun w(tag: String,msg: String){
        if(level <= WARN){
            Log.w(tag, msg)
        }
    }

    fun e(tag: String,msg: String){
        if(level <= ERROR){
            Log.e(tag, msg)
        }
    }
}