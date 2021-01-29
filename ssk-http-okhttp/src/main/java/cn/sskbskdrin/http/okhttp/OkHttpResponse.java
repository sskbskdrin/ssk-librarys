package cn.sskbskdrin.http.okhttp;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sskbskdrin.http.IResponse;
import okhttp3.Headers;
import okhttp3.Response;

/**
 * Created by keayuan on 2021/1/20.
 *
 * @author keayuan
 */
public class OkHttpResponse implements IResponse {

    private Response response;
    private String code;
    private String msg;
    private Throwable throwable;

    public OkHttpResponse(Response response) {
        this.response = response;
    }

    public OkHttpResponse(String code, String desc, Exception e) {
        this.code = code;
        this.msg = desc;
        this.throwable = e;
    }

    @Override
    public byte[] bytes() throws IOException {
        return check() ? response.body().bytes() : new byte[0];
    }

    @Override
    public String string() throws IOException {
        return new String(bytes());
    }

    @Override
    public int code() {
        if (response != null) {
            return response.code();
        }
        if (code == null || code.length() == 0) return -1;
        return Integer.parseInt(code);
    }

    @Override
    public String message() {
        return response == null ? msg : response.message();
    }

    @Override
    public Throwable throwable() {
        return throwable;
    }

    @Override
    public boolean isSuccess() {
        return response != null && response.isSuccessful();
    }

    @Override
    public InputStream byteStream() {
        return check() ? response.body().byteStream() : null;
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        Map<String, List<String>> header = new HashMap<>();
        Headers headers = response.headers();
        for (String name : headers.names()) {
            header.put(name, headers.values(name));
        }
        return header;
    }

    @Override
    public long getContentLength() {
        return check() ? response.body().contentLength() : -1;
    }

    @Override
    public void close() throws IOException {
        response.close();
    }

    private boolean check() {
        return response != null && response.body() != null;
    }

    @Override
    public Response getRawResponse() {
        return response;
    }
}
