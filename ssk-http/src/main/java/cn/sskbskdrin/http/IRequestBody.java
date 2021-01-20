package cn.sskbskdrin.http;

import java.util.HashMap;

public interface IRequestBody {

    /**
     * 获取完整url
     *
     * @return 请求的url
     */
    String getUrl();

    /**
     * 获取请求的参数信息
     *
     * @return 请求参数
     */
    HashMap<String, Object> getParams();

    /**
     * 获取请求头信息
     *
     * @return 请求头信息
     */
    HashMap<String, String> getHeader();

    /**
     * 获取连接超时时间
     *
     * @return 超时时间毫秒
     */
    long getConnectedTimeout();

    /**
     * 获取读取超时时间
     *
     * @return 超时时间毫秒
     */
    long getReadTimeout();

    void publishProgress(float progress);
}
