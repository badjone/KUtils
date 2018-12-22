package com.wugx.k_utils.mvp.model

import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.SDCardUtils
import com.hazz.kotlinmvp.rx.scheduler.SchedulerUtils
import com.wugx.alarm_pro.net.HttpHelper
import com.wugx.alarm_pro.utils.PermissionApply
import com.wugx.k_utils.KUtils
import com.wugx.k_utils.base.IBaseView
import com.wugx.k_utils.net.Api
import com.wugx.k_utils.net.RetrofitManager
import com.wugx.k_utils.net.download.DownloadProgressListener
import com.wugx.k_utils.net.download.DownloadProgressResponseBody
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import okhttp3.Interceptor
import okhttp3.ResponseBody
import java.io.File

/**
 *文件下载
 *
 *@author Wugx
 *@date   2018/12/22
 */
class FileDownloadModel {

    companion object {
        /**
         * 下载文件名
         */
        const val down_file_name = "zzz.apk"
    }

    fun downloadFile(p: IBaseView, url: String, listener: DownloadProgressListener) {
        PermissionApply.request(PermissionApply.FILE_PERMISSION, "文件读写", object : PermissionApply.PermissionListener {
            override fun success() {
                val builder = RetrofitManager.getOkHttpClient(RetrofitManager.addInterceptorParams(KUtils.map))
                //添加拦截器，自定义ResponseBody，添加下载进度
                builder.networkInterceptors().add(Interceptor { chain ->
                    val originalResponse = chain.proceed(chain.request())
                    originalResponse.newBuilder().body(
                            DownloadProgressResponseBody(originalResponse.body()!!, listener))
                            .build()
                })
                HttpHelper.createRetrofit(builder.build(), Api::class.java)
                        .downloadFile(url)
                        .doOnNext(getConsumer(listener))
                        .compose(SchedulerUtils.ioToMain())
                        .compose(p.bindToLife())
                        .subscribe(object : Observer<ResponseBody> {
                            override fun onComplete() {

                            }

                            override fun onSubscribe(d: Disposable) {

                            }

                            override fun onNext(t: ResponseBody) {

                            }

                            override fun onError(e: Throwable) {
                                listener.downError(e.localizedMessage)
                            }
                        })
            }
        })
    }

    /**
     * 存储文件到 sdCard
     */
    private fun getConsumer(listener: DownloadProgressListener): Consumer<ResponseBody> {
        return Consumer { responseBody ->
            //sdCard路径
            val sdPath = SDCardUtils.getSDCardPathByEnvironment()
            val file = File(sdPath, down_file_name)
            FileUtils.createFileByDeleteOldFile(file)
            //写入文件
            val isComplete = FileIOUtils.writeFileFromIS(file, responseBody.byteStream())
            //切换到主线程
            Observable.just(file)
                    .compose(SchedulerUtils.ioToMain())
                    .subscribe {
                        if (isComplete) listener.downComplete(file)
                    }
        }
    }
}