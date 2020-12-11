package cn.sskbskdrin.http;

import android.os.Handler;
import android.os.Looper;
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
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by keayuan on 2020/10/23.
 *
 * @author keayuan
 */
public final class HttpUtils {
    private static final String TAG = "UrlRequest";
    private static final int GET = 1001;
    private static final int POST = 1002;
    private static final int POST_JSON = 1003;
    private static final int POST_FILE = 1004;
    private static final int DOWNLOAD = 1005;

    //boundary就是request头和上传文件内容的分隔符
    private static final String BOUNDARY = "-----------UrlRequest-----------123821742118716";

    private static boolean openLog;

    private static Executor executor = new ThreadPoolExecutor(0, 10, 10, TimeUnit.SECONDS,
        new LinkedBlockingQueue<Runnable>());
    private static Handler mainHandler = new Handler(Looper.getMainLooper());

    private Builder builder;

    private HttpUtils(Builder builder) {
        this.builder = builder;
    }

    private HttpURLConnection generate(String url, long connectedTimeout, long readTimeout, String method) throws Exception {
        SSLSocketFactory ssl;
        if (builder.mSSL == null) {
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
            ssl = builder.mSSL;
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
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
        conn.setRequestProperty("Content-Type", "text/html");
        conn.setRequestProperty("Charset", "utf-8");
        conn.setRequestMethod(method);

        // header
        if (builder.header != null) {
            for (Map.Entry<String, String> entry : builder.header.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        return conn;
    }

    public static void openLog(boolean open) {
        openLog = open;
    }

    private void request(final int method, final String filePath) {
        if (builder.sync) {
            exec(method);
        } else {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    if (DOWNLOAD == method) {
                        download(filePath);
                    } else {
                        exec(method);
                    }
                }
            });
        }
    }

    private void error(final String str, final Exception e) {
        if (builder.error == null) return;
        if (builder.sync) {
            builder.error.call(str, e);
        } else {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    builder.error.call(str, e);
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void success(final T res) {
        if (builder.success == null) return;
        if (builder.sync) {
            builder.success.call(res, null);
        } else {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    builder.success.call(res, null);
                }
            });
        }
    }

    private void progress(final double progress) {
        if (builder.progress == null) return;
        if (builder.sync) {
            builder.progress.call(((int) (progress * 100)) / 100f, null);
        } else {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    builder.progress.call(((int) (progress * 100)) / 100f, null);
                }
            });
        }
    }

    private void download(String filePath) {
        HttpURLConnection conn = null;
        try {
            // header
            StringBuilder sb = new StringBuilder();
            if (builder.params != null) {
                for (Map.Entry<String, Object> entry : builder.params.entrySet()) {
                    if (sb.length() > 0) sb.append('&');
                    sb.append(entry.getKey()).append('=').append(entry.getValue());
                }
            }
            conn = generate(builder.url + "?" + sb.toString(), builder.connectedTimeout, 0, "GET");

            // response
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = conn.getInputStream();
                File file = doDown(in, conn.getContentLength(), filePath);
                in.close();
                success(file);
            } else {
                error(conn.getResponseMessage(), null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            error(e.getLocalizedMessage(), e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private void exec(int method) {
        HttpURLConnection conn = null;
        try {
            conn = generate(builder.url, builder.connectedTimeout, builder.readTimeout, method == GET ? "GET" : "POST");

            long startTime = System.currentTimeMillis();
            // params
            switch (method) {
                case GET:
                    doGet(conn);
                    break;
                case POST:
                    doPost(conn, builder.params);
                    break;
                case POST_JSON:
                    doPostJson(conn, builder.params);
                    break;
                case POST_FILE:
                    doPostFile(conn, builder.params);
                    break;
                default:
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
                if (openLog) {
                    Log.d(TAG, "response: time=" + (System.currentTimeMillis() - startTime));
                    Log.d(TAG, "response:" + new String(buf));
                }
                success(new String(buf));
            } else {
                if (openLog) {
                    Log.w(TAG, "response error: code=" + conn.getResponseCode() + " msg=" + conn.getResponseMessage());
                }
                error(conn.getResponseMessage(), null);
            }
        } catch (Exception e) {
            if (openLog) {
                Log.w(TAG, "response error: " + e.getLocalizedMessage(), e);
            }
            error(e.getLocalizedMessage(), e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private void doGet(HttpURLConnection conn) throws IOException {
        conn.setDoOutput(false);
        conn.setDoInput(true);
        conn.connect();
    }

    private File doDown(InputStream in, int totalLen, String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();

        FileOutputStream os = new FileOutputStream(file);
        long start = System.currentTimeMillis();

        byte[] buf = new byte[1024 * 10];
        int ret, len = 0;
        progress(0);
        while ((ret = in.read(buf)) >= 0) {
            os.write(buf, 0, ret);
            len += ret;
            if (System.currentTimeMillis() - start > 500) {
                start = System.currentTimeMillis();

                progress(len * 1f / totalLen);
            }
        }
        progress(1);
        in.close();
        return file;
    }

    private void doPost(HttpURLConnection conn, Map<String, Object> params) throws IOException {
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

    private void doPostFile(HttpURLConnection conn, Map<String, Object> params) throws IOException {
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
        OutputStream out = conn.getOutputStream();
        String end = "\r\n";
        String twoHyphens = "--";

        Map<String, File> fileMap = new HashMap<>();
        Map<String, File[]> fileArrayMap = new HashMap<>();

        long totalLength = 0;
        progress(0);
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
            writeFile(out, entry.getKey(), entry.getValue(), totalLength, sendLen);
            sendLen += entry.getValue().length();
        }
        for (Map.Entry<String, File[]> entry : fileArrayMap.entrySet()) {
            for (File file : entry.getValue()) {
                writeFile(out, entry.getKey(), file, totalLength, sendLen);
                sendLen += file.length();
            }
        }
        strBuf.setLength(0);
        strBuf.append(end).append(twoHyphens).append(BOUNDARY).append(twoHyphens).append(end);
        out.write(strBuf.toString().getBytes());
        progress(1);
        out.flush();
        out.close();
    }

    private void writeFile(OutputStream out, String key, File file, final long totalLength, long sendLen) throws IOException {
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
                progress(sendLen * 1d / totalLength);
            }
        }
        in.close();
    }

    private static void doPostJson(HttpURLConnection conn, Map<String, Object> params) throws IOException {
        conn.setDoOutput(true);
        conn.setDoInput(true);
        OutputStream out = new DataOutputStream(conn.getOutputStream());
        JSONObject object = new JSONObject(params == null ? new HashMap<String, Object>() : params);
        out.write(object.toString().getBytes());

        out.flush();
        out.close();
    }

    public interface Callback<T> {
        void call(T res, Exception e);
    }

    public static Builder create(String url) {
        return new Builder(url);
    }

    public static class Builder {
        private final String url;
        private Map<String, Object> params = new HashMap<>();
        private Map<String, String> header = new HashMap<>();
        private Callback success;
        private Callback<String> error;
        private Callback<Float> progress;
        private boolean sync = false;
        private SSLSocketFactory mSSL;
        private long connectedTimeout;
        private long readTimeout;

        private Builder(String url) {
            this.url = url;
        }

        public Builder setSSLSocketFactory(SSLSocketFactory ssl) {
            mSSL = ssl;
            return this;
        }

        public Builder connectedTimeout(long timeout) {
            connectedTimeout = timeout;
            return this;
        }

        public Builder readTimeout(long timeout) {
            readTimeout = timeout;
            return this;
        }

        public Builder addHeader(String key, String value) {
            header.put(key, value);
            return this;
        }

        public Builder addParams(String key, Object value) {
            params.put(key, value);
            return this;
        }

        public Builder setParams(Map<String, Object> params) {
            if (params == null) {
                this.params.clear();
            } else {
                this.params = params;
            }
            return this;
        }

        public Builder onSuccess(Callback<String> callback) {
            if (this.success == null) {
                this.success = callback;
            }
            return this;
        }

        public Builder onError(Callback<String> callback) {
            this.error = callback;
            return this;
        }

        public Builder onProgress(Callback<Float> progress) {
            this.progress = progress;
            return this;
        }

        public Builder setSync(boolean sync) {
            this.sync = sync;
            return this;
        }

        public void get() {
            new HttpUtils(this).request(GET, null);
        }

        public void post() {
            new HttpUtils(this).request(POST, null);
        }

        public void postJson() {
            new HttpUtils(this).request(POST_JSON, null);
        }

        public void postFile() {
            new HttpUtils(this).request(POST_FILE, null);
        }

        public void downLoad(final String filePath, final Callback<File> success) {
            this.success = success;
            new HttpUtils(this).request(DOWNLOAD, filePath);
        }

    }
}
