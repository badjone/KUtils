package com.wugx.k_utils.utils.camera

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Rect
import android.hardware.Camera
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Chronometer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.ToastUtils
import com.wugx.alarm_pro.utils.CameraUtils
import com.wugx.k_utils.R
import kotlinx.android.synthetic.main.layout_camera_custom.*
import java.io.File
import java.io.IOException
import java.util.*

/**
 *  视频录制
 *
 *@author Wugx
 *@date   2018/12/20
 */
class CustomCameraActivity : AppCompatActivity(), View.OnClickListener {

    //是否录制中...
    private var isRecording: Boolean = false
    private var mMediaRecorder: MediaRecorder? = null
    //camera2需要api>21，为兼容老版本，用hardware.Camera
    private var mCamera: Camera? = null
    private var mCpPreview: CustomCameraPreview? = null
    //对焦点击焦点区域大小
    private val FOCUS_AREA_SIZE = 500
    //摄像头方向
    private var cameraFront = false
    //闪光灯
    private var isFlash = false
    //视频文件输出路径
    private var url_file: String? = null

    //最大录制时间15秒
    private val MAX_RECORD_TIME = 15


//    override fun isRebound(): Boolean = false
//    override fun isShowTitle(): Boolean = false
//    override val layoutId: Int = R.layout.layout_camera_custom
//
//
//    override fun initData(savedInstanceState: Bundle?) {
//        //设置全屏
//        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
//        initInfo()
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_camera_custom)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        initInfo()
        initCreate()
    }

    private fun initCreate() {
        LogUtils.d("初始化相机>>>" + mCamera)
        mCpPreview = CustomCameraPreview(this, mCamera)
        LogUtils.d("录像初始化>>$mCpPreview")
        ll_camera_layout.addView(mCpPreview)
        ll_camera_layout.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                try {
                    focusOnTouch(event)
                } catch (e: Exception) {
                    LogUtils.d(getString(R.string.fail_when_camera_try_autofocus, e.toString()))
                }
            }
            true
        }
    }

    private fun focusOnTouch(event: MotionEvent) {
        mCamera?.let {
            val parameters = it.getParameters()
            if (parameters.maxNumMeteringAreas > 0) {
                val rect = calculateFocusArea(event.x, event.y)
                parameters.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
                val meteringAreas = ArrayList<Camera.Area>()
                meteringAreas.add(Camera.Area(rect, 800))
                parameters.focusAreas = meteringAreas
                it.parameters = parameters
                it.autoFocus(mAutoFocusTakePictureCallback)
            } else {
                it.autoFocus(mAutoFocusTakePictureCallback)
            }
        }
    }

    private val mAutoFocusTakePictureCallback = Camera.AutoFocusCallback { success, camera ->
        if (success) {
            // do something...
            Log.i("对焦", "success!")
        } else {
            // do something...
            Log.i("对焦", "fail!")
        }
    }

    private fun calculateFocusArea(x: Float, y: Float): Rect {
        val left = clamp(java.lang.Float.valueOf(x / mCpPreview!!.width * 2000 - 1000).toInt(), FOCUS_AREA_SIZE)
        val top = clamp(java.lang.Float.valueOf(y / mCpPreview!!.height * 2000 - 1000).toInt(), FOCUS_AREA_SIZE)
        return Rect(left, top, left + FOCUS_AREA_SIZE, top + FOCUS_AREA_SIZE)
    }

    private fun clamp(touchCoordinateInCameraReper: Int, focusAreaSize: Int): Int {
        return if (Math.abs(touchCoordinateInCameraReper) + focusAreaSize / 2 > 1000) {
            if (touchCoordinateInCameraReper > 0) {
                1000 - focusAreaSize / 2
            } else {
                -1000 + focusAreaSize / 2
            }
        } else {
            touchCoordinateInCameraReper - focusAreaSize / 2
        }
    }

    override fun onPause() {
        super.onPause()
        releaseCamera()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (isRecording) {
            mMediaRecorder!!.stop()
            if (c_count != null && c_count.isActivated)
                c_count.stop()
            releaseMediaRecorder()
            isRecording = false
            val mp4 = File(url_file)
            if (mp4.exists() && mp4.isFile) {
                mp4.delete()
            }
        }
        setResult(CameraUtils.RESULT_CODE_FOR_RECORD_VIDEO_CANCEL)
        releaseMediaRecorder()
        releaseCamera()
        finish()
        return super.onKeyDown(keyCode, event)
    }


    private fun initInfo() {
        img_capture.setOnClickListener(this)
        img_camera_switch.setOnClickListener(this)
        img_flash.setOnClickListener(this)


    }

    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.img_capture -> {
                    startRecord()
                }
                R.id.img_camera_switch -> {
                    switchCamera()
                }
                R.id.img_flash -> {
                    openFlash()
                }
            }
        }
    }

    private fun openFlash() {
        if (!isRecording && !cameraFront) {
            if (isFlash) {
                isFlash = false
                img_flash.setImageResource(R.mipmap.ic_flash_off_white)
                setFlashMode(Camera.Parameters.FLASH_MODE_OFF)
            } else {
                isFlash = true
                img_flash.setImageResource(R.mipmap.ic_flash_on_white)
                setFlashMode(Camera.Parameters.FLASH_MODE_TORCH)
            }
        }
    }

    //闪光灯
    fun setFlashMode(mode: String) {
        try {
            if (packageManager.hasSystemFeature(
                            PackageManager.FEATURE_CAMERA_FLASH)
                    && mCamera != null
                    && !cameraFront) {

                mCpPreview!!.setFlashMode(mode)
                mCpPreview!!.refreshCamera(mCamera!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(applicationContext, R.string.changing_flashLight_mode,
                    Toast.LENGTH_SHORT).show()
        }

    }

    private fun switchCamera() {
        if (!isRecording) {
            val camerasNumber = Camera.getNumberOfCameras()
            if (camerasNumber > 1) {
                releaseCamera()
                chooseCamera()
            } else {
                //只有一个摄像头不允许切换
                Toast.makeText(applicationContext, R.string.only_have_one_camera, Toast.LENGTH_SHORT).show()
            }
        }
    }

    //选择摄像头
    fun chooseCamera() {
        if (cameraFront) {
            //当前是前置摄像头
            val cameraId = findBackFacingCamera()
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview
                mCamera = Camera.open(cameraId)
                // mPicture = getPictureCallback();
                mCpPreview!!.refreshCamera(mCamera!!)
            }
        } else {
            //当前为后置摄像头
            val cameraId = findFrontFacingCamera()
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview
                mCamera = Camera.open(cameraId)
                if (isFlash) {
                    isFlash = false
                    img_flash.setImageResource(R.mipmap.ic_flash_off_white)
                    mCpPreview!!.setFlashMode(Camera.Parameters.FLASH_MODE_OFF)
                }
                // mPicture = getPictureCallback();
                mCpPreview!!.refreshCamera(mCamera!!)
            }
        }
    }

    private fun startRecord() {
        if (isRecording) {
            //如果正在录制点击这个按钮表示录制完成
            stopPlay()
        } else {
            startPlay()
        }
    }

    private fun startPlay() {
        //准备开始录制视频
        if (!prepareVideoRecorder()) {
            ToastUtils.showShort(getString(R.string.camera_init_fail))
            setResult(CameraUtils.RESULT_CODE_FOR_RECORD_VIDEO_FAILED)
            releaseMediaRecorder()
            releaseCamera()
            finish()
        }
        //开始录制视频
        runOnUiThread {
            // If there are stories, add them to the table
            try {
                mMediaRecorder!!.start()
                startChronometer()
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    changeRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                } else {
                    changeRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                }
                img_capture.setImageResource(R.mipmap.player_stop)
            } catch (ex: Exception) {
                Log.i("---", "Exception in thread")
                setResult(CameraUtils.RESULT_CODE_FOR_RECORD_VIDEO_FAILED)
                releaseMediaRecorder()
                releaseCamera()
                finish()
            }
        }
        isRecording = true
    }

    private fun stopPlay() {
        mMediaRecorder!!.stop() //停止
        stopChronometer()
        img_capture.setImageResource(R.mipmap.player_record)
        changeRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR)
        ToastUtils.showShort(R.string.video_captured)
        isRecording = false

        releaseMediaRecorder()
        releaseCamera()
        //setResult
        val intent = Intent()
        intent.putExtra(CameraUtils.INTENT_EXTRA_VIDEO_PATH, url_file)
        setResult(CameraUtils.RESULT_CODE_FOR_RECORD_VIDEO_SUCCEED, intent)
        finish()
    }


    /**
     * 改变方向
     *
     * @param orientation
     */
    private fun changeRequestedOrientation(orientation: Int) {
        requestedOrientation = orientation
    }

    //计时器
    private fun startChronometer() {
        c_count.visibility = View.VISIBLE
        val startTime = SystemClock.elapsedRealtime()
        c_count.onChronometerTickListener = Chronometer.OnChronometerTickListener {
            val countUp = (SystemClock.elapsedRealtime() - startTime) / 1000
            if (countUp >= MAX_RECORD_TIME) {
                //拍摄时间超时，退出
                stopPlay()
            }
            val asText = String.format("%02d", countUp / 60) + ":" + String.format("%02d", countUp % 60)
            c_count.text = asText
        }
        c_count.start()
    }

    private fun stopChronometer() {
        c_count.stop()
        c_count.visibility = View.INVISIBLE
    }


    private fun prepareVideoRecorder(): Boolean {
        mMediaRecorder = MediaRecorder()

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera!!.stopPreview()
        mCamera!!.unlock()
        mMediaRecorder!!.setCamera(mCamera)

        // Step 2: Set sources
        mMediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
        mMediaRecorder!!.setVideoSource(MediaRecorder.VideoSource.CAMERA)

        //        mMediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024); //设置编码比特率即可

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (cameraFront) {
                mMediaRecorder!!.setOrientationHint(270)
            } else {
                mMediaRecorder!!.setOrientationHint(90)
            }
        }

        // Step 3: Set output format and encoding (for versions prior to API Level 8)
        val camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH)
        camcorderProfile.videoFrameWidth = 320
        camcorderProfile.videoFrameHeight = 240

        //        camcorderProfile.videoFrameWidth = 640;
        //        camcorderProfile.videoFrameHeight = 480;
        //      camcorderProfile.videoFrameRate = 15;
        camcorderProfile.videoCodec = MediaRecorder.VideoEncoder.H264
        //        camcorderProfile.videoBitRate=5 * 1024 * 1024;
        //        camcorderProfile.audioCodec = MediaRecorder.AudioEncoder.AAC;
        camcorderProfile.fileFormat = MediaRecorder.OutputFormat.MPEG_4

        mMediaRecorder!!.setProfile(camcorderProfile)
        //        mediaRecorder.setVideoSize(640, 480);

        // Step 4: Set output file
        if (getVideoFilePath()) return false
        mMediaRecorder!!.setOutputFile(url_file)

        // Step 5: Set the preview output
        mMediaRecorder!!.setPreviewDisplay(mCpPreview!!.holder.surface)

        // Step 6: Prepare configured MediaRecorder
        mMediaRecorder!!.setOnErrorListener(MediaRecorder.OnErrorListener { mr, what, extra -> LogUtils.d("录像异常>>>", what, extra, mr.toString()) })
        try {
            mMediaRecorder!!.prepare()
        } catch (e: IllegalStateException) {
            LogUtils.d("DEBUG", "IllegalStateException preparing MediaRecorder: " + e.message)
            releaseMediaRecorder()
            return false
        } catch (e: IOException) {
            LogUtils.d("DEBUG", "IOException preparing MediaRecorder: " + e.message)
            releaseMediaRecorder()
            return false
        }

        return true
    }

    private fun getVideoFilePath(): Boolean {
        val phonePath: String
        if (isSDCardEnableByEnvironment()) {
            phonePath = getSDCardPathByEnvironment()
        } else {
            phonePath = PathUtils.getDataPath()
        }
        val dirFile = File(phonePath, "ranch_video")
        if (!FileUtils.createOrExistsDir(dirFile)) {
            ToastUtils.showShort("创建视频路径失败，请检查是否授予文件存储权限")
            LogUtils.d("创建视频路径失败，请检查是否授予文件存储权限")
            return true
        }

        val outVideoFile = File(dirFile, "outVideo.mp4")
        if (!FileUtils.createFileByDeleteOldFile(outVideoFile)) {
            LogUtils.d("创建视频文件失败")
            return true
        }
        url_file = outVideoFile.absolutePath
        return false
    }

    fun isSDCardEnableByEnvironment(): Boolean {
        return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
    }

    /**
     * Return the path of sdcard by environment.
     *
     * @return the path of sdcard by environment
     */
    fun getSDCardPathByEnvironment(): String {
        return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            Environment.getExternalStorageDirectory().absolutePath
        } else ""
    }


    //检查设备是否有摄像头
    private fun hasCamera(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }


    public override fun onResume() {
        super.onResume()
        if (!hasCamera(applicationContext)) {
            //这台设备没有发现摄像头
            Toast.makeText(applicationContext, R.string.dont_have_camera_error, Toast.LENGTH_SHORT).show()
            setResult(CameraUtils.RESULT_CODE_FOR_RECORD_VIDEO_FAILED)
            releaseMediaRecorder()
            releaseCamera()
            finish()
        }
        if (mCamera == null) {
            releaseCamera()
            val frontal = cameraFront
            var cameraId = findFrontFacingCamera()
            if (cameraId < 0) {
                //前置摄像头不存在
                LogUtils.e(R.string.dont_have_front_camera)
                //尝试寻找后置摄像头
                cameraId = findBackFacingCamera()
                if (isFlash) {
                    mCpPreview!!.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH)
                    img_flash.setImageResource(R.mipmap.ic_flash_on_white)
                }
            } else if (!frontal) {
                cameraId = findBackFacingCamera()
                if (isFlash) {
                    mCpPreview!!.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH)
                    img_flash.setImageResource(R.mipmap.ic_flash_on_white)
                }
            }
            mCamera = Camera.open(cameraId)
            mCpPreview!!.refreshCamera(mCamera!!)
        }
    }


    /**
     * 找前置摄像头,没有则返回-1
     *
     * @return cameraId
     */
    private fun findFrontFacingCamera(): Int {
        var cameraId = -1
        //获取摄像头个数
        val numberOfCameras = Camera.getNumberOfCameras()
        for (i in 0 until numberOfCameras) {
            val info = Camera.CameraInfo()
            Camera.getCameraInfo(i, info)
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i
                cameraFront = true
                break
            }
        }
        return cameraId
    }

    /**
     * 找后置摄像头,没有则返回-1
     *
     * @return cameraId
     */
    private fun findBackFacingCamera(): Int {
        var cameraId = -1
        //获取摄像头个数
        val numberOfCameras = Camera.getNumberOfCameras()
        for (i in 0 until numberOfCameras) {
            val info = Camera.CameraInfo()
            Camera.getCameraInfo(i, info)
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i
                cameraFront = false
                break
            }
        }
        return cameraId
    }


    private fun releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder!!.reset()
            mMediaRecorder!!.release()
            mMediaRecorder = null
            mCamera!!.lock()
        }
    }

    private fun releaseCamera() {
        if (mCamera != null) {
            mCamera!!.release()
            mCamera = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMediaRecorder()
        releaseCamera()
    }
}