package cn.sskbskdrin.lib.demo.widget.swipe;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cn.sskbskdrin.lib.demo.R;
import cn.sskbskdrin.widget.swipe.SwipeLayout;
import cn.sskbskdrin.widget.swipe.SwipePosition;

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
    protected void onInitView(View rootView, Bundle savedInstanceState) {
        super.onInitView(rootView, savedInstanceState);
        content = getView(R.id.scroll_text);
        SwipeLayout swipeLayout = getRootView();
        swipeLayout.setEnabled(SwipePosition.BOTTOM, false);
    }

    @Override
    protected void refreshTop() {
        content.setText("I already refresh");
    }
}
