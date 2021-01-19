package cn.sskbskdrin.http;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by keayuan on 2021/1/19.
 *
 * @author keayuan
 */
public abstract class Callback<T> implements IParseResponse<T> {
    private Type type;

    public Callback() {
        type = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    public abstract IParseResult<T> parse(String tag, IResponse response, Type type);
}
