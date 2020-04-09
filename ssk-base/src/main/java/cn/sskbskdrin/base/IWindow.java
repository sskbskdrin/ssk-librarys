package cn.sskbskdrin.base;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * @author keayuan
 * 2020/3/27
 */
public interface IWindow extends IContext {

    IWindowC iWindowC = new IWindowC();

    class IWindowC {
        private IWindowC() {}

        WeakReference<Dialog> dialog;
        WeakReference<Toast> toast;
    }

    /**
     * 判断页面是否已经销毁
     *
     * @return 是否已经销毁
     */
    boolean isFinish();

    /**
     * 构建一个等待框
     *
     * @param content 等待框显示的内容
     * @return 返回一个dialog
     */
    default Dialog generateLoadingDialog(String content) {
        ProgressDialog dialog = new ProgressDialog(context());
        dialog.setTitle("");
        dialog.setMessage(content);
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.setOnCancelListener(null);
        return dialog;
    }

    default void showLoadingDialog(int resId) {
        Context context = context();
        if (context != null) {
            showLoadingDialog(context.getString(resId));
        }
    }

    default void showLoadingDialog(String content) {
        Dialog dialog = iWindowC.dialog == null ? null : iWindowC.dialog.get();
        if (dialog != null) {
            dialog.dismiss();
        }
        if (!isFinish()) {
            dialog = generateLoadingDialog(content);
            if (dialog != null) {
                dialog.show();
            }
            iWindowC.dialog = new WeakReference<>(dialog);
        }
    }

    default void hideLoadingDialog() {
        Dialog dialog = iWindowC.dialog == null ? null : iWindowC.dialog.get();
        if (dialog != null) {
            dialog.dismiss();
        }
        iWindowC.dialog = null;
    }

    default void showToast(String text) {
        showToast(text, false);
    }

    default void showToast(String text, boolean isLong) {
        Toast toast = iWindowC.toast == null ? null : iWindowC.toast.get();
        if (toast != null) {
            toast.cancel();
        }
        Context context = context();
        if (context != null) {
            toast = Toast.makeText(context, text, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
            toast.show();
            iWindowC.toast = new WeakReference<>(toast);
        }
    }

}
