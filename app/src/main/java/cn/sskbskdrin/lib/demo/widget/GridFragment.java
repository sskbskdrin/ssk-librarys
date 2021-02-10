package cn.sskbskdrin.lib.demo.widget;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import cn.sskbskdrin.base.IFragment;
import cn.sskbskdrin.lib.demo.R;

/**
 * Created by keayuan on 2021/2/10.
 *
 * @author keayuan
 */
public class GridFragment extends IFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.f_grid_layout;
    }

    @Override
    protected void onInitView(View rootView, Bundle savedInstanceState) {
        Rect rect = new Rect();
        Gravity.apply(-1, 40, 40, new Rect(100, 100, 200, 200), rect);
        System.out.println("grid fragment=" + rect);
    }
}
