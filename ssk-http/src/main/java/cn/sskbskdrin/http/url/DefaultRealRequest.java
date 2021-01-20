package cn.sskbskdrin.http.url;

import android.util.Log;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cn.sskbskdrin.http.HTTP;
import cn.sskbskdrin.http.IRealRequest;
import cn.sskbskdrin.http.IRequestBody;
import cn.sskbskdrin.http.IResponse;

/**
 * Created by keayuan on 2019-11-29.
 *
 * @author keayuan
 */
public class DefaultRealRequest implements IRealRequest {
    private static final String TAG = "UrlRequest";
    private static final int GET = 1001;
    private static final int POST = 1002;
    private static final int POST_JSON = 1003;
    private static final int POST_FILE = 1004;

    //boundary就是request头和上传文件内容的分隔符
    private static final String BOUNDARY = "-----------UrlRequest-----------123821742118716";

    private SSLSocketFactory mSSL;
    private HttpURLConnection conn;

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
            SSLContext sslContext = SSLContext.getInstance("TLS");
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
            sslContext.init(new KeyManager[0], tm, new java.security.SecureRandom());
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
    public IResponse get(IRequestBody request) throws Exception {
        return exec(request, GET);
    }

    @Override
    public IResponse post(IRequestBody request) throws Exception {
        return exec(request, POST);
    }

    @Override
    public IResponse postJson(IRequestBody request) throws Exception {
        return exec(request, POST_JSON);
    }

    @Override
    public IResponse postFile(IRequestBody request) throws Exception {
        return exec(request, POST_FILE);
    }

    private IResponse exec(IRequestBody request, int method) throws Exception {
        try {
            conn = generate(request.getUrl(), request.getConnectedTimeout(), request.getReadTimeout(), method == GET
                ? "GET" : "POST", request
                .getHeader());

            HashMap<String, Object> params = method == GET ? null : request.getParams();
            if (HTTP.getConfig().isOpenLog()) {
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
            if (!doDeal(conn, params)) {
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
                        doPostFile(conn, params, request);
                        break;
                    default:
                        break;
                }
            }
            return new UrlResponse(conn);
        } catch (Exception e) {
            close();
            throw e;
        }
    }

    protected boolean doDeal(HttpURLConnection conn, HashMap<String, Object> params) {
        return false;
    }

    private static void doGet(HttpURLConnection conn) throws IOException {
        conn.setDoOutput(false);
        conn.setDoInput(true);
        conn.connect();
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

    private static void doPostFile(HttpURLConnection conn, Map<String, Object> params, IRequestBody progress) throws IOException {
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
        OutputStream out = conn.getOutputStream();
        String end = "\r\n";
        String twoHyphens = "--";

        Map<String, File> fileMap = new HashMap<>();
        Map<String, File[]> fileArrayMap = new HashMap<>();

        long totalLength = 0;
        if (progress != null) {
            progress.publishProgress(0);
        }
        StringBuilder strBuf = new StringBuilder();
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (entry.getValue() instanceof File) {
                    fileMap.put(entry.getKey(), (File) entry.getValue());
                    totalLength += ((File) entry.getValue()).length();
                    continue;
                } else if (entry.getValue() instanceof File[]) {
                    fileArrayMap.put(entry.getKey(), (File[]) entry.getValue());
                    for (File file : (File[]) entry.getValue()) {
                        totalLength += file.length();
                    }
                }
                strBuf.append(end).append(twoHyphens).append(BOUNDARY).append(end);
                strBuf.append("Content-Disposition: form-data; name=");
                strBuf.append('"').append(entry.getKey()).append('"').append(end);
                strBuf.append(entry.getValue());
            }
        }
        out.write(strBuf.toString().getBytes());

        long sendLen = 0;
        for (Map.Entry<String, File> entry : fileMap.entrySet()) {
            writeFile(out, entry.getKey(), entry.getValue(), totalLength, sendLen, progress);
            sendLen += entry.getValue().length();
        }
        for (Map.Entry<String, File[]> entry : fileArrayMap.entrySet()) {
            for (File file : entry.getValue()) {
                writeFile(out, entry.getKey(), file, totalLength, sendLen, progress);
                sendLen += file.length();
            }
        }
        strBuf.setLength(0);
        strBuf.append(end).append(twoHyphens).append(BOUNDARY).append(twoHyphens).append(end);
        out.write(strBuf.toString().getBytes());
        if (progress != null) {
            progress.publishProgress(1);
        }
        out.flush();
        out.close();
    }

    private static void writeFile(OutputStream out, String key, File file, final long totalLength, long sendLen,
                                  IRequestBody progress) throws IOException {
        final String end = "\r\n";
        final String twoHyphens = "--";
        StringBuilder strBuf = new StringBuilder();
        String filename = file.getName();
        String contentType = URLConnection.guessContentTypeFromName(filename);

        strBuf.append(end).append(twoHyphens).append(BOUNDARY).append(end);
        strBuf.append("Content-Disposition: form-data; name=").append('"').append(key).append('"');
        strBuf.append("; filename=").append('"').append(filename).append('"').append(end);
        strBuf.append("Content-Type:").append(contentType).append(end).append(end);
        out.write(strBuf.toString().getBytes());

        FileInputStream in = new FileInputStream(file);
        int ret;
        byte[] buf = new byte[1024 * 10];
        long lastTime = 0;
        while ((ret = in.read(buf)) != -1) {
            out.write(buf, 0, ret);
            sendLen += ret;
            if (System.currentTimeMillis() - lastTime > 500) {
                lastTime = System.currentTimeMillis();
                if (progress != null) {
                    progress.publishProgress(Double.valueOf(sendLen * 1d / totalLength).floatValue());
                }
            }
        }
        in.close();
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

    @Override
    public void close() {
        if (conn != null) {
            conn.disconnect();
        }
        conn = null;
    }
}
