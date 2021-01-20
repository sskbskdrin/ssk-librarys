package cn.sskbskdrin.http;

/**
 * Created by sskbskdrin on 2021/1/19.
 *
 * @author sskbskdrin
 */
public interface ICallback3<T, V, R> {
    void onCallback(String tag, T t, V v, R r);
}
