package com.wugx.k_utils

import android.app.Application
import com.blankj.utilcode.util.Utils
import com.kingja.loadsir.core.LoadSir
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.FalsifyFooter
import com.scwang.smartrefresh.layout.header.FalsifyHeader
import com.wugx.k_utils.widget.callback.*


/**
 *
 *
 *@author Wugx
 *@date   2018/12/19
 */
object KUtils {
    lateinit var base_url: String
    lateinit var map: Map<String, String>

    fun init(app: Application):KUtils {
        Utils.init(app)
        initSmartLayout()
        initLoadSir()
        return this
    }

    /**
     * 设置网络请求参数
     */
    fun initNetParams(baseUrl: String, paramsMap: Map<String, String>) :KUtils{
        base_url = baseUrl
        map = paramsMap
        return this
    }

    private fun initSmartLayout() {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ ->
            FalsifyHeader(context)
        }
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ ->
            FalsifyFooter(context)
        }
    }

    private fun initLoadSir() {
        LoadSir.beginBuilder()
                .addCallback(ErrorCallback())//添加各种状态页
                .addCallback(EmptyCallback())
                .addCallback(LoadingCallback())
                .addCallback(TimeoutCallback())
                .addCallback(CustomCallback())
                .setDefaultCallback(LoadingCallback::class.java!!)//设置默认状态页
                .commit()
    }
}