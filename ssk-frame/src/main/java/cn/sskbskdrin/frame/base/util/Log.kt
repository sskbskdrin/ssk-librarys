package cn.sskbskdrin.frame.base.util

/**
 * Created by keayuan on 2020/4/10.
 * @author keayuan
 */
object Log {
    const val VERBOSE = 2
    const val DEBUG = 3
    const val INFO = 4
    const val WARN = 5
    const val ERROR = 6
    const val ASSERT = 7

    private var DEFAULT_TAG = "default"

    @JvmStatic
    fun logV(tag: String = DEFAULT_TAG, msg: String?, vararg obj: Any?) {
    }

    @JvmStatic
    fun logD(tag: String = DEFAULT_TAG, msg: String?, vararg obj: Any?) {
    }

    @JvmStatic
    fun logI(tag: String = DEFAULT_TAG, msg: String?, vararg obj: Any?) {
    }

    @JvmStatic
    fun logW(tag: String = DEFAULT_TAG, msg: String?, vararg obj: Any?) {
    }

    @JvmStatic
    fun logE(tag: String = DEFAULT_TAG, msg: String?, vararg obj: Any?) {
    }

    fun isLoggable(level: Int): Boolean = false

    @JvmStatic
    fun loggable(level: Int, block: () -> Unit) {
        if (isLoggable(level)) {
            block()
        }
    }
}