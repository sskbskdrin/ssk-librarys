package cn.sskbskdrin.flow;

/**
 * Created by keayuan on 2020/8/20.
 *
 * @author keayuan
 */
public interface IProcess<R, L> {
    R process(IFlow flowProcess, L last, Object... params);
}
