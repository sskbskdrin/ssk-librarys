package cn.sskbskdrin.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

/**
 * Created by sskbskdrin on 2015/5/25.
 * Fragment 基类
 */
public abstract class IFragment extends Fragment implements IA {
    protected final String TAG;
    /**
     * 在initData中可直接使用
     */
    protected View mRootView;

    private Activity mActivity;

    public IFragment() {
        super();
        TAG = getClass().getSimpleName();
    }

    /**
     * @return 返回布局资源的layoutId
     */
    protected abstract int getLayoutId();

    /**
     * 在{@link IFragment#onActivityCreated(Bundle)} 被调用时调用
     *
     * @param arguments 创建时传递的参数
     */
    protected void onInitData(Bundle arguments) {}

    /**
     * 在view被创建时调用,在{@link IFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)} 时调用，如果只不销毁view，就只会调用一次
     *
     * @param rootView           创建的根view
     * @param savedInstanceState 数据
     */
    protected abstract void onInitView(View rootView, Bundle savedInstanceState);

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
        Log.v(TAG, "onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");
    }

    private boolean firstCreateView = true;

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            Log.v(TAG, "onCreateView new");
            int layoutId = getLayoutId();
            if (layoutId > 0) {
                mRootView = inflater.inflate(layoutId, null);
            }
        }
        if (mRootView == null) {
            mRootView = generateRootView(inflater, container, savedInstanceState);
        }
        if (mRootView != null) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null) {
                Log.v(TAG, "onCreateView old");
                parent.removeView(mRootView);
            }
        }
        if (firstCreateView) {
            firstCreateView = false;
            onInitView(mRootView, savedInstanceState);
        }
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onInitData(getArguments());
    }

    /**
     * 当{@link IFragment#getLayoutId()} 返回值小于等于0时调用，可动态创建view
     *
     * @param inflater           构建view用的inflater
     * @param container          父view
     * @param savedInstanceState 保存的数据
     * @return 返回要显示的view
     */
    protected View generateRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    /**
     * 在{@link IFragment#onDestroyView()}时是否销毁view
     *
     * @return true则销毁，重新打开时，会重新创建view
     */
    protected boolean isDestroyView() {
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isDestroyView()) {
            mRootView = null;
            firstCreateView = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
        mActivity = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
        Log.v(TAG, "onDetach");
    }

    @Override
    public Context context() {
        return mActivity;
    }

    @Override
    public boolean isFinish() {
        return mActivity == null || mActivity.isFinishing();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends View> T getView(int id) {
        return (T) mRootView.findViewById(id);
    }

    @SuppressWarnings("unchecked")
    protected final <T extends View> T getRootView() {
        return (T) mRootView;
    }

}
