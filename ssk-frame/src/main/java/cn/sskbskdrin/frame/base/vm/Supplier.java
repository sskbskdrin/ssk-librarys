package cn.sskbskdrin.frame.base.vm;

/**
 * Created by keayuan on 2020/11/20.
 *
 * @author keayuan
 */
public interface Supplier<T> {
    void run(final BaseModel<T> model) throws Exception;
}
