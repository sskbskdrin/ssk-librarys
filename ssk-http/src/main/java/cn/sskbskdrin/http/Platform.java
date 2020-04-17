package cn.sskbskdrin.http;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executors;

/**
 * Created by keayuan on 2019-12-02.
 *
 * @author keayuan
 */

class Platform {
    private static final Platform PLATFORM = findPlatform();

    public static Platform get() {
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
        Executors.newCachedThreadPool().execute(runnable);
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
    }
}
