package cn.sskbskdrin.flow;

/**
 * Created by keayuan on 2020/8/21.
 *
 * @author keayuan
 */
public interface IFlow {
    /**
     * 运行在主线程
     *
     * @param p    Process处理器
     * @param args process参数
     * @param <T>  process参数返回值类型
     * @return process计算结果
     */
    <T, L> IFlow main(IProcess<T, L> p, Object... args);

    <T, L> IFlow main(String tag, IProcess<T, L> p, Object... args);

    /**
     * 运行在io线程
     *
     * @param p    Process处理器
     * @param args process参数
     * @param <T>  process参数返回值类型
     * @return process计算结果
     */
    <T, L> IFlow io(IProcess<T, L> p, Object... args);

    <T, L> IFlow io(String tag, IProcess<T, L> p, Object... args);

    void remove(String tag);

    void removeAll();

}
