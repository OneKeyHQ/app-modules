package cn.jpush.android.api;

import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import cn.jiguang.core.JCoreCrypto;
import cn.jpush.android.helper.JCoreHelper;
import cn.jpush.android.local.ActionHelper;
import org.json.JSONObject;

public abstract class JThirdPlatFormInterface {
    public static final String ACTION_NOTIFICATION_SHOW = "action_notification_show";
    public static final String ACTION_NOTIFICATION_ARRIVED = "action_notification_arrived";
    public static final String ACTION_NOTIFICATION_UN_SHOW = "action_notification_unshow";
    public static final String ACTION_NOTIFICATION_CLCKED = "action_notification_clicked";
    public static final String ACTION_NOTIFICATION_CUSTOM_BUTTON_CLCKED = "action_notification_custom_button_clcked";
    public static final String ACTION_REGISTER_TOKEN = "action_register_token";
    public static final String ACTION_SEND_EXCEPTION = "action_send_exception";
    public static final String KEY_DATA = "data";
    public static final String KEY_MSG_ID = "msg_id";
    public static final String KEY_NOTI_ID = "noti_id";
    public static final String KEY_PLATFORM = "platform";
    public static final String KEY_CODE = "code";
    public static final String KEY_MSG = "msg";
    public static final String KEY_ROM_TYPE = "rom_type";
    public static final String EXCEPTION_TYPE = "exception_type";
    public static final String KEY_EXTRA = "extra";
    public static final String KEY_VENDOR_SDK_VER = "vendor_sdk_ver";
    public static final String KEY_PLUGIN_SDK_VER = "plugin_sdk_ver";
    public static final String KEY_VENDOR_PUSH_APP_KEY_MISS = "j10000";
    public static final String KEY_VENDOR_PUSH_INIT_UNKNOWN_EXCEPTION = "j10001";
    public static final String KEY_VENDOR_PUSH_STATUS_EXCEPTION = "j10002";
    public static final String KEY_VENDOR_PUSH_MANUFACTURE_UNKNOWN = "j10003";
    public static final int CMD_CALL_BACK = 10000;
    public static final String KEY_TOKEN = "token";
    public static final String ACTION_PLUGIN_PALTFORM_REQUEST_REGID = "intent.plugin.platform.REQUEST_REGID";
    public static final String ACTION_PLUGIN_PALTFORM_REFRESSH_REGID = "intent.plugin.platform.REFRESSH_REGID";
    public static final String ACTION_PLUGIN_PALTFORM_ON_MESSAGING = "intent.plugin.platform.ON_MESSAGING";

    public abstract void init(Context context);

    public abstract String getToken(Context context);

    public abstract boolean isSupport(Context context);

    public abstract byte getRomType(Context context);

    public abstract void register(Context context, Bundle bundle);

    public void stopPush(Context context) {
    }

    public void resumePush(Context context) {
    }

    public abstract String getRomName();

    public abstract String getAppkey(Context context);

    public abstract String getAppId(Context context);

    public abstract boolean isNeedClearToken(Context context);

    public abstract void clearNotification(Context context, int notificationId);

    public abstract void clearAllNotification(Context context);

    public boolean needSendToMainProcess() {
        return false;
    }

    public static void onEvent(Thread thread, Throwable error) {
        ActionHelper.getInstance().onEvent(thread, error);
    }

    public static void doAction(Context context, String action, Bundle bundle) {
        Bundle target = bundle != null ? bundle : new Bundle();
        target.putString("third_key_action", action);
        ActionHelper.getInstance().doAction(context, "third_action", target);
    }

    public static int getNofiticationID(String msgId, int defaultId) {
        return msgId != null ? Math.abs(msgId.hashCode()) : defaultId;
    }

    public static void sendActionByJCore(Context context, Bundle bundle, String action) {
        JCoreHelper.runActionWithService(context, "JPUSH", action, bundle);
    }

    public static String toHash(String input) {
        return JCoreCrypto.md5Hex(input);
    }

    public static void setNotificationChannel(
        Context context,
        Notification.Builder builder,
        String channelId,
        CharSequence channelName,
        int priority,
        int defaults,
        int importance
    ) {
    }

    public static void setNotificationChannel(
        Context context,
        Notification.Builder builder,
        String channelId,
        CharSequence channelName,
        int priority,
        int defaults,
        String sound,
        int importance
    ) {
    }

    public static void setNotification(
        Context context,
        Notification notification,
        String channelId,
        CharSequence channelName,
        int priority,
        int defaults,
        String sound,
        int importance
    ) {
    }

    public static void reportThirdSDKMsgActionResult(String msgId, String extra, byte platform, long code, Context context) {
    }

    public static NotificationMessage parseMessage(Context context, String rawJson) {
        NotificationMessage message = new NotificationMessage();
        message.context = context;
        if (rawJson != null) {
            try {
                JSONObject json = new JSONObject(rawJson);
                message.msgId = json.optString("msg_id", "");
                message.notificationTitle = json.optString("n_title", "");
                message.notificationContent = json.optString("n_content", "");
                message.notificationExtras = json.optString("n_extras", "");
            } catch (Throwable ignored) {
            }
        }
        return message;
    }
}
