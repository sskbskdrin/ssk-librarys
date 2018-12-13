package cn.sskbskdrin.log.logcat;

import android.util.Log;

import cn.sskbskdrin.log.Format;
import cn.sskbskdrin.log.Printer;

public class LogcatPrinter extends Printer {

    private boolean isNew = true;

    public LogcatPrinter() {
    }

    public LogcatPrinter(Format format) {
        super(format);
    }

    public LogcatPrinter setNew(boolean isNew) {
        this.isNew = isNew;
        return this;
    }

    @Override
    public void print(int priority, String tag, String message) {
        if (isNew) {
            Log.println(priority, tag, message);
        } else {
            String[] result = message.split(NEW_LINE);
            for (String msg : result) {
                Log.println(priority, tag, msg);
            }
        }
    }
}
