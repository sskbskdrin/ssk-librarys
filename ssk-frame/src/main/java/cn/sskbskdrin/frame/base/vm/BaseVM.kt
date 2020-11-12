package cn.sskbskdrin.frame.base.vm

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel

/**
 * Created by keayuan on 2020/4/3.
 *
 * @author keayuan
 */
open class BaseVM : ViewModel() {
    private var hasHandler = false
    private val mainHandler by lazy {
        hasHandler = true
        Handler(Looper.getMainLooper())
    }

    fun post(delay: Long = 0, block: () -> Unit) {
        mainHandler.postDelayed({ block() }, delay)
    }

    fun isMainThread(): Boolean {
        return Looper.getMainLooper() == Looper.myLooper()
    }

    override fun onCleared() {
        if (hasHandler) {
            mainHandler.removeCallbacksAndMessages(null)
        }
        super.onCleared()
    }
}
