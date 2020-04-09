package cn.sskbskdrin.http;

/**
 * Created by keayuan on 2019-11-29.
 *
 * @author keayuan
 */
public class Response<T> {
    protected boolean isSuccess;
    protected Exception exception;

    byte[] bodyData;
    private String bodyString;

    protected String code;
    protected String msg;
    private T bean;

    public Response() {
    }

    public final void setBean(T bean) {
        this.bean = bean;
    }

    public final void setCode(String code) {
        this.code = code;
    }

    protected final void setException(Exception e) {
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

    Exception getException() {
        return exception;
    }

    public String code() {
        return code;
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
     * msg信息
     *
     * @return msg信息
     */
    public final String msg() {
        return msg;
    }

    /**
     * 解析出的实体类
     *
     * @return 结果
     */
    public T getBean() {
        return bean;
    }

    /**
     * 解析数据，解析数据类型为File时，可以不处理
     *
     * @param tag   请求标签
     * @param data  响应的数据
     * @param clazz 解析类，可能为空,如果T为List，则class应为List中实体的类型
     * @return 返回true则会回调到success或error，返回false则直接回调complete
     */
    protected boolean parse(String tag, byte[] data, Class<?> clazz) {
        return true;
    }

    /**
     * 返回数据转字符串
     *
     * @return 返回的字符串
     */
    public String bodyString() {
        if (bodyString == null) {
            if (bodyData != null && bodyData.length > 0) {
                bodyString = new String(bodyData);
            }
        }
        return bodyString;
    }
}
