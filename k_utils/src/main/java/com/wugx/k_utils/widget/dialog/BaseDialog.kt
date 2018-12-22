package com.wugx.k_utils.widget.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.DialogFragment
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.StringUtils
import com.wugx.k_utils.R

/**
 * title msg next cancel 基本样式的dialog
 *
 * @author Wugx
 * @date 2018/12/18
 */
class BaseDialog : DialogFragment() {
    private var dialogTitle: String? = null
    private var dialogContent: String? = null
    private var dialogNext: String? = null
    private var dialogCancel: String? = null

    private var mListener: DialogBaseListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = arguments
        if (bundle != null) {
            dialogTitle = bundle.getString("base_dialog_title")
            dialogContent = bundle.getString("base_dialog_msg")
            dialogNext = bundle.getString("base_dialog_next")
            dialogCancel = bundle.getString("base_dialog_cancel")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(activity!!, R.style.style_dialog_base)
        val layoutView = LayoutInflater.from(activity).inflate(R.layout.layout_dialog_base, null)
        initData(layoutView)
        dialog.setContentView(layoutView)
        setAttr(dialog)
        initListener()
        return dialog
    }

    private lateinit var tvDialogTitle: AppCompatTextView
    private lateinit var tvDialogContent: AppCompatTextView
    private lateinit var btnDialogNext: AppCompatButton
    private lateinit var btnDialogCancel: AppCompatButton
    private fun initData(v: View) {
        tvDialogTitle = v.findViewById(R.id.tv_dialog_base_title)
        tvDialogContent = v.findViewById(R.id.tv_dialog_base_content)
        btnDialogNext = v.findViewById(R.id.btn_dialog_base_next)
        btnDialogCancel = v.findViewById(R.id.btn_dialog_base_cancel)

        if (!StringUtils.isEmpty(dialogTitle)) {
            tvDialogTitle!!.text = dialogTitle
        }
        if (!StringUtils.isEmpty(dialogContent)) {
            tvDialogContent!!.text = dialogContent
        }
        if (!StringUtils.isEmpty(dialogNext)) {
            btnDialogNext!!.text = dialogNext
        }
        if (!StringUtils.isEmpty(dialogCancel)) {
            btnDialogCancel!!.text = dialogCancel
        }
    }

    private fun initListener() {
        btnDialogNext!!.setOnClickListener {
            if (mListener != null) mListener!!.next()
            dismiss()
        }
        btnDialogCancel!!.setOnClickListener {
            if (mListener != null) mListener!!.cancel()
            dismiss()
        }
    }

    private fun setAttr(dialog: Dialog) {
        val window = dialog.window
        window!!.setWindowAnimations(R.style.dialog_anim_zoom)
        val lp = window.attributes
        //设置dialog大小为宽的0.3
        val screenW = (ScreenUtils.getScreenWidth() * 0.75).toInt()
        lp.width = screenW
        //        lp.height = screenW;
    }

    fun setListener(listener: DialogBaseListener): BaseDialog {
        mListener = listener
        return this
    }

    interface DialogBaseListener {
        operator fun next()

        fun cancel()
    }

    companion object {

        fun newInstance(title: String?, msg: String?, next: String?, cancel: String?): BaseDialog {
            val args = Bundle()
            args.putString("base_dialog_title", title)
            args.putString("base_dialog_msg", msg)
            args.putString("base_dialog_next", next)
            args.putString("base_dialog_cancel", cancel)
            val fragment = BaseDialog()
            fragment.arguments = args
            return fragment
        }
    }

}
