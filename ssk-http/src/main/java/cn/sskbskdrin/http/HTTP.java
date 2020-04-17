package cn.sskbskdrin.http;

import android.content.Context;

/**
 * Created by keayuan on 2019-11-29.
 *
 * @author keayuan
 */
public class HTTP {
    private static String BASE_URL;

    public static void init(Context context) {
    }

    public static <T> IRequest<T> url(String url, Class<T> tClass) {
        return new HttpRequest<>(url, tClass);
    }

    public static <V> IRequest<V> url(String url, TypeToken<V> res) {
        return new HttpRequest<>(url, res.getType());
    }
}
