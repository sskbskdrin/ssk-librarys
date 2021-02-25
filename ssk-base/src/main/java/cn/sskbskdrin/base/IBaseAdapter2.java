package cn.sskbskdrin.base;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by keayuan on 2021/2/25.
 *
 * @author keayuan
 */
public class IBaseAdapter2<T> extends BaseAdapter implements IViewAdapter<T> {
    private static final int TAG_VALUE = 0xff0000ff;
    protected List<T> mList;

    public IBaseAdapter2(List<T> list) {
        mList = list;
        getList();
    }

    public final List<T> getList() {
        if (mList == null) {
            mList = new ArrayList<>();
        }
        return mList;
    }

    @Override
    public T getItem(int position) {
        return getItemBean(position);
    }

    /**
     * 更新数据，并通知view数据改变
     *
     * @param list 更新的list
     */
    @Override
    public final void updateList(List<T> list) {
        mList = list;
        if (mList == null) {
            mList = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    @Override
    public IViewHolder<T> generateHolder(ViewGroup parent, int type) {
        return new DefaultViewHolder<>(new View(parent.getContext()));
    }

    @Override
    public int getCount() {
        return getTotalCount();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        IViewHolder<T> holder;
        if (convertView == null) {
            holder = generateHolder(parent, getItemViewType(position));
            holder.itemView().setTag(TAG_VALUE, holder);
        } else {
            holder = (IViewHolder<T>) convertView.getTag(TAG_VALUE);
        }
        bindHolder(holder, position);
        return holder.itemView();
    }
}
