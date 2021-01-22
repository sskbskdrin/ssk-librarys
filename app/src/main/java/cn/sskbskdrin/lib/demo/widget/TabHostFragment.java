package cn.sskbskdrin.lib.demo.widget;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import cn.sskbskdrin.base.IFragment;
import cn.sskbskdrin.lib.demo.R;
import cn.sskbskdrin.widget.TabHostWidget;
import cn.sskbskdrin.widget.TabWidget;

public class TabHostFragment extends IFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.f_tab_host_layout;
    }

    @Override
    protected void onInitView(View view,  Bundle savedInstanceState) {
        TabHostWidget tabHostWidget = getView(R.id.tab_host_tab_host);
        List<Class<? extends Fragment>> list = new ArrayList<>();
        list.add(TestFragment.class);
        list.add(PullFragment.class);
        list.add(TestFragment.class);
        list.add(PullFragment.class);
        tabHostWidget.setPager(getChildFragmentManager(), list);
        TabWidget tabWidget = tabHostWidget.getTabWidget();

        tabWidget.setItemLayout(R.layout.tab_host_item_layout);
        tabWidget.addTab("首页", R.mipmap.home_normal, R.mipmap.home_selected, "88");
        tabWidget.addTab("通讯录", R.mipmap.category_normal, R.mipmap.category_selected, "");
        tabWidget.addTab("发现", R.drawable.find_icon, R.drawable.find_icon1, "99+");
        tabWidget.addTab("我", R.mipmap.mine_normal, R.mipmap.mine_selected, "0");
        tabWidget.setTextColor(0xff00ac00, 0xff939393);
        tabWidget.setLineColor(0xff939393);
        tabWidget.setOnDoubleClickListener(new TabWidget.OnDoubleClickListener() {
            @Override
            public void onDoubleClick(int position) {
                showToast("double" + position);
            }
        });
        tabWidget.addOnTabChangeListener(new TabWidget.OnTabChangeListener() {
            @Override
            public void onTabChange(int old, int newPosition) {
                showToast("tab " + old + " " + newPosition);
            }
        });
        tabWidget.setIndicatorColor(0);
        tabWidget.setLineColor(0xffeeeeee);
    }

    public static class TestFragment extends IFragment {
        static int count = 0;

        @Override
        protected int getLayoutId() {
            return R.layout.simple_text_layout;
        }

        @Override
        protected void onInitView(View view,  Bundle savedInstanceState) {
            TextView text = getView(R.id.simple_text);
            text.setTextSize(20 * getResources().getDisplayMetrics().density);
            text.setText("Test " + ++count);
        }
    }
}
