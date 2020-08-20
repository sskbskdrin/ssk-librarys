package cn.sskbskdrin.lib.demo;

import android.os.Bundle;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.sskbskdrin.base.IFragmentActivity;
import cn.sskbskdrin.flow.FlowProcess;
import cn.sskbskdrin.lib.demo.simple.SimpleAdapter;
import cn.sskbskdrin.lib.demo.tool.HttpFragment;
import cn.sskbskdrin.lib.demo.widget.BannerFragment;
import cn.sskbskdrin.lib.demo.widget.FlowFragment;
import cn.sskbskdrin.lib.demo.widget.PickerFragment;
import cn.sskbskdrin.lib.demo.widget.TabHostFragment;

public class MainActivity extends IFragmentActivity {

    private static List<ClassItem> mList = new ArrayList<>();

    static {
        mList.add(new ClassItem(FlowFragment.class, "FlowLayout", false));
        mList.add(new ClassItem(TabHostFragment.class, "TabHost", false));
        mList.add(new ClassItem(PickerFragment.class, "PickerView", false));
        mList.add(new ClassItem(BannerFragment.class, "BannerView", false));
        mList.add(new ClassItem(HttpFragment.class, "HTTP", false));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = findViewById(R.id.main_list);
        listView.setAdapter(new SimpleAdapter<>(this, mList));
        listView.setOnItemClickListener((parent, view, position, id) -> {
            ClassItem item = (ClassItem) parent.getAdapter().getItem(position);
            if (item.isActivity) {
                openActivity(item.clazz);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("fragment", item.clazz.getName());
                openActivity(CommonFragmentActivity.class, bundle);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().postDelayed(this::test,100);
    }

    private void test(){
        new FlowProcess().main(params -> {
            System.out.println(Thread.currentThread().getName() + "=main " + System.currentTimeMillis());
            return 8;
        }).io(params -> {
            System.out.println(Thread.currentThread().getName() + "=io " + System.currentTimeMillis());
            return params[0].toString();
        }).main(params -> {
            System.out.println(Thread.currentThread().getName() + "=main " + System.currentTimeMillis());
            return Long.parseLong(params[0].toString());
        }).main(params -> {
            System.out.println(Thread.currentThread().getName() + "=main " + System.currentTimeMillis());
            return Long.parseLong(params[0].toString());
        }).start();
    }

    public static class ClassItem implements Serializable {

        public Class clazz;
        public String text;
        public boolean isActivity;

        public ClassItem(Class clazz, String text, boolean activity) {
            this.clazz = clazz;
            this.text = text;
            isActivity = activity;
        }

        @Override
        public String toString() {
            return text;
        }
    }

}
