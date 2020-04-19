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
     */
    void get(IRequestBody request, IRequestCallback callback);

    /**
     * 发起post请求
     */
    void post(IRequestBody request, IRequestCallback callback);

    /**
     * 发起post json请求
     */
    void postJson(IRequestBody request, IRequestCallback callback);

    /**
     * 发起post file请求
     */
    void postFile(IRequestBody request, IRequestCallback callback);

    /**
     * 发起下载请求，post方式
     */
    void download(IRequestBody request, String filePath, IRequestCallback callback);
}
