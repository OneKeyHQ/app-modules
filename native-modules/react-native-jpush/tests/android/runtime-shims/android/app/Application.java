package android.app;

import android.content.Context;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;

public class Application extends Context {
    public interface ActivityLifecycleCallbacks {
        void onActivityCreated(Activity activity, Bundle state);
        void onActivityStarted(Activity activity);
        void onActivityResumed(Activity activity);
        void onActivityPaused(Activity activity);
        void onActivityStopped(Activity activity);
        void onActivitySaveInstanceState(Activity activity, Bundle state);
        void onActivityDestroyed(Activity activity);
    }

    private final List<ActivityLifecycleCallbacks> callbacks = new ArrayList<>();

    public void registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
        callbacks.add(callback);
    }

    public int registeredActivityCallbacks() {
        return callbacks.size();
    }

    public void startActivityForTest(Activity activity) {
        for (ActivityLifecycleCallbacks callback : callbacks) callback.onActivityStarted(activity);
    }

    public void stopActivityForTest(Activity activity) {
        for (ActivityLifecycleCallbacks callback : callbacks) callback.onActivityStopped(activity);
    }
}
