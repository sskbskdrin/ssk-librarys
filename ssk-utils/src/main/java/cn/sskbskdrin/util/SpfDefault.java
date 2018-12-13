package cn.sskbskdrin.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ex-keayuan001 on 2018/8/13.
 *
 * @author ex-keayuan001
 */
public class SpfDefault {

    private static SharedPreferences sp;

    public static void init(Context context) {
        sp = SpfUtil.get(context);
    }

    public static void init(Context context, String name) {
        sp = SpfUtil.get(context, name);
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param key 关键字
     */
    public static boolean contains(String key) {
        return SpfUtil.contains(sp, key);
    }

    /**
     * @param key   关键字
     * @param value 存储值
     */
    public static void putInt(String key, int value) {
        SpfUtil.putInt(sp, key, value);
    }

    /**
     * @param key          关键字
     * @param defaultValue 默认值
     */
    public static int getInt(String key, int defaultValue) {
        return SpfUtil.getInt(sp, key, defaultValue);
    }

    /**
     * @param key 关键字
     * @return 默认返回0
     */
    public static int getInt(String key) {
        return SpfUtil.getInt(sp, key);
    }

    /**
     * @param key   关键字
     * @param value 存储值
     */
    public static void putBoolean(String key, boolean value) {
        SpfUtil.putBoolean(sp, key, value);
    }

    /**
     * @param key 关键字
     * @return 默认false
     */
    public static boolean getBoolean(String key) {
        return SpfUtil.getBoolean(sp, key);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return SpfUtil.getBoolean(sp, key, defaultValue);
    }

    /**
     * @param key   关键字
     * @param value 存储值
     */
    public static void putString(String key, String value) {
        SpfUtil.putString(sp, key, value);
    }

    /**
     * @param key 关键字
     * @return 默认""
     */
    public static String getString(String key) {
        return SpfUtil.getString(sp, key);
    }

    /**
     * @param key          关键字
     * @param defaultValue 默认值
     */
    public static String getString(String key, String defaultValue) {
        return SpfUtil.getString(sp, key, defaultValue);
    }

    /**
     * @param key   关键字
     * @param value 存储值
     */
    public static void putLong(String key, long value) {
        SpfUtil.putLong(sp, key, value);
    }

    /**
     * @param key 关键字
     * @return 默认返回0
     */
    public static long getLong(String key) {
        return SpfUtil.getLong(sp, key);
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param key 关键字
     */
    public static void remove(String key) {
        SpfUtil.remove(sp, key);
    }

    /**
     * 清除所有数据
     */
    public static void clear(SharedPreferences sp) {
        SpfUtil.clear(sp);
    }
}
