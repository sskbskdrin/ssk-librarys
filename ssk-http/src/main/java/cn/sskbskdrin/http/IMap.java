package cn.sskbskdrin.http;

import java.util.HashMap;

/**
 * Created by keayuan on 2019-11-29.
 *
 * @author keayuan
 */
public interface IMap<K, V> {
    void apply(HashMap<K, V> map);
}
