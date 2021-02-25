package cn.sskbskdrin.base;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by keayuan on 2021/2/25.
 *
 * @author keayuan
 */
public interface IViewAdapter<T> {
    int CLICK_ITEM_ID = 0xff000100;

    IViewHolder<T> generateHolder(ViewGroup parent, int type);

    List<T> getList();

    void updateList(List<T> list);

    void notifyDataSetChanged();

    default void bindHolder(IViewHolder<T> holder, int position) {
        if (holder == null) {
            return;
        }
        T t = getItemBean(position);
        holder.setItem(t, position);
        ClickItem<T> clickItem = (ClickItem<T>) holder.itemView().getTag(CLICK_ITEM_ID);
        if (clickItem == null) {
            clickItem = new ClickItem<>(this);
        }
        clickItem.holder = holder;
        holder.itemView().setTag(CLICK_ITEM_ID, clickItem);

        holder.updateView(t, position);
        holder.itemView().setOnClickListener(clickItem);
        holder.itemView().setOnLongClickListener(clickItem);
    }

    default T getItemBean(int position) {
        return getList().get(position);
    }

    default int getTotalCount() {
        List<T> list = getList();
        return list == null ? 0 : list.size();
    }

    default void onClickItem(IViewHolder<T> holder) {
        holder.onClickItem();
    }

    default boolean onLongClickItem(IViewHolder<T> holder) {
        return holder.onLongClickItem();
    }

    class ClickItem<T> implements View.OnClickListener, View.OnLongClickListener {
        private IViewHolder<T> holder;
        private IViewAdapter<T> adapter;

        private ClickItem(IViewAdapter<T> adapter) {
            this.adapter = adapter;
        }

        @Override
        public void onClick(View v) {
            adapter.onClickItem(holder);
        }

        @Override
        public boolean onLongClick(View v) {
            return adapter.onLongClickItem(holder);
        }
    }
}
