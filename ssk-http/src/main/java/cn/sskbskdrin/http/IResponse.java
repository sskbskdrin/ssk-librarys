package cn.sskbskdrin.http;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by keayuan on 2020/4/7.
 *
 * @author keayuan
 */
public interface IResponse extends Closeable {
    /**
     * 返回byte数据
     *
     * @return 数据数组
     */
    byte[] bytes() throws IOException;

    /**
     * 返回数据转字符串
     *
     * @return 返回的字符串
     */
    String string() throws IOException;

    int code();

    String message();

    Throwable throwable();

    boolean isSuccess();

    InputStream byteStream();

    Map<String, List<String>> getHeaders();

    long getContentLength();
}
