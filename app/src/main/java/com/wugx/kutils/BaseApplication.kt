package com.wugx.kutils

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.wugx.k_utils.KUtils

/**
 *
 *
 *@author Wugx
 *@date   2018/12/19
 */
class BaseApplication : Application() {
    val baseUrl="http://192.144.137.174:8081"

//    http://192.144.137.174:8081/uploadFile/soft/version/1543800668298687447.apk


    override fun onCreate() {
        super.onCreate()
        KUtils.init(this).initNetParams(baseUrl, mutableMapOf())
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}