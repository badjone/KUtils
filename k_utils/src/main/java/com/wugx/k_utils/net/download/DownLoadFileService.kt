package com.wugx.k_utils.net.download

import android.app.IntentService
import android.content.Intent
import android.os.IBinder

/**
 * 下载文件
 *
 *@author Wugx
 *@date   2018/12/22
 */
class DownLoadFileService : IntentService("kUtils_down_file") {

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            isWorking = intent.getBooleanExtra("isWork", true)
        }
        if (isWorking) {
            //正在下载中...
//                FileDownloadModel().downloadFile()

        } else {
            //取消或暂停

        }
    }

    private var isWorking: Boolean = true

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()


    }

    override fun onDestroy() {


        super.onDestroy()
    }
}