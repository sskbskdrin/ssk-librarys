package cn.sskbskdrin.log.disk;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.sskbskdrin.log.Printer;

import static cn.sskbskdrin.log.L.ASSERT;
import static cn.sskbskdrin.log.L.DEBUG;
import static cn.sskbskdrin.log.L.ERROR;
import static cn.sskbskdrin.log.L.INFO;
import static cn.sskbskdrin.log.L.VERBOSE;
import static cn.sskbskdrin.log.L.WARN;

public class DiskPrinter extends Printer {

    private static final String SEPARATOR = " ";
    private final Date date = new Date();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale
            .US);

    public DiskPrinter(String path) {
        this(new DiskLogStrategy(path));
    }

    public DiskPrinter(DiskLogStrategy strategy) {
        super(strategy);
    }

    @Override
    public String formatTag(int priority, String tag) {
        if (tag == null) {
            tag = "";
        }
        date.setTime(System.currentTimeMillis());
        return dateFormat.format(date) + SEPARATOR + logLevel(priority) + SEPARATOR + tag;
    }

    @Override
    public String format(String msg) {
        return msg + NEW_LINE;
    }

    private static String logLevel(int value) {
        switch (value) {
            case VERBOSE:
                return "VERBOSE";
            case DEBUG:
                return "DEBUG";
            case INFO:
                return "INFO";
            case WARN:
                return "WARN";
            case ERROR:
                return "ERROR";
            case ASSERT:
                return "ASSERT";
            default:
                return "UNKNOWN";
        }
    }

    @Override
    public void print(int priority, String tag, String message) {
    }
}
