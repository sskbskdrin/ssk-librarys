package cn.sskbskdrin.http;

import java.io.File;

/**
 * Created by keayuan on 2019-11-29.
 *
 * @author keayuan
 */
public interface IRequest<V> {

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
    IRequest<V> get();

    /**
     * 发起post请求，content_type为表单{@link #CONTENT_TYPE_FORM}
     */
    IRequest<V> post();

    /**
     * 发起post请求，content_type为json数据类型{@link #CONTENT_TYPE_JSON}
     */
    IRequest<V> postJson();

    /**
     * 发起post请求，content_type为表单{@link #CONTENT_TYPE_MULTIPART}
     */
    IRequest<V> postFile();

    /**
     * 下载文件
     *
     * @param filePath 保存到本地的文件路径
     */
    IRequest<V> download(String filePath);

    /**
     * 添加请求头信息
     *
     * @param key   请求头的key信息
     * @param value 请求头的value信息
     */
    IRequest<V> addHeader(String key, String value);

    /**
     * 设置请求头信息提供者
     *
     * @param iMap 提供一个请求头的接口
     */
    IRequest<V> headers(IMap<String, String> iMap);

    /**
     * 添加请求参数
     *
     * @param key   请求参数的key信息
     * @param value 请求参数的value信息
     */
    IRequest<V> addParams(String key, String value);

    /**
     * 添加文件类型请求参数，仅文件请求时有效{@link #postFile()}
     *
     * @param key   请求参数的key信息
     * @param value 请求参数的文件对象
     */
    IRequest<V> addParams(String key, File value);

    /**
     * 设置请求参数的提供者，流式编程使用
     *
     * @param iMap 提供一个请求参数的接口
     */
    IRequest<V> params(IMap<String, String> iMap);

    /**
     * 连接超时时间
     *
     * @param time 超时时间，单位ms
     */
    IRequest<V> connectedTimeout(long time);

    /**
     * 读取超时时间
     *
     * @param time 超时时间，单位ms
     */
    IRequest<V> readTimeout(long time);

    /**
     * 建造响应实体
     *
     * @param response 响应实体
     */
    IRequest<V> response(IResponse<V> response);

    /**
     * 设置请求的tag信息
     *
     * @param tag tag名称
     */
    IRequest<V> tag(String tag);

    IRequest<V> pre(IPreRequest request);

    /**
     * 设置请求结果解析方法，在子线程处理
     *
     * @param parse 解析器
     */
    IRequest<V> parseResponse(IParseResponse<V> parse);

    /**
     * @param clazz 如果V为List，则class应为List中实体类的类型
     */
    IRequest<V> parseResponse(Class<?> clazz);

    /**
     * 下载文件时回调进度
     */
    IRequest<V> progress(IProgress progress);

    /**
     * 设置请求解析成功回调
     *
     * @param success 回调监听器
     */
    IRequest<V> success(ISuccess<V> success);

    /**
     * 设置请求解析失败回调
     *
     * @param error 回调监听器
     */
    IRequest<V> error(IError error);

    /**
     * 请求结束回调，成功与失败均会回调
     *
     * @param complete 回调监听器
     */
    IRequest<V> complete(IComplete complete);

    /**
     * 发起请求
     */
    void request();

    void request(Callback<V> callback);

    /**
     * 取消请求
     */
    void cancel();

}