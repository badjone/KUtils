package com.wugx.k_utils.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.appcompat.widget.Toolbar
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SnackbarUtils
import com.blankj.utilcode.util.ToastUtils
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.trello.rxlifecycle3.LifecycleTransformer
import com.trello.rxlifecycle3.android.ActivityEvent
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity
import com.wugx.k_utils.R
import com.wugx.k_utils.utils.UiUtils
import com.wugx.k_utils.widget.callback.EmptyCallback
import com.wugx.k_utils.widget.callback.ErrorCallback
import com.wugx.k_utils.widget.callback.LoadingCallback

/**
 *
 *
 *@author Wugx
 *@date   2018/12/19
 */
abstract class BaseActivity : RxAppCompatActivity(), IBaseView {

    protected abstract val layoutId: Int
    protected abstract fun initData(@Nullable savedInstanceState: Bundle?)

    open fun isRebound(): Boolean = true
    open fun isShowTitle(): Boolean = true
    open fun isUseLoadService(): Boolean = false
    override fun showError(msg: String) {
        showException(msg)
    }

    override fun showLoading() {
        if (isUseLoadService()) {
            mLoadService.showCallback(LoadingCallback::class.java)
        } else {
            //custom view load...
            UiUtils.showLoadDialog("loading...")
        }
    }

    override fun dismissLoading() {
        if (isUseLoadService()) {
            mLoadService.showSuccess()
        } else {
            //custom view load...
            UiUtils.dismissLoadDialog()
        }
    }

    override fun <T> bindToLife(): LifecycleTransformer<T> {
        return bindUntilEvent<T>(ActivityEvent.DESTROY)
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        //init layout
        val contentLayout = setBaseLayout()
        if (isUseLoadService()) {
            initLoadView(contentLayout)
        }
        initData(savedInstanceState)
        initListener()
    }

    private lateinit var smartRefreshLayout: SmartRefreshLayout
    @Nullable
    private fun setBaseLayout(): View? {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        val parms = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 1f)


        val contentLayout = LayoutInflater.from(this).inflate(layoutId, layout,false)
        if (isShowTitle()) {
            val layoutTitle = View.inflate(this, R.layout.layout_title, null)
            val p = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            initActionBar(layoutTitle)
            layout.addView(layoutTitle, p)
        }

        if (isRebound()) {
            smartRefreshLayout = SmartRefreshLayout(this)
            smartRefreshLayout.setBackgroundResource(R.color.gray_f2f2f2)
            smartRefreshLayout.layoutParams = parms
            //            setSmartRefreshLayoutCommon(smartRefreshLayout);
            if (contentLayout != null) smartRefreshLayout.addView(contentLayout, parms)
            layout.addView(smartRefreshLayout, parms)
        } else {
            layout.addView(contentLayout, parms)
        }
        setContentView(layout, parms)
        return contentLayout
    }

    fun getSmartRefreshLayout(): SmartRefreshLayout? {
        return if (isRebound()) smartRefreshLayout else null
    }

    open fun initListener() {

    }

    private lateinit var tvTitle: TextView
    private lateinit var mToolbar: Toolbar

    private fun initActionBar(v: View) {
        tvTitle = v.findViewById(R.id.tv_title)
        mToolbar = v.findViewById(R.id.toolbar_layout)

        mToolbar.title = ""
        setSupportActionBar(mToolbar)

        supportActionBar!!.setDisplayShowHomeEnabled(false)
        supportActionBar!!.setHomeAsUpIndicator(R.mipmap.ic_arrow_back)

        val topActivity = ActivityUtils.getTopActivity()
        //设置返回按钮
//        supportActionBar!!.setDisplayHomeAsUpEnabled(topActivity::class.java.name != MainActivity::class.java.name)

        val defaultTitle = title.toString()
        if (TextUtils.isEmpty(defaultTitle)) {
            tvTitle.setText(R.string.app_name)
        } else {
            tvTitle.text = defaultTitle
        }
    }

    fun setTitleTxt(title: String) {
        if (!isShowTitle()) return
        if (!TextUtils.isEmpty(title)) {
            tvTitle.text = title
        }
    }

    fun setTitleTxt(resId: Int) {
        if (!isShowTitle()) return
        tvTitle.setText(resId)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    open fun showMsg(msg: String) {
        val view = window.decorView.findViewById<View>(android.R.id.content)
        SnackbarUtils.with(view).setMessage(msg).show()
    }

    open fun showToast(o: Any) {
        if (o is Int) {
            ToastUtils.showShort(o)
        } else {
            ToastUtils.showShort(o.toString())
        }
    }

    open fun showNetError() {
        if (isUseLoadService()) {
            mLoadService.showCallback(ErrorCallback::class.java)
            mTvErrorMsg.text = getString(R.string.txt_net_error_tips)
        } else {
            LogUtils.d("showNetError:")
            showMsg(getString(R.string.txt_net_error_tips))
        }
    }

    open fun showException(msg: String) {
        if (isUseLoadService()) {
            mLoadService.showCallback(ErrorCallback::class.java)
            mTvErrorMsg.text = msg
        } else {
            showMsg(msg)
        }
    }

    /**
     * 显示空数据
     */
    open fun showEmpty(msg: String) {
        if (isUseLoadService()) {
            mTvEmpty.text = msg
            mLoadService.showCallback(EmptyCallback::class.java)
        } else {

        }
        LogUtils.d("showEmpty:")
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private lateinit var mTvErrorMsg: TextView
    private lateinit var mTvEmpty: TextView
    private lateinit var mLoadService: LoadService<Any>
    private fun initLoadView(o: Any?) {
        mLoadService = LoadSir.getDefault().register(o) {
            onReload()
        }
        mLoadService.setCallBack(ErrorCallback::class.java) { _, view -> mTvErrorMsg = view.findViewById(R.id.tv_error_msg) }
        mLoadService.setCallBack(EmptyCallback::class.java) { _, view -> mTvEmpty = view.findViewById(R.id.tv_empty) }
        mLoadService.setCallBack(EmptyCallback::class.java) { _, view ->
            mTvEmpty = view.findViewById(R.id.tv_empty)
            view.setOnClickListener {
                //                ToastUtils.showShort("----")
            }
        }
    }

    /**
     * 点击重新加载操作
     */
    open fun onReload() {

    }

}