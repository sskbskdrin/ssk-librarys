package cn.sskbskdrin.lib.demo.simple;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import cn.sskbskdrin.base.IBaseAdapter;

import static cn.sskbskdrin.base.ProxyView.setText;

public class SimpleAdapter<T> extends IBaseAdapter<T> {
    public SimpleAdapter(Context context, List<T> list) {
        super(context, list, android.R.layout.simple_list_item_1);
    }

    @Override
    protected void convert(View view, int position, T t) {
        setText((TextView) view, t.toString());
    }
}
