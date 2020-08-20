package cn.sskbskdrin.http;

/**
 * Created by sskbskdrin on 2020/8/20.
 *
 * @author sskbskdrin
 */
public interface IRRequest {

    /**
     * 发起get请求
     *
     * @param request 请求体，默认为HttpRequest
     */
    Response get(IRequestBody request);

    /**
     * 发起post请求
     *
     * @param request 请求体，默认为HttpRequest
     */
    Response post(IRequestBody request);

    /**
     * 发起post json请求
     *
     * @param request 请求体，默认为HttpRequest
     */
    Response postJson(IRequestBody request);

    /**
     * 发起post file请求
     *
     * @param request 请求体，默认为HttpRequest
     */
    Response postFile(IRequestBody request, IProgress progress);

    /**
     * 发起下载请求，post方式
     *
     * @param request  请求体，默认为HttpRequest
     * @param filePath 要保存的文件路径，包含文件名
     */
    Response download(IRequestBody request, String filePath, IProgress progress);
}
