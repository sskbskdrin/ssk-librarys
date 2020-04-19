package cn.sskbskdrin.http;

/**
 * Created by keayuan on 2019-11-29.
 *
 * @author keayuan
 */
public class Result<T> implements IParseResult<T> {
    private boolean isSuccess = true;
    private String code;
    private String msg;
    private Exception exception;
    private T bean;

    public Result() {
    }

    public Result(boolean success) {
        isSuccess = success;
    }

    public Result(boolean success, T t) {
        isSuccess = success;
        bean = t;
    }

    public void setBean(T bean) {
        this.bean = bean;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setException(Exception e) {
        exception = e;
    }

    /**
     * 结果解析是否成功
     *
     * @return 成功为true，否则false
     */
    @Override
    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }

    @Override
    public boolean isCancel() {
        return false;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return msg;
    }

    @Override
    public Exception getException() {
        return exception;
    }

    @Override
    public T getT() {
        return bean;
    }

    /**
     * code为int类型时使用
     *
     * @return code值
     */
    public int getCodeInt() {
        return code == null || code.length() == 0 ? 0 : Integer.parseInt(code);
    }
}
