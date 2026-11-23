package android.content;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;

public class Context {
    public static final int MODE_PRIVATE = 0;
    public static final String NOTIFICATION_SERVICE = "notification";

    public Context getApplicationContext() {
        return this;
    }

    public String getPackageName() {
        return "com.devstudio.msgbench";
    }

    public SharedPreferences getSharedPreferences(String name, int mode) {
        return null;
    }

    public ContentResolver getContentResolver() {
        return null;
    }

    public PackageManager getPackageManager() {
        return null;
    }

    public ApplicationInfo getApplicationInfo() {
        return new ApplicationInfo();
    }

    public Resources getResources() {
        return null;
    }

    public Object getSystemService(String name) {
        return null;
    }

    public void sendBroadcast(Intent intent) {}

    public void startActivity(Intent intent) {}
}
