package cn.sskbskdrin.lib.demo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.VisibleForTesting;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import androidx.core.widget.ListViewCompat;

/**
 * Created by keayuan on 2021/1/7.
 *
 * @author keayuan
 */
public class SwipeLayout extends ViewGroup implements NestedScrollingParent, NestedScrollingChild {
    private static final String TAG = "SwipeLayout";
    // Maps to ProgressBar.Large style
    public static final int LARGE = 0;
    // Maps to ProgressBar default style
    public static final int DEFAULT = 1;

    public static final int DEFAULT_SLINGSHOT_DISTANCE = -1;
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    @VisibleForTesting
    static final int CIRCLE_DIAMETER = 40;
    @VisibleForTesting
    static final int CIRCLE_DIAMETER_LARGE = 56;

    private static final String LOG_TAG = SwipeLayout.class.getSimpleName();

    private static final int MAX_ALPHA = 255;
    private static final int STARTING_PROGRESS_ALPHA = (int) (.3f * MAX_ALPHA);

    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
    private static final int INVALID_POINTER = -1;
    private static final float DRAG_RATE = .5f;

    // Max amount of circle that can be filled by progress during swipe gesture,
    // where 1.0 is a full circle
    private static final float MAX_PROGRESS_ANGLE = .8f;

    private static final int SCALE_DOWN_DURATION = 150;

    private static final int ALPHA_ANIMATION_DURATION = 300;

    private static final int ANIMATE_TO_TRIGGER_DURATION = 200;

    private static final int ANIMATE_TO_START_DURATION = 200;

    // Default background for the progress spinner
    private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;
    // Default offset in dips from the top of the view to where the progress spinner should stop
    private static final int DEFAULT_CIRCLE_TARGET = 64;

    private View mTarget; // the target of the gesture
    SwipeLayout.OnRefreshListener mListener;
    boolean mRefreshing = false;
    private int mTouchSlop;
    private float mTotalDragDistance = -1;

    // If nested scrolling is enabled, the total amount that needed to be
    // consumed by this as the nested scrolling parent is used in place of the
    // overscroll determined by MOVE events in the onTouch handler
    private int mTotalUnconsumed;
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
    boolean mScale;

    private static final int[] LAYOUT_ATTRS = new int[]{android.R.attr.enabled};

    float mStartingScale;

    int mSpinnerOffsetEnd;

    int mCustomSlingshotDistance;

    private Animation mScaleAnimation;

    private Animation mScaleDownAnimation;

    private Animation mAlphaStartAnimation;

    private Animation mAlphaMaxAnimation;

    private Animation mScaleDownToStartAnimation;

    boolean mNotify;

    private SwipeLayout.OnChildScrollUpCallback mChildScrollUpCallback;
    private SwipeHelper swipeHelper = new SwipeHelper();
    private int mOrientation = VERTICAL;

    public enum Postition {
        LEFT, TOP, RIGHT, BOTTOM, NONE
    }

    private Postition mPosition = Postition.NONE;

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
     * Sets a custom slingshot distance.
     *
     * @param slingshotDistance The distance in pixels that the refresh indicator can be pulled
     *                          beyond its resting position. Use
     *                          {@link #DEFAULT_SLINGSHOT_DISTANCE} to reset to the default value.
     */
    public void setSlingshotDistance(@Px int slingshotDistance) {
        mCustomSlingshotDistance = slingshotDistance;
    }

    /**
     * Simple constructor to use when creating a SwipeLayout from code.
     *
     * @param context
     */
    public SwipeLayout(@NonNull Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating SwipeLayout from XML.
     *
     * @param context
     * @param attrs
     */
    public SwipeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        setWillNotDraw(false);

        final DisplayMetrics metrics = getResources().getDisplayMetrics();

        // the absolute offset has to take into account that the circle starts at an offset
        mSpinnerOffsetEnd = (int) (DEFAULT_CIRCLE_TARGET * metrics.density);
        mTotalDragDistance = mSpinnerOffsetEnd;
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);

        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);

        moveToStart(1.0f);

        final TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        setEnabled(a.getBoolean(0, true));
        a.recycle();
        ensureTarget();
    }

    /**
     * Set the listener to be notified when a refresh is triggered via the swipe
     * gesture.
     */
    public void setOnRefreshListener(@Nullable SwipeLayout.OnRefreshListener listener) {
        mListener = listener;
    }

    /**
     * Notify the widget that refresh state has changed. Do not call this when
     * refresh is triggered by a swipe gesture.
     *
     * @param refreshing Whether or not the view should show refresh progress.
     */
    public void setRefreshing(boolean refreshing) {
        if (refreshing && mRefreshing != refreshing) {
            // scale and show
            mRefreshing = refreshing;
            setTargetOffsetTopAndBottom(mSpinnerOffsetEnd - mCurrentOffsetX);
            mNotify = false;
        } else {
            setRefreshing(refreshing, false /* notify */);
        }
    }

    /**
     * Pre API 11, this does an alpha animation.
     *
     * @param progress
     */
    void setAnimationProgress(float progress) {
        //        mCircleView.setScaleX(progress);
        //        mCircleView.setScaleY(progress);
        //        moveToStart(1 - progress);
    }

    private void setRefreshing(boolean refreshing, final boolean notify) {
        if (mRefreshing != refreshing) {
            mNotify = notify;
            ensureTarget();
            mRefreshing = refreshing;
        }
    }

    /**
     * @return Whether the SwipeRefreshWidget is actively showing refresh
     * progress.
     */
    public boolean isRefreshing() {
        return mRefreshing;
    }

    private void ensureTarget() {
        // Don't bother getting the parent height if the parent hasn't been laid out yet.
        if (mTarget == null) {
            mTarget = getChildAt(0);
        }
    }

    /**
     * Set the distance to trigger a sync in dips
     *
     * @param distance
     */
    public void setDistanceToTriggerSync(int distance) {
        mTotalDragDistance = distance;
    }

    private boolean isPinTarget;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        if (getChildCount() == 0) {
            return;
        }
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        int offsetY = (isPinTarget ? 0 : 0);
        int offsetX = (isPinTarget ? 0 : mCurrentOffsetX);
        final View child = mTarget;
        final int childLeft = getPaddingLeft() + offsetX;
        final int childTop = getPaddingTop() + offsetY;
        final int childRight = width - getPaddingLeft() - getPaddingRight() + offsetX;
        final int childBottom = height - getPaddingTop() - getPaddingBottom() + offsetY;

        child.layout(childLeft, childTop, childRight, childBottom);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ensureTarget();
        if (mTarget != null) {
            mTarget.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                MeasureSpec.EXACTLY), MeasureSpec
                .makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));
        }

        //        mCircleView.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.AT_MOST),
        //            MeasureSpec.makeMeasureSpec(0x0fffffff, MeasureSpec.UNSPECIFIED));
    }

    private View getTarget() {
        if (mTarget != null) {
            ViewGroup parent = (ViewGroup) mTarget.getParent();
            if (parent == null) {
                addView(mTarget);
            } else if (parent != this) {
                parent.removeView(mTarget);
            }
        }
        return mTarget;
    }

    /**
     * @return Whether it is possible for the child view of this layout to
     * scroll up. Override this if the child view is a custom view.
     */
    public boolean canChildScrollUp() {
        if (mChildScrollUpCallback != null) {
            return mChildScrollUpCallback.canChildScrollUp(this, mTarget);
        }
        if (mTarget instanceof ListView) {
            return ListViewCompat.canScrollList((ListView) mTarget, -1);
        }
        return mTarget.canScrollVertically(-1);
    }

    public boolean canChildScrollDown() {
        return mTarget.canScrollVertically(1);
    }

    /**
     * Set a callback to override {@link SwipeLayout#canChildScrollUp()} method. Non-null
     * callback will return the value provided by the callback and ignore all internal logic.
     *
     * @param callback Callback that should be called when canChildScrollUp() is called.
     */
    public void setOnChildScrollUpCallback(@Nullable SwipeLayout.OnChildScrollUpCallback callback) {
        mChildScrollUpCallback = callback;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();

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
        if ((android.os.Build.VERSION.SDK_INT < 21 && mTarget instanceof AbsListView) || (mTarget != null && !ViewCompat
            .isNestedScrollingEnabled(mTarget))) {
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
            finishSpinner(mCurrentOffsetY);
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
        Log.i(TAG, "moveSpinner: " + dy);
        if (isAnimation == animation) {
            setTargetOffsetTopAndBottom(dy);
            swipeHelper.onSwitchChange(0, dy, 0, mCurrentOffsetY, true);
        }
    }

    private void finishSpinner(float overscrollTop) {
        Log.i(TAG, "finishSpinner: " + overscrollTop);
        if (swipeHelper.isLoading()) {
            if (mCurrentOffsetY > swipeHelper.getSwipeLoad()) {
                swipeToLoad();
            }
            return;
        }
        if (swipeHelper.isComplete()) {
            swipeToReset();
        } else {
            if (mCurrentOffsetY > swipeHelper.getSwipeLoad()) {
                swipeToLoad();
                swipeHelper.startLoad();
            } else {
                swipeToReset();
            }
        }
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
                    finishSpinner(overscrollTop);
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
        if (yDiff > mTouchSlop && !mIsBeingDragged) {
            mInitialMotionY = mInitialDownY + mTouchSlop;
            mIsBeingDragged = true;
            //            mProgress.setAlpha(STARTING_PROGRESS_ALPHA);
        }
    }

    void moveToStart(float interpolatedTime) {
        setTargetOffsetTopAndBottom(0);
    }

    boolean isAnimation;

    void swipeToLoad() {
        final int dest = swipeHelper.getSwipeLoad() - mCurrentOffsetY;
        mScaleDownAnimation = new Animation() {
            float last = 0;

            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    moveSpinner(swipeHelper.getSwipeLoad() - mCurrentOffsetY, true);
                    isAnimation = false;
                } else {
                    moveSpinner((int) (dest * (interpolatedTime - last)), true);
                }
                last = interpolatedTime;
            }
        };
        isAnimation = true;
        mScaleDownAnimation.setDuration(SCALE_DOWN_DURATION);
        mTarget.clearAnimation();
        mTarget.startAnimation(mScaleDownAnimation);
    }

    void swipeToReset() {
        final int dest = -mCurrentOffsetY;
        mScaleDownAnimation = new Animation() {
            float last = 0;

            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    moveSpinner(-mCurrentOffsetY, true);
                    isAnimation = false;
                } else {
                    moveSpinner((int) (dest * (interpolatedTime - last)), true);
                }
                last = interpolatedTime;
            }
        };
        mScaleDownAnimation.setDuration(SCALE_DOWN_DURATION);
        mTarget.clearAnimation();
        isAnimation = true;
        mTarget.startAnimation(mScaleDownAnimation);
    }

    void setTargetOffsetTopAndBottom(int offset) {
        if (mCurrentOffsetY > 0) {
            if (offset + mCurrentOffsetY > swipeHelper.getSwipeMax()) {
                offset = swipeHelper.getSwipeMax() - mCurrentOffsetY;
            }
        } else if (offset + mCurrentOffsetY < -swipeHelper.getSwipeMax()) {
            offset = -swipeHelper.getSwipeMax() - mCurrentOffsetY;
        }
        mCurrentOffsetY += offset;
        if (mTarget != null) {
            ViewCompat.offsetTopAndBottom(mTarget, offset);
        }
    }

    void setTargetOffsetLeftAndRight(int offset) {
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

    /**
     * Classes that wish to be notified when the swipe gesture correctly
     * triggers a refresh should implement this interface.
     */
    public interface OnRefreshListener {
        /**
         * Called when a swipe gesture triggers a refresh.
         */
        void onRefresh();
    }

    /**
     * Classes that wish to override {@link SwipeLayout#canChildScrollUp()} method
     * behavior should implement this interface.
     */
    public interface OnChildScrollUpCallback {
        /**
         * Callback that will be called when {@link SwipeLayout#canChildScrollUp()} method
         * is called to allow the implementer to override its behavior.
         *
         * @param parent SwipeLayout that this callback is overriding.
         * @param child  The child view of SwipeLayout.
         * @return Whether it is possible for the child view of parent layout to scroll up.
         */
        boolean canChildScrollUp(@NonNull SwipeLayout parent, @Nullable View child);
    }
}
