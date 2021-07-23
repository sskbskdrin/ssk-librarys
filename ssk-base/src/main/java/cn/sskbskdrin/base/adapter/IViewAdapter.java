package cn.sskbskdrin.base.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.sskbskdrin.base.IView;
import cn.sskbskdrin.util.CollectionUtils;
import cn.sskbskdrin.util.function.BiFunction;
import cn.sskbskdrin.util.function.Consumer;
import cn.sskbskdrin.util.function.Function;

/**
 * Created by keayuan on 2021/2/25.
 *
 * @author keayuan
 */
interface IViewAdapter<T, VH extends IHolder<T>> extends IView {

    <A extends IViewAdapter<T, VH>> AdapterInternal<A, T, VH> getInternal();

    default List<T> getList() {
        return getInternal().getList();
    }

    default void updateList(List<T> list) {
        getInternal().updateList(list);
    }

    void notifyDataSetChanged();

    default VH onCreateHolder(ViewGroup parent, int type) {
        return getInternal().createHolder(parent, type);
    }

    default void onBindHolder(VH holder) {
        getInternal().onBindHolder(holder);
    }

    default T getItemBean(int position) {
        return getInternal().getItemBean(position);
    }

    default int getTotalCount() {
        return CollectionUtils.getSize(getList());
    }

    int getItemViewType(int position);

    default void onClickItem(VH holder) {
        getInternal().onClickItem(holder);
    }

    default boolean onLongClickItem(VH holder) {
        return getInternal().onLongClickItem(holder);
    }

    @Override
    default Context getContext() {
        return getInternal().getContext();
    }

    @Override
    default <V extends View> V getView(int id) {
        return getInternal().getView(id);
    }

    abstract class Builder<A extends IViewAdapter<B, VH>, B, VH extends IHolder<B>> {
        List<B> list;
        int layoutId;
        BiFunction<ViewGroup, Integer, VH> supplier;
        Consumer<VH> bind;
        Consumer<VH> click;
        Function<VH, Boolean> longClick;

        protected Builder() {}

        protected Builder(int layoutId) {
            this.layoutId = layoutId;
        }

        protected Builder(BiFunction<ViewGroup, Integer, VH> supplier) {
            this.supplier = supplier;
        }

        protected void holder(BiFunction<ViewGroup, Integer, VH> supplier) {
            this.supplier = supplier;
        }

        public Builder<A, B, VH> list(List<B> list) {
            this.list = list;
            return this;
        }

        public Builder<A, B, VH> bindViewHolder(Consumer<VH> bind) {
            this.bind = bind;
            return this;
        }

        public Builder<A, B, VH> clickItem(Consumer<VH> listener) {
            this.click = listener;
            return this;
        }

        public Builder<A, B, VH> clickItemLong(Function<VH, Boolean> listener) {
            this.longClick = listener;
            return this;
        }

        public abstract A build();
    }
}
