package cn.sskbskdrin.lib.demo;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import cn.sskbskdrin.base.BaseActivity;
import cn.sskbskdrin.widget.TabHostWidget;
import cn.sskbskdrin.widget.TabWidget;

public class TabHostActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_host);
        TabHostWidget tabHostWidget = findViewById(R.id.tab_host_tab_host);
        List<Class<? extends Fragment>> list = new ArrayList<>();
        list.add(Test1Fragment.class);
        list.add(Test2Fragment.class);
        list.add(Test3Fragment.class);
        list.add(TestFragment.class);
        tabHostWidget.setPager(getSupportFragmentManager(), list);
        TabWidget tabWidget = tabHostWidget.getTabWidget();

        tabWidget.setItemLayout(R.layout.tab_host_item_layout);
        tabWidget.addTab(new TabWidget.Tab("首页", R.mipmap.home_normal, R.mipmap.home_selected, "88"));
        tabWidget.addTab(new TabWidget.Tab("通讯录", R.mipmap.category_normal, R.mipmap.category_selected, ""));
        tabWidget.addTab(new TabWidget.Tab("发现", R.drawable.find_icon, R.drawable.find_icon1, "99+"));
        tabWidget.addTab(new TabWidget.Tab("我", R.mipmap.mine_normal, R.mipmap.mine_selected, "0"));
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
}
