package cn.sskbskdrin.frame.base.ui;

import java.util.List;

/**
 * Created by keayuan on 2020/11/11.
 *
 * @author keayuan
 */
public abstract class IBaseAdapter<T> extends cn.sskbskdrin.base.IBaseAdapter<T> {

    public IBaseAdapter(List<T> list) {
        super(list);
    }

    public IBaseAdapter(List<T> list, int layoutId) {
        super(list, layoutId);
    }
}
