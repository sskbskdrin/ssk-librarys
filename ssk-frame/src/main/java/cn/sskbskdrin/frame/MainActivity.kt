package cn.sskbskdrin.frame

import android.os.Bundle
import cn.sskbskdrin.frame.base.ui.BaseActivity
import cn.sskbskdrin.frame.ui.main.MainFragment

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }
}
