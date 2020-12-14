package cn.sskbskdrin.widget;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by keayuan on 2020/12/11.
 *
 * @author keayuan
 */
public class FlowLabelAdapter extends BaseAdapter {

    public static class Option {
        private int paddingLeft = 4;
        private int paddingRight = 4;
        private int paddingTop = 4;
        private int paddingBottom = 4;
        private int textSize = 16;
        private int bgRadius = 5;
        private int color = Color.GRAY;
        private float strokeWidth = 1;
        private boolean selectMode = false;

        public Option setTextSize(int textSizeSp) {
            this.textSize = textSizeSp;
            return this;
        }

        public Option setStrokeWidth(float strokeWidthDP) {
            this.strokeWidth = strokeWidthDP;
            return this;
        }

        public Option setColor(int color) {
            this.color = color;
            return this;
        }

        public Option setBgRadius(int bgRadiusDP) {
            this.bgRadius = bgRadiusDP;
            return this;
        }

        public Option setSelectMode(boolean selectMode) {
            this.selectMode = selectMode;
            return this;
        }

        public Option setPadding(int paddingDP) {
            return setPadding(paddingDP, paddingDP);
        }

        public Option setPadding(int horizontalDP, int verticalDP) {
            this.paddingLeft = this.paddingRight = horizontalDP;
            this.paddingTop = this.paddingBottom = verticalDP;
            return this;
        }

        public Option setPaddingDP(int left, int top, int right, int bottom) {
            this.paddingLeft = left;
            this.paddingTop = top;
            this.paddingRight = right;
            this.paddingBottom = bottom;
            return this;
        }
    }

    public static Option createOption() {
        return new Option();
    }

    public interface NameString {
        String name();
    }

    public interface OnItemClickListener {
        void onClickItem(int position, Object item, View view);
    }

    private Option option = new Option();
    private boolean[] selected;
    private List<?> list;
    private OnItemClickListener listener;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Integer pos = (Integer) v.getTag();
            if (listener != null) {
                listener.onClickItem(pos, list.get(pos), v);
            }
            if (option.selectMode) {
                selected[pos] = !selected[pos];
                getView(pos, v, null);
            }
            v.postInvalidate();
        }
    };

    public FlowLabelAdapter(List<?> list, OnItemClickListener listener) {
        this.list = list;
        selected = new boolean[list.size()];
        this.listener = listener;
    }

    public FlowLabelAdapter(List<?> list, Option option, OnItemClickListener listener) {
        this.list = list;
        if (list == null) {
            list = new ArrayList<>();
        }
        selected = new boolean[list.size()];
        this.listener = listener;
        if (option != null) {
            this.option = option;
        }
    }

    public List<?> getSelected() {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < getCount(); i++) {
            if (selected[i]) {
                list.add(getItem(i));
            }
        }
        return list;
    }

    @Override
    public void notifyDataSetChanged() {
        if (getCount() > selected.length) {
            selected = Arrays.copyOf(selected, getCount());
        }
        super.notifyDataSetChanged();
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

    private int dp2px(float dp, Resources resources) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Object obj = getItem(position);
        TextView view = (TextView) convertView;
        if (view == null) {
            view = new TextView(parent.getContext());
            int padding = 4;
            int lp = dp2px(option.paddingLeft > 0 ? option.paddingLeft : padding, parent.getResources());
            int tp = dp2px(option.paddingTop > 0 ? option.paddingTop : padding, parent.getResources());
            int rp = dp2px(option.paddingRight > 0 ? option.paddingRight : padding, parent.getResources());
            int bp = dp2px(option.paddingBottom > 0 ? option.paddingBottom : padding, parent.getResources());
            view.setPadding(lp, tp, rp, bp);
            view.setTextSize(option.textSize);
        }
        view.setText(obj instanceof FlowLabelAdapter.NameString ? ((FlowLabelAdapter.NameString) obj).name() :
            obj.toString());
        view.setOnClickListener(clickListener);

        ShapeDrawable drawable = new ShapeDrawable(new RoundRectShape(dp2px(option.bgRadius, parent.getResources()),
            selected[position] ? 0 : dp2px(option.strokeWidth, parent
            .getResources())));
        drawable.getPaint().setColor(option.color);
        view.setTextColor(selected[position] ? Color.WHITE : option.color);
        view.setBackgroundDrawable(drawable);
        view.setTag(position);
        return view;
    }

    private static class RoundRectShape extends RectShape {
        private Path mPath;
        private float mRadius;
        private float strokeWidth;
        private RectF inRect;

        public RoundRectShape(float radius, float strokeWidth) {
            mPath = new Path();
            mRadius = radius;
            this.strokeWidth = strokeWidth;
        }

        @Override
        protected void onResize(float width, float height) {
            super.onResize(width, height);
            inRect = null;
        }

        @Override
        public void draw(Canvas canvas, Paint paint) {
            mPath.reset();
            mPath.addRoundRect(rect(), mRadius, mRadius, Path.Direction.CW);
            if (strokeWidth > 0) {
                if (inRect == null) {
                    inRect = new RectF(strokeWidth, strokeWidth, getWidth() - strokeWidth, getHeight() - strokeWidth);
                }
                if (mRadius > strokeWidth)
                    mPath.addRoundRect(inRect, mRadius - strokeWidth, mRadius - strokeWidth, Path.Direction.CCW);
                else mPath.addRect(inRect, Path.Direction.CCW);
            }
            canvas.drawPath(mPath, paint);
        }

        @Override
        public RoundRectShape clone() throws CloneNotSupportedException {
            final RoundRectShape shape = (RoundRectShape) super.clone();
            shape.mRadius = mRadius;
            shape.strokeWidth = strokeWidth;
            shape.mPath = new Path(mPath);
            return shape;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }
            RoundRectShape that = (RoundRectShape) o;
            return mPath.equals(that.mPath) && strokeWidth == that.strokeWidth && mRadius == that.mRadius;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(new Object[]{super.hashCode(), mPath, strokeWidth, mRadius});
        }
    }
}
