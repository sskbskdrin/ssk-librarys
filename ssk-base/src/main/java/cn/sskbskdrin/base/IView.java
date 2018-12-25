package cn.sskbskdrin.base;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.IdRes;
import android.view.View;

public interface IView {
    /**
     * 通过id查找view
     *
     * @param id  view 的 id
     * @param <T> view 的类型
     * @return 返回找到的view，找不到则返回空
     */
    <T extends View> T getView(@IdRes int id);

    /**
     * @return 当前视图对应的上下文
     */
    Context context();

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
    Dialog generateLoadingDialog(String content);
}
