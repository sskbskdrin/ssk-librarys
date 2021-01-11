package cn.sskbskdrin.lib.demo.widget;

/**
 * Created by keayuan on 2021/1/8.
 *
 * @author keayuan
 */
public interface SwipeHandler extends SwipeChangeListener {
    /**
     * 复位
     */
    void onReset();

    /**
     * 刚刚拉动
     */
    void onSwipe();

    /**
     * 已达到刷新范围
     */
    void onPrepare();

    /**
     * 开始加载
     */
    void onLoad();

    /**
     * 加载完成
     *
     * @param success 加载成功或失败
     */
    void onComplete(boolean success);

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
}
