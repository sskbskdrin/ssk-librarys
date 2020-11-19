package cn.sskbskdrin.lib.demo.simple;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.sskbskdrin.base.IBaseAdapter;
import cn.sskbskdrin.lib.demo.R;

public class SimpleAdapter<T> extends IBaseAdapter<T> {
    private int itemHeight;
    private boolean singleColor;

    public SimpleAdapter(List<T> list) {
        super(list, android.R.layout.simple_list_item_1);
    }

    public SimpleAdapter(T[] array) {
        this(array, 0);
    }

    public SimpleAdapter(T[] array, int height) {
        this(array, height, false);
    }

    public SimpleAdapter(T[] array, int height, boolean singleColor) {
        super(null, android.R.layout.simple_list_item_1);
        itemHeight = height;
        this.singleColor = singleColor;
        List<T> list = new ArrayList<>();
        if (array != null) {
            Collections.addAll(list, array);
        }
        updateList(list);
    }

    @Override
    protected void convert(View view, int position, T t) {
        if (itemHeight > 0) {
            if (view.getLayoutParams() == null) {
                view.setLayoutParams(new ViewGroup.LayoutParams(-1, itemHeight));
            }
            view.getLayoutParams().height = itemHeight;
        }
        setText((TextView) view, t.toString());
        if (!singleColor) setBackgroundColor(view, position % 2 == 0 ? Color.LTGRAY : Color.WHITE);
        view.setBackgroundResource(R.drawable.rect_white_bg);
    }
}
