package cn.sskbskdrin.base.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cn.sskbskdrin.util.function.BiFunction;

/**
 * Created by keayuan on 2021/2/5.
 *
 * @author keayuan
 */
public class IAdapter<T, VH extends IHolder<T>> extends RecyclerView.Adapter<IAdapter.VHInternal<T, VH>> implements IViewAdapter<T, VH> {

    private final AdapterInternal<IAdapter<T, VH>, T, VH> internal;

    public IAdapter() {
        internal = new AdapterInternal<>(this);
    }

    @NonNull
    @Override
    public final VHInternal<T, VH> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VHInternal<>(internal.createHolder(parent, viewType));
    }

    private IAdapter(Builder<T, VH> builder) {
        internal = new AdapterInternal<>(this, builder);
    }

    @Override
    public final void onBindViewHolder(@NonNull IAdapter.VHInternal<T, VH> holder, int position) {
        internal.bindHolder(this, holder.holder, position);
    }

    @Override
    public final int getItemCount() {
        return getTotalCount();
    }

    @Override
    public final AdapterInternal<IAdapter<T, VH>, T, VH> getInternal() {
        return internal;
    }

    public static class Builder<B, VH extends IHolder<B>> extends IViewAdapter.Builder<IAdapter<B, VH>, B, VH> {

        public Builder(int layoutId) {
            super(layoutId);
        }

        public Builder(BiFunction<ViewGroup, Integer, VH> supplier) {
            super(supplier);
        }

        public IAdapter<B, VH> build() {
            return new IAdapter<>(this);
        }
    }

    protected static class VHInternal<B, VH extends IHolder<B>> extends RecyclerView.ViewHolder {
        VH holder;

        public VHInternal(@NonNull VH holder) {
            super(holder.rootView());
            this.holder = holder;
        }
    }

}
