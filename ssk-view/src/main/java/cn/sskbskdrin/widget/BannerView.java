package cn.sskbskdrin.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ListAdapter;
import android.widget.Scroller;

import java.util.HashMap;

public class BannerView extends ViewGroup {
    private static final String TAG = "BannerView";

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private int orientation = HORIZONTAL;
    private boolean isCycle = true;
    private ListAdapter adapter;
    private int adapterCount;
    private final HashMap<Integer, View> cache = new HashMap<>(5);
    private final ScrollChecker mScrollChecker = new ScrollChecker();
    private int mDuration = 1000;
    private int nextTime = 3000;
    private boolean isAutoScroll = true;

    private final Rect contentRect = new Rect();
    private int contentWidth;
    private OnItemClickListener onItemClickListener;
    private OnScrollListener onScrollListener;

    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        contentWidth = r - l - getPaddingLeft() - getPaddingRight();
        if (changed) {
            contentRect.left = getPaddingLeft();
            contentRect.top = getPaddingTop();
            contentRect.right = getWidth() - getPaddingRight();
            contentRect.bottom = getHeight() - getPaddingBottom();
            post(load);
        }
    }

    public void setAdapter(ListAdapter adapter) {
        this.adapter = adapter;
        if (adapter == null) {
            return;
        }
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                post(load);
            }

            @Override
            public void onInvalidated() {
            }
        });
    }

    private Runnable load = new Runnable() {
        @Override
        public void run() {
            scrollTo(0, 0);
            adapterCount = adapter != null ? adapter.getCount() : 0;
            removeAllViews();
            cache.clear();
            show(false);
            start();
        }
    };

    private GestureDetector gestureDetector = new GestureDetector(getContext(),
        new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            mScrollChecker.destroy();
            stop();
            return super.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            repair((int) velocityX);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            move((int) distanceX, 0, true);
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            int index = getPosition((int) (getScrollX() + e.getX()), true);
            int pos = getPosition((int) (getScrollX() + e.getX()), false);
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(BannerView.this, getView(index), pos);
            }
            return super.onSingleTapUp(e);
        }
    });

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            repair(0);
        }
        gestureDetector.onTouchEvent(ev);
        if (action == MotionEvent.ACTION_DOWN) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldL, int oldT) {
        int oldPosition = oldL / contentWidth;
        int newPosition = l / contentWidth;
        contentRect.offset((newPosition - oldPosition) * contentWidth, 0);
        if (onScrollListener != null) {
            int scrollX = getScrollX();
            int position = getPosition(scrollX, true);
            float offset = scrollX - getOffset(position);
            onScrollListener.onScroll(this, getPosition(getScrollX(), false), offset / contentWidth);
        }
        super.onScrollChanged(l, t, oldL, oldT);
    }

    private void repair(int velocityX) {
        int pos = getPosition(getScrollX(), true);
        if (Math.abs(velocityX) > 500) {
            int sign = velocityX > 0 ? 0 : 1;
            int offset = getOffset(pos + sign) - getScrollX();
            mScrollChecker.tryToScrollTo(offset, 0, Math.abs(offset) * mDuration / contentWidth);
        } else {
            int scrollX = getScrollX();
            int nextOffset = getOffset(pos + 1);
            int offset;
            if (nextOffset - scrollX > contentWidth / 2) {
                offset = getOffset(pos) - scrollX;
            } else {
                offset = nextOffset - scrollX;
            }
            mScrollChecker.tryToScrollTo(offset, 0, Math.abs(offset) * mDuration / contentWidth);
        }
        start();
    }

    private void move(int deltaX, int deltaY, boolean isUnderTouch) {
        if (deltaX == 0 && deltaY == 0) {
            return;
        }
        // 大于0表示向左移动
        if (deltaX > 0) {
            if (!has(getScrollX() / contentWidth + 1)) {
                show(true);
            }
        } else {
            int scrollX = getScrollX();
            if (scrollX < 0) {
                scrollX -= contentWidth;
            }
            if (!has(scrollX / contentWidth)) {
                show(false);
            }
        }
        if (!isCycle) {
            if (getScrollX() + deltaX < 0) {
                scrollBy(getScrollX(), 0);
                invalidate();
                stop();
                return;
            } else {
                int count = adapterCount;
                if (count > 0 && getScrollX() + deltaX > (count - 1) * contentWidth) {
                    scrollBy((count - 1) * contentWidth - getScrollX(), 0);
                    stop();
                    invalidate();
                    return;
                }
            }
        }
        scrollBy(deltaX, deltaY);
        invalidate();
    }

    private void show(boolean after) {
        int w = MeasureSpec.makeMeasureSpec(contentRect.width(), MeasureSpec.EXACTLY);
        int h = MeasureSpec.makeMeasureSpec(contentRect.height(), MeasureSpec.EXACTLY);
        int scrollX = getScrollX();
        if (scrollX < 0) {
            scrollX -= contentWidth;
        }
        int index = scrollX / contentWidth + (after ? 1 : 0);
        View view = getView(index);
        if (view != null) {
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
            addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            view.measure(w, h);
            view.layout(index * contentWidth, contentRect.top, (index + 1) * contentWidth, contentRect.bottom);
        }
    }

    private boolean has(int index) {
        return cache.containsKey(index);
    }

    private View getView(int index) {
        if (cache.get(index) == null && adapter != null) {
            int count = adapterCount;
            View tempView = cache.remove(index + 3);
            if (tempView == null) {
                tempView = cache.remove(index - 3);
            }
            if (tempView != null) {
                removeView(tempView);
            }
            if (index >= count || index < 0) {
                if (!isCycle) {
                    return null;
                }
            }
            cache.put(index, adapter.getView((index + Math.abs(index * count)) % count, tempView, this));
        }
        return cache.get(index);
    }

    /**
     * 计算某个位置的左边坐标
     *
     * @param position item绝对位置
     * @return 计算出的左边偏移量
     */
    private int getOffset(int position) {
        return contentWidth * position;
    }

    /**
     * 获取某个偏移量的item项
     *
     * @param offset 偏移量
     * @param abs    是否是绝对位置
     * @return 计算出来的位置坐标
     */
    private int getPosition(int offset, boolean abs) {
        int position;
        int sign = offset >= 0 ? 1 : -1;
        if (offset >= 0) {
            position = offset / contentWidth;
        } else {
            position = (offset - contentWidth) / contentWidth;
        }
        if (!abs) {
            int count = adapterCount;
            position = count > 0 ? (position + (position * sign * count)) % count : 0;
        }
        return position;
    }

    public void setNextTime(int ms) {
        nextTime = ms;
    }

    public void setDuration(int ms) {
        mDuration = ms;
    }

    public void setInterpolator(Interpolator interpolator) {
        mScrollChecker.setInterpolator(interpolator);
    }

    public void setCycle(boolean cycle) {
        this.isCycle = cycle;
    }

    public void setOnItemClickListener(BannerView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnScrollListener(BannerView.OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public void setAutoScroll(boolean auto) {
        isAutoScroll = auto;
        if (isAutoScroll) {
            start();
        } else {
            stop();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    private Runnable autoScroll = new Runnable() {
        @Override
        public void run() {
            mScrollChecker.tryToScrollTo(contentWidth, 0, mDuration);
            postDelayed(this, nextTime);
        }
    };

    private void start() {
        stop();
        if (isAutoScroll) {
            postDelayed(autoScroll, nextTime);
        }
    }

    private void stop() {
        removeCallbacks(autoScroll);
    }

    private class ScrollChecker implements Runnable {

        private int mLastFlingX;
        private int mLastFlingY;
        private Scroller mScroller;

        public ScrollChecker() {
            mScroller = new Scroller(getContext());
        }

        public void setInterpolator(Interpolator interpolator) {
            mScroller = new Scroller(getContext(), interpolator);
        }

        public void run() {
            if (mScroller.computeScrollOffset()) {
                int deltaX = mScroller.getCurrX() - mLastFlingX;
                int deltaY = mScroller.getCurrY() - mLastFlingY;
                mLastFlingX = mScroller.getCurrX();
                mLastFlingY = mScroller.getCurrY();

                move(deltaX, deltaY, false);
                post(this);
            } else {
                finish();
            }
        }

        private void finish() {
            reset();
        }

        private void reset() {
            mLastFlingX = 0;
            mLastFlingY = 0;
            removeCallbacks(this);
        }

        private void destroy() {
            reset();
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }
        }

        public void tryToScrollTo(int dx, int dy, int duration) {
            if (dx == 0 && dy == 0) return;
            destroy();
            mScroller.startScroll(0, 0, dx, dy, duration);
            post(this);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(BannerView parent, View view, int position);
    }

    public interface OnScrollListener {
        void onScroll(BannerView parent, int position, float offset);
    }

}
