package com.wugx.k_utils.utils.zxing

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.uuzuche.lib_zxing.DisplayUtil
import com.uuzuche.lib_zxing.activity.CaptureActivity
import com.uuzuche.lib_zxing.activity.CodeUtils
import com.wugx.alarm_pro.utils.PermissionApply
import com.wugx.alarm_pro.utils.zxing.ImagePathUtil
import com.wugx.k_utils.R


/**
 * 二维码扫描工具类
 *
 *
 *
 *
 * (1.注意添加相机权限 2.在onActivityResult中调用onResult)
 *
 * @author wugx
 * @data 2018/1/5.
 */

object ZXUtils {
    /**
     * 扫描跳转Activity RequestCode
     */
    val REQUEST_CODE = 111
    /**
     * 选择系统图片Request Code
     */
    val REQUEST_IMAGE = 112

    @SuppressLint("WrongConstant")
    fun scanCode(context: Activity, vararg args: Int) {
        PermissionApply.request(PermissionApply.CAMERA_PERMISSION, "扫描二维码", object : PermissionApply.PermissionListener {
            override fun success() {
                if (args.isEmpty()) {
                    initDisplayOpinion()
                    //默认扫描界面
                    val intent = Intent(Utils.getApp(), CaptureActivity::class.java)
                    context.startActivityForResult(intent, REQUEST_CODE)
                } else {
                    //定制扫描界面
                    val intent = Intent(Utils.getApp(), CustomScanActivity::class.java)
                    context.startActivityForResult(intent, REQUEST_CODE)
                }
            }
        })
    }

    /**
     * 默认二维码扫描 必要的初始化
     */
    private fun initDisplayOpinion() {
        val dm = Utils.getApp().resources.displayMetrics
        DisplayUtil.density = dm.density
        DisplayUtil.densityDPI = dm.densityDpi
        DisplayUtil.screenWidthPx = dm.widthPixels
        DisplayUtil.screenhightPx = dm.heightPixels
        DisplayUtil.screenWidthDip = DisplayUtil.px2dip(Utils.getApp(), dm.widthPixels.toFloat()).toFloat()
        DisplayUtil.screenHightDip = DisplayUtil.px2dip(Utils.getApp(), dm.heightPixels.toFloat()).toFloat()
    }

    /**
     * 从图库选择二维码图片进行扫描
     *
     * @param context
     */
    fun SelectImgCode(context: Activity) {
        val intent = Intent()
        intent.action = Intent.ACTION_PICK
        intent.type = "image/*"
        context.startActivityForResult(intent, REQUEST_IMAGE)
    }

    /**
     * * 生成二维码
     *
     * @param content 要生成的内容
     * @param isLogo  中间是否带logo
     * @return
     */
    fun createCode(content: String, isLogo: Boolean): Bitmap {
        //        Bitmap bitmap = CodeUtils.createImage(content, 400, 400, isLogo ? BitmapFactory.decodeResource(Utils.getApp().getResources(), R.mipmap.ic_launcher) : null);

        return CodeUtils.createImage(content, 400, 400, null)
    }


    fun onResult(requestCode: Int, data: Intent?, zxListener: ZXListener?) {
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                val bundle = data.extras ?: return
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    val result = bundle.getString(CodeUtils.RESULT_STRING)
                    zxListener?.scanResult(result)
                    LogUtils.d("解析结果:" + result!!)
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    ToastUtils.showShort(R.string.txt_resolve_failure)
                }
            }
        } else if (requestCode == REQUEST_IMAGE) {
            if (data != null) {
                val uri = data.data
                try {
                    CodeUtils.analyzeBitmap(ImagePathUtil.getImageAbsolutePath(Utils.getApp(), uri),
                            object : CodeUtils.AnalyzeCallback {
                                override fun onAnalyzeSuccess(mBitmap: Bitmap, result: String) {
                                    LogUtils.d("解析结果:$result")
                                    zxListener?.selectResult(mBitmap, result)
                                }

                                override fun onAnalyzeFailed() {
                                    LogUtils.d("解析二维码失败")
                                    ToastUtils.showShort(R.string.txt_resolve_failure)
                                }
                            })
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
        /**
         * 选择系统图片并解析
         */
        /* else if (requestCode == REQUEST_CAMERA_PERM) {
            Toast.makeText(this, "从设置页面返回...", Toast.LENGTH_SHORT)
                    .show();
        }*/
    }

    interface ZXListener {
        /**
         * 扫描二维码结果
         *
         * @param result
         */
        fun scanResult(result: String?)

        /**
         * 选择图片二维码结果
         *
         * @param mBitmap
         * @param result
         */
        fun selectResult(mBitmap: Bitmap, result: String)
    }
}
