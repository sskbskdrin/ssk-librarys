package cn.sskbskdrin.log;

/**
 * A proxy interface to enable additional operations.
 * Contains all possible Log message usages.
 */
interface LogHelper {

    /**
     * 添加打印者
     *
     * @param printer 打印者
     */
    void addPrinter(Printer printer);

    /**
     * 清除所有打印者
     */
    void clearAdapters();

    /**
     * 格式化json 或者 xml
     *
     * @param json 是否格式化json
     * @param xml  是否格式化xml
     */
    void formatJSONorXML(boolean json, boolean xml);

    /**
     * 打印日志
     *
     * @param priority 优先级
     * @param tag      打印的tag
     * @param message  打印的内容
     * @param obj      异常打印
     */
    void log(int priority, String tag, String message, Object... obj);
}
