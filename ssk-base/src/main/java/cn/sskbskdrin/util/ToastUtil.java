package cn.sskbskdrin.util;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * @ClassName: ToastUtil
 * @Description: 土司通知类
 * @author: keayuan
 * @date: 2014-5-13 下午4:10:41
 */
public class ToastUtil {

    private static WeakReference<Toast> toastWeak;
    private static Handler mainHandler = new Handler(Looper.getMainLooper());
    private static final Object[] EMPTY = new Object[0];
    private static WeakReference<Context> contextWeak;

    public static void init(Context context) {
        if (contextWeak == null) {
            contextWeak = new WeakReference<>(context.getApplicationContext());
        }
    }

    private static class Task implements Runnable {

        private String content;
        private boolean isLong;
        private WeakReference<Context> weakContext;

        private Task(String content, boolean isLong) {
            this(null, content, isLong);
        }

        private Task(Context context, String content, boolean isLong) {
            weakContext = new WeakReference<>(context == null ? contextWeak.get() : context);
            this.content = content;
            this.isLong = isLong;
        }

        @Override
        public void run() {
            Toast toast = toastWeak == null ? null : toastWeak.get();
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
            toastWeak = new WeakReference<>(toast);
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
        if (args != null && args.length > 0) {
            content = String.format(content, args);
        }
        showToast(new Task(content, isLong));
    }

    public static void show(String content, Object... args) {
        show(content, false, args);
    }

    public static void show(int resId, boolean isLong, Object... args) {
        show(contextWeak.get().getResources().getText(resId).toString(), isLong, EMPTY);
    }

    public static void show(int resId, Object... args) {
        show(resId, false, args);
    }

    // context

    public static void show(Context context, int resId, Object... args) {
        show(context, resId, false, args);
    }

    public static void show(Context context, int resId, boolean isLong, Object... args) {
        show(context, context.getResources().getString(resId, args), isLong, EMPTY);
    }

    public static void show(Context context, String format, Object... args) {
        show(context, format, false, args);
    }

    public static void show(Context context, String format, boolean isLong, Object... args) {
        if (args != null && args.length > 0) {
            format = String.format(format, args);
        }
        showToast(new Task(context, format, isLong));
    }

    private static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}