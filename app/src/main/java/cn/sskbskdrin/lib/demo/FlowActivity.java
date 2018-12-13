package cn.sskbskdrin.lib.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.sskbskdrin.base.BaseActivity;
import cn.sskbskdrin.base.IBaseAdapter;
import cn.sskbskdrin.util.CommonUtils;
import cn.sskbskdrin.widget.FlowLayout;

public class FlowActivity extends BaseActivity {
    private FlowLayout flowLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow);
        SeekBar seed_h = findViewById(R.id.flow_seek_h);
        seed_h.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                flowLayout.setHorizontalSpace(seekBar.getProgress());
            }
        });
        SeekBar seed_v = findViewById(R.id.flow_seek_v);
        seed_v.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                flowLayout.setVerticalSpace(seekBar.getProgress());
            }
        });

        flowLayout = findViewById(R.id.flow_layout);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            int count = CommonUtils.random(3, 11);
            StringBuilder builder = new StringBuilder(count);
            for (int j = 0; j < count; j++) {
                builder.append((char) CommonUtils.random('a', 'z' + 1));
            }
            list.add(builder.toString());
        }
        flowLayout.setAdapter(new IBaseAdapter<String>(this, list, android.R.layout.simple_list_item_1) {
            @Override
            protected void convert(View view, int position, String s) {
                ((TextView) view).setText(s);
            }
        });
    }
}
