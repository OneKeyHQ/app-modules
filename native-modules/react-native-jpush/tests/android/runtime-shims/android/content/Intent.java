package android.content;
import android.net.Uri;
import android.os.Bundle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Intent {
    public static final int FLAG_ACTIVITY_NEW_TASK = 0x10000000;
    public static final int FLAG_ACTIVITY_SINGLE_TOP = 0x20000000;
    public static final int FLAG_ACTIVITY_CLEAR_TOP = 0x04000000;

    private String action;
    private String pkg;
    private final Set<String> categories = new HashSet<String>();
    private final Bundle extras = new Bundle();
    private int flags;
    private Uri data;

    public Intent() {}

    public Intent(String action) {
        this.action = action;
    }

    public Intent(Context packageContext, Class<?> cls) {}

    public ComponentName getComponent() {
        return null;
    }

    public String getAction() {
        return action;
    }

    public Intent setAction(String action) {
        this.action = action;
        return this;
    }

    public Intent setPackage(String pkg) {
        this.pkg = pkg;
        return this;
    }

    public String getPackage() {
        return pkg;
    }

    public Intent addCategory(String category) {
        if (category != null) {
            categories.add(category);
        }
        return this;
    }

    public Intent setFlags(int flags) {
        this.flags = flags;
        return this;
    }

    public Intent addFlags(int flags) {
        this.flags |= flags;
        return this;
    }

    public Intent setData(Uri data) {
        this.data = data;
        return this;
    }

    public Bundle getExtras() {
        return extras;
    }

    public boolean hasExtra(String name) {
        return extras.containsKey(name);
    }

    public Intent putExtra(String name, String value) {
        extras.putString(name, value);
        return this;
    }

    public Intent putExtra(String name, long value) {
        extras.putLong(name, value);
        return this;
    }

    public Intent putExtra(String name, int value) {
        extras.putInt(name, value);
        return this;
    }

    public Intent putExtra(String name, boolean value) {
        extras.putBoolean(name, value);
        return this;
    }

    public Intent putExtra(String name, Serializable value) {
        extras.putSerializable(name, value);
        return this;
    }

    public Intent putStringArrayListExtra(String name, ArrayList<String> value) {
        extras.putSerializable(name, value);
        return this;
    }

    public String getStringExtra(String name) {
        return extras.getString(name);
    }

    public long getLongExtra(String name, long defaultValue) {
        return extras.getLong(name, defaultValue);
    }

    public int getIntExtra(String name, int defaultValue) {
        return extras.getInt(name, defaultValue);
    }

    public boolean getBooleanExtra(String name, boolean defaultValue) {
        return extras.getBoolean(name, defaultValue);
    }

    public Serializable getSerializableExtra(String name) {
        return extras.getSerializable(name);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<String> getStringArrayListExtra(String name) {
        Serializable s = extras.getSerializable(name);
        return s instanceof ArrayList ? (ArrayList<String>) s : null;
    }
}
