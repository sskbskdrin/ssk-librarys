package cn.sskbskdrin.lib.demo.widget;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by keayuan on 2020/11/25.
 *
 * @author keayuan
 */
public class PullFragment extends IFragment {
    final List list = new ArrayList();
    public static final int PAGE_SIZE = 30;

    @Override
    protected int getLayoutId() {
        return R.layout.f_pull_layout;
    }

    @Override
    protected void onViewCreated(View rootView, Bundle arguments, Bundle savedInstanceState) {
        RecyclerView recyclerView = getView(R.id.pull_recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        for (int i = 0; i < 20; i++) {
            list.add("init " + i);
        }
        final LoadMoreAdapter adapter = new LoadMoreAdapter(list, PAGE_SIZE) {

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
                ((TextView) (holder.itemView)).setMaxWidth(300);
                ((TextView) (holder.itemView)).setText(list.get(position).toString());
            }
        };
        adapter.setOnLoadMoreListener((adapter1, page) -> {
            Log.d(TAG, "onLoadMore: " + page);
            postDelayed(() -> {
                if (Math.random() < 0.2 && page > 0) {
                    adapter.loadFailed();
                    return;
                }
                for (int i = 0; i < (page < 1 ? PAGE_SIZE : 5); i++) {
                    list.add("more " + list.size());
                }
                adapter.notifyDataSetChanged();
            }, 10);
        });
        adapter.setNoDataView(getView(R.id.simple_no_data));
        //        adapter.bindRecyclerView(recyclerView);
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new RecyclerView.ViewHolder(View.inflate(parent.getContext(),
                    android.R.layout.simple_list_item_1, null)) {};
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                holder.itemView.setBackgroundResource(R.drawable.rect_white_bg);
                //                ((TextView) (holder.itemView)).setMaxWidth(300);
                ((TextView) (holder.itemView)).setText(list.get(position).toString());
            }

            @Override
            public int getItemCount() {
                return list.size();
            }
        });

        SwipeLayout refreshLayout = getView(R.id.swipe);
        refreshLayout.addSwipeRefreshListener((position) -> postDelayed(() -> {
            list.clear();
            for (int i = 0; i < PAGE_SIZE; i++) {
                list.add("refresh " + list.size());
            }
            recyclerView.getAdapter().notifyDataSetChanged();
            refreshLayout.refreshComplete(SwipePosition.TOP, true);
            refreshLayout.setEnabled(SwipePosition.BOTTOM, true);
        }, 2000));

        refreshLayout.addSwipeRefreshListener(SwipePosition.BOTTOM, position -> postDelayed(() -> {
            for (int i = 0; i < PAGE_SIZE; i++) {
                list.add("more " + list.size());
            }
            recyclerView.getAdapter().notifyDataSetChanged();
            refreshLayout.refreshComplete(SwipePosition.BOTTOM, false);
            refreshLayout.setEnabled(SwipePosition.BOTTOM, false);
        }, 2000));

        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), 1));
    }
}
