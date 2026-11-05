package cn.jpush.android.local;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public class ActivityLifeCallBack implements Application.ActivityLifecycleCallbacks {
    private static int activityTaskCount = 0;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (activityTaskCount == 0) {
            JPushConstants.isForeGround = true;
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
