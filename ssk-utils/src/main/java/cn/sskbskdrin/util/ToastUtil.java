package cn.sskbskdrin.util;

import android.content.Context;
import android.widget.Toast;

/**
 * @ClassName: ToastUtil
 * @Description: 土司通知类
 * @author: ayke
 * @date: 2014-5-13 下午4:10:41
 */
public class ToastUtil {

    private static Toast toast;

    public static void show(Context context, CharSequence info, boolean longTime) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(context, info, longTime ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void show(Context context, int resId) {
        show(context, context.getResources().getText(resId), false);
    }

    public static void showToast(Context context, int resId, boolean longTime) {
        show(context, context.getResources().getText(resId), longTime);
    }

    public static void show(Context context, CharSequence text) {
        show(context, text, false);
    }

    public static void show(Context context, int resId, Object... args) {
        show(context, String.format(context.getResources().getString(resId), args), false);
    }

    public static void show(Context context, String format, Object... args) {
        show(context, String.format(format, args), false);
    }

    public static void show(Context context, int resId, boolean longTime, Object... args) {
        show(context, String.format(context.getResources().getString(resId), args), longTime);
    }

    public static void show(Context context, String format, boolean longTime, Object... args) {
        show(context, String.format(format, args), longTime);
    }
}