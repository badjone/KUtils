package com.wugx.k_utils.widget.callback

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.Toast

import com.kingja.loadsir.callback.Callback
import com.wugx.k_utils.R


/**
 * Description:TODO
 * Create Time:2017/9/3 10:22
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
class AnimateCallback : Callback() {

    private var context: Context? = null
    private var animateView: View? = null

    override fun onCreateView(): Int {
        return R.layout.layout_animate
    }

    override fun onViewCreate(context: Context?, view: View?) {
        super.onViewCreate(context, view)
    }

    override fun onAttach(context: Context?, view: View?) {
        this.context = context
        animateView = view!!.findViewById(R.id.view_animate)
        val animation = RotateAnimation(0f, 359f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        animation.duration = 1000
        animation.repeatCount = Integer.MAX_VALUE
        animation.fillAfter = true
        animation.interpolator = LinearInterpolator()
        animateView!!.startAnimation(animation)
        Toast.makeText(context!!.applicationContext, "start animation", Toast.LENGTH_SHORT).show()
    }

    override fun onDetach() {
        super.onDetach()
        if (animateView != null) {
            animateView!!.clearAnimation()
        }
        Toast.makeText(context!!.applicationContext, "stop animation", Toast.LENGTH_SHORT).show()
    }
}
