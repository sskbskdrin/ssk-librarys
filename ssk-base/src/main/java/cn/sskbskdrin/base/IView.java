package cn.sskbskdrin.base;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.IdRes;
import android.view.View;

public interface IView {

    <T extends View> T getView(@IdRes int id);

    Context context();

    boolean isFinish();

    Dialog generateLoadingDialog(String content);
}
