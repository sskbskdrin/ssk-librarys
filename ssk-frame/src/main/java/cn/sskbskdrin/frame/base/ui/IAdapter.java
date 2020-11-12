package cn.sskbskdrin.frame.base.ui;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by keayuan on 2020/4/7.
 *
 * @author keayuan
 */
public interface IAdapter<T, H> {
    int getLayoutId(int type);

    View generateView(ViewGroup parent, int type);

    H createHolder(View view, int type);

    void convert(View view, H holder, int position, T t);
}
