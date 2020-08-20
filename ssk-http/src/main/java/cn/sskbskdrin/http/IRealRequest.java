package cn.sskbskdrin.http;

/**
 * 实际发起请求接口，可实现不同方式，例OkHttp、Volley等等
 * Created by keayuan on 2019-11-29.
 *
 * @author keayuan
 */
public interface IRealRequest {

    /**
     * 发起get请求
     *
     * @param request  请求体，默认为HttpRequest
     * @param callback 请求结果回调，默认为HttpRequest
     */
    void get(IRequestBody request, IRequestCallback callback);

    /**
     * 发起post请求
     *
     * @param request  请求体，默认为HttpRequest
     * @param callback 请求结果回调，默认为HttpRequest
     */
    void post(IRequestBody request, IRequestCallback callback);

    /**
     * 发起post json请求
     *
     * @param request  请求体，默认为HttpRequest
     * @param callback 请求结果回调，默认为HttpRequest
     */
    void postJson(IRequestBody request, IRequestCallback callback);

    /**
     * 发起post file请求
     *
     * @param request  请求体，默认为HttpRequest
     * @param callback 请求结果回调，默认为HttpRequest
     */
    void postFile(IRequestBody request, IRequestCallback callback);

    /**
     * 发起下载请求，post方式
     *
     * @param request  请求体，默认为HttpRequest
     * @param filePath 要保存的文件路径，包含文件名
     * @param callback 请求结果回调，默认为HttpRequest
     */
    void download(IRequestBody request, String filePath, IRequestCallback callback);
}
