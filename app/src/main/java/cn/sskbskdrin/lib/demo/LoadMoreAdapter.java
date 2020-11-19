package cn.sskbskdrin.lib.demo;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver;

/**
 * Created by keayuan on 2020/11/10.
 *
 * @author keayuan
 */
public abstract class LoadMoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int type_more = 1000000;
    private static final int type_no_data = 1000001;

    public static final int STATUS_READY = 0;
    public static final int STATUS_LOADING = 1;
    public static final int STATUS_FAIL = 2;
    public static final int STATUS_END = 3;
    public static final int STATUS_HIDE = -1;

    private int status = 0;
    private final int pageSize;
    private View moreView;
    private View noDataView;
    private List<Object> list;

    private OnLoadMoreListener listener;

    public LoadMoreAdapter(List<Object> list, int pageSize) {
        this(list, pageSize, null);
    }

    public LoadMoreAdapter(List<Object> list, int pageSize, View moreView) {
        this(list, pageSize, moreView, null);
    }

    public LoadMoreAdapter(List<Object> list, int pageSize, View moreView, View noDataView) {
        this.list = list;
        this.pageSize = pageSize;
        setMoreView(moreView);
        setNoDataView(noDataView);
        registerAdapterDataObserver(new AdapterDataObserver() {
            @Override
            public void onChanged() {
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
                super.onItemRangeChanged(positionStart, itemCount, payload);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
            }
        });
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.listener = listener;
    }

    public void bindRecyclerView(RecyclerView view) {
        view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (recyclerView.computeVerticalScrollOffset() < 1) return;
                if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                    int pos =
                        ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                    if (pos == list.size() - 1 && status == STATUS_READY) {
                        changeStatus(STATUS_LOADING);
                        loadMore();
                    }
                }
            }
        });
        view.setAdapter(this);
        registerAdapterDataObserver(new AdapterDataObserver() {
            @Override
            public void onChanged() {
                if (list == null || list.size() == 0) {
                    changeStatus(STATUS_HIDE);
                } else if (list.size() % pageSize > 0) {
                    changeStatus(STATUS_END);
                } else {
                    changeStatus(STATUS_READY);
                }
            }
        });
        notifyDataSetChanged();
    }

    public void loadFailed() {
        changeStatus(STATUS_FAIL);
    }

    private void changeStatus(int s) {
        if (status != s) {
            status = s;
            onMoreChangeStatus(moreView, s);
        }
    }

    @NonNull
    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (moreView == null) {
            moreView = new TextView(parent.getContext());
            ((TextView) moreView).setGravity(Gravity.CENTER);
            int h = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, parent.getContext()
                .getResources()
                .getDisplayMetrics());
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(-1, h);
            moreView.setLayoutParams(lp);
            moreView.setOnClickListener(v -> {
                if (status == STATUS_FAIL) {
                    changeStatus(STATUS_LOADING);
                    loadMore();
                }
            });
        }
        if (noDataView == null) {
            noDataView = new View(parent.getContext());
        }
        if (viewType == type_no_data) return new RecyclerView.ViewHolder(noDataView) {};
        if (viewType == type_more) return new RecyclerView.ViewHolder(moreView) {};
        return onCreateHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position,
                                 @NonNull List<Object> payloads) {
        if (list != null && position < list.size()) {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    protected abstract RecyclerView.ViewHolder onCreateHolder(@NonNull ViewGroup parent, int viewType);

    protected abstract void onMoreChangeStatus(View view, int status);

    public void loadMore() {
        if (listener != null) {
            listener.onLoadMore(LoadMoreAdapter.this, list == null ? 0 : list.size() / pageSize);
        }
    }

    public void setMoreView(View view) {
        if (view == null) return;
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        moreView = view;
    }

    public void setNoDataView(View view) {
        if (view == null) return;
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        noDataView = view;
    }

    @Override
    public final int getItemViewType(int position) {
        if (list == null || list.size() == 0) return type_no_data;
        if (position == list.size()) return type_more;
        return getItemType(position);
    }

    public int getItemType(int position) {
        return 0;
    }

    @Override
    public final int getItemCount() {
        if (list == null || list.size() == 0) return 1;
        if (list.size() < pageSize) return list.size();
        return list.size() + 1;
    }

    public interface OnLoadMoreListener {
        void onLoadMore(LoadMoreAdapter adapter, int page);
    }
}
