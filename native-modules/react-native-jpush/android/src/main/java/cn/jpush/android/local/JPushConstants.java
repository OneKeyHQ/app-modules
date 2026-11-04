package cn.jpush.android.local;

import android.content.Context;
import cn.jiguang.core.JCoreStorage;

public class JPushConstants {
    public static final String SDK_TYPE = "JPUSH";
    public static final String SDK_NAME = "JPush";
    public static final String SDK_VERSION_NAME = "5.9.0";
    public static final int SDK_VERSION_CODE = 590;
    public static final String DISPATCH_ACTION_CLASS = "cn.jpush.android.local.JPushDispatchAction";
    public static final String ENCODING_UTF_8 = "UTF-8";
    public static boolean isForeGround = true;
    public static Context mApplicationContext;

    public static void init(Context context) {
        if (context != null) {
            try {
                Context app = context.getApplicationContext();
                mApplicationContext = app != null ? app : context;
            } catch (Throwable ignored) {
                mApplicationContext = context;
            }
            JCoreStorage.setAppContext(mApplicationContext);
        }
    }

    public static Context getAppContext(Context context) {
        if (mApplicationContext == null && context != null) {
            init(context);
        }
        return mApplicationContext != null ? mApplicationContext : JCoreStorage.getAppContext(context);
    }
}
