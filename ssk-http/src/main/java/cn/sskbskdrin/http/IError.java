package cn.sskbskdrin.http;

/**
 * Created by keayuan on 2019-11-29.
 *
 * @author keayuan
 */
public interface IError {
    /**
     * 请求失败，或解析失败回调
     *
     * @param tag  请求的tag
     * @param code 失败的code
     * @param desc 失败的说明
     * @param e    失败抛出的异常
     */
    void error(String tag, String code, String desc, Exception e);
}
