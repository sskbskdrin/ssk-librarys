package cn.sskbskdrin.http;

/**
 * 实际请求工厂
 * Created by keayuan on 2019-12-02.
 *
 * @author keayuan
 */
public interface IRealRequestFactory {
    /**
     * 构造实际请求对象，发起请求
     *
     * @return 返回请求实体对象
     */
    IRealRequest generateRealRequest();
}
