package cn.sskbskdrin.frame.util;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import cn.sskbskdrin.frame.app.AppContext;

/**
 * Toast工具类
 */
public class ToastUtil {

    private static Toast toast;
    private static Handler mainHandler = new Handler(Looper.getMainLooper());

    private static class Task implements Runnable {

        private String content;
        private boolean isLong;
        private WeakReference<Context> weakContext;

        private Task(String content, boolean isLong) {
            this(null, content, isLong);
        }

        private Task(Context context, String content, boolean isLong) {
            weakContext = new WeakReference<>(context == null ? AppContext.get() : context);
            this.content = content;
            this.isLong = isLong;
        }

        @Override
        public void run() {
            if (toast != null) {
                toast.cancel();
            }
            Context context = weakContext.get();
            if (context == null || (context instanceof Activity && ((Activity) context).isFinishing())) {
                return;
            }
            if (content == null) {
                return;
            }
            toast = Toast.makeText(context, content, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private static void showToast(Task task) {
        if (isMainThread()) {
            task.run();
        } else {
            mainHandler.post(task);
        }
    }

    /**
     * Toast提示
     *
     * @param content 文案
     */
    public static void show(String content, boolean isLong, Object... args) {
        showToast(new Task(content, isLong));
    }

    public static void show(String content, Object... args) {
        show(content, false, args);
    }

    public static void show(int resId, boolean isLong, Object... args) {
        show(AppContext.get().getResources().getText(resId).toString(), isLong, args);
    }

    public static void show(int resId, Object... args) {
        show(resId, false, args);
    }

    // context

    public static void show(Context context, int resId, Object... args) {
        show(context, resId, false, args);
    }

    public static void show(Context context, int resId, boolean isLong, Object... args) {
        show(context, context.getResources().getString(resId, args), isLong);
    }

    public static void show(Context context, String format, Object... args) {
        show(context, format, false, args);
    }

    public static void show(Context context, String format, boolean isLong, Object... args) {
        showToast(new Task(context, String.format(format, args), isLong));
    }

    private static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
