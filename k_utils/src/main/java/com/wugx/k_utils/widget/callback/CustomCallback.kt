package com.wugx.k_utils.widget.callback

import android.content.Context
import android.view.View
import android.widget.Toast

import com.kingja.loadsir.callback.Callback
import com.wugx.k_utils.R


/**
 * Description:TODO
 * Create Time:2017/9/3 10:22
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
class CustomCallback : Callback() {

    override fun onCreateView(): Int {
        return R.layout.layout_custom
    }

    //当前Callback的点击事件，如果返回true则覆盖注册时的onReloa()，如果返回false则两者都执行，先执行onReloadEvent()。
    override fun onReloadEvent(context: Context?, view: View?): Boolean {
        Toast.makeText(context!!.applicationContext, "Hello buddy! :p", Toast.LENGTH_SHORT).show()
        view!!.findViewById<View>(R.id.iv_gift).setOnClickListener { Toast.makeText(context.applicationContext, "It's your gift! :p", Toast.LENGTH_SHORT).show() }
        return true
    }

    //是否在显示Callback视图的时候显示原始图(SuccessView)，返回true显示，false隐藏
    override fun getSuccessVisible(): Boolean {
        return super.getSuccessVisible()
    }

    //将Callback添加到当前视图时的回调，View为当前Callback的布局View
    override fun onAttach(context: Context?, view: View?) {
        super.onAttach(context, view)
    }

    //将Callback从当前视图删除时的回调，View为当前Callback的布局View
    override fun onDetach() {
        //        super.onDetach(context, view);
    }

}
