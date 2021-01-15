package cn.sskbskdrin.widget.swipe.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.Nullable;
import cn.sskbskdrin.widget.swipe.SwipeHandler;
import cn.sskbskdrin.widget.swipe.SwipePosition;

/**
 * Created by keayuan on 2021/1/8.
 *
 * @author keayuan
 */
public class MoreView extends TextView implements SwipeHandler {

    public MoreView(Context context) {
        this(context, null);
    }

    public MoreView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setGravity(Gravity.CENTER);
        setMinHeight((int) (getTextSize() * 2));
    }

    @Override
    public void onReset() {

    }

    @Override
    public void onSwipe() {
    }

    @Override
    public void onPrepare() {
    }

    @Override
    public void onLoading(SwipePosition position) {
        setText("加载中。。。");
    }

    @Override
    public void onComplete(boolean success) {
        setText("加载" + (success ? "成功" : "失败"));
    }

    @Override
    public int getSwipeMax() {
        return getMeasuredHeight();
    }

    @Override
    public int getSwipeLoad() {
        return 0;
    }

    @Override
    public float getResistance() {
        return 0f;
    }

    @Override
    public boolean isReleaseRefresh() {
        return false;
    }

    @Override
    public void onSwitchChange(int dx, int dy, int offsetX, int offsetY, boolean isTouch) {

    }
}
