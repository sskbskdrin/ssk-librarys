package cn.sskbskdrin.base;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 权限请求工具，需要用到Activity
 *
 * @author keayuan
 * 2020/3/27
 */
public interface IPermission extends IContext {
    class IPermissionC {
        private PermissionCallback callback;

        private IPermissionC() {
        }
    }

    IPermissionC mPC = new IPermissionC();

    /**
     * 只检查，不申请
     *
     * @param permissions 检查的权限
     * @return 权限列表是否都已经拥有权限
     */
    default boolean hasPermission(String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Context context = context();
            if (context != null) {
                List<String> list = new ArrayList<>();
                for (String permission : permissions) {
                    if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                        list.add(permission);
                    }
                }
                return list.size() == 0;
            }
        }
        return checkPermissionUnderM(permissions);
    }

    /**
     * 检查权限，没有则申请
     * 在{@link IPermission#onRequestPermissions(int, List, List)}中回调
     * activity中权限申请回调需要调用{@link IPermission#requestPermissionsResult(int, String[], int[])}才能成功回调到callback
     *
     * @param requestCode 申请码
     * @param permissions 权限列表
     * @return 返回当前权限列表是否已经拥有权限
     */
    default boolean checkPermission(int requestCode, String... permissions) {
        return checkPermission(requestCode, null, permissions);
    }

    /**
     * 检查权限，没有则申请
     * activity中权限申请回调需要调用{@link IPermission#requestPermissionsResult(int, String[], int[])}才能成功回调到callback
     *
     * @param requestCode 申请码
     * @param callback    申请结果回调
     * @param permissions 权限列表
     * @return 返回当前权限列表是否已经拥有权限
     */
    default boolean checkPermission(int requestCode, PermissionCallback callback, String... permissions) {
        mPC.callback = callback;
        Context context = context();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean flag = false;
            for (String permission : permissions) {
                if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                PermissionFragment.request((Activity) context, this, requestCode, permissions);
                return false;
            } else {
                if (callback != null) {
                    callback.onRequestPermissions(requestCode, Collections.emptyList(), Arrays.asList(permissions));
                }
                return true;
            }
        }
        return checkPermissionUnderM(permissions);
    }

    default List<String> getNoShowTipPermission(String... permissions) {
        Context context = context();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context instanceof Activity) {
            List<String> list = new ArrayList<>();
            for (String permission : permissions) {
                if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED && !((Activity) context)
                    .shouldShowRequestPermissionRationale(permission)) {
                    list.add(permission);
                }
            }
            return list;
        }
        return Collections.emptyList();
    }

    /**
     * 检查小于Build.VERSION_CODES.M的版本权限
     *
     * @param permissions 权限列表
     * @return 返回是否有权限
     */
    default boolean checkPermissionUnderM(String... permissions) {
        return true;
    }

    /**
     * 请求权限，回调时调用本方法，否则内部callback无法回调
     *
     * @param requestCode  请求码
     * @param permissions  权限列表
     * @param grantResults 申请结果列表
     */
    default void requestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        List<String> grantedList = new ArrayList<>();
        List<String> deniedList = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                grantedList.add(permissions[i]);
            } else {
                deniedList.add(permissions[i]);
            }
        }
        if (mPC.callback != null) {
            mPC.callback.onRequestPermissions(requestCode, deniedList, grantedList);
        } else {
            onRequestPermissions(requestCode, deniedList, grantedList);
        }
        mPC.callback = null;
    }

    /**
     * 申请权限时回调，
     *
     * @param requestCode 请求码
     * @param deniedList  被拒绝列表，为空时表明申请成功
     * @param grantedList 请求成功的列表
     */
    default void onRequestPermissions(int requestCode, List<String> deniedList, List<String> grantedList) {
    }

    /**
     * 基本的提示信息，
     *
     * @param deniedList 没有权限的权限列表
     * @param content    中间提示内容
     * @return 提示的一句话
     */
    static String getPermissionTips(List<String> deniedList, String content) {
        StringBuilder builder = new StringBuilder("请开启");
        for (String permission : deniedList) {
            if (Manifest.permission.RECORD_AUDIO.equals(permission)) {
                builder.append("录音");
            } else if (Manifest.permission.READ_EXTERNAL_STORAGE.equals(permission) || Manifest.permission.WRITE_EXTERNAL_STORAGE
                .equals(permission)) {
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

    interface PermissionCallback {
        /**
         * 申请权限时回调
         *
         * @param requestCode 请求码
         * @param deniedList  被拒绝列表，为空时表明申请成功
         * @param grantedList 请求成功的列表
         */
        void onRequestPermissions(int requestCode, List<String> deniedList, List<String> grantedList);
    }
}
