package cn.sskbskdrin.http;

import java.lang.reflect.Type;

/**
 * Created by keayuan on 2019-11-29.
 *
 * @author keayuan
 */
public interface IParseResponse<V> {
    /**
     * 解析数据
     *
     * @param tag      标签
     * @param response 响应的response，如果{@link Res#parse(String, byte[], Class)}返回false
     *                 或者{@link Res#isSuccess()}返回false，则不会回调
     * @param clazz    解析类，在{@link IRequest#parseResponse(Class)}中设置
     */
    IParseResult<V> parse(String tag, IResponse response, Type clazz);
}
