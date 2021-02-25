package cn.sskbskdrin.util.function;

/**
 * Created by keayuan on 2021/1/28.
 *
 * @author keayuan
 */
public interface Supplier<T> {
    /**
     * Gets a result.
     *
     * @return a result
     */
    T get();
}
