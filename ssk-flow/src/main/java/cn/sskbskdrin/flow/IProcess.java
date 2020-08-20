package cn.sskbskdrin.flow;

/**
 * Created by keayuan on 2020/8/20.
 *
 * @author keayuan
 */
public interface IProcess<R> {
    R process(int[] jump, Object... params);
}
