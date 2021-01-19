package cn.sskbskdrin.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

/**
 * Created by keayuan on 2021/1/19.
 *
 * @author keayuan
 */
class UrlResponse implements IResponse {

    private HttpURLConnection connection;

    UrlResponse(HttpURLConnection connection) {
        this.connection = connection;
    }

    @Override
    public byte[] bytes() {
        try {
            InputStream is = connection.getInputStream();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buf = new byte[1024 * 8];
            int len;
            while ((len = is.read(buf)) != -1) {
                os.write(buf, 0, len);
            }
            return os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    @Override
    public String string() {
        return new String(bytes());
    }

    @Override
    public int code() {
        try {
            return connection.getResponseCode();
        } catch (IOException ignored) {
        }
        return -1;
    }

    @Override
    public String message() {
        return null;
    }

    @Override
    public Exception exception() {
        return null;
    }

    @Override
    public boolean isSuccess() {
        try {
            int code = connection.getResponseCode();
            return code >= 200 && code < 300;
        } catch (IOException ignored) {
        }
        return false;
    }

    @Override
    public InputStream byteStream() {
        try {
            return connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        return connection.getHeaderFields();
    }

    @Override
    public long getContentLength() {
        return connection.getContentLength();
    }
}
