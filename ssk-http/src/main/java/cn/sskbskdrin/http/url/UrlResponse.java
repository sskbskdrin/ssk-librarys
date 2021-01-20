package cn.sskbskdrin.http.url;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import cn.sskbskdrin.http.IResponse;

/**
 * Created by keayuan on 2021/1/19.
 *
 * @author keayuan
 */
class UrlResponse implements IResponse {

    private HttpURLConnection connection;
    private String code;
    private String msg;
    private Throwable throwable;

    UrlResponse(HttpURLConnection connection) {
        this.connection = connection;
    }

    UrlResponse(String code, String desc, Exception e) {
        this.code = code;
        this.msg = desc;
        this.throwable = e;
    }

    @Override
    public byte[] bytes() throws IOException {
        if (connection != null) {
            InputStream is = connection.getInputStream();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buf = new byte[1024 * 8];
            int len;
            while ((len = is.read(buf)) != -1) {
                os.write(buf, 0, len);
            }
            return os.toByteArray();
        }
        return new byte[0];
    }

    @Override
    public String string() throws IOException {
        return new String(bytes());
    }

    @Override
    public int code() {
        if (connection != null) {
            try {
                return connection.getResponseCode();
            } catch (IOException ignored) {
            }
        }
        if (code == null || code.length() == 0) return -1;
        return Integer.parseInt(code);
    }

    @Override
    public String message() {
        if (connection != null) {
            try {
                return connection.getResponseMessage();
            } catch (IOException e) {
            }
        }
        return msg;
    }

    @Override
    public Throwable throwable() {
        return throwable;
    }

    @Override
    public boolean isSuccess() {
        int code = code();
        return code >= 200 && code < 300;
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

    @Override
    public void close() {
        if (connection != null) {
            connection.disconnect();
            connection = null;
        }
    }
}
