//package com.wugx.alarm_pro.app
//
//import android.graphics.Typeface
//import com.blankj.utilcode.util.Utils
//import com.scwang.smartrefresh.layout.SmartRefreshLayout
//import com.scwang.smartrefresh.layout.footer.FalsifyFooter
//import com.scwang.smartrefresh.layout.header.FalsifyHeader
//
//
///**
// *一些公共配置
// *
// *@author Wugx
// *@date   2018/11/27
// */
//object AppConfig {
//
//    fun init(ba: BaseApplication) {
//        //utilCode
//        Utils.init(ba)
////        initFont()
//        //百度地图
//        val baiduAppKey = "PP1i9BPcspPzzbIApfNaNrpnrYHpTScw"
//
////        AnrDoSomeThings()
//    }
//
//    /**
//     * ANR 异常拦截处理
//     */
//    private fun AnrDoSomeThings() {
//        Thread.setDefaultUncaughtExceptionHandler { t, e ->
//            //                e.printStackTrace();
//            //                ActivityUtils.finishAllActivities();
//            //                ActivityUtils.startActivity(SplashActivity.class);
//            //将异常信息写入文件
////            PathUtils.getDataPath()
////            FileIOUtils.
//        }
//    }
//
//    /**
//     * 加载全局字体样式
//     */
//    private fun initFont() {
//        val createFromAsset = Typeface.createFromAsset(Utils.getApp().assets, "fzlbt.ttf")
//        try {
//            val defaultField = Typeface::class.java.getDeclaredField("SERIF")
//            defaultField.isAccessible = true
//            defaultField.set(null, createFromAsset)
//        } catch (e: NoSuchFieldException) {
//            e.printStackTrace()
//        } catch (e: IllegalAccessException) {
//            e.printStackTrace()
//        }
//
//    }
//
//    init {
//        //设置全局的Header构建器
//        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ ->
//            //                layout.setPrimaryColorsId(R.color.grey_f2f2f2, android.R.color.darker_gray);//全局设置主题颜色
//            //                return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
//
//            FalsifyHeader(context)
//        }
//        //设置全局的Footer构建器
//        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ ->
//            //指定为经典Footer，默认是 BallPulseFooter
//            //                return new ClassicsFooter(context).setDrawableSize(20);
//            FalsifyFooter(context)
//        }
//    }
//
//}