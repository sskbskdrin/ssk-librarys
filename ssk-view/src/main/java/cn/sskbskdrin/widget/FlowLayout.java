package cn.sskbskdrin.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends ViewGroup {
    private static final String TAG = "FlowLayout";

    protected List<List<View>> mAllViews = new ArrayList<>();
    protected List<Integer> mLineHeight = new ArrayList<>();
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
        if (modeWidth == MeasureSpec.UNSPECIFIED) {
            sizeWidth = Integer.MAX_VALUE;
        }

        // wrap_content
        int width = 0;
        int height = 0;

        int lineWidth = -mHorizontalSpace;
        int lineHeight = 0;

        int cCount = getChildCount();

        int contentWidth = sizeWidth - getPaddingLeft() - getPaddingRight();

        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            if (lineWidth + childWidth + mHorizontalSpace <= contentWidth) {
                lineWidth += childWidth + mHorizontalSpace;
                lineHeight = Math.max(lineHeight, childHeight);
            } else {
                width = Math.max(width, lineWidth);
                lineWidth = childWidth;
                height += lineHeight + mVerticalSpace;
                lineHeight = childHeight;
            }
            if (i == cCount - 1) {
                width = Math.max(lineWidth, width);
                height += lineHeight + mVerticalSpace;
            }
        }
        if (height > 0) {
            height -= mVerticalSpace;
        }
        setMeasuredDimension(modeWidth == MeasureSpec.EXACTLY ? sizeWidth :
            width + getPaddingLeft() + getPaddingRight(), modeHeight == MeasureSpec.EXACTLY ? sizeHeight :
            height + getPaddingTop() + getPaddingBottom());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineHeight.clear();
        lineViews.clear();

        int width = getWidth();

        int paddingH = getPaddingLeft() + getPaddingRight();
        int lineWidth = 0;
        int lineHeight = 0;

        int cCount = getChildCount();

        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            if (childWidth + paddingH + lineWidth > width) {
                mLineHeight.add(lineHeight);
                mAllViews.add(lineViews);

                lineWidth = 0;
                lineHeight = childHeight + mVerticalSpace;
                lineViews = new ArrayList<>();
            }
            lineWidth += childWidth + mHorizontalSpace;
            lineHeight = Math.max(lineHeight, childHeight + mVerticalSpace);
            lineViews.add(child);
        }
        mLineHeight.add(lineHeight);
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

                child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());

                left += child.getMeasuredWidth() + mHorizontalSpace;
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
    
	public BaseAdapter getAdapter() {
        return mAdapter;
    }
	
    private void changeAdapter() {
        BaseAdapter adapter = mAdapter;
        if (adapter == null) {
            removeAllViews();
        } else {
            if (adapter.getViewTypeCount() > 1) {
                removeAllViews();
            }
            int count = adapter.getCount();
            for (int i = 0; i < count; i++) {
                View view = adapter.getView(i, getChildAt(i), this);
                if (view.getParent() == null) {
                    addView(view);
                }
            }
            if (getChildCount() > count) {
                removeViews(count, getChildCount());
            }
        }
        requestLayout();
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new LayoutParams(p);
    }

    private int dip2px(float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue,
            getResources().getDisplayMetrics());
    }
}
