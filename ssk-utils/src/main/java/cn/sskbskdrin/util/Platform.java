package cn.sskbskdrin.util;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * Created by keayuan on 2019-12-02.
 *
 * @author keayuan
 */

public class Platform {
    private static final Platform PLATFORM = findPlatform();

    static Platform get() {
        return PLATFORM;
    }

    private static Platform findPlatform() {
        try {
            Class.forName("android.os.Build");
            if (Build.VERSION.SDK_INT != 0) {
                return new Android();
            }
        } catch (ClassNotFoundException ignored) {
        }
        return new Platform();
    }

    boolean isCallbackThread() {
        return true;
    }

    void callback(Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
    }

    void log(String tag, String msg) {
        log(tag, msg, null);
    }

    void log(String tag, String msg, Exception e) {
        System.out.println(tag + ":" + msg);
        if (e != null) {
            e.printStackTrace();
        }
    }

    static class Android extends Platform {
        Handler mainHandler;

        Android() {
            mainHandler = new Handler(Looper.getMainLooper());
        }

        @Override
        void callback(Runnable runnable) {
            mainHandler.post(runnable);
        }

        @Override
        boolean isCallbackThread() {
            return Thread.currentThread() == Looper.getMainLooper().getThread();
        }

        @Override
        void log(String tag, String msg, Exception e) {
            if (e != null) {
                Log.w(tag, msg, e);
            } else {
                Log.d(tag, msg, e);
            }
        }
    }
}
