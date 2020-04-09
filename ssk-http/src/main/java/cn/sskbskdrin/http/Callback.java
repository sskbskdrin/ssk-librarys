package cn.sskbskdrin.http;

/**
 * Created by keayuan on 2019-11-29.
 *
 * @author keayuan
 */
public interface Callback<T> extends ISuccess<T>, IError, IComplete {}
