package cn.sskbskdrin.lib.demo.widget;

import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.sskbskdrin.base.IBaseAdapter;
import cn.sskbskdrin.base.IFragment;
import cn.sskbskdrin.lib.demo.R;
import cn.sskbskdrin.util.CommonUtils;
import cn.sskbskdrin.widget.FlowLayout;

public class FlowFragment extends IFragment {
    private FlowLayout flowLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.f_flow_layout;
    }

    @Override
    protected void initView() {
        SeekBar seed_h = getView(R.id.flow_seek_h);
        SeekBar seed_v = getView(R.id.flow_seek_v);
        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getId() == R.id.flow_seek_v) {
                    flowLayout.setVerticalSpace(seekBar.getProgress());
                } else {
                    flowLayout.setHorizontalSpace(seekBar.getProgress());
                }
            }
        };
        seed_h.setOnSeekBarChangeListener(listener);
        seed_v.setOnSeekBarChangeListener(listener);
        flowLayout = getView(R.id.flow_layout);
    }

    @Override
    protected void initData() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            int count = CommonUtils.random(3, 11);
            StringBuilder builder = new StringBuilder(count);
            for (int j = 0; j < count; j++) {
                builder.append((char) CommonUtils.random('a', 'z' + 1));
            }
            list.add(builder.toString());
        }
        flowLayout.setAdapter(new IBaseAdapter<String>(getActivity(), list, R.layout.simple_text_layout) {
            @Override
            protected void convert(View view, int position, String s) {
                ((TextView) view).setText(s);
            }
        });
    }

}
