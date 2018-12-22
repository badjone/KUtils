package com.wugx.k_utils.base.test

import com.blankj.utilcode.util.LogUtils
import com.wugx.k_utils.base.BasePresenter
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 *
 *
 *@author Wugx
 *@date   2018/12/19
 */
class TestPresenter : BasePresenter<TestContract.IView>(), TestContract.Presenter {

    override fun req() {
        mRootView?.let {
            Observable.interval(2, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(it.bindToLife())
//                    .bindUntilEvent(it.bindToLife(), DrmStore.Playback.STOP)
                    .subscribe(object : Observer<Long> {
                        override fun onComplete() {

                        }

                        override fun onSubscribe(d: Disposable) {

                        }

                        override fun onNext(t: Long) {
                            LogUtils.d("$t")
                            it.showDatas("" + t)
                        }

                        override fun onError(e: Throwable) {

                        }
                    })
//
//            Observable.interval(2, TimeUnit.SECONDS)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .compose(it.bindToLife())
////                    .bindUntilEvent(it.bindToLife(), DrmStore.Playback.STOP)
//                    .subscribe()


        }


    }
}