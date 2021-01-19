package cn.sskbskdrin.http;

/**
 * Created by sskbskdrin on 2021/1/19.
 *
 * @author sskbskdrin
 */
public class HTTPNew {

    public static INewRequest<String> url(String url) {
        return new HttpNewRequest<>(url, String.class);
    }

    public static <V> INewRequest<V> url(String url, Class<V> tClass) {
        return new HttpNewRequest<>(url, tClass);
    }

    public static <V, T> INewRequest<V> url(String url, TypeToken<T> token) {
        return new HttpNewRequest<>(url, token.getType());
    }

    public static <V> INewRequest<V> url(String url, IParseResponse<V> iParseResponse) {
        return new HttpNewRequest<>(url, iParseResponse);
    }
}
