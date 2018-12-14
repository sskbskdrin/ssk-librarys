package cn.sskbskdrin.lib.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.sskbskdrin.base.BaseActivity;
import cn.sskbskdrin.lib.demo.simple.SampleListFragment;
import cn.sskbskdrin.lib.demo.simple.SimpleAdapter;

public class MainActivity extends BaseActivity {

    private static List<ClassItem> mList = new ArrayList<>();

    static {
        mList.add(new ClassItem(SampleListFragment.class, "SampleList", false));
        mList.add(new ClassItem(FlowActivity.class, "FlowLayout", true));
        mList.add(new ClassItem(TabHostActivity.class, "TabHost", true));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = findViewById(R.id.main_list);
        listView.setAdapter(new SimpleAdapter<>(this, mList));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ClassItem item = (ClassItem) parent.getAdapter().getItem(position);
                if (item.isActivity) {
                    openActivity(item.clazz);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("fragment", item.clazz.getName());
                    openActivity(CommonFragmentActivity.class, bundle);
                }
            }
        });
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