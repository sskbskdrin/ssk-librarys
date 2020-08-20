package cn.sskbskdrin.http;

/**
 * Created by keayuan on 2019-11-29.
 *
 * @author keayuan
 */
public interface IRequest<V> {

    String ERROR_REAL_REQUEST = "-1001";
    String ERROR_PARSE = "-1002";
    String ERROR_NO_PARSE = "-1003";

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
    String CONTENT_TYPE_GET = "text/html";

    String CONTENT_TYPE_DOWN = "*/*";

    /**
     * 发起get请求，忽略CONTENT_TYPE
     */
    void get();

    /**
     * 发起post请求，content_type为表单{@link #CONTENT_TYPE_FORM}
     */
    void post();

    /**
     * 发起post请求，content_type为json数据类型{@link #CONTENT_TYPE_JSON}
     */
    void postJson();

    /**
     * 发起post请求，content_type为表单{@link #CONTENT_TYPE_MULTIPART}
     */
    void postFile();

    /**
     * 下载文件
     *
     * @param filePath 保存到本地的文件路径
     */
    void download(String filePath);

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
    IRequest<V> pre(IPreRequest request);

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
    IRequest<V> progress(IProgress progress);

    /**
     * 设置请求解析成功回调
     *
     * @param success 回调监听器
     * @return IRequest
     */
    IRequest<V> success(ISuccess<V> success);

    /**
     * 设置请求解析成功回调，在IO线程回调，调用在{@link #success(ISuccess)}之前
     *
     * @param success 回调监听器
     * @return IRequest
     */
    IRequest<V> successIO(ISuccess<V> success);

    /**
     * 设置请求解析失败回调
     *
     * @param error 回调监听器
     * @return IRequest
     */
    IRequest<V> error(IError error);

    /**
     * 请求结束回调，成功与失败均会回调
     *
     * @param complete 回调监听器
     * @return IRequest
     */
    IRequest<V> complete(IComplete complete);

    /**
     * 取消请求
     */
    void cancel();

}
