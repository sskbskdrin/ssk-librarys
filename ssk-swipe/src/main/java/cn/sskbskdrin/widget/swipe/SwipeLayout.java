package cn.sskbskdrin.widget.swipe;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;

/**
 * Created by keayuan on 2021/1/7.
 *
 * @author keayuan
 */
public class SwipeLayout extends ViewGroup implements NestedScrollingParent, NestedScrollingChild,
    SwipeHelper.SwipeAble {
    private static final String TAG = "SwipeLayout";

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private static final String LOG_TAG = SwipeLayout.class.getSimpleName();

    private static final int INVALID_POINTER = -1;
    private static final float DRAG_RATE = .5f;

    private static final int SCROLL_DURATION = 200;

    private View mTarget; // the target of the gesture
    boolean mRefreshing = false;
    private final NestedScrollingParentHelper mNestedScrollingParentHelper;
    private final NestedScrollingChildHelper mNestedScrollingChildHelper;
    private final int[] mParentScrollConsumed = new int[2];
    private final int[] mParentOffsetInWindow = new int[2];
    private boolean mNestedScrollInProgress;

    private int mCurrentOffsetY = 0;
    private int mCurrentOffsetX = 0;

    private float mInitialMotionY;
    private float mInitialDownY;
    private boolean mIsBeingDragged;
    private int mActivePointerId = INVALID_POINTER;
    // Whether this item is scaled up rather than clipped

    private static final int[] LAYOUT_ATTRS = new int[]{android.R.attr.enabled};

    private SwipeHelper swipeHelper = new SwipeHelper(this);
    private int mOrientation = VERTICAL;

    void reset() {
        setTargetOffsetTopAndBottom(-mCurrentOffsetY);
        setTargetOffsetLeftAndRight(-mCurrentOffsetX);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            reset();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        reset();
    }

    /**
     * Simple constructor to use when creating a SwipeLayout from code.
     *
     * @param context
     */
    public SwipeLayout(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating SwipeLayout from XML.
     *
     * @param context
     * @param attrs
     */
    public SwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        setWillNotDraw(false);

        // the absolute offset has to take into account that the circle starts at an offset
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);

        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);

        final TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        setEnabled(a.getBoolean(0, true));
        a.recycle();
    }

    /**
     * Set the listener to be notified when a refresh is triggered via the swipe
     * gesture.
     */
    public void addSwipeRefreshListener(SwipeRefreshListener listener) {
        addSwipeRefreshListener(SwipePosition.TOP, listener);
    }

    public void addSwipeRefreshListener(SwipePosition position, SwipeRefreshListener listener) {
        swipeHelper.addSwipeRefreshListener(position, listener);
    }

    public void setRefreshing() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeToTarget(swipeHelper.getController(SwipePosition.TOP).getSwipeLoad());
            }
        }, 100);
    }

    public void refreshComplete(SwipePosition position, boolean success) {
        swipeHelper.complete(position, success);
    }

    public void setEnabled(SwipePosition position, boolean enable) {
        swipeHelper.getController(position).setEnable(enable);
    }

    private boolean isPinTarget = false;

    public void setPinTarget(boolean pin) {
        isPinTarget = pin;
    }

    @Override
    protected void onFinishInflate() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            LayoutParams lp = (LayoutParams) view.getLayoutParams();
            if (lp.isContent) {
                mTarget = view;
            } else if (lp.direction != SwipePosition.NONE) {
                if (view instanceof SwipeHandler) {
                    swipeHelper.addSwipeHandler(lp.direction, (SwipeHandler) view);
                }
            }
        }
        if (mTarget == null) {
            if (childCount > 0) mTarget = getChildAt(0);
        }
        if (mTarget == null) {
            TextView errorView = new TextView(getContext());
            errorView.setClickable(true);
            errorView.setTextColor(0xffff6600);
            errorView.setGravity(Gravity.CENTER);
            errorView.setTextSize(20);
            errorView.setText("content view is empty!!!");
            mTarget = errorView;
            addView(mTarget);
            ((LayoutParams) mTarget.getLayoutParams()).gravity = Gravity.CENTER;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTarget.setNestedScrollingEnabled(true);
        } else if (mTarget instanceof NestedScrollingChild) {
            ((NestedScrollingChild) mTarget).setNestedScrollingEnabled(true);
        }

        ((LayoutParams) mTarget.getLayoutParams()).isContent = true;
        super.onFinishInflate();
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int count = getChildCount();

        int maxHeight = 0;
        int maxWidth = 0;

        // Find rightmost and bottommost child
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                maxWidth = Math.max(maxWidth, child.getMeasuredWidth());
                maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
            }
        }

        // Account for padding too
        maxWidth += getPaddingLeft() + getPaddingRight();
        maxHeight += getPaddingTop() + getPaddingBottom();

        // Check against our minimum height and width
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        setMeasuredDimension(resolveSize(maxWidth, widthMeasureSpec), resolveSize(maxHeight, heightMeasureSpec));
    }

    private final Map<View, Integer> mTempViews = new HashMap<>();
    private final Map<SwipePosition, View> mPositionView = new HashMap<>();

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int parentLeft = getPaddingLeft();
        final int parentRight = right - left - getPaddingRight();
        final int parentTop = getPaddingTop();
        final int parentBottom = bottom - top - getPaddingBottom();

        int offsetX = mCurrentOffsetX;
        int offsetY = mCurrentOffsetY;
        final int count = getChildCount();
        mTempViews.clear();
        int targetId = -1;
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            SwipeLayout.LayoutParams lp = (SwipeLayout.LayoutParams) view.getLayoutParams();
            if (view instanceof SwipeHandler) {
                swipeHelper.addSwipeHandler(lp.direction, (SwipeHandler) view);
            }
            if (lp.isContent) {
                int tempX = offsetX;
                int tempY = offsetY;
                if (isPinTarget) {
                    tempX = 0;
                    tempY = 0;
                }
                int l = parentLeft + lp.leftMargin + tempX;
                int t = parentTop + lp.topMargin + tempY;
                int r = parentRight - lp.rightMargin + tempX;
                int b = parentBottom - lp.bottomMargin + tempY;
                view.layout(l, t, r, b);
                mTarget = view;
                targetId = i;
            } else if (lp.direction != SwipePosition.NONE) {
                int width = view.getMeasuredWidth();
                int height = view.getMeasuredHeight();
                int l = parentLeft + lp.leftMargin;
                int t = parentTop + lp.topMargin;
                int r = parentRight - lp.rightMargin;
                int b = parentBottom - lp.bottomMargin;
                SwipePosition currentPosition = swipeHelper.getCurrentPosition();
                if (lp.direction == SwipePosition.LEFT) {
                    l = l - width + offsetX - lp.rightMargin - lp.leftMargin;
                    if (currentPosition != SwipePosition.LEFT) l -= offsetX;
                    view.layout(l, t, l + width, t + height);
                }
                if (lp.direction == SwipePosition.TOP) {
                    t = t - height + offsetY - lp.topMargin - lp.bottomMargin;
                    if (currentPosition != SwipePosition.TOP) t -= offsetY;
                    view.layout(l, t, l + width, t + height);
                }
                if (lp.direction == SwipePosition.RIGHT) {
                    r = r + offsetX + width + lp.rightMargin + lp.leftMargin;
                    if (currentPosition != SwipePosition.RIGHT) r -= offsetX;
                    view.layout(r - width, t, r, t + height);
                }
                if (lp.direction == SwipePosition.BOTTOM) {
                    b = b + offsetY + height + lp.topMargin + lp.bottomMargin;
                    if (currentPosition != SwipePosition.BOTTOM) b -= offsetY;
                    view.layout(l, b - height, l + width, b);
                }
                mTempViews.put(view, i);
                mPositionView.put(lp.direction, view);
            } else {
                layoutChildren(view, parentLeft, parentTop, parentRight, parentBottom);
            }
        }
        for (Map.Entry<View, Integer> entry : mTempViews.entrySet()) {
            if (entry.getValue() < targetId) {
                entry.getKey().bringToFront();
            }
        }
    }

    private void layoutChildren(View child, int parentLeft, int parentTop, int parentRight, int parentBottom) {
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        final int width = child.getMeasuredWidth();
        final int height = child.getMeasuredHeight();

        int childLeft = parentLeft;
        int childTop = parentTop;

        final int gravity = lp.gravity;

        if (gravity != -1) {
            final int horizontalGravity = gravity & Gravity.HORIZONTAL_GRAVITY_MASK;
            final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;

            switch (horizontalGravity) {
                case Gravity.LEFT:
                    childLeft = parentLeft + lp.leftMargin;
                    break;
                case Gravity.CENTER_HORIZONTAL:
                    childLeft = parentLeft + (parentRight - parentLeft - width) / 2 + lp.leftMargin - lp.rightMargin;
                    break;
                case Gravity.RIGHT:
                    childLeft = parentRight - width - lp.rightMargin;
                    break;
                default:
                    childLeft = parentLeft + lp.leftMargin;
            }

            switch (verticalGravity) {
                case Gravity.TOP:
                    childTop = parentTop + lp.topMargin;
                    break;
                case Gravity.CENTER_VERTICAL:
                    childTop = parentTop + (parentBottom - parentTop - height) / 2 + lp.topMargin - lp.bottomMargin;
                    break;
                case Gravity.BOTTOM:
                    childTop = parentBottom - height - lp.bottomMargin;
                    break;
                default:
                    childTop = parentTop + lp.topMargin;
            }
        }
        child.layout(childLeft, childTop, childLeft + width, childTop + height);
    }

    /**
     * @return Whether it is possible for the child view of this layout to
     * scroll up. Override this if the child view is a custom view.
     */
    private boolean canChildScrollUp() {
        if (mTarget instanceof AbsListView) {
            return canScrollList((AbsListView) mTarget, -1);
        }
        return mTarget.canScrollVertically(-1);
    }

    private static boolean canScrollList(AbsListView listView, int direction) {
        if (Build.VERSION.SDK_INT >= 19) {
            // Call the framework version directly
            return listView.canScrollList(direction);
        } else {
            // provide backport on earlier versions
            final int childCount = listView.getChildCount();
            if (childCount == 0) {
                return false;
            }

            final int firstPosition = listView.getFirstVisiblePosition();
            if (direction > 0) {
                final int lastBottom = listView.getChildAt(childCount - 1).getBottom();
                final int lastPosition = firstPosition + childCount;
                return lastPosition < listView.getCount() || (lastBottom > listView.getHeight() - listView.getListPaddingBottom());
            } else {
                final int firstTop = listView.getChildAt(0).getTop();
                return firstPosition > 0 || firstTop < listView.getListPaddingTop();
            }
        }
    }

    private boolean canChildScrollDown() {
        if (mTarget instanceof AbsListView) {
            return canScrollList((AbsListView) mTarget, 1);
        }
        return mTarget.canScrollVertically(1);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        int pointerIndex;

        if (!isEnabled() || canChildScrollUp() || mRefreshing || mNestedScrollInProgress) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = false;

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                mInitialDownY = ev.getY(pointerIndex);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
                    return false;
                }

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                final float y = ev.getY(pointerIndex);
                startDragging(y);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
        }
        return mIsBeingDragged;
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        // if this is a List < L or another view that doesn't support nested
        // scrolling, ignore this request so that the vertical scroll event
        // isn't stolen
        if ((Build.VERSION.SDK_INT < 21 && mTarget instanceof AbsListView) || (mTarget != null && !ViewCompat.isNestedScrollingEnabled(mTarget))) {
            // Nope.
        } else {
            super.requestDisallowInterceptTouchEvent(b);
        }
    }

    // NestedScrollingParent

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return isEnabled() && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        // Reset the counter of how much leftover scroll needs to be consumed.
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
        // Dispatch up to the nested parent
        startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
        //        mTotalUnconsumed = 0;
        mNestedScrollInProgress = true;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        // If we are in the middle of consuming, a scroll, then we want to move the spinner back up
        // before allowing the list to scroll
        if (mCurrentOffsetY != 0) {
            int tempDy = (int) (dy * swipeHelper.getResistance());
            if (mCurrentOffsetY > 0) {
                if (tempDy > mCurrentOffsetY) {
                    consumed[1] = (int) (mCurrentOffsetY / swipeHelper.getResistance());
                    tempDy = -mCurrentOffsetY;
                } else {
                    tempDy = -tempDy;
                    consumed[1] = dy;
                }
            } else {
                if (tempDy < mCurrentOffsetY) {
                    consumed[1] = (int) (mCurrentOffsetY / swipeHelper.getResistance());
                    tempDy = -mCurrentOffsetY;
                } else {
                    tempDy = -tempDy;
                    consumed[1] = dy;
                }
            }
            Log.d(TAG, "onNestedPreScroll: offsetY=" + mCurrentOffsetY + " dy=" + dy + " consumedY=" + consumed[1]);
            moveSpinner(tempDy);
        }
        consumed[0] = dx > 0 ? Math.abs(consumed[0]) : -Math.abs(consumed[0]);
        consumed[1] = dy > 0 ? Math.abs(consumed[1]) : -Math.abs(consumed[1]);

        // If a client layout is using a custom start position for the circle
        // view, they mean to hide it again before scrolling the child view
        // If we get back to mTotalUnconsumed == 0 and there is more to go, hide
        // the circle so it isn't exposed if its blocking content is moved

        // Now let our nested parent consume the leftovers
        final int[] parentConsumed = mParentScrollConsumed;
        if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
            consumed[0] += parentConsumed[0];
            consumed[1] += parentConsumed[1];
        }
    }

    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }

    @Override
    public void onStopNestedScroll(View target) {
        mNestedScrollingParentHelper.onStopNestedScroll(target);
        mNestedScrollInProgress = false;
        // Finish the spinner for nested scrolling if we ever consumed any
        // unconsumed nested scroll
        if (mCurrentOffsetY != 0) {
            finishSpinner();
            //            mTotalUnconsumed = 0;
        }
        // Dispatch up our nested parent
        stopNestedScroll();
    }

    @Override
    public void onNestedScroll(final View target, final int dxConsumed, final int dyConsumed, final int dxUnconsumed,
                               final int dyUnconsumed) {
        // Dispatch up to the nested parent first
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, mParentOffsetInWindow);

        // This is a bit of a hack. Nested scrolling works from the bottom up, and as we are
        // sometimes between two nested scrolling views, we need a way to be able to know when any
        // nested scrolling parent has stopped handling events. We do that by using the
        // 'offset in window 'functionality to see if we have been moved from the event.
        // This is a decent indication of whether we should take over the event stream or not.
        final int dy = dyUnconsumed + mParentOffsetInWindow[1];
        Log.i(TAG, "onNestedScroll:dy=" + dy + " total=" + mCurrentOffsetY);
        if (dy != 0) {
            if (dy < 0) {
                if (!canChildScrollUp()) {
                    moveSpinner(-(int) (dy * swipeHelper.getResistance()));
                }
            } else {
                if (!canChildScrollDown()) {
                    moveSpinner(-(int) (dy * swipeHelper.getResistance()));
                }
            }
        }
    }

    // NestedScrollingChild

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed,
                                        int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
            offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        if (mCurrentOffsetY != 0 || mCurrentOffsetX != 0) return true;
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        if (mCurrentOffsetY != 0 || mCurrentOffsetX != 0) return true;
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    private void moveSpinner(int dy) {
        moveSpinner(dy, false);
    }

    private void moveSpinner(int dy, boolean animation) {
        Log.i(TAG, "moveSpinner: " + dy + " curr=" + mCurrentOffsetY);
        if (isAnimation == animation) {
            setTargetOffsetTopAndBottom(dy);
            swipeHelper.moveSpinner(0, dy, 0, mCurrentOffsetY, true);
        }
    }

    private void finishSpinner() {
        Log.i(TAG, "finishSpinner: currX=" + mCurrentOffsetX + " currY=" + mCurrentOffsetY);
        swipeHelper.finishSpinner(mCurrentOffsetX, mCurrentOffsetY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        int pointerIndex = -1;

        if (!isEnabled() || canChildScrollUp() || mRefreshing || mNestedScrollInProgress) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = false;
                break;

            case MotionEvent.ACTION_MOVE: {
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                }

                final float y = ev.getY(pointerIndex);
                startDragging(y);

                if (mIsBeingDragged) {
                    final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                    if (overscrollTop > 0) {
                        moveSpinner((int) overscrollTop);
                    } else {
                        return false;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                pointerIndex = ev.getActionIndex();
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                    return false;
                }
                mActivePointerId = ev.getPointerId(pointerIndex);
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
            case MotionEvent.ACTION_UP: {
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.");
                    return false;
                }
                if (mIsBeingDragged) {
                    final float y = ev.getY(pointerIndex);
                    final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                    mIsBeingDragged = false;
                    finishSpinner();
                }
                mActivePointerId = INVALID_POINTER;
                return false;
            }
            case MotionEvent.ACTION_CANCEL:
                return false;
        }
        return true;
    }

    private void startDragging(float y) {
        final float yDiff = y - mInitialDownY;
        //        if (yDiff > mTouchSlop && !mIsBeingDragged) {
        //            mInitialMotionY = mInitialDownY + mTouchSlop;
        //            mIsBeingDragged = true;
        //        mProgress.setAlpha(STARTING_PROGRESS_ALPHA);
        //        }
    }

    private boolean isAnimation;
    private Animation scrollAnimation;

    @Override
    public void swipeToTarget(final int targetPos) {
        if (scrollAnimation != null) {
            scrollAnimation.cancel();
        }
        final int total = targetPos - mCurrentOffsetY;
        long time = SCROLL_DURATION;
        if (Math.abs(total) < 300) {
            time = (long) (SCROLL_DURATION * Math.abs(total) / 300f);
        }
        scrollAnimation = new Animation() {
            float last = 0;

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                Log.i(TAG, "applyTransformation: " + interpolatedTime);
                if (interpolatedTime == 1) {
                    moveSpinner(targetPos - mCurrentOffsetY, true);
                    finishSpinner();
                    isAnimation = false;
                } else {
                    moveSpinner((int) (total * (interpolatedTime - last)), true);
                }
                last = interpolatedTime;
            }
        };
        scrollAnimation.setDuration(time);
        mTarget.clearAnimation();
        isAnimation = true;
        mTarget.startAnimation(scrollAnimation);
    }

    private SwipePosition getPosition(int offset) {
        SwipePosition position;
        if (mCurrentOffsetY != 0) {
            if (mCurrentOffsetY > 0) {
                position = SwipePosition.TOP;
            } else {
                position = SwipePosition.BOTTOM;
            }
        } else if (offset > 0) {
            position = SwipePosition.TOP;
        } else {
            position = SwipePosition.BOTTOM;
        }
        return position;
    }

    private void setTargetOffsetTopAndBottom(int offset) {
        if (mCurrentOffsetY == 0) {
            SwipePosition position = getPosition(offset);
            if (!swipeHelper.getController(position).isEnable()) {
                offset = 0;
            }
        } else {
            if (mCurrentOffsetY > 0) {
                if (offset + mCurrentOffsetY > swipeHelper.getSwipeMax()) {
                    offset = swipeHelper.getSwipeMax() - mCurrentOffsetY;
                }
            } else if (offset + mCurrentOffsetY < -swipeHelper.getSwipeMax()) {
                offset = -swipeHelper.getSwipeMax() - mCurrentOffsetY;
            }
        }
        if (offset == 0) return;
        SwipePosition position = getPosition(offset);
        View view = mPositionView.get(position);
        if (view != null) {
            if (swipeHelper.getController(position).isEnable()) {
                ViewCompat.offsetTopAndBottom(view, offset);
            } else {
                if (position == SwipePosition.TOP) {
                    if (view.getBottom() != 0) {
                        ViewCompat.offsetTopAndBottom(view, -view.getBottom());
                    }
                } else {
                    if (view.getTop() != mTarget.getMeasuredHeight()) {
                        ViewCompat.offsetTopAndBottom(view, mTarget.getMeasuredHeight() - view.getTop());
                    }
                }
            }
        }
        if (mTarget != null && !isPinTarget) {
            ViewCompat.offsetTopAndBottom(mTarget, offset);
        }
        mCurrentOffsetY += offset;
    }

    private void setTargetOffsetLeftAndRight(int offset) {
        mCurrentOffsetX += offset;
        if (mTarget != null) {
            ViewCompat.offsetTopAndBottom(mTarget, offset);
        }
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = ev.getActionIndex();
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends MarginLayoutParams {

        /**
         * 相对父view的位置，为NONE则根据gravity确定位置；否则会在父view的上下左右的位置，且会在父view拖动的时候跟随移动
         */
        public SwipePosition direction = SwipePosition.NONE;
        /**
         * 是否是PullLayout中控制的view。
         */
        public boolean isContent;

        public int gravity = -1;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray arr = c.obtainStyledAttributes(attrs, R.styleable.SwipeLayout_Layout, 0, 0);
            isContent = arr.getBoolean(R.styleable.SwipeLayout_Layout_swipe_isContentView, false);
            int position = arr.getInt(R.styleable.SwipeLayout_Layout_swipe_inParentPosition, 0);
            if (position == 1) {
                direction = SwipePosition.LEFT;
            } else if (position == 2) {
                direction = SwipePosition.TOP;
            } else if (position == 3) {
                direction = SwipePosition.RIGHT;
            } else if (position == 4) {
                direction = SwipePosition.BOTTOM;
            } else {
                direction = SwipePosition.NONE;
            }
            gravity = arr.getInt(R.styleable.SwipeLayout_Layout_android_layout_gravity, -1);
            arr.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
