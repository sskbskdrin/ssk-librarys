package cn.sskbskdrin.lib.demo.widget;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import cn.sskbskdrin.base.IFragment;
import cn.sskbskdrin.lib.demo.R;
import cn.sskbskdrin.widget.SwitchButton;

/**
 * Created by keayuan on 2020/4/8.
 *
 * @author keayuan
 */
public class SwitchButtonFragment extends IFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.f_switch_button;
    }

    @Override
    protected void onInitView(View view,  Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) getRootView();
        SwitchButton button = (SwitchButton) root.getChildAt(0);
        button.setText("on", "off");
        button.setBackMeasureRatio(2);
        button.setThumbSize(200, 200);
        button.setThumbRadius(100);
        button.setThumbMargin(20, 20, 20, 20);
        button.setBackRadius(140);
        //        button.setDrawDebugRect(true);
    }
}
