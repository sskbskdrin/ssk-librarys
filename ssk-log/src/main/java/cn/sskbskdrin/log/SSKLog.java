package cn.sskbskdrin.log;

/**
 * Created by ex-keayuan001 on 2019-05-17.
 *
 * @author ex-keayuan001
 */
public class SSKLog {

    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;
    public static final int ASSERT = 7;

    private static String TAG_PREFIX = "";

    private final LogHelper helper = new LogManager();

    private int level = VERBOSE;

    /**
     * 设置全局tag
     *
     * @param tagPrefix tag前缀
     */
    public final void tag(String tagPrefix) {
        TAG_PREFIX = tagPrefix;
    }

    /**
     * 添加打印者
     * <ul>
     * <li>Different {@link cn.sskbskdrin.log.logcat.LogcatPrinter}</li>
     * <li>Different {@link cn.sskbskdrin.log.disk.DiskPrinter}</li>
     * <li>Different {@link cn.sskbskdrin.log.console.ConsolePrinter}</li>
     * </ul>
     *
     * @param printers 打印者
     * @see cn.sskbskdrin.log.logcat.LogcatPrinter
     * @see cn.sskbskdrin.log.disk.DiskPrinter
     * @see cn.sskbskdrin.log.console.ConsolePrinter
     */
    public final void addPinter(Printer... printers) {
        if (printers != null) {
            for (Printer printer : printers) {
                if (printer != null) {
                    helper.addPrinter(printer);
                }
            }
        }
    }

    /**
     * 设置打印的log的优先级
     * <ul>
     * <li>{@link SSKLog#VERBOSE}</li>
     * <li>{@link SSKLog#DEBUG}</li>
     * <li>{@link SSKLog#INFO}</li>
     * <li>{@link SSKLog#WARN}</li>
     * <li>{@link SSKLog#ERROR}</li>
     * <li>{@link SSKLog#ASSERT}</li>
     * </ul>
     *
     * @param level 优先级
     */
    public void setLogLevel(int level) {
        this.level = level;
    }

    /**
     * 清除所有打印者
     */
    public final void clearPrinters() {
        helper.clearAdapters();
    }

    /**
     * 格式化json 或者 xml
     *
     * @param json 是否格式化json
     * @param xml  是否格式化xml
     */
    public final void enableJsonOrXml(boolean json, boolean xml) {
        helper.formatJSONorXML(json, xml);
    }

    public void v(String tag, String msg, Object... obj) {
        println(VERBOSE, tag, msg, obj);
    }

    public void d(String tag, String msg, Object... obj) {
        println(DEBUG, tag, msg, obj);
    }

    public void i(String tag, String msg, Object... obj) {
        println(INFO, tag, msg, obj);
    }

    public void w(String tag, String msg, Object... obj) {
        println(WARN, tag, msg, obj);
    }

    public void e(String tag, String msg, Object... obj) {
        println(ERROR, tag, msg, obj);
    }

    public void println(int priority, String tag, String msg, Object... obj) {
        if (level <= priority) {
            helper.log(priority, TAG_PREFIX + "_" + (tag == null ? "" : tag), msg, obj);
        }
    }

}
