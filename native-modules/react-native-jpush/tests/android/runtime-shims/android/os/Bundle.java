package android.os;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class Bundle implements Serializable {
    private final Map<String, Object> map = new HashMap<String, Object>();

    public Bundle() {}

    public Bundle(Bundle other) {
        if (other != null) {
            map.putAll(other.map);
        }
    }

    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    public Object get(String key) {
        return map.get(key);
    }

    public void putString(String key, String value) {
        map.put(key, value);
    }

    public String getString(String key) {
        Object v = map.get(key);
        return v instanceof String ? (String) v : null;
    }

    public String getString(String key, String defaultValue) {
        String v = getString(key);
        return v != null ? v : defaultValue;
    }

    public void putInt(String key, int value) {
        map.put(key, value);
    }

    public void putByte(String key, byte value) {
        map.put(key, value);
    }

    public Byte getByte(String key, byte defaultValue) {
        Object value = map.get(key);
        return value instanceof Byte ? ((Byte) value).byteValue() : defaultValue;
    }

    public int getInt(String key, int defaultValue) {
        Object v = map.get(key);
        return v instanceof Number ? ((Number) v).intValue() : defaultValue;
    }

    public void putLong(String key, long value) {
        map.put(key, value);
    }

    public long getLong(String key, long defaultValue) {
        Object v = map.get(key);
        return v instanceof Number ? ((Number) v).longValue() : defaultValue;
    }

    public void putBoolean(String key, boolean value) {
        map.put(key, value);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        Object v = map.get(key);
        return v instanceof Boolean ? (Boolean) v : defaultValue;
    }

    public void putSerializable(String key, Serializable value) {
        map.put(key, value);
    }

    public Serializable getSerializable(String key) {
        Object v = map.get(key);
        return v instanceof Serializable ? (Serializable) v : null;
    }

    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public String toString() {
        return "Bundle" + map.toString();
    }
}
