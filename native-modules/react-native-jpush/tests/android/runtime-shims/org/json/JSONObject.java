package org.json;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class JSONObject {
    public static final Object NULL = new Object() {
        @Override
        public boolean equals(Object o) {
            return o == this || o == null;
        }

        @Override
        public String toString() {
            return "null";
        }
    };
    private final Map<String, Object> map = new LinkedHashMap<String, Object>();

    public JSONObject() {}

    public JSONObject(Map<?, ?> source) {
        if (source != null) {
            for (Map.Entry<?, ?> entry : source.entrySet()) {
                if (entry.getKey() != null) {
                    map.put(String.valueOf(entry.getKey()), entry.getValue());
                }
            }
        }
    }

    public JSONObject(String raw) throws JSONException {
        if (raw == null) {
            throw new JSONException("Null JSON");
        }
        String trimmed = raw.trim();
        if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) {
            throw new JSONException("Invalid JSON object");
        }
        String body = trimmed.substring(1, trimmed.length() - 1).trim();
        if (body.isEmpty()) {
            return;
        }
        int pos = 0;
        while (pos < body.length()) {
            int k1 = body.indexOf('"', pos);
            if (k1 < 0) break;
            int k2 = body.indexOf('"', k1 + 1);
            if (k2 < 0) break;
            String key = body.substring(k1 + 1, k2);
            int colon = body.indexOf(':', k2 + 1);
            if (colon < 0) break;
            int valStart = colon + 1;
            while (valStart < body.length() && Character.isWhitespace(body.charAt(valStart))) {
                valStart++;
            }
            if (valStart >= body.length()) break;
            char ch = body.charAt(valStart);
            if (ch == '"') {
                int v2 = body.indexOf('"', valStart + 1);
                if (v2 < 0) break;
                map.put(key, body.substring(valStart + 1, v2));
                pos = v2 + 1;
            } else if (ch == '{') {
                int depth = 0;
                int endObj = valStart;
                while (endObj < body.length()) {
                    char c = body.charAt(endObj);
                    if (c == '{') depth++;
                    else if (c == '}') {
                        depth--;
                        if (depth == 0) {
                            endObj++;
                            break;
                        }
                    }
                    endObj++;
                }
                map.put(key, new JSONObject(body.substring(valStart, endObj)));
                pos = endObj;
            } else {
                int comma = body.indexOf(',', valStart);
                String token = (comma >= 0 ? body.substring(valStart, comma) : body.substring(valStart)).trim();
                try {
                    map.put(key, Integer.parseInt(token));
                } catch (NumberFormatException e) {
                    map.put(key, token);
                }
                pos = comma >= 0 ? comma + 1 : body.length();
            }
            int nextComma = body.indexOf(',', pos);
            if (nextComma >= 0 && nextComma < body.indexOf('"', pos)) {
                pos = nextComma + 1;
            }
        }
    }

    public JSONObject put(String key, Object value) throws JSONException {
        if (key != null) {
            map.put(key, value);
        }
        return this;
    }

    public Object opt(String key) {
        return map.get(key);
    }

    public boolean has(String key) {
        return map.containsKey(key);
    }

    public int optInt(String key, int fallback) {
        Object v = map.get(key);
        return v instanceof Number ? ((Number) v).intValue() : fallback;
    }

    public String optString(String key, String fallback) {
        Object v = map.get(key);
        return v != null ? String.valueOf(v) : fallback;
    }

    public String getString(String key) throws JSONException {
        Object v = map.get(key);
        if (v == null) {
            throw new JSONException("Missing key: " + key);
        }
        return String.valueOf(v);
    }

    public JSONObject optJSONObject(String key) {
        Object v = map.get(key);
        return v instanceof JSONObject ? (JSONObject) v : null;
    }

    public JSONArray optJSONArray(String key) {
        Object v = map.get(key);
        return v instanceof JSONArray ? (JSONArray) v : null;
    }

    public Iterator<String> keys() {
        return map.keySet().iterator();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                sb.append(',');
            }
            first = false;
            sb.append('"').append(entry.getKey()).append('"').append(':');
            Object val = entry.getValue();
            if (val instanceof Number || val instanceof Boolean || val instanceof JSONObject || val instanceof JSONArray) {
                sb.append(val);
            } else {
                sb.append('"').append(val != null ? String.valueOf(val) : "").append('"');
            }
        }
        sb.append('}');
        return sb.toString();
    }
}
