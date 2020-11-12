package cn.sskbskdrin.frame.base.ui;

import cn.sskbskdrin.frame.base.vm.BaseModel;

/**
 * Created by keayuan on 2020/4/3.
 *
 * @author keayuan
 */
class WindowModel extends BaseModel<WindowData> {

    private WindowData.CMD cmd;

    WindowModel(WindowData.CMD cmd) {
        this.cmd = cmd;
    }

    void send(Object... obj) {
        postValue(new WindowData(cmd, obj));
    }

    void send(WindowData.CMD cmd, Object... obj) {
        postValue(new WindowData(cmd, obj));
    }
}
