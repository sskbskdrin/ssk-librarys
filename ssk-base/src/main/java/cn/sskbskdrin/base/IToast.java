package cn.sskbskdrin.base;

import android.content.Context;
import android.widget.Toast;

import java.util.WeakHashMap;

/**
 * Created by keayuan on 2021/8/12.
 *
 * @author keayuan
 */
public interface IToast extends IFinish, IContext, IPost, IResource {
    WeakHashMap<IToast, Toast> toastMap = new WeakHashMap<>();

    default Toast generateToast(String text, boolean isLong) {
        return Toast.makeText(context(), text, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
    }

    default void showToast(int resId) {
        showToast(resId, false);
    }

    default void showToast(int resId, boolean isLong, Object... args) {
        showToast(string(resId, args), isLong);
    }

    default void showToast(String text) {
        showToast(text, false);
    }

    default void showToast(String text, boolean isLong, Object... args) {
        Toast toast = toastMap.get(this);
        if (toast != null) {
            toast.cancel();
        }
        Context context = context();
        if (context != null) {
            if (args.length > 0) {
                text = String.format(text, args);
            }
            toast = generateToast(text, isLong);
            toast.show();
            toastMap.put(this, toast);
        }
    }

    default void cancelToast() {
        Toast toast = toastMap.get(this);
        if (toast != null) {
            toast.cancel();
        }
    }
}
