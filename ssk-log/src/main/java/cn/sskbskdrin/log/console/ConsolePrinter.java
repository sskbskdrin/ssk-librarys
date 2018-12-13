package cn.sskbskdrin.log.console;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.sskbskdrin.log.Format;
import cn.sskbskdrin.log.L;
import cn.sskbskdrin.log.Printer;

public class ConsolePrinter extends Printer {

    private final Date date = new Date();
    private final SimpleDateFormat dateFormat;

    public ConsolePrinter() {
        dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.UK);
    }

    public ConsolePrinter(Format format) {
        super(format);
        dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.UK);
    }

    @Override
    public String formatTag(int priority, String tag) {
        if (tag == null) {
            tag = "";
        }
        String type = "";
        String start = "";
        switch (priority) {
            case L.VERBOSE:
                type = " V/";
                start = "\u001b[30;37m";
                break;
            case L.DEBUG:
                type = " D/";
                start = "\u001b[30;34m";
                break;
            case L.INFO:
                type = " I/";
                start = "\u001b[30;32m";
                break;
            case L.WARN:
                type = " W/";
                start = "\u001b[30;33m";
                break;
            case L.ERROR:
                type = " E/";
                start = "\u001b[30;31m";
                break;
            default:
        }
        date.setTime(System.currentTimeMillis());
        return start + dateFormat.format(date) + Thread.currentThread().getId() + type + tag + ": ";
    }

    @Override
    public String format(String msg) {
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
