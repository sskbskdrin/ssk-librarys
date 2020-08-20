package cn.sskbskdrin.http;

/**
 * Created by keayuan on 2019-11-29.
 *
 * @author keayuan
 */
public interface ISuccess<V> {
    /**
     * 请求成功，且解析成功回调
     *
     * @param tag      请求的tag
     * @param result   解析成功结果
     * @param response 响应结果
     */
    void success(String tag, V result, IResponse response);
}
