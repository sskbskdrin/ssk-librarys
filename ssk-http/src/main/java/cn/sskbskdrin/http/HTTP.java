package cn.sskbskdrin.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * Created by sskbskdrin on 2021/1/19.
 *
 * @author sskbskdrin
 */
public final class HTTP {
    public static final String METHOD_GET = IRequest.CONTENT_TYPE_GET;
    public static final String METHOD_POST = IRequest.CONTENT_TYPE_FORM;
    public static final String METHOD_POST_FILE = IRequest.CONTENT_TYPE_MULTIPART;
    public static final String METHOD_POST_JSON = IRequest.CONTENT_TYPE_JSON;

    static {
        try {
            Class.forName("cn.sskbskdrin.http.okhttp.OkHttpRealRequest");
        } catch (ClassNotFoundException ignored) {
        }
    }

    public static Config getConfig() {
        return Config.INSTANCE;
    }

    public static <V> IRequest<V> url(String url, Type type, String method) {
        return new HttpRequest<>(url, type, method);
    }

    public static <V> IRequest<V> url(String url, Class<V> tClass, String method) {
        return new HttpRequest<>(url, tClass, method);
    }

    public static <V> IRequest<V> url(String url, TypeToken<V> token, String method) {
        return new HttpRequest<>(url, token.getType(), method);
    }

    public static <V> IRequest<V> get(String url, Type type) {
        return url(url, type, METHOD_GET);
    }

    public static <V> IRequest<V> get(String url, Class<V> clazz) {
        return url(url, clazz, METHOD_GET);
    }

    public static <V> IRequest<V> get(String url, TypeToken<V> token) {
        return url(url, token, METHOD_GET);
    }

    public static <V> IRequest<V> post(String url, Type tClass) {
        return url(url, tClass, METHOD_POST);
    }

    public static <V> IRequest<V> post(String url, Class<V> tClass) {
        return url(url, tClass, METHOD_POST);
    }

    public static <V> IRequest<V> post(String url, TypeToken<V> token) {
        return url(url, token, METHOD_POST);
    }

    public static <V> IRequest<V> postJson(String url, Type tClass) {
        return url(url, tClass, METHOD_POST_JSON);
    }

    public static <V> IRequest<V> postJson(String url, Class<V> tClass) {
        return url(url, tClass, METHOD_POST_JSON);
    }

    public static <V> IRequest<V> postJson(String url, TypeToken<V> token) {
        return url(url, token, METHOD_POST_JSON);
    }

    public static <V> IRequest<V> postFile(String url, Type tClass) {
        return url(url, tClass, METHOD_POST_FILE);
    }

    public static <V> IRequest<V> postFile(String url, Class<V> tClass) {
        return url(url, tClass, METHOD_POST_FILE);
    }

    public static <V> IRequest<V> postFile(String url, TypeToken<V> token) {
        return url(url, token, METHOD_POST_FILE);
    }

    public static IRequest<File> download(String url, String filePath) {
        return url(url, File.class, METHOD_GET).parseResponse(new FileParse(filePath));
    }

    public static IParseResponse<String> STRING_PARSE_RESPONSE = new IParseResponse<String>() {
        @Override
        public IParseResult<String> parse(String tag, IResponse response, Type type, IRequestBody request) throws Throwable {
            return new Result<>(true, response.string());
        }
    };

    public static class FileParse implements IParseResponse<File> {

        private String filePath;

        private FileParse(String path) {
            filePath = path;
        }

        @Override
        public IParseResult<File> parse(String tag, IResponse response, Type type, IRequestBody request) throws Throwable {
            File file = new File(filePath);
            try {
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();

                FileOutputStream os = new FileOutputStream(file);
                long start = System.currentTimeMillis();

                long totalLen = response.getContentLength();
                InputStream is = response.byteStream();
                byte[] buf = new byte[1024 * 10];
                int ret, len = 0;
                if (request != null) {
                    request.publishProgress(0);
                }
                while ((ret = is.read(buf)) >= 0) {
                    os.write(buf, 0, ret);
                    len += ret;
                    if (System.currentTimeMillis() - start > 500) {
                        start = System.currentTimeMillis();
                        if (request != null) {
                            if (totalLen == 0) {
                                totalLen = -1;
                            }
                            request.publishProgress(len * 1f / totalLen);
                        }
                    }
                }
                if (request != null) {
                    request.publishProgress(1);
                }
            } catch (Throwable throwable) {
                if (file.exists()) {
                    file.delete();
                }
                throw throwable;
            } finally {
                response.close();
            }
            return new Result<>(true, file);
        }
    }
}
