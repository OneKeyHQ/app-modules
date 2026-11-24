package cn.jpush.android.local;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import cn.jiguang.core.JCoreTcpEngine;

public class ActivityLifeCallBack implements Application.ActivityLifecycleCallbacks {
    private static boolean registered;
    private int activityTaskCount;

    public static synchronized void register(Application application) {
        if (!registered) {
            application.registerActivityLifecycleCallbacks(new ActivityLifeCallBack());
            registered = true;
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (activityTaskCount == 0) {
            JPushConstants.isForeGround = true;
            JCoreTcpEngine.getInstance().onAppForeground(
                activity != null ? activity.getApplicationContext() : null
            );
        }
        activityTaskCount++;
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (activityTaskCount > 0) {
            activityTaskCount--;
        }
        if (activityTaskCount == 0) {
            JPushConstants.isForeGround = false;
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }
}
