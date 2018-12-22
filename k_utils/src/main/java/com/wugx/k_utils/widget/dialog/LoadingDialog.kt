package com.wugx.k_utils.widget.dialog

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.blankj.utilcode.util.ScreenUtils
import com.github.ybq.android.spinkit.style.DoubleBounce
import com.wugx.k_utils.R

/**
 * loading dialog
 *
 * @author Wugx
 * @date 2018/12/17
 */
class LoadingDialog : DialogFragment() {
    private var layout: View? = null
    private var mTvDialogMsg: android.widget.TextView? = null
    private var msg: String? = null
    private var mPbDialog: android.widget.ProgressBar? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(activity!!, R.style.selectorDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        layout = LayoutInflater.from(activity).inflate(R.layout.layout_custom_loading_dialog, null)
        initView()
        dialog.setContentView(layout!!)
        dialog.setCanceledOnTouchOutside(false)

        mPbDialog!!.indeterminateDrawable = DoubleBounce()
        if (!TextUtils.isEmpty(msg))
            mTvDialogMsg!!.text = msg
        setAttr(dialog)
        return dialog
    }

    /**
     * 设置属性
     */
    private fun setAttr(dialog: Dialog) {
        val window = dialog.window
        val lp = window!!.attributes
        //设置dialog大小为宽的0.3
        val screenW = (ScreenUtils.getScreenWidth() * 0.3).toInt()
        lp.width = screenW
        lp.height = screenW
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        if (bundle != null) {
            msg = bundle.getString("msg")
        }
    }

    private fun initView() {
        mTvDialogMsg = layout!!.findViewById(R.id.tv_dialog_msg)
        mPbDialog = layout!!.findViewById(R.id.pb_dialog)
    }

    companion object {
        fun newInstance(msg: String): LoadingDialog {
            val args = Bundle()
            args.putString("msg", msg)
            val fragment = LoadingDialog()
            fragment.arguments = args
            return fragment
        }
    }

}
