package cn.sskbskdrin.http;

/**
 * Created by keayuan on 2019-11-29.
 *
 * @author keayuan
 */
public class Res<T> implements IParseResult<T> {
    protected boolean isSuccess;
    protected Exception exception;

    protected String code;
    protected String msg;
    private T bean;

    public Res() {
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

    /**
     * 解析出的实体类
     *
     * @return 结果
     */
    public T getBean() {
        return bean;
    }
}
