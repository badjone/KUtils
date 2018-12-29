package com.wugx.k_utils.utils


import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ActivityUtils
import com.wugx.k_utils.widget.dialog.BaseDialog
import com.wugx.k_utils.widget.dialog.LoadingDialog


/**
 * 一些 ui显示的公共类
 *
 * @author Wugx
 * @date 2018/11/8
 */
object UiUtils {
    /**
     * 显示加载中dialog
     *
     * @param msg
     */
    fun showLoadDialog(msg: String) {

        val topActivity = ActivityUtils.getTopActivity() ?: return
        topActivity as AppCompatActivity
        val fragment = LoadingDialog.newInstance(msg)
        val fm = topActivity.supportFragmentManager
        val ft = fm.beginTransaction()
        val curr_dialog = fm.findFragmentByTag("loading_dialog")
        //如果存在，则移除掉
        if (curr_dialog != null) {
            ft.remove(curr_dialog).commit()
        }
        ft.add(fragment, "loading_dialog")
        ft.commitAllowingStateLoss()
        ft.show(fragment)
    }

    fun dismissLoadDialog() {
        val topActivity = ActivityUtils.getTopActivity() as AppCompatActivity ?: return
        val fm = topActivity.supportFragmentManager
        val ft = fm.beginTransaction()
        val curr_dialog = fm.findFragmentByTag("loading_dialog")
        //如果存在，则移除掉
        if (curr_dialog != null) {
            ft.remove(curr_dialog).commit()
        }
    }

    /**
     *
     */
    fun showBaseDialog(listener: BaseDialog.DialogBaseListener, vararg params: String) {
        val topActivity = ActivityUtils.getTopActivity() ?: return
        topActivity as AppCompatActivity
        var title: String? = null
        var content: String? = null
        var next: String? = null
        var cancel: String? = null
        when (params.size) {
            1 -> title = params[0]
            2 -> {
                title = params[0]
                content = params[1]
            }
            3 -> {
                title = params[0]
                content = params[1]
                next = params[2]
            }
            4 -> {
                title = params[0]
                content = params[1]
                next = params[2]
                cancel = params[3]
            }
        }
        val fragment = BaseDialog.newInstance(title, content, next, cancel)
                .setListener(listener)
        val fm = topActivity.supportFragmentManager
        val ft = fm.beginTransaction()
        val curr_dialog = fm.findFragmentByTag("base_dialog")
        //如果存在，则移除掉
        if (curr_dialog != null) {
            ft.remove(curr_dialog).commit()
        }
        ft.add(fragment, "base_dialog")
        ft.commitAllowingStateLoss()
        ft.show(fragment)
    }


}
