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
public class RefreshView extends TextView implements SwipeHandler {

    public RefreshView(Context context) {
        super(context);
    }

    public RefreshView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setGravity(Gravity.CENTER);
        setMinHeight((int) (getTextSize() * 2));
    }

    @Override
    public void onReset() {

    }

    @Override
    public void onSwipe() {
        setText("下拉刷新");
    }

    @Override
    public void onPrepare() {
        setText("释放开始刷新");
    }

    @Override
    public void onLoading(SwipePosition position) {
        setText("刷新中。。。");
    }

    @Override
    public void onComplete(boolean success) {
        setText("刷新" + (success ? "成功" : "失败"));
    }

    @Override
    public int getSwipeMax() {
        return getMeasuredHeight() * 2;
    }

    @Override
    public int getSwipeLoad() {
        return getMeasuredHeight();
    }

    @Override
    public float getResistance() {
        return 0.5f;
    }

    @Override
    public boolean isReleaseRefresh() {
        return true;
    }

    @Override
    public void onSwitchChange(int dx, int dy, int offsetX, int offsetY, boolean isTouch) {

    }
}
