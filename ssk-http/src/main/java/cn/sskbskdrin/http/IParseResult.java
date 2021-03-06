package cn.sskbskdrin.http;

/**
 * Created by keayuan on 2020/4/17.
 *
 * @author keayuan
 */
public interface IParseResult<T> {
    /**
     * 结果解析是否成功
     *
     * @return 成功为true，否则false
     */
    boolean isSuccess();

    /**
     * 是否取消回调,取消则不会进入success，会直接进入complete
     *
     * @return 取消为true，，否则false
     */
    boolean isCancel();

    /**
     * 解析出的实体类
     *
     * @return 结果
     */
    T getT();

    String getCode();

    String getMessage();

    Exception getException();

}
