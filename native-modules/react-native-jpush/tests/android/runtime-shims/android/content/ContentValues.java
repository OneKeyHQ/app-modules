package android.content;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class ContentValues {
    private final Map<String, Object> values = new HashMap<String, Object>();

    public ContentValues() {}

    public void put(String key, String value) {
        values.put(key, value);
    }

    public boolean containsKey(String key) {
        return values.containsKey(key);
    }

    public Object get(String key) {
        return values.get(key);
    }

    public String getAsString(String key) {
        Object v = values.get(key);
        return v != null ? String.valueOf(v) : null;
    }

    public Set<String> keySet() {
        return values.keySet();
    }
}
