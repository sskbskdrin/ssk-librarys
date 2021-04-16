package cn.sskbskdrin.log.console;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.sskbskdrin.log.Format;
import cn.sskbskdrin.log.Printer;
import cn.sskbskdrin.log.SSKLog;
import cn.sskbskdrin.log.logcat.LogcatPrinter;

public class ConsolePrinter extends Printer {

    private final Date date = new Date();
    private final SimpleDateFormat dateFormat;
    private static ConsolePrinter mInstance;

    public static ConsolePrinter getInstance(Format format) {
        if (mInstance == null) {
            synchronized (LogcatPrinter.class) {
                if (mInstance == null) {
                    mInstance = new ConsolePrinter();
                }
            }
        }
        mInstance.setFormat(format);
        return mInstance;
    }

    private ConsolePrinter() {
        dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault());
    }

    @Override
    public String formatTag(int priority, String tag) {
        if (tag == null) {
            tag = "";
        }
        String type = "";
        String start = "";
        switch (priority) {
            case SSKLog.VERBOSE:
                type = " V/";
                start = "\u001b[30;37m";
                break;
            case SSKLog.DEBUG:
                type = " D/";
                start = "\u001b[30;34m";
                break;
            case SSKLog.INFO:
                type = " I/";
                start = "\u001b[30;32m";
                break;
            case SSKLog.WARN:
                type = " W/";
                start = "\u001b[30;33m";
                break;
            case SSKLog.ERROR:
                type = " E/";
                start = "\u001b[30;31m";
                break;
            default:
        }
        date.setTime(System.currentTimeMillis());
        return start + dateFormat.format(date) + " " + Thread.currentThread().getId() + type + tag + ": ";
    }

    @Override
    protected String format(String msg) {
        return msg + "\u001b[0m";
    }

    @Override
    public void print(int priority, String tag, String message) {
        String[] result = message.split(NEW_LINE);
        for (String msg : result) {
            System.out.println(tag + msg);
        }
    }
}
