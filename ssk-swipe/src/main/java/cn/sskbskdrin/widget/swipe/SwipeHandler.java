package cn.sskbskdrin.widget.swipe;

/**
 * Created by keayuan on 2021/1/8.
 *
 * @author keayuan
 */
public interface SwipeHandler extends SwipeStatusChangeListener, SwipePositionChangeListener {

    /**
     * 拉动的最大值
     */
    int getSwipeMax();

    /**
     * 开发刷新阈值
     */
    int getSwipeLoad();

    /**
     * 阻力系数[0,1]
     */
    float getResistance();

    boolean isReleaseRefresh();

    @Override
    void onSwitchChange(int dx, int dy, int offsetX, int offsetY, boolean isTouch);
}
