package cn.sskbskdrin.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends ViewGroup {
    private static final String TAG = "FlowLayout";

    protected List<List<View>> mAllViews = new ArrayList<>();
    protected List<Integer> mLineHeight = new ArrayList<>();
    protected List<Integer> mLineWidth = new ArrayList<>();
    private int mHorizontalSpace;
    private int mVerticalSpace;
    private List<View> lineViews = new ArrayList<>();

    private BaseAdapter mAdapter;

    private DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            changeAdapter();
        }
    };

    public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mHorizontalSpace = dip2px(5);
        mVerticalSpace = dip2px(5);
    }

    public void setHorizontalSpace(int space) {
        mHorizontalSpace = space;
        requestLayout();
    }

    public void setVerticalSpace(int space) {
        mVerticalSpace = space;
        requestLayout();
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        // wrap_content
        int width = 0;
        int height = 0;

        int lineWidth = 0;
        int lineHeight = 0;

        int cCount = getChildCount();

        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                if (i == cCount - 1) {
                    width = Math.max(lineWidth, width);
                    height += lineHeight;
                }
                continue;
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin + mHorizontalSpace;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin + mVerticalSpace;

            if (lineWidth + childWidth > sizeWidth - getPaddingLeft() - getPaddingRight()) {
                width = Math.max(width, lineWidth);
                lineWidth = childWidth;
                height += lineHeight;
                lineHeight = childHeight;
            } else {
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }
            if (i == cCount - 1) {
                width = Math.max(lineWidth, width);
                height += lineHeight;
            }
        }
        setMeasuredDimension(modeWidth == MeasureSpec.EXACTLY ? sizeWidth :
            width + getPaddingLeft() + getPaddingRight(), modeHeight == MeasureSpec.EXACTLY ? sizeHeight :
            height + getPaddingTop() + getPaddingBottom());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineHeight.clear();
        mLineWidth.clear();
        lineViews.clear();

        int width = getWidth();

        int lineWidth = getPaddingLeft() + getPaddingRight();
        int lineHeight = 0;

        int cCount = getChildCount();

        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            if (childWidth + lineWidth + lp.leftMargin + lp.rightMargin + mHorizontalSpace > width) {
                mLineHeight.add(lineHeight);
                mAllViews.add(lineViews);
                mLineWidth.add(lineWidth);

                lineWidth = getPaddingLeft() + getPaddingRight();
                lineHeight = childHeight + lp.topMargin + lp.bottomMargin + mVerticalSpace;
                lineViews = new ArrayList<>();
            }
            lineWidth += childWidth + lp.leftMargin + lp.rightMargin + mHorizontalSpace;
            lineHeight = Math.max(lineHeight, childHeight + lp.topMargin + lp.bottomMargin + mVerticalSpace);
            lineViews.add(child);
        }
        mLineHeight.add(lineHeight);
        mLineWidth.add(lineWidth);
        mAllViews.add(lineViews);

        int left;
        int top = getPaddingTop();

        int lineNum = mAllViews.size();

        for (int i = 0; i < lineNum; i++) {
            lineViews = mAllViews.get(i);
            lineHeight = mLineHeight.get(i);
            left = getPaddingLeft();

            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }

                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

                int lc = left + lp.leftMargin;
                int tc = top + lp.topMargin;
                int rc = lc + child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();

                child.layout(lc, tc, rc, bc);

                left += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin + mHorizontalSpace;
            }
            top += lineHeight;
        }
    }

    public void notifyDataChange() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setAdapter(BaseAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }
        mAdapter = adapter;
        mAdapter.registerDataSetObserver(mDataSetObserver);
        changeAdapter();
    }

    private void changeAdapter() {
        removeAllViews();
        BaseAdapter adapter = mAdapter;
        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                View view = adapter.getView(i, null, this);
                addView(view);
            }
        }
        requestLayout();
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    private int dip2px(float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue,
            getResources().getDisplayMetrics());
    }

    public static LabelAdapter generateLabelAdapter(List<?> list, LabelAdapter.OnItemClickListener listener) {
        return new LabelAdapter(list, listener);
    }

    public static class LabelAdapter extends BaseAdapter {

        public static class Option {
            int padding = 10;
            int textSize = 48;
        }

        Option option = new Option();

        public interface NameString {
            String name();
        }

        public interface OnItemClickListener {
            void onClickItem(int position, Object item, View view);
        }

        List<?> list;
        OnItemClickListener listener;
        private OnClickListener clickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    Integer pos = (Integer) v.getTag();
                    listener.onClickItem(pos, list.get(pos), v);
                }
            }
        };

        LabelAdapter(List<?> list, LabelAdapter.OnItemClickListener listener) {
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
            view.setText(obj instanceof NameString ? ((NameString) obj).name() : obj.toString());
            view.setOnClickListener(clickListener);
            view.setTag(position);
            return view;
        }
    }
}
