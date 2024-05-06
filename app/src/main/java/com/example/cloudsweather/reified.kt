package com.example.cloudsweather

import android.content.Context
import android.content.Intent

inline fun <reified T:Any> startActivity(context: Context,block:Intent.()-> Unit){
    val intent = Intent(context,T::class.java)
    intent.block()
    context.startActivity(intent)
}

inline fun <reified T:Any>startService(context: Context,block: Intent.() -> Unit = {}){
    val intent = Intent(context,T::class.java)
    intent.block()
    context.startService(intent)
}

inline fun <reified T:Any>stopService(context: Context){
    val intent = Intent(context,T::class.java)
    context.stopService(intent)
}






