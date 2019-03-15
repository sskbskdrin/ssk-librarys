package cn.sskbskdrin.lib.demo.widget;

import android.graphics.Color;

import cn.sskbskdrin.base.IFragment;
import cn.sskbskdrin.lib.demo.R;
import cn.sskbskdrin.lib.demo.Utils;
import cn.sskbskdrin.widget.pikers.DateTimePicker;
import cn.sskbskdrin.widget.pikers.PickerView;

/**
 * Created by ex-keayuan001 on 2019/3/15.
 *
 * @author ex-keayuan001
 */
public class PickerFragment extends IFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.f_picker_layout;
    }

    @Override
    protected void initView() {
        DateTimePicker mDateTimePicker = getView(R.id.picker_date_time);
        mDateTimePicker.setEnable(true, true);

        PickerView pickerView = getView(R.id.picker_view);
        pickerView.setCycle(true);
        pickerView.setDataList(Utils.getSimpleList(20));
        pickerView.setSelectTextColor(Color.RED);
        pickerView.setTextColor(Color.YELLOW);
        pickerView.setShowCount(13);
    }

    @Override
    protected void initData() {

    }
}
