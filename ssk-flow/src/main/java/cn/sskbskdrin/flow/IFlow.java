package cn.sskbskdrin.flow;

import java.io.Closeable;

/**
 * Created by keayuan on 2020/8/21.
 *
 * @author keayuan
 */
public interface IFlow<L> extends Closeable {
    /**
     * 运行在主线程
     *
     * @param p    Process处理器
     * @param args process参数
     * @param <T>  process参数返回值类型
     * @return process计算结果
     */
    <T> IFlow<T> main(IProcess<T, L> p, Object... args);

    /**
     * 运行在主线程
     *
     * @param tag  程序标签
     * @param p    Process处理器
     * @param args process参数
     * @param <T>  process参数返回值类型
     * @return process计算结果
     */
    <T> IFlow<T> main(String tag, IProcess<T, L> p, Object... args);

    /**
     * 运行在io线程
     *
     * @param p    Process处理器
     * @param args process参数
     * @param <T>  process参数返回值类型
     * @return process计算结果
     */
    <T> IFlow<T> io(IProcess<T, L> p, Object... args);

    /**
     * 运行在io线程
     *
     * @param tag  程序标签
     * @param p    Process处理器
     * @param args process参数
     * @param <T>  process参数返回值类型
     * @return process计算结果
     */
    <T> IFlow<T> io(String tag, IProcess<T, L> p, Object... args);

    void remove(String tag);

    Closeable start();

    @Override
    void close();
}
