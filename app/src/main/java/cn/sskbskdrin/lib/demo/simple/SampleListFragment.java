package cn.sskbskdrin.lib.demo.simple;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.sskbskdrin.base.IFragment;
import cn.sskbskdrin.lib.demo.LoadMoreAdapter;
import cn.sskbskdrin.lib.demo.R;
import cn.sskbskdrin.pull.PullLayout;
import cn.sskbskdrin.pull.PullRefreshHolder;

public class SampleListFragment extends IFragment implements OnItemClickListener {

    public static final String LIST_DATA = "list_data";
    private RecyclerView mListView;
    private OnItemClickListener mOnItemClickListener;
    private PullRefreshHolder refreshHolder;

    final List list = new ArrayList();

    @Override
    protected int getLayoutId() {
        return R.layout.f_simple_layout;
    }

    @Override
    protected View generateRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new RecyclerView(inflater.getContext());
    }

    @Override
    protected void onInitView(View rootView, Bundle savedInstanceState) {
        mListView = getView(R.id.simple_list);
        PullLayout pullLayout = getView(R.id.simple_pull);
        refreshHolder = pullLayout.getPullRefreshHolder();
        final LoadMoreAdapter adapter = new LoadMoreAdapter(list, 20) {

            @Override
            protected RecyclerView.ViewHolder onCreateHolder(@NonNull ViewGroup parent, int viewType) {
                return new RecyclerView.ViewHolder(View.inflate(parent.getContext(),
                    android.R.layout.simple_list_item_1, null)) {};
            }

            @Override
            protected void onMoreChangeStatus(View view, int status) {
                Log.d(TAG, "onMoreChangeStatus: " + status);
                if (status == LoadMoreAdapter.STATUS_READY) ((TextView) view).setText("加载更多");
                if (status == LoadMoreAdapter.STATUS_LOADING) ((TextView) view).setText("加载中...");
                if (status == LoadMoreAdapter.STATUS_FAIL) ((TextView) view).setText("加载失败，点击重试");
                if (status == LoadMoreAdapter.STATUS_END) ((TextView) view).setText("没有更多了");
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                holder.itemView.setBackgroundResource(R.drawable.rect_white_bg);
                ((TextView) (holder.itemView)).setText(list.get(position).toString());
            }
        };
        adapter.setOnLoadMoreListener((adapter1, page) -> {
            Log.d(TAG, "onLoadMore: " + page);
            postDelayed(() -> {
                if (Math.random() < 0.5 && page > 0) {
                    adapter.loadFailed();
                    return;
                }
                for (int i = 0; i < (page < 2 ? 20 : 5); i++) {
                    list.add("more " + list.size());
                }
                adapter.notifyDataSetChanged();
            }, 10);
        });
        adapter.setNoDataView(getView(R.id.simple_no_data));
        mListView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.bindRecyclerView(mListView);
        refreshHolder.addPullRefreshCallback(PullLayout.Direction.TOP, direction -> {
            list.clear();
            for (int i = 0; i < 20; i++) {
                list.add("more " + list.size());
            }
            adapter.notifyDataSetChanged();
            refreshHolder.refreshComplete(PullLayout.Direction.TOP);
        });
        mListView.addItemDecoration(new DividerItemDecoration(getContext(), 1));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(adapterView, view, position, id);
        }
    }
}
