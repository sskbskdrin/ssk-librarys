package cn.sskbskdrin.http;

/**
 * Created by keayuan on 2021/1/19.
 *
 * @author keayuan
 */
public class Error {
    public final String code;
    public final String msg;
    public final Throwable throwable;

    Error(String code, String msg, Throwable throwable) {
        this.code = code;
        this.msg = msg;
        this.throwable = throwable;
    }

    public String code() {
        return code;
    }

    public String message() {
        return msg;
    }

    public Throwable throwable() {
        return throwable;
    }
}
