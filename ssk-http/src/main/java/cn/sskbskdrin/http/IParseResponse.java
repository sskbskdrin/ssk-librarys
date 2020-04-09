package cn.sskbskdrin.http;

/**
 * Created by keayuan on 2019-11-29.
 *
 * @author keayuan
 */
public interface IParseResponse<T> {
    /**
     * 解析数据
     *
     * @param tag      标签
     * @param response 响应的response，如果{@link Response#parse(String, byte[], Class)}返回false
     *                 或者{@link Response#isSuccess()}返回false，则不会回调
     * @param clazz    解析类，在{@link IRequest#parseResponse(Class)}中设置
     */
    void parse(String tag, Response<T> response, Class<?> clazz);
}
