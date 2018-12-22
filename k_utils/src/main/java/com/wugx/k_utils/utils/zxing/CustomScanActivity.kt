//package com.wugx.k_utils.utils.zxing
//
//import android.app.Activity
//import android.content.Context
//import android.content.Intent
//import android.graphics.Bitmap
//import android.os.Bundle
//import android.text.TextUtils
//import android.view.View
//import android.view.inputmethod.InputMethodManager
//import androidx.appcompat.app.AppCompatActivity
//import androidx.fragment.app.Fragment
//import com.blankj.utilcode.util.BarUtils
//import com.uuzuche.lib_zxing.activity.CaptureFragment
//import com.uuzuche.lib_zxing.activity.CodeUtils
//import com.wugx.k_utils.R
//import kotlinx.android.synthetic.main.layout_scan_qode.*
//
///**
// * 二维码扫描
// *
// *
// * Created by Wugx on 2018/8/27.
// */
//class CustomScanActivity : AppCompatActivity(), View.OnClickListener {
//    private var captureFragment: CaptureFragment? = null
//    private var imm: InputMethodManager? = null
//
//    /**
//     * 二维码解析回调函数
//     */
//    private var analyzeCallback: CodeUtils.AnalyzeCallback = object : CodeUtils.AnalyzeCallback {
//        override fun onAnalyzeSuccess(mBitmap: Bitmap, result: String) {
//            val resultIntent = Intent()
//            val bundle = Bundle()
//            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS)
//            bundle.putString(CodeUtils.RESULT_STRING, result)
//            resultIntent.putExtras(bundle)
//            setResult(Activity.RESULT_OK, resultIntent)
//            finish()
//        }
//
//        override fun onAnalyzeFailed() {
//            val resultIntent = Intent()
//            val bundle = Bundle()
//            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED)
//            bundle.putString(CodeUtils.RESULT_STRING, "")
//            resultIntent.putExtras(bundle)
//            setResult(Activity.RESULT_OK, resultIntent)
//            finish()
//        }
//    }
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.layout_scan_qode)
//        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        loadData()
//        initListener()
//    }
//
//    private fun loadData() {
//        //设置bar透明
//        BarUtils.setStatusBarColor(this, resources.getColor(R.color.transparent), 0)
//        val transaction = supportFragmentManager.beginTransaction()
//        captureFragment = CaptureFragment()
//        captureFragment!!.analyzeCallback = analyzeCallback
//
//        transaction.replace(R.id.fl_scan_content, captureFragment).commit()
//
//    }
//
//    private fun initListener() {
//        cb_scan_light.setOnClickListener(this)
//        cb_scan_input.setOnClickListener(this)
//        img_scan_back.setOnClickListener(this)
//    }
//
//    override fun onClick(v: View?) {
//        if (v != null) {
//            when (v.id) {
//                R.id.cb_scan_light -> {
//                    isOpen = if (!isOpen) {
//                        CodeUtils.isLightEnable(true)
//                        true
//                    } else {
//                        CodeUtils.isLightEnable(false)
//                        false
//                    }
//                }
//                R.id.cb_scan_input -> startActivityForResult(Intent(this, ISBNActivity::class.java), OPEN_ISBN_ACTIVITY)
//                R.id.img_scan_back -> finish()
//            }
//        }
//    }
//
//    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (requestCode == OPEN_ISBN_ACTIVITY) {
//            if (data == null) return
//            val isbn = data.getStringExtra("isbn")
//            if (!TextUtils.isEmpty(isbn)) {
//                isbnSearch(isbn)
//            }
//        }
//    }
//
//    private fun isbnSearch(isbn: String) {
//        val intent = Intent()
//        intent.putExtra(CodeUtils.RESULT_STRING, isbn)
//        intent.putExtra(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS)
//        setResult(Activity.RESULT_OK, intent)
//        finish()
//    }
//
//    companion object {
//        const val OPEN_ISBN_ACTIVITY = 0xFF16
//        var isOpen = false
//    }
//
//}
