package cn.sskbskdrin.frame.base.vm;

/**
 * Created by keayuan on 2020/7/13.
 *
 * @author keayuan
 */
public interface Observer2<V, T> {
    void onChanged(V v, T t);
}
