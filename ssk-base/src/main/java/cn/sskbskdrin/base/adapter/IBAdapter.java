package cn.sskbskdrin.base.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import cn.sskbskdrin.util.function.BiFunction;

/**
 * Created by keayuan on 2021/7/21.
 *
 * @author keayuan
 */
public class IBAdapter<B, VH extends IHolder<B>> extends BaseAdapter implements IViewAdapter<B, VH> {
    private static final int HOLDER_ITEM_ID = 0x35486783;

    private final AdapterInternal<IBAdapter<B, VH>, B, VH> internal;

    public IBAdapter(int layoutId) {
        this(new Builder<>(layoutId));
    }

    private IBAdapter(Builder<B, VH> builder) {
        internal = new AdapterInternal<>(this, builder);
    }

    @Override
    public final int getCount() {
        return getTotalCount();
    }

    @Override
    public final B getItem(int position) {
        return getItemBean(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        VH holder;
        if (convertView == null) {
            holder = onCreateHolder(parent, getItemViewType(position));
            convertView = holder.rootView();
            convertView.setTag(HOLDER_ITEM_ID, holder);
        } else {
            holder = (VH) convertView.getTag(HOLDER_ITEM_ID);
        }
        internal.bindHolder(this, holder, position);
        return convertView;
    }

    @Override
    public AdapterInternal<IBAdapter<B, VH>, B, VH> getInternal() {
        return internal;
    }

    public static class Builder<B, VH extends IHolder<B>> extends IViewAdapter.Builder<IBAdapter<B, VH>, B, VH> {

        public Builder(int layoutId) {
            super(layoutId);
        }

        public Builder(BiFunction<ViewGroup, Integer, VH> supplier) {
            super(supplier);
        }

        public IBAdapter<B, VH> build() {
            return new IBAdapter<>(this);
        }
    }

}
