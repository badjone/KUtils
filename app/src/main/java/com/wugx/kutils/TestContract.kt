package com.wugx.k_utils.base.test

import com.wugx.k_utils.base.IBaseView
import com.wugx.k_utils.base.IPresenter


/**
 *
 *
 *@author Wugx
 *@date   2018/12/19
 */
interface TestContract {

    interface IView : IBaseView {
        fun showDatas(result:String)
    }

    interface Presenter: IPresenter<IView> {
        fun req()
    }
}