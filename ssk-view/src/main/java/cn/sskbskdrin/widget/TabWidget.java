package cn.sskbskdrin.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ex-keayuan001
 */
public class TabWidget extends LinearLayout implements View.OnClickListener {
    private static final String TAG = "TabWidget";
    private static final int NAME_ID = android.R.id.text1;
    private static final int TIP_ID = android.R.id.text2;
    private static final int IMAGE_ID = android.R.id.icon;

    private LinearLayout mContentLayout;

    private Adapter mAdapter;

    private int mCurrentTab = 0;
    private long mClickTime;

    private int[] mFocusColor = new int[]{0xFF, 0xFF, 0, 0};
    private int[] mNormalColor = new int[]{0xFF, 0x99, 0x99, 0x99};

    private OnDoubleClickListener onDoubleClickListener;
    private Set<OnTabChangeListener> mOnTabChangeListeners;
    private IndicatorView mIndicatorView;

    public TabWidget(Context context) {
        this(context, null);
    }

    public TabWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TabWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);

        mIndicatorView = new IndicatorView(getContext());
        addView(mIndicatorView, new LayoutParams(LayoutParams.MATCH_PARENT, dp2px(2)));

        mContentLayout = new LinearLayout(getContext());
        mContentLayout.setOrientation(HORIZONTAL);
        addView(mContentLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        mAdapter = new Adapter();

        mOnTabChangeListeners = new HashSet<>();
    }

    public void setIndicatorPosition(boolean isTop) {
        if (isTop) {
            if (getChildAt(0) != mIndicatorView) {
                removeView(mIndicatorView);
                addView(mIndicatorView, 0);
            }
        } else {
            if (getChildAt(0) == mIndicatorView) {
                removeView(mIndicatorView);
                addView(mIndicatorView);
            }
        }
    }

    public void setIndicatorWidth(int width) {
        mIndicatorView.setIndicatorWidth(width);
    }

    public void setLineColor(int color) {
        mIndicatorView.setBackgroundColor(color);
    }

    public void setIndicatorColor(int color) {
        mIndicatorView.setIndicatorColor(color);
    }

    public void setIndicatorHeight(int height) {
        mIndicatorView.getLayoutParams().height = height;
        requestLayout();
    }

    public void setTextColor(int focus, int normal) {
        mFocusColor[1] = Color.red(focus);
        mFocusColor[2] = Color.green(focus);
        mFocusColor[3] = Color.blue(focus);
        mNormalColor[1] = Color.red(normal);
        mNormalColor[2] = Color.green(normal);
        mNormalColor[3] = Color.blue(normal);
        notifyDataSetChanged();
    }

    public void setSelectTab(int select, boolean callback) {
        if (select < mAdapter.getCount()) {
            tabChange(select, callback);
        }
    }

    public int getCurrentTab() {
        return mCurrentTab;
    }

    /**
     * tab改变偏移
     *
     * @param offset 偏移量，(1，1) offset<0向前一个变，offset>0 向后一个变
     */
    void offsetTab(int position, float offset) {
        mIndicatorView.onTabScrolled(position, offset);
        if (offset == 0) {
            tabChange(position, true);
        }
        int level = (int) (Math.abs(offset) * 10000);
        updateItemLevel(mContentLayout.getChildAt(position), level);
        updateItemLevel(mContentLayout.getChildAt(offset > 0 ? position + 1 : position - 1), 10000 - level);
    }

    private int getColor(float off) {
        if (off > 0) {
            int r2 = (int) (mFocusColor[1] + (mNormalColor[1] - mFocusColor[1]) * off);
            int g2 = (int) (mFocusColor[2] + (mNormalColor[2] - mFocusColor[2]) * off);
            int b2 = (int) (mFocusColor[3] + (mNormalColor[3] - mFocusColor[3]) * off);
            return Color.rgb(r2, g2, b2);
        } else {
            return Color.rgb(mFocusColor[1], mFocusColor[2], mFocusColor[3]);
        }
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag(mAdapter.layoutId);
        long time = System.currentTimeMillis();
        if (mCurrentTab != position) {
            tabChange(position, true);
        } else {
            if (time - mClickTime < ViewConfiguration.getDoubleTapTimeout()) {
                if (onDoubleClickListener != null) {
                    onDoubleClickListener.onDoubleClick(position);
                }
            }
        }
        mClickTime = time;
    }

    private void tabChange(int position, boolean callback) {
        if (position != mCurrentTab && position < mAdapter.getCount()) {
            update(position);

            int oldPosition = mCurrentTab;
            mCurrentTab = position;
            if (callback) {
                for (OnTabChangeListener listener : mOnTabChangeListeners) {
                    listener.onTabChange(oldPosition, position);
                }
            }
        }
    }

    private void update(int tab) {
        mIndicatorView.onTabScrolled(tab, 0);
        for (int i = 0; i < mContentLayout.getChildCount(); i++) {
            View content = mContentLayout.getChildAt(i);
            content.setSelected(tab == i);
            updateItemLevel(content, tab == i ? 0 : 10000);
        }
    }

    private void updateItemLevel(View parent, int level) {
        if (parent != null) {
            TextView textView = (TextView) parent.findViewById(NAME_ID);
            if (textView != null) {
                ColorStateList state = textView.getTextColors();
                if (state != null && state instanceof ColorState) {
                    ((ColorState) state).setLevel(level);
                    textView.setTextColor(state);
                }
            }
            ImageView imageView = (ImageView) parent.findViewById(IMAGE_ID);
            if (imageView != null) {
                imageView.setImageLevel(level);
            }
        }
    }

    public void setOnDoubleClickListener(OnDoubleClickListener listener) {
        this.onDoubleClickListener = listener;
    }

    public void addOnTabChangeListener(OnTabChangeListener listener) {
        if (!mOnTabChangeListeners.contains(listener)) {
            mOnTabChangeListeners.add(listener);
        }
    }

    public void setTabs(List<Tab> list) {
        mAdapter.setList(list);
        notifyDataSetChanged();
    }

    public void addTab(Tab tab) {
        if (mAdapter.mList == null) {
            mAdapter.mList = new ArrayList<>();
        }
        mAdapter.mList.add(tab);
        notifyDataSetChanged();
    }

    public void addTab(String name, int normalResId, int selectResId, String tip) {
        addTab(new Tab(name, normalResId, selectResId, tip));
    }

    public void setAdapter(ListAdapter adapter) {
        if (adapter != mAdapter.adapter) {
            mContentLayout.removeAllViews();
        }
        mAdapter.adapter = adapter;
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View content = null;
            if (mContentLayout.getChildCount() > i) {
                content = mContentLayout.getChildAt(i);
                mContentLayout.removeView(content);
            }
            content = mAdapter.getView(i, content, mContentLayout);

            if (content != null) {
                LayoutParams lp = (LayoutParams) content.getLayoutParams();
                if (lp == null) {
                    lp = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
                }
                lp.width = 0;
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                lp.weight = 1;
                content.setOnClickListener(this);
                content.setTag(mAdapter.layoutId, i);
                content.setSelected(false);
                mContentLayout.addView(content, i, lp);
            }
        }
        while (mContentLayout.getChildCount() > mAdapter.getCount()) {
            mContentLayout.removeViewAt(mContentLayout.getChildCount() - 1);
        }
        if (mContentLayout.getChildCount() > mCurrentTab) {
            update(mCurrentTab);
        }
    }

    public void setItemLayout(int layoutId) {
        mAdapter.layoutId = layoutId;
    }

    public interface OnDoubleClickListener {
        /**
         * 双击时回调
         *
         * @param position 双击的位置
         */
        void onDoubleClick(int position);
    }

    public interface OnTabChangeListener {
        /**
         * tab 改变
         *
         * @param oldPosition 改变之前的位置
         * @param newPosition 改变之后的位置
         */
        void onTabChange(int oldPosition, int newPosition);
    }

    public static class Tab {
        public String text;
        int imageNormal;
        int imageSelect;
        public String tip;

        public Tab(String name, int normalResId, int selectResId, String tip) {
            this.text = name;
            this.tip = tip;
            imageNormal = normalResId;
            imageSelect = selectResId;
        }
    }

    private int dp2px(float dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    private class Adapter extends BaseAdapter {
        ListAdapter adapter;
        List<Tab> mList;
        int layoutId;

        public void setList(List<Tab> list) {
            mList = list;
        }

        @Override
        public int getCount() {
            if (adapter != null) {
                return adapter.getCount();
            }
            return mList == null ? 0 : mList.size();
        }

        @Override
        public Object getItem(int position) {
            if (adapter != null) {
                return adapter.getItem(position);
            }
            return mList == null ? null : mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            if (adapter != null) {
                return adapter.getItemId(position);
            }
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (adapter != null) {
                convertView = adapter.getView(position, convertView, parent);
            } else {
                if (convertView == null) {
                    convertView = inflate(getContext(), layoutId, null);
                }

                Tab tab = (Tab) getItem(position);
                TextView textView = (TextView) convertView.findViewById(NAME_ID);
                ImageView imageView = (ImageView) convertView.findViewById(IMAGE_ID);
                TextView tipView = (TextView) convertView.findViewById(TIP_ID);

                if (textView != null) {
                    textView.setText(tab.text);
                    textView.setTextColor(new ColorState());
                }
                if (imageView != null) {
                    Drawable normal = getResources().getDrawable(tab.imageNormal).mutate();
                    Drawable select = getResources().getDrawable(tab.imageSelect).mutate();
                    imageView.setImageDrawable(new StateDrawable(normal, select));
                }
                if (tipView != null) {
                    if (!TextUtils.isEmpty(tab.tip)) {
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                            tipView.setBackground(new RedCircle());
                        } else {
                            tipView.setBackgroundDrawable(new RedCircle());
                        }
                        ViewGroup.LayoutParams lp = tipView.getLayoutParams();
                        if ("0".equals(tab.tip)) {
                            lp.width = dp2px(8);
                            lp.height = dp2px(8);
                            tipView.setText("");
                        } else {
                            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                            lp.height = dp2px(16);
                            tipView.setText(tab.tip);
                        }
                    }
                    tipView.setVisibility(TextUtils.isEmpty(tab.tip) ? GONE : VISIBLE);
                }
            }
            return convertView;
        }
    }

    private static class RedCircle extends Drawable {

        private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private int radius = 0;
        private Rect rect;

        RedCircle() {
            paint.setColor(Color.RED);
            rect = new Rect();
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            radius = bounds.height() / 2;
            rect.set(radius, bounds.top, bounds.right - radius, bounds.bottom);
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawCircle(radius, radius, radius, paint);
            canvas.drawRect(rect, paint);
            canvas.drawCircle(getBounds().right - radius, radius, radius, paint);
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSPARENT;
        }
    }

    private class StateDrawable extends Drawable {
        Drawable normal;
        Drawable select;

        Paint normalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint selectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        boolean mState;

        private StateDrawable(Drawable normal, Drawable select) {
            this.normal = normal;
            this.select = select;
        }

        @Override
        public void draw(Canvas canvas) {
            if (mState) {
                if (select != null) {
                    select.setAlpha(255);
                    select.draw(canvas);
                }
            } else {
                if (normal != null) {
                    normal.draw(canvas);
                }
                if (getLevel() < 5000) {
                    if (select != null) {
                        select.draw(canvas);
                    }
                }
            }
        }

        @Override
        public void setBounds(Rect bounds) {
            if (normal != null) {
                normal.setBounds(bounds);
            }
            if (select != null) {
                select.setBounds(bounds);
            }
            super.setBounds(bounds);
        }

        @Override
        public void setBounds(int left, int top, int right, int bottom) {
            if (normal != null) {
                normal.setBounds(left, top, right, bottom);
            }
            if (select != null) {
                select.setBounds(left, top, right, bottom);
            }
            super.setBounds(left, top, right, bottom);
        }

        @Override
        public int getIntrinsicWidth() {
            if (normal != null) {
                return normal.getIntrinsicWidth();
            }
            return super.getIntrinsicWidth();
        }

        @Override
        public int getIntrinsicHeight() {
            if (normal != null) {
                return normal.getIntrinsicHeight();
            }
            return super.getIntrinsicHeight();
        }

        @Override
        public void setAlpha(int alpha) {
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
        }

        @Override
        public boolean isStateful() {
            return true;
        }

        @Override
        protected boolean onStateChange(int[] state) {
            if (state != null) {
                for (int s : state) {
                    mState = s == android.R.attr.state_pressed;
                    if (mState) {
                        break;
                    }
                }
                invalidateSelf();
                return mState;
            }
            return false;
        }

        @Override
        protected boolean onLevelChange(int level) {
            normal.setColorFilter(new PorterDuffColorFilter(getColor(), PorterDuff.Mode.SRC_IN));
            if (level <= 5000) {
                int alpha = (int) ((5000 - level) / 5000f * 255);
                select.setAlpha(alpha);
                normal.setAlpha(255 - alpha);
            } else {
                select.setAlpha(0);
                normal.setAlpha(255);
            }
            invalidateSelf();
            return true;
        }

        private int getColor() {
            float off = (getLevel() - 5000) / 5000f;
            return TabWidget.this.getColor(off);
        }

        @Override
        public void invalidateSelf() {
            if (normal != null) {
                normal.invalidateSelf();
            }
            if (select != null) {
                select.invalidateSelf();
            }
            super.invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    private class ColorState extends ColorStateList {

        int mLevel = 0;

        /**
         * Creates a ColorStateList that returns the specified mapping from
         * states to colors.
         */
        public ColorState() {
            super(new int[][]{new int[]{android.R.attr.state_pressed}, new int[0]},
                new int[]{Color.rgb(mFocusColor[1], mFocusColor[2], mFocusColor[3]), Color.rgb(mNormalColor[1],
                    mNormalColor[2], mNormalColor[3])});
        }

        public void setLevel(int level) {
            mLevel = level;
        }

        @Override
        public int getColorForState(int[] stateSet, int defaultColor) {
            if (stateSet != null) {
                for (int s : stateSet) {
                    if (s == android.R.attr.state_pressed) {
                        return Color.rgb(mFocusColor[1], mFocusColor[2], mFocusColor[3]);
                    }
                }
            }
            return TabWidget.this.getColor(mLevel / 10000f);
        }

        @Override
        public boolean isStateful() {
            return true;
        }
    }

    public class IndicatorView extends View {

        private Paint mPaint;
        private int mStart = 0;
        private int mWidth = -1;
        private int item;
        private int mPosition;

        public IndicatorView(Context context) {
            super(context);
            init();
        }

        private void init() {
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }

        public void setIndicatorColor(int color) {
            mPaint.setColor(color);
            invalidate();
        }

        public void setIndicatorWidth(int width) {
            mWidth = width;
            invalidate();
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            item = getMeasuredWidth() / mAdapter.getCount();
            onTabScrolled(mPosition, 0);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (mWidth < 0) {
                if (mAdapter.getCount() > 0) {
                    mWidth = getMeasuredWidth() / mAdapter.getCount();
                }
            }
            canvas.drawRect(mStart, 0, mStart + mWidth, getMeasuredHeight(), mPaint);
        }

        public void onTabScrolled(int position, float offset) {
            if (mAdapter != null && mAdapter.getCount() > 0) {
                mPosition = position;
                mStart = (int) ((item - mWidth) / 2 + (position + offset) * item);
                invalidate();
            }
        }
    }
}
