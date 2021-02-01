package cn.sskbskdrin.flow;

/**
 * Created by keayuan on 2020/8/20.
 *
 * @author keayuan
 */
public interface IProcess<P, T> {
    T process(IFlow<P> flowProcess, P last, Object... params);
}
