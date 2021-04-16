package cn.sskbskdrin.log;

/**
 * Created by ex-keayuan001 on 2018/12/24.
 *
 * @author ex-keayuan001
 */
public interface Filter {
    boolean filter(int priority, String tag);
}
