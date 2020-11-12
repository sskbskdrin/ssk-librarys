package cn.sskbskdrin.frame.base.ui

import android.content.Intent
import android.os.Bundle
import cn.sskbskdrin.frame.base.vm.BaseVM
import cn.sskbskdrin.frame.base.vm.LifeOwner
import cn.sskbskdrin.frame.base.vm.Observer

/**
 * Created by keayuan on 2020/11/11.
 * @author keayuan
 */
open class WindowVM : BaseVM() {

    private val toastModel = WindowModel(WindowData.CMD.SHOW_TOAST)
    private val dialogModel = WindowModel(WindowData.CMD.LOADING_DIALOG)
    private val finishModel = WindowModel(WindowData.CMD.FINISH)
    private val openModel = WindowModel(WindowData.CMD.OPEN)

    internal fun bind(owner: LifeOwner, observer: Observer<WindowData>) {
        toastModel.observe(owner, observer)
        dialogModel.observe(owner, observer)
        finishModel.observe(owner, observer)
        openModel.observe(owner, observer)
    }

    protected fun showToast(resId: Int) {
        toastModel.send(resId)
    }

    protected fun showToast(content: String, isLong: Boolean = false) {
        toastModel.send(content, isLong)
    }

    protected fun showLoading(content: String) {
        dialogModel.send(content)
    }

    protected fun hideLoading() {
        dialogModel.send()
    }

    protected fun finish() {
        finishModel.send()
    }

    protected fun openActivity(intent: Intent, requestCode: Int = -1) {
        openModel.send(intent, requestCode)
    }

    protected fun openActivity(clazz: Class<*>, bundle: Bundle? = null, requestCode: Int = -1) {
        openModel.send(clazz, bundle, requestCode)
    }

    protected fun replaceFragment(fragment: BaseFragment, id: Int) {
        openModel.send(fragment, id)
    }

    protected fun addFragment(fragment: BaseFragment, id: Int) {
        openModel.send(fragment, id, "add")
    }
}