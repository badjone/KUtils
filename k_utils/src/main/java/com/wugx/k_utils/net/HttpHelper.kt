package com.wugx.alarm_pro.net

import com.wugx.k_utils.KUtils
import com.wugx.k_utils.net.RetrofitManager
import okhttp3.OkHttpClient

/**
 * @author Wugx
 * @date 2018/11/9
 */
object HttpHelper {

    fun <T> createRetrofit(cls: Class<T>): T {
        return RetrofitManager.getRetrofit(KUtils.base_url, KUtils.map).create(cls)
    }


    fun <T> createRetrofit(okHttpClient: OkHttpClient,cls: Class<T>): T {
        return RetrofitManager.getRetrofit(KUtils.base_url,okHttpClient, KUtils.map).create(cls)
    }


}
