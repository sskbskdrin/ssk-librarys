package cn.sskbskdrin.lib.demo.simple;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.sskbskdrin.base.IFragment;

public class SampleListFragment extends IFragment implements OnItemClickListener {

    public static final String LIST_DATA = "list_data";
    private ListView mListView;
    private OnItemClickListener mOnItemClickListener;

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected View generateRootView(LayoutInflater inflater) {
        return new ListView(inflater.getContext());
    }

    @Override
    protected void initView() {
        mListView = (ListView) getRootView();
        mListView.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        List<Object> list = null;
        if (mBundle != null) {
            list = (List<Object>) mBundle.getSerializable(LIST_DATA);
        }
        if (list == null) {
            list = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                list.add("Simple item +" + i);
            }
        }
        mListView.setAdapter(new SimpleAdapter<>(getActivity(), list));
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(adapterView, view, position, id);
        }
    }
}
