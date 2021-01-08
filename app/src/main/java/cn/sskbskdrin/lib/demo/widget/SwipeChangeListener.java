package cn.sskbskdrin.lib.demo.widget;

/**
 * Created by keayuan on 2021/1/8.
 *
 * @author keayuan
 */
public interface SwipeChangeListener {
    void onSwitchChange(int dx, int dy, int offsetX, int offsetY, boolean isTouch);

    void onTouchUp();
}
