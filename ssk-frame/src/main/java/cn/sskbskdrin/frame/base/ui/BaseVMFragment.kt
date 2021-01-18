package com.lemondm.handmap.base.ui

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.lemondm.handmap.base.vm.BaseVM
import java.lang.reflect.ParameterizedType

/**
 * Created by keayuan on 2020/11/18.
 * @author keayuan
 */
abstract class BaseVMFragment<T : BaseVM> : BaseFragment() {
    protected lateinit var vm: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getVMClass()?.apply {
            vm = getViewModel(this)
        }
    }

    fun <T : BaseVM> getViewModel(clazz: Class<T>) = ViewModelProvider(this).get(clazz)

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