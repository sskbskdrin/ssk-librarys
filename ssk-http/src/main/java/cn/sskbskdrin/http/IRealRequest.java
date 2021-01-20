package cn.sskbskdrin.http;

import java.io.Closeable;
import java.io.IOException;

/**
 * 实际发起请求接口，可实现不同方式，例OkHttp、Volley等等
 * Created by keayuan on 2019-11-29.
 *
 * @author keayuan
 */
public interface IRealRequest extends Closeable {

    /**
     * 发起get请求
     *
     * @param request 请求体，默认为HttpRequest
     * @return
     */
    IResponse get(IRequestBody request) throws Exception;

    /**
     * 发起post请求
     *
     * @param request 请求体，默认为HttpRequest
     * @return
     */
    IResponse post(IRequestBody request) throws Exception;

    /**
     * 发起post json请求
     *
     * @param request 请求体，默认为HttpRequest
     * @return
     */
    IResponse postJson(IRequestBody request) throws Exception;

    /**
     * 发起post file请求
     *
     * @param request 请求体，默认为HttpRequest
     * @return
     */
    IResponse postFile(IRequestBody request) throws Exception;

    @Override
    void close() throws IOException;
}
