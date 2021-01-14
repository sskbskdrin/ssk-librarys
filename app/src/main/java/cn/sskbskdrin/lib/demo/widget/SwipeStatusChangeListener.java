package cn.sskbskdrin.lib.demo.widget;

/**
 * Created by keayuan on 2021/1/14.
 *
 * @author keayuan
 */
public interface SwipeStatusChangeListener extends SwipeRefreshListener {
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
    @Override
    void onLoading(SwipePosition position);

    /**
     * 加载完成
     *
     * @param success 加载成功或失败
     */
    void onComplete(boolean success);
}
