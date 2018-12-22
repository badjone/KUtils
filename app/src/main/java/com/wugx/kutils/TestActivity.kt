package com.wugx.kutils

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.RemoteViews
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.trello.rxlifecycle3.LifecycleTransformer
import com.trello.rxlifecycle3.android.ActivityEvent
import com.wugx.k_utils.base.BaseActivity
import com.wugx.k_utils.mvp.model.FileDownloadModel
import com.wugx.k_utils.net.download.DownloadProgressListener
import com.wugx.k_utils.utils.zxing.ZXUtils
import kotlinx.android.synthetic.main.activity_test.*
import java.io.File

/**
 *
 *
 *@author Wugx
 *@date   2018/12/20
 */
class TestActivity : BaseActivity() {
    override fun isUseLoadService(): Boolean = true
    override val layoutId: Int = R.layout.activity_test
    override fun initData(savedInstanceState: Bundle?) {
        showEmpty("没有数据拉....")
//        ActivityUtils.startActivity(MainActivity::class.java)
        btn_go.setOnClickListener {

            //            CameraUtils.takeVideo2(object : PermissionApply.CameraPermissionListener {
//                override fun success(intent: Intent) {
//                    LogUtils.d("click。。。。")
//                    startActivityForResult(intent, 1)
//
//                }
//            })

//            ZXUtils.scanCode(this, 1)

//            UiUtils.showBaseDialog(object :BaseDialog.DialogBaseListener{
//                override fun next() {
//
//                }
//
//                override fun cancel() {
//                }
//            },"重大爆料","深圳金融。。。。")

            download()

        }
    }

    private fun download() {
        val downUrl = "http://192.144.137.174:8081/uploadFile/soft/version/1543800668298687447.apk"
        FileDownloadModel().downloadFile(this, downUrl, object : DownloadProgressListener {
            override fun onProgress(progress: Long, total: Long, done: Boolean) {
                val result = "下载 ${(100 * progress / total).toInt()} %"
                LogUtils.d("下载 ${(100 * progress / total).toInt()} %  >>${Thread.currentThread()}")
                tv_test_data.text = result
            }

            override fun downComplete(file: File) {
                val result = "下载完成 ${FileUtils.isFileExists(file)} >>>${FileUtils.getFileSize(file)}"
                LogUtils.d("下载完成 ${FileUtils.isFileExists(file)} >>>${FileUtils.getFileSize(file)}>>${Thread.currentThread()}")
                tv_test_data.text = result
            }

            override fun downError(reason: String) {
                LogUtils.d("下载异常 $reason  >>${Thread.currentThread()}")
//                tv_test_data.text=reason
            }
        })
    }

    override fun onReload() {
        super.onReload()
        showLoading()
        dismissLoading()
    }

    override fun <T> bindToLife(): LifecycleTransformer<T> {
        return bindUntilEvent(ActivityEvent.DESTROY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        LogUtils.d("onActivityResult。。。。$data")
//        if (requestCode == 1) {
//            CameraUtils.getTakeVideo2(resultCode, data, object : CameraUtils.VideoDataListener {
//                override fun success(videoLength: Double, videoSavePath: String) {
//                    LogUtils.i("success: 视频录制>>$videoSavePath")
//                }
//
//                override fun failure(msg: String) {
//                    LogUtils.i("success: 视频录制失败>>")
//                }
//            })
//        }

        ZXUtils.onResult(requestCode, data, object : ZXUtils.ZXListener {
            override fun scanResult(result: String?) {
                LogUtils.d("扫码>>>$result")

            }

            override fun selectResult(mBitmap: Bitmap, result: String) {
            }
        })
    }
}