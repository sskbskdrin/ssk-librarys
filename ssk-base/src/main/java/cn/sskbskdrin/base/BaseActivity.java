package cn.sskbskdrin.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ex-keayuan001
 * @date 17/10/19
 */
@SuppressLint("Registered")
public class BaseActivity extends FragmentActivity implements IView {

    protected Context mContext;
    protected int screenWidth;
    protected int screenHeight;
    private boolean isStop;

    private Dialog mLoadingDialog;
    protected ProxyView mProxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProxy = new ProxyView(this);
        mContext = this;
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }

    @Override
    protected void onResume() {
        isStop = false;
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        isStop = true;
        super.onStop();
    }

    public boolean isStop() {
        return isStop;
    }

    public Context getContext() {
        return this;
    }

    protected void setText(@IdRes int viewId, @StringRes int resId) {
        TextView view = getView(viewId);
        if (view != null) {
            view.setText(resId);
        }
    }

    protected void setText(TextView view, CharSequence text) {
        if (view != null) {
            view.setText(text != null ? text : "");
        }
    }

    public void showLoadingDialog(String content) {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
        if (!isFinishing()) {
            mLoadingDialog = generateLoadingDialog(content);
            mLoadingDialog.show();
        }
    }

    public void hideLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
        mLoadingDialog = null;
    }

    @Override
    public Dialog generateLoadingDialog(String content) {
        return ProgressDialog.show(this, "", content);
    }

    public static void showView(boolean show, View... views) {
        ProxyView.showView(show, views);
    }

    public void showToast(String text) {
        showToast(text, false);
    }

    public void showToast(String text, boolean isLong) {
        if (!isFinishing()) {
            Toast.makeText(this, text, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
        }
    }

    public void openActivity(Class clazz) {
        openActivity(clazz, null);
    }

    public void openActivity(Class clazz, Bundle bundle) {
        openActivity(clazz, bundle, -1);
    }

    public void openActivity(Class clazz, Bundle bundle, int requestCode) {
        Intent intent = new Intent(this, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        try {
            if (requestCode >= 0) {
                startActivityForResult(intent, requestCode);
            } else {
                startActivity(intent);
            }
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int id) {
        return (T) findViewById(id);
    }

    @Override
    public Context context() {
        return this;
    }

    @Override
    public boolean isFinish() {
        return isFinishing();
    }

    @SuppressWarnings("unchecked")
    protected static <T extends View> T getView(View parent, int id) {
        if (parent != null) {
            return (T) parent.findViewById(id);
        }
        return null;
    }

    private PermissionCallback mPermissionCallback;

    public static boolean checkPermission(@NonNull BaseActivity activity, int requestCode, PermissionCallback
        callback, String... permissions) {
        return activity.checkPermission(requestCode, callback, permissions);
    }

    public boolean checkPermission(int requestCode, String... permissions) {
        return checkPermission(requestCode, null, permissions);
    }

    public boolean checkPermission(int requestCode, PermissionCallback callback, String... permissions) {
        mPermissionCallback = callback;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> list = new ArrayList<>();
            for (String permission : permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    list.add(permission);
                }
            }
            if (list.size() > 0) {
                String a[] = new String[list.size()];
                list.toArray(a);
                requestPermissions(a, requestCode);
                return false;
            }
        }
        return true;
    }

    @Override
    public final void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
        grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                list.add(permissions[i]);
            }
        }
        if (mPermissionCallback != null) {
            mPermissionCallback.onRequestPermissions(requestCode, list);
        } else {
            onRequestPermissions(requestCode, list);
        }
        mPermissionCallback = null;
    }

    /**
     * 申请权限时回调，
     *
     * @param requestCode 请求码
     * @param deniedList  被拒绝列表，为空时表明申请成功
     */
    protected void onRequestPermissions(int requestCode, @NonNull List<String> deniedList) {

    }

    public static String getPermissionTips(@NonNull List<String> deniedList, String content) {
        StringBuilder builder = new StringBuilder("请开启");
        for (String permission : deniedList) {
            if (Manifest.permission.RECORD_AUDIO.equals(permission)) {
                builder.append("录音");
            } else if (Manifest.permission.READ_EXTERNAL_STORAGE.equals(permission) || Manifest.permission
                .WRITE_EXTERNAL_STORAGE.equals(permission)) {
                builder.append("存储");
            } else if (Manifest.permission.CAMERA.equals(permission)) {
                builder.append("相机");
            }
            builder.append("、");
        }
        builder.setLength(builder.length() - 1);
        builder.append("权限，否则无法使用");
        builder.append(content != null ? content : "该");
        builder.append("功能！");
        return builder.toString();
    }

    public interface PermissionCallback {
        /**
         * 申请权限时回调
         *
         * @param requestCode 请求码
         * @param deniedList  被拒绝列表，为空时表明申请成功
         */
        void onRequestPermissions(int requestCode, @NonNull List<String> deniedList);
    }
}
