package cn.jiguang.api.utils;

import android.content.Context;
import cn.jiguang.core.JCoreStorage;
import cn.jpush.android.api.JThirdPlatFormInterface;

public class JCollectionAuth {
    public static synchronized void setAuth(Context context, boolean auth) {
        boolean wasAuthorized = JCoreStorage.isCollectionAuth(context);
        JCoreStorage.setCollectionAuth(context, auth);
        if (auth && !wasAuthorized) {
            JThirdPlatFormInterface.initializeFcm(context);
        }
    }

    public static boolean isAuth(Context context) {
        return JCoreStorage.isCollectionAuth(context);
    }

    public static void enableAutoWakeup(Context context, boolean enable) {
    }

    public static void enableDynamicLoad(Context context, boolean enable) {
    }

    public static void enableAppTerminate(Context context, boolean enable) {
    }
}
