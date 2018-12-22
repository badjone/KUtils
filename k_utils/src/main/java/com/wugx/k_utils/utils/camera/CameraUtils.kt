package com.wugx.alarm_pro.utils

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StrictMode
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import androidx.core.content.FileProvider
import com.blankj.utilcode.util.*
import com.wugx.k_utils.utils.camera.CustomCameraActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * 相机，拍照，摄像,相册
 *
 *
 * 注意：7.0以上权限
 *
 * @author Wugx
 * @date 2018/10/10
 */

object CameraUtils {

    interface VideoDataListener {
        fun success(videoLength: Double, videoSavePath: String)

        fun failure(msg: String)
    }

    private val SAVETAKEPHOTO = "save_take_photo"
    private val SAVECROPPHOTO = "save_crop_photo"

    val MAX_VIDEO_TIME_LENGTH = 15

    /**
     * 拍照
     *
     * @return
     */
    fun takePhoto(listener: PermissionApply.CameraPermissionListener) {
        PermissionApply.request(PermissionApply.CAMERA_PERMISSION, "拍照", object : PermissionApply.PermissionListener {
            override fun success() {
                val file = getOutputMediaFileNew(1)
                SPUtils.getInstance().put(SAVETAKEPHOTO, file!!.absolutePath)

                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, file2Uri(file))
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                listener.success(intent)
            }
        })
    }

    /**
     * onActivityResult中使用
     * 获取拍照完文件路径
     *
     * @return 照片文件路径
     */
    val takePhoto: String
        get() = SPUtils.getInstance().getString(SAVETAKEPHOTO)

    /**
     * 录视频
     */
    fun takeVideo(listener: PermissionApply.CameraPermissionListener) {
        PermissionApply.request(PermissionApply.AUDIO_PERMISSION, "录视频", object : PermissionApply.PermissionListener {
            override fun success() {
                val builder = StrictMode.VmPolicy.Builder()
                StrictMode.setVmPolicy(builder.build())
                val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)//Intent action type for requesting a video from an existing camera application.
                //设置视频录制的最长时间
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, MAX_VIDEO_TIME_LENGTH)//限制录制时间(10秒=10)
                val fileUri = Uri.fromFile(getOutputMediaFileNew(2))
                //        Uri fileUri = getOutputMediaFileUri();  // create a file to save the video

                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)  // set the image file name
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1) // set the video image quality to high
                listener.success(intent)
            }
        })

    }

    /**
     * 使用原生相机录像
     *
     *
     *
     *
     * onActivityResult中使用
     * 获取视频文件路径
     *
     * @param data onActivityResult 的intent
     * @return 视频文件path
     */
    fun getTakeVedio(data: Intent?): String? {
        if (data == null || data.data == null) {
            ToastUtils.showShort("视频文件不存在")
            return null
        }

        val path = getPath(data.data)
        val file = File(path)
        //拍摄后的video文件
        //        File file = UriUtils.uri2File(data.getData(), "file");
        if (!file.exists()) {
            ToastUtils.showShort("视频文件不存在")
            return null
        }
        return file.absolutePath
    }

    /**
     * @param type 1图片 2视频
     * @return
     */
    private fun getOutputMediaFileNew(type: Int): File? {
        var vedioPath: File? = null
        val dirPath: String
        if (StringUtils.isEmpty(PathUtils.getExternalDcimPath())) {
            dirPath = PathUtils.getDataPath()
        } else {
            dirPath = PathUtils.getExternalDcimPath()
        }
        val dirFile = File(dirPath, if (type == 1) "RancherVideo" else "RancherImage")
        if (dirFile.exists()) {
            FileUtils.deleteAllInDir(dirFile)
        }

        val orExistsDir = FileUtils.createOrExistsDir(dirFile)
        if (orExistsDir) {
            @SuppressLint("SimpleDateFormat")
            val dateTimes = TimeUtils.date2String(Date(), SimpleDateFormat("yyyyMMdd_HHmmss"))
            vedioPath = File(dirFile.absolutePath, (if (type == 1) "IMG_" else "VID_") + dateTimes + if (type == 1) ".jpg" else ".mp4")
            //            saveVedioFile = vedioPath;
        }
        return vedioPath
    }

    /**
     * 裁剪图片
     */
    fun crop(file: File): Intent {
        val CropPhoto = File(PathUtils.getExternalDcimPath(), "ranchImgCrop.jpg")//这个是创建一个截取后的图片路径和名称。
        val intent = Intent("com.android.camera.action.CROP")
        val outPutUri = Uri.fromFile(CropPhoto)
        //sdk>=24
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setDataAndType(file2Uri(file), "image/*")
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri)
            intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(FLAG_GRANT_WRITE_URI_PERMISSION)
        } else {
            intent.setDataAndType(file2Uri(file), "image/*")
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri)
        }
        SPUtils.getInstance().put(SAVECROPPHOTO, getPath(outPutUri))

        // 设置裁剪
        intent.putExtra("crop", "true")
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 400)
        intent.putExtra("outputY", 400)
        intent.putExtra("return-data", false)
        intent.putExtra("noFaceDetection", false)//去除默认的人脸识别，否则和剪裁匡重叠
        intent.putExtra("outputFormat", "JPEG")
        //intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());// 图片格式

        return intent
    }

    /**
     * onActivityResult中使用
     * 获取裁剪照片路径
     *
     * @return 裁剪文件路径
     */
    val cropPath: String
        get() = SPUtils.getInstance().getString(SAVECROPPHOTO)


    /**
     * onActivityResult中使用
     * 获取选择照片的 Intent
     *
     * @return
     */
    fun toGallery(listener: PermissionApply.CameraPermissionListener) {
        PermissionApply.request(PermissionApply.FILE_PERMISSION, "文件", object : PermissionApply.PermissionListener {
            override fun success() {
                //默认打开系统目录
                val parentFlie = File(PathUtils.getRootPath())
                val intentNew = Intent(Intent.ACTION_GET_CONTENT)
                intentNew.setDataAndType(UriUtils.file2Uri(parentFlie), "image/*")
                intentNew.addCategory(Intent.CATEGORY_OPENABLE)
                listener.success(intentNew)
            }
        })

        //        return new Intent(
        //                Intent.ACTION_PICK,
        //                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    }

    /**
     * 从相册选择图片的路径
     *
     * @param data
     * @return
     */
    fun getGalleryPath(data: Intent?): String? {
        return if (data == null) null else getPath(data.data)
    }

    /**
     * 获取从文件中选择照片的 Intent
     *
     * @return
     */
    val pickIntentWithDocuments: Intent
        get() {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            return intent.setType("image*//*")
        }


    @SuppressLint("NewApi")
    private fun getPath(uri: Uri?): String? {
        val context = Utils.getApp()
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri!!)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(id))
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection,
                        selectionArgs)
            }// MediaProvider
            // DownloadsProvider
        } else if ("content".equals(uri!!.scheme!!, ignoreCase = true)) {
            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
            return uri.path
        }// File
        // MediaStore (and general)
        return null

    }

    fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(uri!!, projection,
                    selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null

    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    fun file2Uri(file: File): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val authority = AppUtils.getAppPackageName() + ".fileProvider"
            FileProvider.getUriForFile(Utils.getApp(), authority, file)
        } else {
            Uri.fromFile(file)
        }
    }

    /**
     * 根据文件类型打开文件
     *
     * @param file
     * @return
     */
    fun openFile(file: File): Intent? {
        try {
            val intent = Intent()
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            //设置intent的Action属性
            intent.action = Intent.ACTION_VIEW
            //获取文件file的MIME类型
            val type = getMIMEType(file)
            //设置intent的data和Type属性。
            //            intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
            val uriForFile: Uri
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val authority = AppUtils.getAppPackageName() + ".fileProvider"
                uriForFile = FileProvider.getUriForFile(Utils.getApp(), authority, file)
            } else {
                uriForFile = Uri.fromFile(file)
            }
            intent.setDataAndType(uriForFile, type)
            //跳转
            return intent
        } catch (e: Exception) {
            ToastUtils.showShort("不能打开文件")
            return null
        }

    }

    /**
     * 根据文件后缀名获得对应的MIME类型。
     *
     * @param file
     */
    private fun getMIMEType(file: File): String {

        var type = "*/*"
        val fName = file.name
        //获取后缀名前的分隔符"."在fName中的位置。
        val dotIndex = fName.lastIndexOf(".")
        if (dotIndex < 0) {
            return type
        }
        /* 获取文件的后缀名*/
        val end = fName.substring(dotIndex, fName.length).toLowerCase()
        if (end === "") return type
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (i in MIME_MapTable.indices) {
            if (end == MIME_MapTable[i][0])
                type = MIME_MapTable[i][1]
        }
        return type
    }

    private val MIME_MapTable = arrayOf(
            //{后缀名，MIME类型}
            arrayOf(".3gp", "video/3gpp"), arrayOf(".apk", "application/vnd.android.package-archive"), arrayOf(".asf", "video/x-ms-asf"), arrayOf(".avi", "video/x-msvideo"), arrayOf(".bin", "application/octet-stream"), arrayOf(".bmp", "image/bmp"), arrayOf(".c", "text/plain"), arrayOf(".class", "application/octet-stream"), arrayOf(".conf", "text/plain"), arrayOf(".cpp", "text/plain"), arrayOf(".doc", "application/msword"), arrayOf(".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"), arrayOf(".xls", "application/vnd.ms-excel"), arrayOf(".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"), arrayOf(".exe", "application/octet-stream"), arrayOf(".gif", "image/gif"), arrayOf(".gtar", "application/x-gtar"), arrayOf(".gz", "application/x-gzip"), arrayOf(".h", "text/plain"), arrayOf(".htm", "text/html"), arrayOf(".html", "text/html"), arrayOf(".jar", "application/java-archive"), arrayOf(".java", "text/plain"), arrayOf(".jpeg", "image/jpeg"), arrayOf(".jpg", "image/jpeg"), arrayOf(".js", "application/x-javascript"), arrayOf(".log", "text/plain"), arrayOf(".m3u", "audio/x-mpegurl"), arrayOf(".m4a", "audio/mp4a-latm"), arrayOf(".m4b", "audio/mp4a-latm"), arrayOf(".m4p", "audio/mp4a-latm"), arrayOf(".m4u", "video/vnd.mpegurl"), arrayOf(".m4v", "video/x-m4v"), arrayOf(".mov", "video/quicktime"), arrayOf(".mp2", "audio/x-mpeg"), arrayOf(".mp3", "audio/x-mpeg"), arrayOf(".mp4", "video/mp4"), arrayOf(".mpc", "application/vnd.mpohun.certificate"), arrayOf(".mpe", "video/mpeg"), arrayOf(".mpeg", "video/mpeg"), arrayOf(".mpg", "video/mpeg"), arrayOf(".mpg4", "video/mp4"), arrayOf(".mpga", "audio/mpeg"), arrayOf(".msg", "application/vnd.ms-outlook"), arrayOf(".ogg", "audio/ogg"), arrayOf(".pdf", "application/pdf"), arrayOf(".png", "image/png"), arrayOf(".pps", "application/vnd.ms-powerpoint"), arrayOf(".ppt", "application/vnd.ms-powerpoint"), arrayOf(".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"), arrayOf(".prop", "text/plain"), arrayOf(".rc", "text/plain"), arrayOf(".rmvb", "audio/x-pn-realaudio"), arrayOf(".rtf", "application/rtf"), arrayOf(".sh", "text/plain"), arrayOf(".tar", "application/x-tar"), arrayOf(".tgz", "application/x-compressed"), arrayOf(".txt", "text/plain"), arrayOf(".wav", "audio/x-wav"), arrayOf(".wma", "audio/x-ms-wma"), arrayOf(".wmv", "audio/x-ms-wmv"), arrayOf(".wps", "application/vnd.ms-works"), arrayOf(".xml", "text/plain"), arrayOf(".z", "application/x-compress"), arrayOf(".zip", "application/x-zip-compressed"), arrayOf("", "*/*"))


    private val REQUEST_CODE_FOR_PERMISSIONS = 0//

    const val REQUEST_CODE_FOR_RECORD_VIDEO = 1//录制视频请求码
    const val RESULT_CODE_FOR_RECORD_VIDEO_SUCCEED = 2//视频录制成功
    const val RESULT_CODE_FOR_RECORD_VIDEO_FAILED = 3//视频录制出错
    const val RESULT_CODE_FOR_RECORD_VIDEO_CANCEL = 4//取消录制
    const val INTENT_EXTRA_VIDEO_PATH = "intent_extra_video_path"//录制的视频路径

    /**
     * 自定义视频录制
     *
     * @return
     */
    fun takeVideo2(listener: PermissionApply.CameraPermissionListener?) {
        PermissionApply.request(PermissionApply.AUDIO_PERMISSION, "录像", object : PermissionApply.PermissionListener {
            override fun success() {
                listener?.success(Intent(Utils.getApp(), CustomCameraActivity::class.java))
            }
        })
    }

    /**
     * 对应 takeVideo2
     *
     * @param resultCode
     * @param data
     * @param listener
     */
    fun getTakeVideo2(resultCode: Int, data: Intent?, listener: VideoDataListener?) {
        if (listener == null) return
        val currentInputVideoPath: String

        //录制视频
        if (resultCode == RESULT_CODE_FOR_RECORD_VIDEO_SUCCEED) {
            //为空返回
            if (data == null) return
            //录制成功
            val videoPath = data.getStringExtra(INTENT_EXTRA_VIDEO_PATH)
            if (!TextUtils.isEmpty(videoPath)) {
                currentInputVideoPath = videoPath

                val retr = MediaMetadataRetriever()
                retr.setDataSource(currentInputVideoPath)
                val time = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)//获取视频时长
                //7680
                var videoLength: Double
                try {
                    videoLength = java.lang.Double.parseDouble(time) / 1000.00
                } catch (e: Exception) {
                    e.printStackTrace()
                    videoLength = 0.00
                }

                listener.success(videoLength, currentInputVideoPath)
            }
        } else if (resultCode == RESULT_CODE_FOR_RECORD_VIDEO_FAILED) {
            //录制失败
            currentInputVideoPath = ""
            listener.failure("录制失败")
        }
    }

}
