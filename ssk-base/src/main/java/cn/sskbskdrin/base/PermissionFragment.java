package cn.sskbskdrin.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.Arrays;

/**
 * Created by keayuan on 2020/11/11.
 *
 * @author keayuan
 */
@SuppressLint("ValidFragment")
public final class PermissionFragment extends Fragment {
    private static final String TAG = "PermissionFragment";
    private static final String KEY_PERMISSIONS = "key_permissions";
    private static final String KEY_CODE = "key_code";
    private WeakReference<IPermission> permission;

    private PermissionFragment() {}

    static void request(Activity activity, IPermission permission, int requestCode, String... permissions) {
        PermissionFragment fragment = new PermissionFragment();
        fragment.permission = new WeakReference<>(permission);
        Bundle bundle = new Bundle();
        bundle.putStringArray(KEY_PERMISSIONS, permissions);
        bundle.putInt(KEY_CODE, requestCode);
        fragment.setArguments(bundle);
        Fragment temp = activity.getFragmentManager().findFragmentByTag(TAG);
        if (temp != null) {
            activity.getFragmentManager().beginTransaction().remove(temp).commit();
        }
        activity.getFragmentManager().beginTransaction().add(fragment, TAG).commit();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        String[] permissions = bundle.getStringArray(KEY_PERMISSIONS);
        int requestCode = bundle.getInt(KEY_CODE);
        Log.d(TAG, "request permissions: " + Arrays.toString(permissions));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            requestPermissions(permissions, requestCode);
        } else {
            getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        IPermission IP = permission == null ? null : permission.get();
        if (IP != null) {
            IP.requestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
