package cn.sskbskdrin.http;

import android.content.Context;

import java.io.File;
import java.util.concurrent.Executor;

/**
 * Created by keayuan on 2019-11-29.
 *
 * @author keayuan
 */
public class HTTP {
    private static String BASE_URL;

    public static void init(Context context) {
    }

    public static void setBaseUrl(String url) {
        BASE_URL = url;
    }

    public static <T> IRequest<T> get(String url) {
        return new HttpRequest<T>(fixUrl(url)).get();
    }

    public static <T> IRequest<T> post(String url) {
        return new HttpRequest<T>(fixUrl(url)).post();
    }

    public static <T> IRequest<T> postJson(String url) {
        return new HttpRequest<T>(fixUrl(url)).postJson();
    }

    public static <T> IRequest<T> postFile(String url) {
        return new HttpRequest<T>(fixUrl(url)).postFile();
    }

    public static IRequest<File> download(String url, String path) {
        return new HttpRequest<File>(fixUrl(url)).download(path);
    }

    public static void setExecuteService(Executor executor) {
        HttpRequest.executor = executor;
    }

    public static void putGlobalHeader(String key, String value) {
        HttpRequest.putGlobalHeader(key, value);
    }

    public static void globalHeader(IMap<String, String> global) {
        HttpRequest.globalHeader(global);
    }

    public static void setRealRequestFactory(IRealRequestFactory factory) {
        HttpRequest.setRealRequestFactory(factory);
    }

    /**
     * 完善url
     *
     * @param url url
     */
    private static String fixUrl(String url) {
        return isFullUrl(url) ? url : BASE_URL + url;
    }

    private static boolean isFullUrl(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }

}
