package cn.sskbskdrin.log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by keayuan on 2021/4/16.
 *
 * @author keayuan
 */
class JSONUtils {

    private static String getJSON(JSONObject object) {
        try {
            if (object != null) {
                return object.toString(2);
            }
        } catch (JSONException ignored) {
        }
        return object == null ? Utils.NULL : object.toString();
    }

    private static String getJSON(JSONArray array) {
        try {
            if (array != null) {
                return array.toString(2);
            }
        } catch (JSONException ignored) {
        }
        return array == null ? Utils.NULL : array.toString();
    }

    static String json(String json, int indent) {
        try {
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                return "\n" + jsonObject.toString(indent);
            }
            if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                return "\n" + jsonArray.toString(indent);
            }
        } catch (JSONException ignored) {
        }
        return json;
    }

    static boolean objToString(StringBuilder builder, Object obj) {
        if (obj instanceof JSONObject) {
            builder.append('\n');
            builder.append(getJSON((JSONObject) obj));
            return true;
        } else if (obj instanceof JSONArray) {
            builder.append('\n');
            builder.append(getJSON((JSONArray) obj));
            return true;
        }
        return false;
    }
}
