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
     * 是否取消回调,取消则不会进入{@link ISuccess#success(String, Object, IResponse)}，
     * {@link IError#error(String, String, String, Exception)},会直接进入{@link IComplete#complete(String)}
     *
     * @return 取消为true，，否则false
     */
    boolean isCancel();

    /**
     * 获取解析的状态码
     *
     * @return 返回状态码
     */
    String getCode();

    /**
     * msg信息
     *
     * @return msg信息
     */
    String getMessage();

    /**
     * 获取解析异常
     *
     * @return 返回异常
     */
    Exception getException();

    /**
     * 解析出的实体类
     *
     * @return 结果
     */
    T getT();
}
