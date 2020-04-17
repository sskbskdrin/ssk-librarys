package cn.sskbskdrin.http;

/**
 * Created by keayuan on 2020/4/17.
 *
 * @author keayuan
 */
class Response<V> implements IResponse<V> {

    private byte[] bodyData;
    private String bodyString;

    Response(byte[] data) {
        bodyData = data;
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
}
