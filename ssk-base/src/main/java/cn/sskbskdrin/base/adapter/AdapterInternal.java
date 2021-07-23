package cn.sskbskdrin.base.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.sskbskdrin.base.DebounceOnClickListener;
import cn.sskbskdrin.base.IView;
import cn.sskbskdrin.util.function.BiFunction;
import cn.sskbskdrin.util.function.Consumer;
import cn.sskbskdrin.util.function.Function;

/**
 * Created by keayuan on 2021/7/21.
 *
 * @author keayuan
 */
class AdapterInternal<A extends IViewAdapter<T, VH>, T, VH extends IHolder<T>> implements IView {
    private static final int CLICK_ITEM_ID = 0x35486782;

    private final A adapter;
    private List<T> mList;
    private int layoutId;
    private BiFunction<ViewGroup, Integer, VH> holderSupplier;
    private Consumer<VH> bindHolder;
    private Consumer<VH> click;
    private Function<VH, Boolean> longClick;
    private View currentView;

    AdapterInternal(A adapter) {
        this(adapter, null);
    }

    AdapterInternal(A adapter, IViewAdapter.Builder<A, T, VH> builder) {
        this.adapter = adapter;
        if (builder != null) {
            holderSupplier = builder.supplier;
            bindHolder = builder.bind;
            layoutId = builder.layoutId;
            mList = builder.list;
            this.click = builder.click;
            this.longClick = builder.longClick;
        }
    }

    VH createHolder(ViewGroup parent, int type) {
        if (layoutId != 0) {
            return (VH) new IHolder.Builder<>(parent).view(layoutId).build();
        }
        if (holderSupplier != null) {
            return holderSupplier.apply(parent, type);
        }
        return null;
    }

    void createHolder(BiFunction<ViewGroup, Integer, VH> holderSupplier) {
        this.holderSupplier = holderSupplier;
    }

    void bindHolder(Consumer<VH> bindHolder) {
        this.bindHolder = bindHolder;
    }

    List<T> getList() {
        if (mList == null) {
            mList = new ArrayList<>();
        }
        return mList;
    }

    void updateList(List<T> list) {
        mList = list;
        adapter.notifyDataSetChanged();
    }

    void bindHolder(IViewAdapter<T, VH> adapter, VH holder, int position) {
        if (holder == null) {
            return;
        }
        currentView = holder.rootView();
        T t = getItemBean(position);
        holder.setItem(t, position, adapter.getItemViewType(position));

        adapter.onBindHolder(holder);

        ClickItem<T, VH> clickItem = (ClickItem<T, VH>) holder.rootView().getTag(CLICK_ITEM_ID);
        if (clickItem == null) {
            clickItem = new ClickItem<>(adapter);
        }
        clickItem.holder = holder;
        holder.rootView().setTag(CLICK_ITEM_ID, clickItem);
        holder.rootView().setOnClickListener(clickItem);
        holder.rootView().setOnLongClickListener(clickItem);
    }

    void onBindHolder(VH holder) {
        if (bindHolder == null) {
            holder.updateView();
        } else {
            bindHolder.accept(holder);
        }
    }

    T getItemBean(int position) {
        return getList().get(position);
    }

    int getTotalCount() {
        List<T> list = getList();
        return list == null ? 0 : list.size();
    }

    View getCurrentView() {
        return currentView;
    }

    void onClickItem(VH holder) {
        if (click != null) {
            click.accept(holder);
        } else {
            holder.onClickItem();
        }
    }

    boolean onLongClickItem(VH holder) {
        if (longClick != null) {
            return longClick.apply(holder);
        }
        return holder.onLongClickItem();
    }

    @Override
    public Context getContext() {
        if (currentView == null) return null;
        return currentView.getContext();
    }

    @Override
    public <V extends View> V getView(int id) {
        if (currentView == null) return null;
        return IView.getView(currentView, id);
    }

    private static class ClickItem<T, VH extends IHolder<?>> implements DebounceOnClickListener,
        View.OnLongClickListener {
        private VH holder;
        private final IViewAdapter<?, VH> adapter;

        private ClickItem(IViewAdapter<?, VH> adapter) {
            this.adapter = adapter;
        }

        @Override
        public void doClick(View v) {
            adapter.onClickItem(holder);
        }

        @Override
        public boolean onLongClick(View v) {
            return adapter.onLongClickItem(holder);
        }
    }
}
