package cn.sskbskdrin.lib.demo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * Created by keayuan on 2021/1/8.
 *
 * @author keayuan
 */
public class TopView extends TextView implements SwipeHandler {
    private static final String TAG = "TopView";

    public TopView(Context context) {
        super(context);
    }

    public TopView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onReset() {

    }

    @Override
    public void onSwipe() {
        Log.d(TAG, "onSwipe: ");
        setText("下拉刷新");
    }

    @Override
    public void onPrepare() {
        Log.d(TAG, "onPrepare: ");
        setText("释放开始刷新");
    }

    @Override
    public void onLoad() {
        Log.i(TAG, "onLoad: ");
        setText("刷新中。。。");
    }

    @Override
    public void onComplete(boolean success) {
        Log.i(TAG, "onComplete: ");
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
    public void onSwitchChange(int dx, int dy, int offsetX, int offsetY, boolean isTouch) {

    }
}
