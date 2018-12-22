package com.wugx.k_utils.utils.camera

import android.content.Context
import android.content.res.Configuration
import android.hardware.Camera
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ScreenUtils
import java.io.IOException

/**
 *
 *
 *@author Wugx
 *@date   2018/12/20
 */
class CustomCameraPreview(context: Context, val camera: Camera?) : SurfaceView(context), SurfaceHolder.Callback {
    private var flashMode = Camera.Parameters.FLASH_MODE_OFF
    private var mCamera: Camera? = null
    private lateinit var mHolder: SurfaceHolder

    init {
        mCamera = camera
        mHolder = holder
        mHolder.addCallback(this)
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        refreshCamera(mCamera!!)
    }

    fun refreshCamera(camera: Camera) {
        if (holder.surface == null) {
            // preview surface does not exist
            return
        }
        // stop preview before making changes
        try {
            mCamera!!.stopPreview()
        } catch (e: Exception) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            camera?.setDisplayOrientation(90)
        }

        // start preview with new settings
        setCamera(camera)

        try {
            camera.setPreviewDisplay(holder)
            optimizeCameraDimens(camera)
            camera.startPreview()
        } catch (e: Exception) {
            LogUtils.d("Error starting camera preview: ${e.message}")
        }
    }

    fun setCamera(camera: Camera?) {
        //method to set a camera instance
        mCamera = camera

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
//        camera.release()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        try {
            mCamera?.let {
                // create the surface and start camera preview
                it.setPreviewDisplay(holder)
                optimizeCameraDimens(it)
                it.startPreview()
            }
        } catch (e: IOException) {
            LogUtils.d("Error setting camera preview: ${e.message}")
        }
    }


    private fun optimizeCameraDimens(mCamera: Camera) {
        val mSupportedPreviewSizes = mCamera.parameters.supportedPreviewSizes

        val mPreviewSize: Camera.Size?
        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes,
                    ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight())

            val ratio = if (mPreviewSize!!.height >= mPreviewSize.width)
                mPreviewSize.height.toFloat() / mPreviewSize.width.toFloat()
            else
                mPreviewSize.width.toFloat() / mPreviewSize.height.toFloat()

            // One of these methods should be used, second method squishes preview slightly
            setMeasuredDimension(ScreenUtils.getScreenWidth(), (ScreenUtils.getScreenWidth() * ratio).toInt())

            val parameters = mCamera.parameters
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height)
            parameters.flashMode = flashMode
            mCamera.parameters = parameters
        }
    }

    private fun getOptimalPreviewSize(sizes: List<Camera.Size>?, w: Int, h: Int): Camera.Size? {
        val aspect_tolerance = 0.1

        var targetRatio = h.toDouble() / w
        if (w > h) targetRatio = w.toDouble() / h
        if (sizes == null) return null

        var optimalSize: Camera.Size? = null
        var minDiff = java.lang.Double.MAX_VALUE

        for (size in sizes) {

            var ratio = size.width.toDouble() / size.height
            if (size.height >= size.width)
                ratio = (size.height.toFloat() / size.width).toDouble()

            if (Math.abs(ratio - targetRatio) > aspect_tolerance)
                continue

            if (Math.abs(size.height - h) < minDiff) {
                optimalSize = size
                minDiff = Math.abs(size.height - h).toDouble()
            }
        }

        if (optimalSize == null) {
            minDiff = java.lang.Double.MAX_VALUE
            for (size in sizes) {
                if (Math.abs(size.height - h) < minDiff) {
                    optimalSize = size
                    minDiff = Math.abs(size.height - h).toDouble()
                }
            }
        }
        return optimalSize
    }

    fun setFlashMode(mode: String) {
        flashMode = mode
    }


}