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
     * @param response 响应的response，{@link IResponse}
     *                 或者{@link Result#isSuccess()}返回false，则不会回调
     * @param type     泛型类型
     * @return 返回 {@link IParseResult}对像
     */
    IParseResult<V> parse(String tag, IResponse response, Type type, IRequestBody request) throws Throwable;
}
