package cn.sskbskdrin.log;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

import cn.sskbskdrin.log.console.ConsolePrinter;
import cn.sskbskdrin.log.logcat.LogcatPrinter;

class LogManager implements LogHelper {

    private final Set<Printer> logPrinters = new HashSet<>();
    private Printer defaultPrint;

    private static boolean logcat;
    private final ReentrantLock lock = new ReentrantLock(true);

    static boolean enableJson = false;
    static boolean enableXML = false;

    private final Pattern patternPercent = Pattern.compile("%%");
    private StringBuilder builder = new StringBuilder();

    LogManager() {
        try {
            logcat = Class.forName("android.util.Log") != null;
        } catch (ClassNotFoundException ignored) {
            log(SSKLog.WARN, "", "环境不支持 logcat, 没有找到android.util.Log");
        }
        logcat = false;
        defaultPrint = logcat ? LogcatPrinter.getInstance(null) : ConsolePrinter.getInstance(null);
        logPrinters.add(defaultPrint);
    }

    @Override
    public void formatJSONorXML(boolean json, boolean xml) {
        if (json) {
            try {
                enableJson = Class.forName("org.json.JSONObject") != null;
            } catch (ClassNotFoundException e) {
                log(SSKLog.ERROR, "globalTag", "环境不支持 json, 没有找到org.json.JSONObject");
                enableJson = false;
            }
        } else {
            enableJson = false;
        }
        enableXML = xml;
    }

    @Override
    public void log(int priority, String tag, String message, Object... obj) {
        lock.lock();
        try {
            if (obj != null && obj.length > 0) {
                if (message == null) {
                    message = "";
                }

                int index = 0;
                while (message != null) {
                    int i = message.indexOf("%%");
                    String s = message.substring(0, i >= 0 ? i : message.length());
                    while (s != null) {
                        int pos = s.indexOf("%s");
                        if (pos >= 0) {
                            builder.append(s.substring(0, pos));
                            if (index < obj.length) {
                                Utils.objToString(builder, obj[index++]);
                            } else {
                                builder.append("%s");
                            }
                            s = s.substring(pos + 2);
                        } else {
                            builder.append(s);
                            s = null;
                        }
                    }
                    if (i >= 0) {
                        builder.append("%");
                        message = message.substring(i + 2);
                    } else {
                        message = null;
                    }
                }
                while (index < obj.length) {
                    Utils.objToString(builder, obj[index++]);
                }
                message = builder.toString();
                if (builder.length() > 32 * 1024) {
                    builder = new StringBuilder();
                }
                builder.setLength(0);
            }

            if (message == null) {
                message = "";
            }
            for (Printer printer : logPrinters) {
                printer.log(priority, tag, message);
            }
        } finally {
            lock.unlock();
        }

    }

    @Override
    public void clearAdapters() {
        logPrinters.clear();
    }

    @Override
    public void addPrinter(Printer printer) {
        if (!logcat && printer instanceof LogcatPrinter) {
            throw new IllegalArgumentException("环境不支持 cn.sskbskdrin.log.logcat, 没有找到android.util.Log");
        }
        if (defaultPrint != null) {
            logPrinters.remove(defaultPrint);
            defaultPrint = null;
        }
        logPrinters.add(printer);
    }
}
