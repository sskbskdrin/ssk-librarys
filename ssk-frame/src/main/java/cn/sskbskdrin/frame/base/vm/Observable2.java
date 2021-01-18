package cn.sskbskdrin.frame.base.vm;

/**
 * Created by keayuan on 2020/11/20.
 *
 * @author keayuan
 */
public interface Observable2<V, T> {
    void observe(Observer2<V, T> observer);
}
