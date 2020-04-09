package cn.sskbskdrin.lib.demo.simple;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.sskbskdrin.base.IBaseAdapter;

public class SimpleAdapter<T> extends IBaseAdapter<T> {
    public SimpleAdapter(Context context, List<T> list) {
        super(context, list, android.R.layout.simple_list_item_1);
    }

    public SimpleAdapter(Context context, T[] array) {
        super(context, null, android.R.layout.simple_list_item_1);
        List<T> list = new ArrayList<>();
        if (array != null) {
            Collections.addAll(list, array);
        }
        updateList(list);
    }

    @Override
    protected void convert(View view, int position, T t) {
        setText((TextView) view, t.toString());
        setBackgroundColor(view, position % 2 == 0 ? Color.LTGRAY : Color.WHITE);
    }
}
