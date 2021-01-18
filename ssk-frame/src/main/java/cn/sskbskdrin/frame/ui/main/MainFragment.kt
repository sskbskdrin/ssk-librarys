package cn.sskbskdrin.frame.ui.main

import android.os.Bundle
import android.view.View
import cn.sskbskdrin.frame.R
import cn.sskbskdrin.frame.base.ui.BaseFragment

class MainFragment : BaseFragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onViewCreated(rootView: View?, arguments: Bundle?, savedInstanceState: Bundle?) {
    }

    override fun getLayoutId() = R.layout.main_fragment

    override fun initView() {
    }

}
