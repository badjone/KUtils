package com.wugx.k_utils.widget.callback

import android.content.Context
import android.view.View

import com.kingja.loadsir.callback.Callback
import com.wugx.k_utils.R


/**
 * Description:TODO
 * Create Time:2017/9/4 10:22
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */

class LoadingCallback : Callback() {

    override fun onCreateView(): Int {
        return R.layout.layout_loading
    }

    override fun getSuccessVisible(): Boolean {
        return super.getSuccessVisible()
    }

    override fun onReloadEvent(context: Context?, view: View?): Boolean {
        return true
    }
}
