package cn.jpush.android.local;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cn.jpush.android.api.CallBackParams;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;
import java.nio.ByteBuffer;

public abstract class JPushAction {
    public abstract Object beforLogin(Context context, String sdkType, int cmd, String key);

    public abstract Object beforRegister(Context context, String sdkType, int cmd, String key);

    public abstract boolean checkActionInfo(String sdkType);

    public abstract void dispatchMessage(
        Context context,
        String sdkType,
        int command,
        int version,
        long rid,
        long uid,
        ByteBuffer body
    );

    public abstract void dispatchTimeOutMessage(Context context, String sdkType, long rid, int command);

    public abstract Object getDataByCmd(Context context, int cmd);

    public abstract ProxyActivityAction getPopWinActivity(Context context);

    public abstract ProxyActivityAction getPushActivity(Context context);

    public abstract String getSdkVersion(String sdkType);

    public abstract void handleAction(Context context, String action, Object data);

    public abstract void handleNotificationIntent(Context context, Intent intent);

    public abstract boolean isSupportedCMD(String sdkType, int command);

    public abstract void onActivityLifeCallback(Activity activity, String lifecycleEvent);

    public abstract void onInAppMessageClick(Context context, NotificationMessage message);

    public abstract void onInAppMessageShow(Context context, NotificationMessage message);

    public abstract void onJPushMessageReceive(Context context, JPushMessageReceiver receiver, Intent intent);

    public abstract void onJPushMessageReceive(Context context, String receiverClassName, String messageType, Bundle bundle);

    public abstract void onMessage(Context context, CustomMessage message);

    public abstract void onMultiAction(Context context, Intent intent);

    public abstract void onNotifyMessageArrived(Context context, NotificationMessage message);

    public abstract void onNotifyMessageOpened(Context context, NotificationMessage message);

    public abstract void onTagAliasResponse(Context context, long rid, int statusCode, Intent intent);

    public abstract void setAliasAndTags(Context context, String alias, java.util.Set<String> tags, CallBackParams params);
}
