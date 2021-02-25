package cn.sskbskdrin.base;

import android.content.Context;
import android.view.View;

/**
 * Created by keayuan on 2021/2/25.
 *
 * @author keayuan
 */
public interface IViewHolder<T> extends IView {

    void setItem(T t, int position);

    void updateView(T t, int position);

    View itemView();

    T bean();

    int position();

    default void onClickItem() {
    }

    default boolean onLongClickItem() {
        return false;
    }

    @Override
    default Context getContext() {
        return itemView().getContext();
    }

    @Override
    default <V extends View> V getView(int id) {
        return IView.getView(itemView(), id);
    }

    interface Consumer<B> {
        void accept(IViewHolder<B> holder, B b, int position);
    }
}
