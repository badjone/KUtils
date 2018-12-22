package com.wugx.k_utils.utils.zxing;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.blankj.utilcode.util.BarUtils;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.wugx.k_utils.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;


/**
 * 二维码扫描
 * <p>
 * Created by Wugx on 2018/8/27.
 */

public class CustomScanActivity extends AppCompatActivity implements View.OnClickListener {
    private CaptureFragment captureFragment;

    public static final int OPEN_ISBN_ACTIVITY = 0xFF16;
    private InputMethodManager imm;
    private FrameLayout mFlScanContent;
    private ConstraintLayout mLayoutTitle;
    private ImageView mImgScanBack;
    private CheckBox mCbScanLight;
    private Guideline mLine;
    private CheckBox mCbScanInput;

    public static boolean isOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_scan_qode);
        initView();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        loadData();
    }


    protected void loadData() {
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.transparent), 0);
        captureFragment = new CaptureFragment();
        // 为二维码扫描界面设置定制化界面
        CodeUtils.setFragmentArgs(captureFragment, R.layout.layout_camera_scan);
        captureFragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_scan_content, captureFragment).commit();
    }

    /**
     * 二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
            bundle.putString(CodeUtils.RESULT_STRING, result);
            resultIntent.putExtras(bundle);
            setResult(RESULT_OK, resultIntent);
            finish();
        }

        @Override
        public void onAnalyzeFailed() {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
            bundle.putString(CodeUtils.RESULT_STRING, "");
            resultIntent.putExtras(bundle);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OPEN_ISBN_ACTIVITY) {
            if (data == null) return;
            String isbn = data.getStringExtra("isbn");
            if (!TextUtils.isEmpty(isbn)) {
                isbnSearch(isbn);
            }
        }
    }

    private void isbnSearch(String isbn) {
        Intent intent = new Intent();
        intent.putExtra(CodeUtils.RESULT_STRING, isbn);
        intent.putExtra(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void initView() {
        mFlScanContent = findViewById(R.id.fl_scan_content);
        mLayoutTitle = findViewById(R.id.layout_title);
        mImgScanBack = findViewById(R.id.img_scan_back);
        mCbScanLight = findViewById(R.id.cb_scan_light);
        mLine = findViewById(R.id.line);
        mCbScanInput = findViewById(R.id.cb_scan_input);

        mCbScanLight.setOnClickListener(this);
        mCbScanInput.setOnClickListener(this);
        mImgScanBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.cb_scan_light) {
            if (!isOpen) {
                CodeUtils.isLightEnable(true);
                isOpen = true;
            } else {
                CodeUtils.isLightEnable(false);
                isOpen = false;
            }

        } else if (i == R.id.cb_scan_input) {
            startActivityForResult(new Intent(this, ISBNActivity.class), OPEN_ISBN_ACTIVITY);

        } else if (i == R.id.img_scan_back) {
            finish();

        }
    }
}
