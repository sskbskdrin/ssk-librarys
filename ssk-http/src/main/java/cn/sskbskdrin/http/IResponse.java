package cn.sskbskdrin.http;

/**
 * Created by keayuan on 2020/4/7.
 *
 * @author keayuan
 */
public interface IResponse<T> {
    Response<T> generate();
}
