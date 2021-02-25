package cn.sskbskdrin.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Random;

/**
 * Created by keayuan on 16/1/22.
 */
public class CommonUtils {

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale);
    }

    /**
     * 获取屏幕的实际宽度
     *
     * @return 屏幕宽度
     */
    public static int getRealScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager != null ? manager.getDefaultDisplay() : null;
        Point point = new Point();
        if (display != null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                display.getRealSize(point);
            } else {
                display.getSize(point);
            }
        }
        return point.x;
    }

    /**
     * 获取屏幕的宽度
     *
     * @return 屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        Display display = manager != null ? manager.getDefaultDisplay() : null;
        if (display != null) {
            display.getSize(point);
        }
        return point.x;
    }

    /**
     * 获取屏幕的实际高度
     *
     * @return 屏幕高度
     */
    public static int getRealScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager != null ? manager.getDefaultDisplay() : null;
        Point point = new Point();
        if (display != null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                manager.getDefaultDisplay().getRealSize(point);
            } else {
                manager.getDefaultDisplay().getSize(point);
            }
        }
        return point.y;
    }

    /**
     * 获取屏幕显示的高度
     *
     * @return 屏幕高度
     */
    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        Display display = manager != null ? manager.getDefaultDisplay() : null;
        if (display != null) {
            display.getSize(point);
        }
        return point.y;
    }

    public static boolean hasShowNavigation(Activity activity) {
        if (activity != null && activity.getWindow() != null) {
            FrameLayout layout = (FrameLayout) activity.getWindow().getDecorView();
            if (layout != null) {
                for (int i = 0; i < layout.getChildCount(); i++) {
                    if (layout.getChildAt(i).getId() == android.R.id.navigationBarBackground) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static int getColor(Context context, int resId) {
        int color = 0;
        if (context != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                color = context.getResources().getColor(resId);
            } else {
                color = context.getColor(resId);
            }
        }
        return color;
    }

    public static String getString(Context context, int resId, Object... args) {
        if (context != null) {
            return context.getString(resId, args);
        }
        return null;
    }

    public static void setDrawableSize(TextView view, int size) {
        setDrawableSize(view, size, size);
    }

    public static void setDrawableSize(TextView view, int width, int height) {
        if (view == null || width <= 0 || height <= 0) return;
        Drawable[] drawables = view.getCompoundDrawables();
        for (Drawable drawable : drawables) {
            if (drawable != null) {
                drawable.setBounds(0, 0, width, height);
            }
        }
        view.setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view instanceof CompoundButton) {
                Drawable buttonDrawable = ((CompoundButton) view).getButtonDrawable();
                if (buttonDrawable != null) {
                    buttonDrawable.setBounds(0, 0, width, height);
                    ((CompoundButton) view).setButtonDrawable(buttonDrawable);
                }
            }
        }
    }

    public static Drawable getDrawable(Context context, int resId) {
        Drawable drawable = null;
        if (context != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                drawable = context.getResources().getDrawable(resId);
            } else {
                drawable = context.getDrawable(resId);
            }
        }
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        }
        return drawable;
    }

    public static Drawable getDrawable(Context context, int resId, int size) {
        return getDrawable(context, resId, size, size);
    }

    public static Drawable getDrawable(Context context, int resId, int width, int height) {
        Drawable drawable = getDrawable(context, resId);
        if (drawable == null) return null;
        drawable.setBounds(0, 0, width, height);
        return drawable;
    }

    public static float getScreenDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * 获取顶部状态栏高度
     *
     * @return 状态栏高度
     */
    public static int getStatusHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * 获取底部导航栏高度
     *
     * @return 导航栏高度
     */
    public static int getNavigationHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * 程序是否在前台运行
     */
    public static boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getApplicationContext()
            .getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return false;
        }
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        String packageName = context.getApplicationContext().getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName) && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    public static void showInputManager(EditText view) {
        InputMethodManager manager = ((InputMethodManager) view.getContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE));
        if (manager != null) {
            manager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /**
     * 隐藏软键盘
     *
     * @param activity
     */
    public static void hideInputManager(Activity activity) {
        try {
            InputMethodManager manager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            View view = activity.getCurrentFocus();
            if (view != null && view.getWindowToken() != null && manager != null) {
                manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
            Log.e("test", "hideInputManager Catch error,skip it!", e);
        }
    }

    /**
     * 拨打电话（跳转到拨号界面，用户手动点击拨打）
     *
     * @param phoneNum 电话号码
     */
    public static void callPhone(Context context, String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNum));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static String MD5(String str) {
        String reStr = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(str.getBytes());
            StringBuilder builder = new StringBuilder();
            for (byte b : bytes) {
                int bt = b & 0xff;
                if (bt < 16) {
                    builder.append(0);
                }
                builder.append(Integer.toHexString(bt));
            }
            reStr = builder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return reStr;
    }

    /**
     * 动态改变listView的高度
     *
     * @param listView
     */
    public static void measureListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        int listViewWidth = listView.getMeasuredWidth();
        int widthSpec = View.MeasureSpec.makeMeasureSpec(listViewWidth, View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(widthSpec, 0);

            int itemHeight = listItem.getMeasuredHeight();
            totalHeight += itemHeight;
        }
        listView.getLayoutParams().height = totalHeight + listView.getDividerHeight() * listAdapter.getCount();
        listView.requestLayout();
    }

    public static String getCurrentProcessName(Context application) {
        int myPid = Process.myPid();
        ActivityManager manager = (ActivityManager) application.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : manager.getRunningAppProcesses()) {
                if (runningAppProcessInfo.pid == myPid) {
                    return runningAppProcessInfo.processName;
                }
            }
        }
        return null;
    }

    /**
     * 获取一个随机数，在一定范围内[start,end)
     *
     * @param start 随机数开始值，包含
     * @param end   随机数结束值，不包含
     * @return 返回一个随机数
     */
    public static int random(int start, int end) {
        Random random = new Random();
        int min = Math.min(start, end);
        int max = Math.max(start, end);
        return min + random.nextInt(max - min);
    }

    /**
     * 创建目录
     *
     * @param path 目录绝对路径
     */
    public static String getPath(String path) {
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        return path;
    }

    /**
     * 判断手机格式是否正确
     */
    public static boolean isPhoneNumber(String number) {
        String telRegex = "[1][3456789]\\d{9}";
        return !TextUtils.isEmpty(number) && number.matches(telRegex);
    }

    public static boolean isEmail(String mail) {
        if (mail != null) {
            String[] part = mail.split("@");
            if (part.length == 2) {
                //    String ATOM = "[a-zA-Z0-9-_]";
                //    String DOMAIN = ATOM + "+(\\." + ATOM + "+)*";
                //    String IP = "[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}]";
                //    Pattern localPattern = Pattern.compile(ATOM + "+(\\." + ATOM + "+)*", Pattern.CASE_INSENSITIVE);
                //    Pattern domainPattern = Pattern.compile(DOMAIN + "|" + IP);
                return Patterns.EMAIL_ADDRESS.matcher(mail).matches();
            }
        }
        return false;
    }

    public static SpannableString spanColor(CharSequence str, int color, int start, int end) {
        SpannableString ss = SpannableString.valueOf(str);
        ss.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return ss;
    }

    public static SpannableString spanSize(CharSequence str, int sp, int start, int end) {
        SpannableString ss = SpannableString.valueOf(str);
        ss.setSpan(new AbsoluteSizeSpan(sp, true), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return ss;
    }
}
