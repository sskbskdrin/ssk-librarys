package cn.sskbskdrin.frame.ui.main

import cn.sskbskdrin.frame.R
import cn.sskbskdrin.frame.base.ui.BaseFragment

class MainFragment : BaseFragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun getLayoutId() = R.layout.main_fragment

    override fun initView() {
    }

}
