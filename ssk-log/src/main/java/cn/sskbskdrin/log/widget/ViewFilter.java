package cn.sskbskdrin.log.widget;

/**
 * Created by ex-keayuan001 on 2019-08-14.
 *
 * @author ex-keayuan001
 */
interface ViewFilter {
    /**
     * 过滤条件
     *
     * @param clear   是否清空日志
     * @param level   过滤等级[0,5]
     * @param content 内容过滤
     */
    void onFilter(boolean clear, int level, String content);
}
