package com.wugx.alarm_pro.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import com.afollestad.materialdialogs.MaterialDialog
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.wugx.k_utils.R

/**
 * 权限申请 在此申请一些公共权限
 *
 * @author Wugx
 * @date 2018/11/8
 */
object PermissionApply {
    /**
     * 相机拍照权限
     */
    @JvmField
    val CAMERA_PERMISSION = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    /**
     * 录像权限
     */
    @JvmField
    val AUDIO_PERMISSION = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
    /**
     * 读写文件
     */
    @JvmField
    val FILE_PERMISSION = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

    /**
     * 手机状态
     */
    val PHONE_STATE = arrayOf(Manifest.permission.READ_PHONE_STATE)

    /**
     * 定位相关权限
     */
    val LOCATION_PERMISSION = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    )

    @SuppressLint("WrongConstant")
    @JvmStatic
    fun request(perms: Array<String>, permsDesc: String, listener: PermissionListener) {
        PermissionUtils.permission(*perms)
                .rationale { shouldRequest -> shouldRequest.again(true) }
                .callback(object : PermissionUtils.FullCallback {
                    override fun onGranted(permissionsGranted: List<String>) {
                        listener.success()
                    }

                    override fun onDenied(permissionsDeniedForever: List<String>?, permissionsDenied: List<String>?) {
                        if (permissionsDenied != null && permissionsDenied.isNotEmpty()) {
                            ToastUtils.showShort(String.format(Utils.getApp().getString(R.string.permission_denied_tips), permsDesc))
                        }
                        if (permissionsDeniedForever != null && permissionsDeniedForever.isNotEmpty()) {
                            val topActivity = ActivityUtils.getTopActivity()
                            if (topActivity != null) {
                                MaterialDialog(topActivity)
                                        .message(text = String.format(Utils.getApp().getString(R.string.permission_refuse_tips), permsDesc))
                                        .cancelOnTouchOutside(false)
                                        .negativeButton(R.string.cancel)
                                        .positiveButton(R.string.txt_set) {
                                            PermissionUtils.launchAppDetailsSettings()
                                        }.show()
                            }
                        }
                    }
                })
                .request()
    }

    open interface PermissionListener {
        fun success()

    }

    /**
     * back intent
     */
    interface CameraPermissionListener {
        fun success(intent: Intent)
    }

}
