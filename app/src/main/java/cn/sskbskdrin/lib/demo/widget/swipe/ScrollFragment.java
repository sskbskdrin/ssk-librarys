package cn.sskbskdrin.lib.demo.widget.swipe;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cn.sskbskdrin.lib.demo.R;

/**
 * Created by ayke on 2016/9/26 0026.
 */

public class ScrollFragment extends BaseFragment {
    TextView content;

    @Override
    protected int getLayoutId() {
        return R.layout.f_swipe_scroll;
    }

    @Override
    protected void onViewCreated(View rootView, Bundle arguments, Bundle savedInstanceState) {
        super.onViewCreated(rootView, arguments, savedInstanceState);
        content = getView(R.id.scroll_text);
    }

    @Override
    protected void refreshTop() {
        content.setText("I already refresh");
    }
}
