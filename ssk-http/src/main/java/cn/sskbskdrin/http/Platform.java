package cn.sskbskdrin.http;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by keayuan on 2019-12-02.
 *
 * @author keayuan
 */

class Platform {
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
        Config.INSTANCE.execute(runnable);
    }

    void log(String tag, String msg) {
        log(tag, msg, null);
    }

    void log(String tag, String msg, Throwable e) {
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
            if (isCallbackThread()) {
                runnable.run();
            } else {
                mainHandler.post(runnable);
            }
        }

        @Override
        boolean isCallbackThread() {
            return Looper.myLooper() == Looper.getMainLooper();
        }

        @Override
        void log(String tag, String msg, Throwable e) {
            if (e != null) {
                Log.w(tag, msg, e);
            } else {
                Log.d(tag, msg);
            }
        }
    }

    public static void safeClose(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
            }
        }
    }
}
