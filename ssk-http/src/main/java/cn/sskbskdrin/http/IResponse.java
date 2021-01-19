package cn.sskbskdrin.http;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by keayuan on 2020/4/7.
 *
 * @author keayuan
 */
public interface IResponse {
    /**
     * 返回byte数据
     *
     * @return 数据数组
     */
    byte[] bytes();

    /**
     * 返回数据转字符串
     *
     * @return 返回的字符串
     */
    String string();

    int code();

    String message();

    Exception exception();

    boolean isSuccess();

    InputStream byteStream();

    Map<String, List<String>> getHeaders();

    long getContentLength();
}
