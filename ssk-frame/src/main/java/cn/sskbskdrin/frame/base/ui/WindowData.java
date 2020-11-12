package cn.sskbskdrin.frame.base.ui;

/**
 * Created by keayuan on 2020/4/3.
 *
 * @author keayuan
 */
class WindowData {
    final Object[] obj;
    final CMD type;

    enum CMD {
        OPEN, SHOW_TOAST, LOADING_DIALOG, FINISH
    }

    WindowData(CMD type, Object... obj) {
        this.type = type;
        this.obj = obj;
    }
}
