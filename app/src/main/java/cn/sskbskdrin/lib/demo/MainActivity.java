package cn.sskbskdrin.lib.demo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import cn.sskbskdrin.base.IA;
import cn.sskbskdrin.base.adapter.IHolder;
import cn.sskbskdrin.lib.demo.simple.SampleListFragment;
import cn.sskbskdrin.lib.demo.simple.SimpleAdapter;
import cn.sskbskdrin.lib.demo.tool.HttpFragment;
import cn.sskbskdrin.lib.demo.widget.BannerFragment;
import cn.sskbskdrin.lib.demo.widget.CalendarFragment;
import cn.sskbskdrin.lib.demo.widget.FlowFragment;
import cn.sskbskdrin.lib.demo.widget.GridFragment;
import cn.sskbskdrin.lib.demo.widget.PickerFragment;
import cn.sskbskdrin.lib.demo.widget.PullFragment;
import cn.sskbskdrin.lib.demo.widget.SwitchButtonFragment;
import cn.sskbskdrin.lib.demo.widget.TabHostFragment;
import cn.sskbskdrin.lib.demo.widget.swipe.ScrollFragment;
import cn.sskbskdrin.lib.demo.widget.swipe.WebFragment;

@Route(path = "/main/main")
public class MainActivity extends AppCompatActivity implements IA {
    private static final String TAG = "MainActivity";

    private static List<ClassItem> mList = new ArrayList<>();

    static {
        mList.add(new ClassItem(SampleListFragment.class, "SampleList"));
        mList.add(new ClassItem(FlowFragment.class, "FlowLayout"));
        mList.add(new ClassItem(TabHostFragment.class, "TabHost"));
        mList.add(new ClassItem(PickerFragment.class, "PickerView"));
        mList.add(new ClassItem(BannerFragment.class, "BannerView"));
        mList.add(new ClassItem(HttpFragment.class, "HTTP"));
        mList.add(new ClassItem(SwitchButtonFragment.class, "SwitchButton"));
        mList.add(new ClassItem(PullFragment.class, "PullLayout"));
        mList.add(new ClassItem(WebFragment.class, "SwipeWebLayout"));
        mList.add(new ClassItem(ScrollFragment.class, "SwipeScrollLayout"));
        mList.add(new ClassItem(GridFragment.class, "GridLayout"));
        mList.add(new ClassItem(CalendarFragment.class, "Calendar"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.openDebug();
        ARouter.openLog();
        ARouter.init(getApplication());
        setContentView(R.layout.activity_main);
        //        StatusBar.hideStatusBar(this);
        StatusBar.setMarginTop(this, true);
        //        StatusBar.setDarkMode(this, true);
        //        StatusBar.hideStatusBar(this);
        //        StatusBar.setTranslucentColor(this, 0x8800a000);
        StatusBar.setTranslucentColor(this, color(R.color.colorPrimaryDark));
        ListView listView = findViewById(R.id.main_list);
        listView.setAdapter(new SimpleAdapter<ClassItem>(mList) {
            @Override
            public void onClickItem(IHolder<ClassItem> holder) {
                ClassItem item = holder.bean();
                if (Activity.class.isAssignableFrom(item.clazz)) {
                    ARouter.getInstance().build("common").navigation();
                    //                    openActivity(item.clazz);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("fragment", item.clazz.getName());
                    //                    openActivity(CommonFragmentActivity.class, bundle);
                    ARouter.getInstance().build("/main/common").with(bundle).navigation();
                }
            }
        });
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public boolean isFinish() {
        return isFinishing();
    }

    public static class ClassItem implements Serializable {

        public Class clazz;
        public String text;

        public ClassItem(Class clazz, String text) {
            this.clazz = clazz;
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}
