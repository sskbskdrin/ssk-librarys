package cn.sskbskdrin.http.impl;

import android.util.Log;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cn.sskbskdrin.http.IRealRequest;
import cn.sskbskdrin.http.IRequestBody;
import cn.sskbskdrin.http.IResponseCallback;

/**
 * Created by keayuan on 2019-11-29.
 *
 * @author keayuan
 */
public class UrlRequest implements IRealRequest {
    private static final String TAG = "UrlRequest";
    private static final int GET = 1001;
    private static final int POST = 1002;
    private static final int POST_JSON = 1003;
    private static final int POST_FILE = 1004;

    //boundary就是request头和上传文件内容的分隔符
    private static final String BOUNDARY = "-----------UrlRequest-----------123821742118716";

    private final boolean openLog;
    private SSLSocketFactory mSSL;

    public UrlRequest(boolean openLog) {
        this.openLog = openLog;
    }

    public final void setSSLSocketFactory(SSLSocketFactory ssl) {
        mSSL = ssl;
    }

    private HttpURLConnection generate(String url, long connectedTimeout, long readTimeout, String method, Map<String
        , String> header) throws Exception {
        SSLSocketFactory ssl;
        if (mSSL == null) {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String string, SSLSession ssl) {
                    return true;
                }
            });
            SSLContext sslContext = SSLContext.getInstance("SSL");
            TrustManager[] tm = {new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }};
            sslContext.init(null, tm, new java.security.SecureRandom());
            ssl = sslContext.getSocketFactory();
        } else {
            ssl = mSSL;
        }
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setConnectTimeout((int) connectedTimeout);
        conn.setReadTimeout((int) readTimeout);
        conn.setDoOutput(false);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        if (conn instanceof HttpsURLConnection) {
            ((HttpsURLConnection) conn).setSSLSocketFactory(ssl);
        }
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (Linux; X11;Windows; U; Windows NT 6.1; zh-CN)");
        conn.setRequestProperty("Content-Type", "text/html");
        conn.setRequestProperty("Charset", "utf-8");
        conn.setRequestMethod(method);

        // header
        for (Map.Entry<String, String> entry : header.entrySet()) {
            conn.setRequestProperty(entry.getKey(), entry.getValue());
        }

        return conn;
    }

    @Override
    public void get(IRequestBody request, IResponseCallback callback) {
        exec(request, GET, callback);
    }

    @Override
    public void post(IRequestBody request, IResponseCallback callback) {
        exec(request, POST, callback);
    }

    @Override
    public void postJson(IRequestBody request, IResponseCallback callback) {
        exec(request, POST_JSON, callback);
    }

    @Override
    public void postFile(IRequestBody request, IResponseCallback callback) {
        exec(request, POST_FILE, callback);
    }

    @Override
    public void download(IRequestBody request, String filePath, IResponseCallback callback) {
        exec(request, filePath, callback);
    }

    private void exec(IRequestBody request, int method, IResponseCallback callback) {
        HttpURLConnection conn = null;
        try {
            conn = generate(request.getUrl(), request.getConnectedTimeout(), request.getReadTimeout(), method == GET
                ? "GET" : "POST", request
                .getHeader());

            HashMap<String, Object> params = method == GET ? null : request.getParams();
            if (openLog) {
                Log.d(TAG, "url: " + request.getUrl());
                Log.d(TAG, "connectedTimeout: " + request.getConnectedTimeout() + "ms");
                Log.d(TAG, "readTimeout: " + request.getReadTimeout() + "ms");
                Log.d(TAG, "method: " + (method == GET ? "GET" : "POST"));
                Map<String, List<String>> requestProperties = conn.getRequestProperties();
                for (Map.Entry<String, List<String>> entry : requestProperties.entrySet()) {
                    Log.d(TAG, "header: " + entry.getKey() + "=" + entry.getValue());
                }
                if (params != null) {
                    for (Map.Entry<String, Object> entry : params.entrySet()) {
                        Log.d(TAG, "params: " + entry.getKey() + "=" + entry.getValue());
                    }
                }
            }
            // params
            switch (method) {
                case GET:
                    doGet(conn);
                    break;
                case POST:
                    doPost(conn, params);
                    break;
                case POST_JSON:
                    doPostJson(conn, params);
                    break;
                case POST_FILE:
                    doPostFile(conn, params, request.getFileParams());
                    break;
            }

            // response
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = conn.getInputStream();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                byte[] buf = new byte[1024 * 10];
                int len;
                while ((len = in.read(buf)) >= 0) {
                    os.write(buf, 0, len);
                }
                buf = os.toByteArray();
                in.close();
                if (callback != null) {
                    callback.onResponseData(buf);
                }
                if (openLog) {
                    Log.d(TAG, "response:" + new String(buf));
                }
            } else {
                if (callback != null) {
                    callback.onError(String.valueOf(conn.getResponseCode()), conn.getResponseMessage(), null);
                }
                if (openLog) {
                    Log.e(TAG, "response error: code=" + conn.getResponseCode() + " msg=" + conn.getResponseMessage());
                }
            }
        } catch (Exception e) {
            if (openLog) {
                Log.e(TAG, "response error: " + e.getLocalizedMessage(), e);
            }
            if (callback != null) {
                callback.onError("-1", "", e);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private String parseParams(HashMap<String, String> params) {
        if (params == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey());
            sb.append('=');
            sb.append(entry.getValue());
            sb.append('\n');
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    private void exec(IRequestBody request, String filePath, IResponseCallback callback) {
        HttpURLConnection conn = null;
        try {
            conn = generate(request.getUrl(), request.getConnectedTimeout(), request.getReadTimeout(), "GET",
                request.getHeader());

            // header
            HashMap<String, String> header = request.getHeader();
            for (Map.Entry<String, String> entry : header.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }

            // response
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = conn.getInputStream();
                File file = doDown(in, conn.getContentLength(), filePath, callback);
                in.close();
                if (callback != null) {
                    callback.onResponseFile(file);
                }
            } else {
                if (callback != null) {
                    callback.onError(String.valueOf(conn.getResponseCode()), conn.getResponseMessage(), null);
                }
            }
        } catch (Exception e) {
            if (callback != null) {
                callback.onError("-1", "", e);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static void doGet(HttpURLConnection conn) throws IOException {
        conn.setDoOutput(false);
        conn.setDoInput(true);
        conn.connect();
    }

    private File doDown(InputStream in, int totalLen, String filePath, IResponseCallback callback) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();

        FileOutputStream os = new FileOutputStream(file);
        long start = System.currentTimeMillis();

        byte[] buf = new byte[1024 * 10];
        int ret, len = 0;
        if (callback != null) {
            callback.onProgress(0);
        }
        while ((ret = in.read(buf)) >= 0) {
            os.write(buf, 0, ret);
            len += ret;
            if (System.currentTimeMillis() - start > 500) {
                start = System.currentTimeMillis();
                if (callback != null) {
                    callback.onProgress(len * 1f / totalLen);
                }
            }
        }
        if (callback != null) {
            callback.onProgress(1);
        }
        in.close();
        return file;
    }

    private static void doPost(HttpURLConnection conn, Map<String, Object> params) throws IOException {
        conn.setDoOutput(true);
        conn.setDoInput(true);
        OutputStream out = conn.getOutputStream();

        StringBuilder strBuf = new StringBuilder();
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                strBuf.append(entry.getKey()).append("=").append(entry.getValue()).append('&');
            }
            if (strBuf.length() > 0) {
                strBuf.setLength(strBuf.length() - 1);
            }
        }

        out.write(strBuf.toString().getBytes());

        out.flush();
        out.close();
    }

    private static void doPostFile(HttpURLConnection conn, Map<String, Object> params, Map<String, File> fileMap) throws IOException {
        conn.setDoOutput(true);
        conn.setDoInput(true);
        OutputStream out = conn.getOutputStream();
        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
        String end = "\r\n";
        String twoHyphens = "--";

        StringBuilder strBuf = new StringBuilder();
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                strBuf.append(end).append(twoHyphens).append(BOUNDARY).append(end);
                strBuf.append("Content-Disposition: form-data; name=");
                strBuf.append('"').append(entry.getKey()).append('"').append(end);
                strBuf.append(entry.getValue());
            }
        }
        out.write(strBuf.toString().getBytes());

        if (fileMap != null && fileMap.size() > 0) {
            for (Map.Entry<String, File> entry : fileMap.entrySet()) {
                strBuf.setLength(0);
                File file = entry.getValue();
                String filename = file.getName();
                String contentType = URLConnection.guessContentTypeFromName(filename);

                strBuf.append(end).append(twoHyphens).append(BOUNDARY).append(end);
                strBuf.append("Content-Disposition: form-data; name=").append('"').append(entry.getKey()).append('"');
                strBuf.append(";filename=").append(filename).append('"').append(end);
                strBuf.append("Content-Type:").append(contentType).append(end).append(end);
                out.write(strBuf.toString().getBytes());

                FileInputStream in = new FileInputStream(file);
                int ret;
                byte[] buf = new byte[1024 * 10];
                while ((ret = in.read(buf)) != -1) {
                    out.write(buf, 0, ret);
                }
                in.close();
            }
        }
        out.flush();
        out.close();
    }

    private static void doPostJson(HttpURLConnection conn, Map<String, Object> params) throws IOException {
        conn.setDoOutput(true);
        conn.setDoInput(true);
        OutputStream out = new DataOutputStream(conn.getOutputStream());
        JSONObject object = new JSONObject(params == null ? new HashMap() : params);
        out.write(object.toString().getBytes());

        out.flush();
        out.close();
    }
}
