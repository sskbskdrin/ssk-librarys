package cn.sskbskdrin.base;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

import java.util.WeakHashMap;

/**
 * Created by keayuan on 2021/8/12.
 *
 * @author keayuan
 */
public interface ILoadingDialog extends IFinish, IContext, IPost {
    WeakHashMap<ILoadingDialog, Run<Dialog>> dialogMap = new WeakHashMap<>();

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
        showLoadingDialog(content, 0);
    }

    default void showLoadingDialog(String content, long delay) {
        Run<Dialog> dialogRun = dialogMap.get(this);
        if (isFinish()) return;
        Dialog dialog = generateLoadingDialog(content);
        if (dialogRun != null) {
            removeCallbacks(dialogRun);
        }
        dialogRun = new Run<Dialog>(dialog) {
            @Override
            public void run() {
                if (!isFinish()) {
                    target.show();
                }
            }
        };
        dialogMap.put(this, dialogRun);
        postDelayed(dialogRun, delay);
    }

    default void hideLoadingDialog() {
        Run<Dialog> dialog = dialogMap.get(this);
        if (dialog != null) {
            removeCallbacks(dialog);
            dialog.target.dismiss();
        }
        dialogMap.remove(this);
    }

}
