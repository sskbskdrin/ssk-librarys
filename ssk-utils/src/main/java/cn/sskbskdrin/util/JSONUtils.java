package cn.sskbskdrin.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ex-keayuan001 on 2018/9/3.
 *
 * @author ex-keayuan001
 */
public class JSONUtils {

    public static JSONObject generateJSONObject(String string) {
        JSONObject result = null;
        if (!StringUtils.isEmpty(string)) {
            try {
                result = new JSONObject(string);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (result == null) {
            result = new JSONObject();
        }
        return result;
    }

    public static JSONArray generateJSONArray(String string) {
        JSONArray result = null;
        if (!StringUtils.isEmpty(string)) {
            try {
                result = new JSONArray(string);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (result == null) {
            result = new JSONArray();
        }
        return result;
    }

    public static JSONObject getJSONObject(JSONArray array, int index) {
        JSONObject result = null;
        if (array != null && array.length() > index) {
            try {
                result = array.getJSONObject(index);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static JSONObject getJSONObject(JSONObject object, String key) {
        JSONObject result = null;
        if (check(object, key)) {
            result = object.optJSONObject(key);
        }
        return result;
    }

    public static JSONArray getJSONArray(JSONObject object, String key) {
        JSONArray result = null;
        if (check(object, key)) {
            result = object.optJSONArray(key);
        }
        return result;
    }

    public static String getString(JSONObject object, String key) {
        String result = null;
        if (check(object, key)) {
            result = object.optString(key);
        }
        return result;
    }

    public static long getLong(JSONObject object, String key) {
        long result = 0;
        if (check(object, key)) {
            result = object.optLong(key);
        }
        return result;
    }

    public static int getInt(JSONObject object, String key) {
        int result = 0;
        if (check(object, key)) {
            result = object.optInt(key);
        }
        return result;
    }

    public static boolean getBoolean(JSONObject object, String key) {
        boolean result = false;
        if (check(object, key)) {
            result = object.optBoolean(key);
        }
        return result;
    }

    private static boolean check(JSONObject object, String key) {
        return object != null && key != null && !object.isNull(key);
    }
}
