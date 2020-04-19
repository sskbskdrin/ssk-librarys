package cn.sskbskdrin.http;

/**
 * Created by keayuan on 2019-11-29.
 *
 * @author keayuan
 */
public final class HTTP {
    private HTTP() {}

    public static Config globalConfig() {
        return Config.INSTANCE;
    }

    public static <V> IRequest<V> url(String url) {
        return new HttpRequest<>(url, null);
    }

    public static <V> IRequest<V> url(String url, Class<V> tClass) {
        return new HttpRequest<>(url, tClass);
    }

    public static <V> IRequest<V> url(String url, TypeToken<V> token) {
        return new HttpRequest<>(url, token.getType());
    }
}
