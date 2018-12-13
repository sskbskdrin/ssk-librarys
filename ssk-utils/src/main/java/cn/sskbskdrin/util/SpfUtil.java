package cn.sskbskdrin.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * SharedPreference 工具类
 *
 * @author keayuan001
 * @date 2013-9-9
 * @time 上午11:42:54
 */
public class SpfUtil {
    private static final String DEFAULT = "default";
    private static final String TAG = "SpfUtils";

    public static SharedPreferences get(Context context) {
        return get(context, DEFAULT);
    }

    public static SharedPreferences get(Context context, String name) {
        return get(context, name, Context.MODE_PRIVATE);
    }

    public static SharedPreferences get(Context context, String name, int mode) {
        if (context != null) {
            return context.getSharedPreferences(name, mode);
        }
        return null;
    }

    public static boolean check(SharedPreferences sp) {
        if (sp == null) {
            Log.e(TAG, "sharePreferences is null");
        }
        return sp != null;
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param key 关键字
     */
    public static boolean contains(SharedPreferences sp, String key) {
        return check(sp) && sp.contains(key);
    }

    /**
     * @param key   关键字
     * @param value 存储值
     */
    public static void putInt(SharedPreferences sp, String key, int value) {
        if (check(sp)) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(key, value);
            editor.apply();
        }
    }

    /**
     * @param key          关键字
     * @param defaultValue 默认值
     */
    public static int getInt(SharedPreferences sp, String key, int defaultValue) {
        if (check(sp)) {
            return sp.getInt(key, defaultValue);
        }
        return defaultValue;
    }

    /**
     * @param key 关键字
     * @return 默认返回0
     */
    public static int getInt(SharedPreferences sp, String key) {
        return getInt(sp, key, 0);
    }

    /**
     * @param key   关键字
     * @param value 存储值
     */
    public static void putBoolean(SharedPreferences sp, String key, boolean value) {
        if (check(sp)) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(key, value);
            editor.apply();
        }
    }

    /**
     * @param key 关键字
     * @return 默认false
     */
    public static boolean getBoolean(SharedPreferences sp, String key) {
        return getBoolean(sp, key, false);
    }

    public static boolean getBoolean(SharedPreferences sp, String key, boolean defaultValue) {
        if (check(sp)) {
            return sp.getBoolean(key, false);
        }
        return defaultValue;
    }

    /**
     * @param key   关键字
     * @param value 存储值
     */
    public static void putString(SharedPreferences sp, String key, String value) {
        if (check(sp)) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, value == null ? "" : value);
            editor.apply();
        }
    }

    /**
     * @param key 关键字
     * @return 默认""
     */
    public static String getString(SharedPreferences sp, String key) {
        return getString(sp, key, "");
    }

    /**
     * @param key          关键字
     * @param defaultValue 默认值
     */
    public static String getString(SharedPreferences sp, String key, String defaultValue) {
        if (check(sp)) {
            return sp.getString(key, defaultValue);
        }
        return defaultValue;
    }

    /**
     * @param key   关键字
     * @param value 存储值
     */
    public static void putFloat(SharedPreferences sp, String key, float value) {
        if (check(sp)) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putFloat(key, value);
            editor.apply();
        }
    }

    /**
     * @param key 关键字
     * @return 默认返回0
     */
    public static float getFloat(SharedPreferences sp, String key) {
        if (check(sp)) {
            return sp.getFloat(key, 0);
        }
        return 0;
    }

    /**
     * @param key   关键字
     * @param value 存储值
     */
    public static void putLong(SharedPreferences sp, String key, long value) {
        if (check(sp)) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putLong(key, value);
            editor.apply();
        }
    }

    /**
     * @param key 关键字
     * @return 默认返回0
     */
    public static long getLong(SharedPreferences sp, String key) {
        if (check(sp)) {
            return sp.getLong(key, 0);
        }
        return 0;
    }

    /**
     * 设置SharePreference文件中的字段的值
     *
     * @param key   关键字
     * @param value 值
     */
    public static boolean setValue(SharedPreferences sp, String key, Object value) {
        if (key != null && value != null) {
            if (value instanceof String) {
                putString(sp, key, (String) value);
            } else if (value instanceof Integer || value.getClass() == int.class) {
                putInt(sp, key, value instanceof Integer ? (Integer) value : (int) value);
            } else if (value instanceof Long || value.getClass() == long.class) {
                putLong(sp, key, value instanceof Long ? (Long) value : (long) value);
            } else if (value instanceof Float || value.getClass() == float.class) {
                putFloat(sp, key, value instanceof Float ? (Float) value : (float) value);
            } else if (value instanceof Boolean || value.getClass() == boolean.class) {
                putBoolean(sp, key, value instanceof Boolean ? (Boolean) value : (boolean) value);
            } else {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 获得SharePreference的值
     *
     * @param key          关键字
     * @param defaultValue 默认值
     * @return 获得对应key的值
     */
    @SuppressWarnings("unchecked")
    public static <T> T getValue(SharedPreferences sp, String key, T defaultValue) {
        if (key != null && check(sp)) {
            if (defaultValue instanceof String) {
                return (T) getString(sp, key, (String) defaultValue);
            } else if (defaultValue instanceof Integer || defaultValue.getClass() == int.class) {
                return (T) Integer.valueOf(getInt(sp, key));
            } else if (defaultValue instanceof Long || defaultValue.getClass() == long.class) {
                return (T) Long.valueOf(getLong(sp, key));
            } else if (defaultValue instanceof Float || defaultValue.getClass() == float.class) {
                return (T) Float.valueOf(getFloat(sp, key));
            } else if (defaultValue instanceof Boolean || defaultValue.getClass() == boolean.class) {
                return (T) Boolean.valueOf(getBoolean(sp, key));
            } else {
                return null;
            }
        }
        return null;
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param key 关键字
     */
    public static void remove(SharedPreferences sp, String key) {
        if (check(sp)) {
            SharedPreferences.Editor editor = sp.edit();
            editor.remove(key);
            editor.apply();
        }
    }

    /**
     * 清除所有数据
     */
    public static void clear(SharedPreferences sp) {
        if (check(sp)) {
            sp.edit().clear().apply();
        }
    }
}
