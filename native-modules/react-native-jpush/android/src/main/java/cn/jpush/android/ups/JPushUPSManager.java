package cn.jpush.android.ups;

import android.content.Context;
import cn.jiguang.api.JCoreManager;
import cn.jiguang.core.JCoreStorage;
import cn.jpush.android.api.JPushInterface;

public class JPushUPSManager {
    public static void registerToken(Context context, String appId, String appKey, String appSecret, UPSRegisterCallBack callback) {
        JCoreManager.register(context, appId);
        if (callback != null) {
            callback.onResult(new TokenResult(JCoreStorage.getRegistrationId(context), 0, "ups.register"));
        }
    }

    public static void unRegisterToken(Context context, UPSUnRegisterCallBack callback) {
        JCoreManager.unRegister(context);
        if (callback != null) {
            callback.onResult(new TokenResult("", 0, "ups.unregister"));
        }
    }

    public static void turnOnPush(Context context, UPSTurnCallBack callback) {
        JPushInterface.resumePush(context);
        if (callback != null) {
            callback.onResult(new TokenResult(JCoreStorage.getRegistrationId(context), 0, "ups.turnOn"));
        }
    }

    public static void turnOffPush(Context context, UPSTurnCallBack callback) {
        JPushInterface.stopPush(context);
        if (callback != null) {
            callback.onResult(new TokenResult("", 0, "ups.turnOff"));
        }
    }
}
