package cn.sskbskdrin.frame.base.ui;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by keayuan on 2020/4/7.
 *
 * @author keayuan
 */
public abstract class IRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int layoutId;
    private List<T> mList;
    private RecyclerView.ViewHolder mCurrentHolder;
    private OnClickItemListener<T> onClickItemListener;

    public IRecyclerAdapter(List<T> list) {
        updateList(list);
    }

    public IRecyclerAdapter(List<T> list, int layoutId) {
        this.layoutId = layoutId;
        updateList(list);
    }

    public final void updateList(List<T> list) {
        mList = list;
        if (mList == null) {
            mList = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView;
        int layoutId = getLayoutId(viewType);
        if (layoutId <= 0) {
            convertView = generateView(parent, viewType);
        } else {
            convertView = View.inflate(parent.getContext(), layoutId, null);
        }
        return new ViewHolder(convertView);
    }

    @Override
    public final void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        mCurrentHolder = holder;
        holder.itemView.setOnClickListener(v -> onClickItem(v, position, mList.get(position)));
        convert(holder.itemView, holder, position, mList.get(position));
    }

    @SuppressWarnings("unchecked")
    public <V> V getView(int id) {
        return (V) mCurrentHolder.itemView.findViewById(id);
    }

    protected int getLayoutId(int type) {
        return layoutId;
    }

    protected View generateView(ViewGroup parent, int type) {
        return null;
    }

    protected RecyclerView.ViewHolder createHolder(View view, int type) {
        return new ViewHolder(view);
    }

    protected abstract void convert(View view, Object holder, int position, T t);

    public void onClickItem(View view, int position, T t) {
        if (onClickItemListener != null) {
            onClickItemListener.onClickItem(view, position, t);
        }
    }

    public void setOnClickItemListener(OnClickItemListener<T> listener) {
        onClickItemListener = listener;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public interface OnClickItemListener<T> {
        void onClickItem(View view, int position, T t);
    }
}
