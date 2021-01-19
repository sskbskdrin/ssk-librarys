package cn.sskbskdrin.http;

/**
 * Created by sskbskdrin on 2021/1/19.
 *
 * @author sskbskdrin
 */
public interface ICallback2<T, V> {
    void onCallback(String tag, T t, V v);
}
