package cn.sskbskdrin.frame.base.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import cn.sskbskdrin.base.IA
import cn.sskbskdrin.frame.R
import cn.sskbskdrin.frame.base.util.Log.VERBOSE
import cn.sskbskdrin.frame.base.util.Log.logV
import cn.sskbskdrin.frame.base.util.Log.loggable
import cn.sskbskdrin.frame.base.vm.BaseVM
import cn.sskbskdrin.frame.base.vm.LifeOwner
import cn.sskbskdrin.frame.base.vm.Observer

@SuppressLint("Registered")
open class BaseActivity : FragmentActivity(), IA, LifeOwner {
    private val TAG = javaClass.simpleName
    private var isStop = false

    override fun getContext() = this

    override fun isFinish() = isFinishing

    override fun <T : View?> getView(id: Int): T = findViewById<T>(id)

    private val observer by lazy {
        Observer<WindowData> {
            var obj1: Any? = null
            var obj2: Any? = null
            var obj3: Any? = null
            if (it.obj != null) {
                if (it.obj.isNotEmpty()) obj1 = it.obj[0]
                if (it.obj.size > 1) obj2 = it.obj[1]
                if (it.obj.size > 2) obj3 = it.obj[2]
            }
            when (it?.type) {
                WindowData.CMD.SHOW_TOAST -> {
                    showToast(
                        if (obj1 is String) obj1.toString() else getString(obj1 as Int),
                        if (obj2 == null) false else obj2 as Boolean
                    )
                }
                WindowData.CMD.LOADING_DIALOG -> {
                    if (obj1 == null) {
                        hideLoadingDialog()
                    } else {
                        showLoadingDialog(
                            if (obj1 is String) obj1.toString()
                            else getString(obj1 as Int)
                        )
                    }
                }
                WindowData.CMD.FINISH -> {
                    hideLoadingDialog()
                    finish()
                }
                WindowData.CMD.OPEN -> {
                    if (obj1 is Intent) {
                        openActivity(obj1 as Intent, obj2 as Int)
                    } else if (obj1 is Class<*>) {
                        openActivity(
                            obj1 as Class<Activity>, if (obj2 == null) null else obj2 as Bundle, obj3 as Int
                        )
                    } else if (obj1 is BaseFragment) {
                        if ("add" == obj3) {
                            addFragment(obj1 as BaseFragment, obj2 as Int)
                        } else {
                            replaceFragment(obj1 as BaseFragment, obj2 as Int)
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        loggable(VERBOSE) {
            logV(TAG, "onCreate:")
        }
    }

    fun <T : BaseVM> getViewModel(clazz: Class<T>): T {
        val vm = ViewModelProvider(this).get(clazz)
        if (vm is WindowVM) {
            vm.bind(this, observer)
        }
        return vm
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        findViewById<View>(R.id.action_bar_back)?.setOnClickListener { onBackPressed() }
        if (isEnableImmersive()) {
            val contentView = findViewById<ViewGroup>(android.R.id.content)
            val childAt = contentView.getChildAt(0)
            childAt?.fitsSystemWindows = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = color(R.color.main_color)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                val v = View(this)
                v.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, getStatusHeight()
                )
                v.setBackgroundColor(color(R.color.main_color))
                contentView.addView(v)
            }
        }
    }

    protected fun setTitle(title: String) {
        findViewById<TextView>(R.id.action_bar_title)?.text = title
    }

    open fun isEnableImmersive() = true

    override fun onResume() {
        super.onResume()
        loggable(VERBOSE) {
            logV(TAG, "onResume:")
        }
        isStop = false
    }

    override fun onPause() {
        super.onPause()
        loggable(VERBOSE) {
            logV(TAG, "onPause:")
        }
    }

    override fun onStop() {
        super.onStop()
        loggable(VERBOSE) {
            logV(TAG, "onStop:")
        }
        isStop = true
    }

    fun isStop() = isStop

    /**
     * 获取顶部状态栏高度
     *
     * @return 状态栏高度
     */
    private fun getStatusHeight(): Int {
        val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

    fun replaceFragment(fragment: BaseFragment, id: Int) {
        supportFragmentManager.beginTransaction().replace(id, fragment).commit()
    }

    private fun addFragment(fragment: BaseFragment, id: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.right_enter, R.anim.left_exit, R.anim.left_enter, R.anim.right_exit
        )
        transaction.addToBackStack(fragment.javaClass.name)
        transaction.replace(id, fragment).commit()
    }
}