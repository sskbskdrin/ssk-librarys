package cn.sskbskdrin.util;

import android.util.Log;

/**
 * Created by keayuan on 2021/7/22.
 *
 * @author keayuan
 */
public class LogFactory {

    private static ILog log = new DefaultLog();

    public static void setLog(ILog log) {
        LogFactory.log = log;
    }

    public static ILog getLog() {
        return log;
    }

    public interface ILog {
        void v(String tag, String msg);

        void d(String tag, String msg);

        void i(String tag, String msg);

        void w(String tag, String msg);

        void w(String tag, String msg, Throwable throwable);

        void e(String tag, String msg);

        void e(String tag, String msg, Throwable throwable);
    }

    private static class DefaultLog implements ILog {

        @Override
        public void v(String tag, String msg) {
            Log.v(tag, msg);
        }

        @Override
        public void d(String tag, String msg) {
            Log.d(tag, msg);
        }

        @Override
        public void i(String tag, String msg) {
            Log.i(tag, msg);
        }

        @Override
        public void w(String tag, String msg) {
            Log.w(tag, msg);

        }

        @Override
        public void w(String tag, String msg, Throwable throwable) {
            Log.w(tag, msg, throwable);

        }

        @Override
        public void e(String tag, String msg) {
            Log.e(tag, msg);
        }

        @Override
        public void e(String tag, String msg, Throwable throwable) {
            Log.e(tag, msg, throwable);
        }
    }
}
