package cn.sskbskdrin.http;

/**
 * Created by keayuan on 2021/1/19.
 *
 * @author keayuan
 */
class Error {
    public String code;
    public String msg;
    public Exception throwable;

    Error(String code, String msg, Exception throwable) {
        this.code = code;
        this.msg = msg;
        this.throwable = throwable;
    }
}
