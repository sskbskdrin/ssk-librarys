package cn.sskbskdrin.http;

import java.io.Closeable;

/**
 * Created by keayuan on 2019-11-29.
 *
 * @author keayuan
 */
public interface IRequest<V> extends Closeable {

    String ERROR_UNKNOWN = "-1";
    String ERROR_REAL_REQUEST = "-1001";
    String ERROR_PARSE = "-1002";
    String ERROR_NO_PARSE = "-1003";
    String ERROR_CONNECT = "-1004";

    /**
     * 请求参数类型为json
     */
    String CONTENT_TYPE_JSON = "application/json";
    /**
     * 请求参数类型为form
     */
    String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
    /**
     * 请求参数类型为multipart，含有文件上传时使用
     */
    String CONTENT_TYPE_MULTIPART = "multipart/form-data";
    /**
     * get请求时，忽略CONTENT_TYPE
     */
    String CONTENT_TYPE_GET = "*/*";

    /**
     * 添加请求头信息
     *
     * @param key   请求头的key信息
     * @param value 请求头的value信息
     * @return IRequest
     */
    IRequest<V> addHeader(String key, String value);

    /**
     * 设置请求头信息提供者
     *
     * @param iMap 提供一个请求头的接口
     * @return IRequest
     */
    IRequest<V> headers(IMap<String, String> iMap);

    /**
     * 添加请求参数
     *
     * @param key   请求参数的key信息
     * @param value 请求参数的value信息
     * @return IRequest
     */
    IRequest<V> addParams(String key, Object value);

    /**
     * 设置请求参数的提供者，流式编程使用
     *
     * @param iMap 提供一个请求参数的接口
     * @return IRequest
     */
    IRequest<V> params(IMap<String, Object> iMap);

    /**
     * 连接超时时间
     *
     * @param time 超时时间，单位ms
     * @return IRequest
     */
    IRequest<V> connectedTimeout(long time);

    /**
     * 读取超时时间
     *
     * @param time 超时时间，单位ms
     * @return IRequest
     */
    IRequest<V> readTimeout(long time);

    /**
     * 设置缓存时间
     *
     * @param second 缓存时间，单位为s，小于等于0时不缓存
     * @return IRequest
     */
    IRequest<V> cacheTimeout(long second);

    /**
     * 设置请求的tag信息
     *
     * @param tag tag名称
     * @return IRequest
     */
    IRequest<V> tag(String tag);

    /**
     * 设置请求前回调监听
     *
     * @param request 回调
     * @return IRequest
     */
    IRequest<V> pre(ICallback<IRequest<V>> request);

    /**
     * 设置请求结果解析方法，在子线程处理，设置此解析时，全局解析器不会再调用
     *
     * @param parse 解析器
     * @return IRequest
     */
    IRequest<V> parseResponse(IParseResponse<V> parse);

    /**
     * 下载文件时回调进度
     *
     * @param progress 文件下载进度监听器
     * @return IRequest
     */
    IRequest<V> progress(ICallback<Float> progress);

    /**
     * 设置请求解析成功回调
     *
     * @param success 回调监听器
     * @return IRequest
     */
    IRequest<V> success(ICallback2<V, IParseResult<V>> success);

    /**
     * 设置请求解析成功回调，在IO线程回调，调用在{@link #success(ICallback2)}之前
     *
     * @param success 回调监听器
     * @return IRequest
     */
    IRequest<V> successIO(ICallback2<V, IParseResult<V>> success);

    /**
     * 设置请求解析失败回调
     *
     * @param error 回调监听器
     * @return IRequest
     */
    IRequest<V> error(ICallback3<String, String, Throwable> error);

    /**
     * 请求结束回调，成功与失败均会回调
     *
     * @param complete 回调监听器
     * @return IRequest
     */
    IRequest<V> complete(ICallback<IRequest<V>> complete);

    /**
     * 发起请求
     *
     * @return IRequest
     */
    Closeable request();

    /**
     * 发起同步请求
     *
     * @return 返回响应体
     * @throws Exception 可能抛出异常
     */
    IResponse requestSync() throws Exception;

    /**
     * 取消请求
     */
    @Override
    void close();
}
