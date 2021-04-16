package cn.sskbskdrin.log.disk;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.sskbskdrin.log.LogStrategy;
import cn.sskbskdrin.log.Printer;
import cn.sskbskdrin.log.SSKLog;

public class DiskPrinter extends Printer {

    private static final String SEPARATOR = " ";
    private final Date date = new Date();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US);

    public DiskPrinter(String path) {
        this(new DiskLogStrategy(path));
    }

    public DiskPrinter(LogStrategy strategy) {
        super(strategy);
    }

    @Override
    public String formatTag(int priority, String tag) {
        if (tag == null) {
            tag = "";
        }
        date.setTime(System.currentTimeMillis());
        return dateFormat.format(date) + SEPARATOR + Thread.currentThread()
            .getId() + SEPARATOR + logLevel(priority) + SEPARATOR + tag;
    }

    @Override
    public String format(String msg) {
        return msg + NEW_LINE;
    }

    private static String logLevel(int value) {
        switch (value) {
            case SSKLog.VERBOSE:
                return "VERBOSE";
            case SSKLog.DEBUG:
                return "DEBUG";
            case SSKLog.INFO:
                return "INFO";
            case SSKLog.WARN:
                return "WARN";
            case SSKLog.ERROR:
                return "ERROR";
            case SSKLog.ASSERT:
                return "ASSERT";
            default:
                return "UNKNOWN";
        }
    }

    @Override
    public void print(int priority, String tag, String message) {
    }
}
