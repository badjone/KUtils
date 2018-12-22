package com.wugx.k_utils.widget

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Nullable

import com.blankj.utilcode.util.StringUtils
import com.wugx.k_utils.R

/**
 * @author wugx
 * @data 2017/12/5.
 */
class ItemLayout(context: Context, @Nullable attrs: AttributeSet) : LinearLayout(context, attrs) {
    private lateinit var imgLayoutItemIcon: ImageView
    private lateinit var tvLayoutItemTitle: TextView
    private lateinit var tvLayoutItemRight: TextView

    private lateinit var v: View
    private val a: TypedArray = context.obtainStyledAttributes(attrs,
            R.styleable.ItemLayout)
    private lateinit var mImgLayoutItemMore: ImageView

    private var title: String? = null
    private var rightTxt: String? = null
    private var icon: Int = 0
    private var isShowMoreIcon: Boolean = false

    init {
        init()
    }

    private fun init() {
        //        setBackground(getResources().getDrawable(R.drawable.selected_item_ll));
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        v = LayoutInflater.from(context).inflate(R.layout.layout_item_select, null)
        initView()
        initAttr()
        addView(v, layoutParams)
        a.recycle()
    }

    private fun initAttr() {
        title = a.getString(R.styleable.ItemLayout_lefttitle)
        rightTxt = a.getString(R.styleable.ItemLayout_rightTxt)
        icon = a.getResourceId(R.styleable.ItemLayout_icon, 0)
        isShowMoreIcon = a.getBoolean(R.styleable.ItemLayout_isShowMoreIcon, false)

        if (icon != 0) {
            imgLayoutItemIcon.visibility = View.VISIBLE
            imgLayoutItemIcon.setImageResource(icon)
        } else {
            imgLayoutItemIcon.visibility = View.GONE
        }
        tvLayoutItemTitle.text = if (!StringUtils.isEmpty(title)) title else context.resources.getString(R.string.app_name)
        if (!StringUtils.isEmpty(rightTxt)) {
            tvLayoutItemRight.text = rightTxt
        }
        mImgLayoutItemMore.visibility = if (isShowMoreIcon) View.VISIBLE else View.GONE
    }

    private fun initView() {
        imgLayoutItemIcon = v.findViewById(R.id.img_layout_item_icon)
        tvLayoutItemTitle = v.findViewById(R.id.tv_layout_item_title)
        tvLayoutItemRight = v.findViewById(R.id.tv_layout_item_right)
        mImgLayoutItemMore = v.findViewById(R.id.img_layout_item_more)
    }

    fun setTitle(title: String) {
        this.title = title
        tvLayoutItemTitle.text = if (!StringUtils.isEmpty(title)) title else context.resources.getString(R.string.app_name)
    }

    fun setRightTxt(rightTxt: String) {
        this.rightTxt = rightTxt
        if (!StringUtils.isEmpty(rightTxt)) {
            tvLayoutItemRight.text = rightTxt
        }
    }

    fun getRightTxt(): String? {
        return rightTxt
    }

    fun setIcon(icon: Int) {
        this.icon = icon
        if (icon != 0) {
            imgLayoutItemIcon.visibility = View.VISIBLE
            imgLayoutItemIcon.setImageResource(icon)
        } else {
            imgLayoutItemIcon.visibility = View.GONE
        }
    }
}
