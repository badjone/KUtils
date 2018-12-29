package com.wugx_mvp.app.utils.json

import com.blankj.utilcode.util.LogUtils
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

/**
 *
 * json解析工具类
 */
object JsonUtils {

    private val gson by lazy { GsonBuilder().serializeNulls().create() }

    fun <T> parseJson2Obj(jsonData: String, c: Class<T>): T? {
        checkNotNull(jsonData)
        return try {
            gson.fromJson(jsonData, c)
        } catch (e: Exception) {
            LogUtils.e("JsonUtils parse exception ${e.localizedMessage}")
            null
        }
    }

    /**
     *
     * 解析含泛型json  TypeBuilder
     */
    fun <T> parseJson2Obj(jsonData: String, type: Type): T? {
        checkNotNull(jsonData)
        return try {
            gson.fromJson<T>(jsonData.trim(), type)
        } catch (e: Exception) {
            LogUtils.e("JsonUtils parse exception ${e.localizedMessage}")
            null
        }
    }

    /**
     * ogj-->list
     */
    fun <T> parseObj2List(jsonData: String, claz: Class<T>): List<T>? {
        checkNotNull(jsonData)
        val type = object : TypeToken<ArrayList<T>>() {

        }.type
        return gson.fromJson<List<T>>(jsonData, type)
    }

    /**
     * 将java对象转换成json对象
     *
     * @param obj
     * @return
     */
    fun parseObj2Json(obj: Any?): String? {
        if (null == obj) {
            return null
        }
        return gson.toJson(obj)
    }
}
