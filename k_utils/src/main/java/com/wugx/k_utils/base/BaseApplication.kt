//package com.wugx.alarm_pro.app
//
//import android.app.Application
//import android.content.Context
//import com.squareup.leakcanary.LeakCanary
//import com.squareup.leakcanary.RefWatcher
//
///**
// *
// *
// *@author Wugx
// *@date   2018/11/27
// */
//class BaseApplication : Application() {
//
//    private var refWatcher: RefWatcher? = null
//
//    companion object {
//        /**
//         * 全局应用实例
//         */
//        private lateinit var mInstance: BaseApplication
//
//        /**
//         * 获取全局应用实例
//         *
//         * @return
//         */
//        fun getInstance(): BaseApplication {
//            return mInstance
//        }
//
//        fun getRefWatcher(context: Context): RefWatcher? {
//            val myApplication = context.applicationContext as BaseApplication
//            return myApplication.refWatcher
//        }
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        mInstance = this
//        AppConfig.init(this)
//        refWatcher=  setupLeakCanary()
//    }
//
//    private fun setupLeakCanary(): RefWatcher {
//        return if (LeakCanary.isInAnalyzerProcess(this)) {
//            RefWatcher.DISABLED
//        } else LeakCanary.install(this)
//    }
//
//}