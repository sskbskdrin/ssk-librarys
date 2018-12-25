package cn.sskbskdrin.base;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sskbskdrin on 2015/6/2.
 */
public abstract class IBaseAdapter<T> extends BaseAdapter implements IView {
    private static final int TAG_VALUE = 0xff0000ff;
    protected Context mContext;
    protected List<T> mList;
    protected ProxyView mProxy;

    private int mLayoutId = 0;

    private View mCurrentView;

    public IBaseAdapter(Context context, List<T> list) {
        this(context, list, 0);
    }

    public IBaseAdapter(Context context, List<T> list, @LayoutRes int layoutId) {
        mProxy = new ProxyView(this);
        mContext = context;
        mLayoutId = layoutId;
        mList = list;
        if (mList == null) {
            mList = new ArrayList<>();
        }
    }

    public List<T> getList() {
        if (mList == null) {
            mList = new ArrayList<>();
        }
        return mList;
    }

    /**
     * 更新数据，并通知view数据改变
     *
     * @param list 更新的list
     */
    public void updateList(List<T> list) {
        mList = list;
        if (mList == null) {
            mList = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    @Override
    public final int getCount() {
        return mList.size();
    }

    @Override
    public final T getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if (convertView == null) {
            int layoutId = getLayoutId(type);
            if (layoutId <= 0) {
                convertView = generateView(position, parent, type);
            } else {
                convertView = View.inflate(mContext, layoutId, null);
            }
            if (convertView != null) {
                convertView.setTag(TAG_VALUE, new SparseArray<>(4));
            }
        }
        mCurrentView = convertView;
        convert(convertView, position, getItem(position));
        return convertView;
    }

    @SuppressWarnings("unchecked")
    public final <V extends View> V getView(@IdRes int id) {
        SparseArray<View> array = (SparseArray<View>) mCurrentView.getTag(TAG_VALUE);
        if (array == null) {
            array = new SparseArray<>();
            mCurrentView.setTag(TAG_VALUE, array);
        }
        View view = array.get(id);
        if (view == null) {
            view = mCurrentView.findViewById(id);
            array.put(id, view);
        }
        return (V) view;
    }

    @Override
    public Context context() {
        return mContext;
    }

    @Override
    public boolean isFinish() {
        return mContext == null;
    }

    @Override
    public Dialog generateLoadingDialog(String content) {
        return null;
    }

    @SuppressWarnings("unchecked")
    protected final <V extends View> V getView(View parent, @IdRes int id) {
        if (parent != null) {
            return (V) parent.findViewById(id);
        }
        return null;
    }

    @LayoutRes
    protected int getLayoutId(int type) {
        return mLayoutId;
    }

    /**
     * {@link IBaseAdapter#getLayoutId(int)}返回值小于等于0时调用
     *
     * @param position item的位置
     * @param parent   父控件
     * @param type     {@link IBaseAdapter#getItemViewType(int)}返回的类型
     * @return 返回一个view
     */
    protected View generateView(int position, ViewGroup parent, int type) {
        return null;
    }

    /**
     * @param view     要操作的view
     * @param position current position
     * @param t        当前位置对应的bean
     */
    protected abstract void convert(View view, int position, T t);
}
