package cn.sskbskdrin.widget.swipe;

/**
 * Created by keayuan on 2021/1/15.
 *
 * @author keayuan
 */
interface Swipe {
    void addSwipeRefreshListener(SwipeRefreshListener listener);

    void addSwipeRefreshListener(SwipePosition position, SwipeRefreshListener listener);

    void setRefreshing();

    void refreshComplete(SwipePosition position, boolean success);

    void setEnabled(SwipePosition position, boolean enable);

    void setPinTarget(boolean pin);
}
