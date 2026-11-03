package cn.jiguang.api.utils;

import android.content.Context;
import cn.jiguang.core.JCoreStorage;

public class JCollectionAuth {
    public static void setAuth(Context context, boolean auth) {
        JCoreStorage.setCollectionAuth(context, auth);
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
