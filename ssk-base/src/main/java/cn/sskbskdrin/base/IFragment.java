package cn.sskbskdrin.base;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by sskbskdrin on 2015/5/25.
 * Fragment 基类
 */
public abstract class IFragment extends Fragment implements IView, Handler.Callback {
    protected final String TAG;
    /**
     * 在initData中可直接使用
     */
    protected Bundle mBundle;
    protected View mRootView;
    protected boolean isRunning;

    private Handler mHandler;
    private Activity mActivity;
    protected final ProxyView mProxy;

    public IFragment() {
        super();
        TAG = getClass().getSimpleName();
        mProxy = new ProxyView(this);
    }

    /**
     * @return 返回布局资源的layoutId
     */
    protected abstract int getLayoutId();

    /**
     * 在view被创建时调用{@link IFragment#onViewCreated(View, Bundle)}
     */
    protected abstract void initView();

    /**
     * 在{@link IFragment#onActivityCreated(Bundle)}被调用时调用
     */
    protected abstract void initData();

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }

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
        isRunning = true;
        Log.v(TAG, "onCreate");
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            Log.v(TAG, "onCreateView new");
            if (getLayoutId() > 0) {
                mRootView = inflater.inflate(getLayoutId(), null);
            }
        }
        if (mRootView == null) {
            mRootView = generateRootView(inflater);
        }
        if (mRootView != null) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null) {
                Log.v(TAG, "onCreateView old");
                parent.removeView(mRootView);
            }
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initView();
    }

    /**
     * 当{@link IFragment#getLayoutId()} 返回值小于等于0时调用，可动态创建view
     *
     * @param inflater 构建view用的inflater
     * @return 返回要显示的view
     */
    protected View generateRootView(LayoutInflater inflater) {
        return null;
    }

    @Override
    public final void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v(TAG, "onActivityCreated");
        mBundle = getArguments();
        initData();
    }

    public final View getRootView() {
        return mRootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.v(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
        mActivity = null;
        isRunning = false;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
        Log.v(TAG, "onDetach");
    }

    public void showToast(String text) {
        showToast(text, false);
    }

    public void showToast(String text, boolean isLong) {
        if (mActivity != null && !mActivity.isFinishing()) {
            Toast.makeText(mActivity, text, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
        }
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

    @Override
    public Dialog generateLoadingDialog(String content) {
        return ProgressDialog.show(mActivity, "", content);
    }

    protected Handler getMainHandler() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper(), this);
        }
        return mHandler;
    }
}
