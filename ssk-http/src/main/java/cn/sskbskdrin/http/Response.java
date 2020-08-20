package cn.sskbskdrin.http;

/**
 * Created by keayuan on 2020/4/17.
 *
 * @author keayuan
 */
public final class Response implements IResponse {

    private byte[] bodyData;
    private String bodyString;

    private String code;
    private String desc;
    private Exception e;
    IParseResult result;


    private Response() {
        code = "200";
    }

    static Response get(String str) {
        Response res = new Response();
        res.bodyString = str;
        return res;
    }

    static Response get(byte[] data) {
        Response res = new Response();
        res.bodyData = data;
        return res;
    }

    static Response get(String code, String desc, Exception e) {
        Response res = new Response();
        res.code = code;
        res.desc = desc;
        res.e = e;
        return res;
    }

    @Override
    public byte[] bytes() {
        return bodyData;
    }

    @Override
    public String string() {
        if (bodyString == null) {
            if (bodyData != null && bodyData.length > 0) {
                bodyString = new String(bodyData);
            }
        }
        return bodyString;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String desc() {
        return desc;
    }

    @Override
    public Exception exception() {
        return e;
    }

    @Override
    public boolean isSuccess() {
        return "200".equals(code);
    }

    @Override
    public boolean isFile() {
        return bodyData == null && bodyString != null;
    }
}
