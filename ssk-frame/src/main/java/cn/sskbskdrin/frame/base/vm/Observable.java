package cn.sskbskdrin.frame.base.vm;

/**
 * Created by keayuan on 2020/11/20.
 *
 * @author keayuan
 */
public interface Observable<T> {
    void observe(Observer<T> observer);
}
