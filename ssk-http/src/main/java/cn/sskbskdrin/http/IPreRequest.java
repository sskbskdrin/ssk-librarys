package cn.sskbskdrin.http;

public interface IPreRequest {
    /**
     * 在开始请求之前回调run ui thread
     *
     * @param tag 请求tag
     */
    void onPreRequest(String tag);
}
