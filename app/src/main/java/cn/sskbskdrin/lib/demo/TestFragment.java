package cn.sskbskdrin.lib.demo;

import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import cn.sskbskdrin.base.IFragment;
import cn.sskbskdrin.widget.TabHostWidget;
import cn.sskbskdrin.widget.TabWidget;

public final class TestFragment extends IFragment {
    private static final String KEY_CONTENT = "TestFragment:Content";

    private static int mmm = 0;

    public static TestFragment newInstance() {
        return newInstance(String.valueOf(++mmm));
    }

    public static TestFragment newInstance(String content) {
        return new TestFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.tab_host_tab_layout;
    }

    @Override
    protected void initView() {
        TabHostWidget tabHostWidget = getView(R.id.main_tab_host);
        List<Class<? extends Fragment>> list = new ArrayList<>();
        list.add(Test1Fragment.class);
        list.add(Test1Fragment.class);
        list.add(Test3Fragment.class);
        tabHostWidget.setPager(getChildFragmentManager(), list);
        TabWidget tabWidget = tabHostWidget.getTabWidget();

        tabWidget.setItemLayout(R.layout.tab_host_tab_item_layout);
        tabWidget.addTab(new TabWidget.Tab("通讯录", R.drawable.rect, R.drawable.rect, null));
        tabWidget.addTab(new TabWidget.Tab("发现", R.drawable.rect, R.drawable.rect, null));
        tabWidget.addTab(new TabWidget.Tab("我", R.drawable.rect, R.drawable.rect, null));
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
        tabWidget.setIndicatorPosition(false);
        tabWidget.setIndicatorWidth(200);
        tabWidget.setIndicatorHeight(4);
        tabWidget.setIndicatorColor(0xff00ac00);

        tabHostWidget.setTabWidgetPosition(true);
    }

    @Override
    protected void initData() {

    }
}
