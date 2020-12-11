package cn.sskbskdrin.widget;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by keayuan on 2020/12/11.
 *
 * @author keayuan
 */
public class FlowLabelAdapter extends BaseAdapter {

    public static class Option {
        int padding = 10;
        int textSize = 16;
        int bgRadius = 15;
    }

    public interface NameString {
        String name();
    }

    public interface OnItemClickListener {
        void onClickItem(int position, Object item, View view);
    }

    private Option option = new Option();
    private List<?> list;
    private OnItemClickListener listener;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (listener != null) {
                Integer pos = (Integer) v.getTag();
                listener.onClickItem(pos, list.get(pos), v);
            }
        }
    };

    public FlowLabelAdapter(List<?> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list == null ? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Object obj = getItem(position);
        TextView view = (TextView) convertView;
        if (view == null) {
            view = new TextView(parent.getContext());
            view.setPadding(option.padding, option.padding, option.padding, option.padding);
            view.setTextSize(option.textSize);
        }
        view.setText(obj instanceof FlowLayout.LabelAdapter.NameString ?
            ((FlowLayout.LabelAdapter.NameString) obj).name() : obj
            .toString());
        view.setOnClickListener(clickListener);
        RoundRectShape shape = new RoundRectShape(new float[]{option.bgRadius, option.bgRadius, option.bgRadius,
            option.bgRadius, option.bgRadius, option.bgRadius, option.bgRadius, option.bgRadius}, null,
            new float[]{option.bgRadius - 6, option.bgRadius - 6, option.bgRadius - 6, option.bgRadius - 6,
                option.bgRadius - 6, option.bgRadius - 6, option.bgRadius - 6, option.bgRadius - 6});
        ShapeDrawable drawable = new ShapeDrawable(shape);
        drawable.getPaint().setColor(Color.RED);
        drawable.getPaint().setStyle(Paint.Style.STROKE);
        drawable.getPaint().setStrokeWidth(4);
        view.setBackgroundDrawable(drawable);
        view.setTag(position);
        return view;
    }
}
