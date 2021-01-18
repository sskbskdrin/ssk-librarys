package cn.sskbskdrin.frame.base.ui

import android.os.Bundle
import cn.sskbskdrin.frame.base.vm.BaseVM
import java.lang.reflect.ParameterizedType

/**
 * Created by keayuan on 2020/11/18.
 * @author keayuan
 */
abstract class BaseVMActivity<T : BaseVM> : BaseActivity() {
    protected lateinit var vm: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getVMClass()?.apply {
            vm = getViewModel(this)
        }
    }

    private fun getVMClass(): Class<T>? {
        try {
            val type = javaClass.genericSuperclass
            if (type is ParameterizedType) {
                if (type.actualTypeArguments[0] is ParameterizedType)
                    return (type.actualTypeArguments[0] as ParameterizedType).rawType as Class<T>
                return type.actualTypeArguments[0] as Class<T>
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return null
    }
}