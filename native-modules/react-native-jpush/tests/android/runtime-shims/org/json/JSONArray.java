package org.json;
import java.util.ArrayList;
import java.util.List;

public class JSONArray {
    private final List<Object> values = new ArrayList<Object>();

    public JSONArray() {}

    public int length() {
        return values.size();
    }

    public JSONArray put(Object value) {
        values.add(value);
        return this;
    }

    public String optString(int index, String fallback) {
        if (index < 0 || index >= values.size()) {
            return fallback;
        }
        Object v = values.get(index);
        return v != null ? String.valueOf(v) : fallback;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            Object val = values.get(i);
            if (val instanceof Number || val instanceof Boolean || val instanceof JSONObject || val instanceof JSONArray) {
                sb.append(val);
            } else {
                sb.append('"').append(val != null ? String.valueOf(val) : "").append('"');
            }
        }
        sb.append(']');
        return sb.toString();
    }
}
