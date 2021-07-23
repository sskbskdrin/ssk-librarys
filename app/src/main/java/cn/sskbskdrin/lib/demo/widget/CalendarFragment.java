package cn.sskbskdrin.lib.demo.widget;

import android.os.Bundle;
import android.view.View;

import cn.sskbskdrin.base.IFragment;
import cn.sskbskdrin.lib.demo.R;
import cn.sskbskdrin.widget.calendar.CalendarView;

/**
 * Created by keayuan on 2021/2/10.
 *
 * @author keayuan
 */
public class CalendarFragment extends IFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.f_calendar_layout;
    }

    @Override
    protected void onInitView(View rootView, Bundle savedInstanceState) {
        CalendarView view = getView(R.id.calendar);
    }
}
