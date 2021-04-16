package cn.sskbskdrin.log.logcat;

import android.util.Log;

import cn.sskbskdrin.log.Format;
import cn.sskbskdrin.log.Printer;

public class LogcatPrinter extends Printer {

    private boolean isNew = true;

    private static LogcatPrinter mInstance;

    public static LogcatPrinter getInstance(Format format) {
        if (mInstance == null) {
            synchronized (LogcatPrinter.class) {
                if (mInstance == null) {
                    mInstance = new LogcatPrinter();
                }
            }
        }
        mInstance.setFormat(format);
        return mInstance;
    }

    private LogcatPrinter() {
    }

    public LogcatPrinter setNew(boolean isNew) {
        this.isNew = isNew;
        return this;
    }

    @Override
    public void print(int priority, String tag, String message) {
        if (isNew) {
            while (message.length() > 8000) {
                int index = message.substring(0, 8000).lastIndexOf(NEW_LINE);
                if (index > 0) {
                    Log.println(priority, tag, message.substring(0, index));
                    message = message.substring(index);
                }
            }
            Log.println(priority, tag, message);
        } else {
            String[] result = message.split(NEW_LINE);
            for (String msg : result) {
                Log.println(priority, tag, msg);
            }
        }
    }
}
