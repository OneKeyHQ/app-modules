package cn.jpush.android.api;

import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import cn.jiguang.core.JCoreCrypto;
import cn.jiguang.core.JCoreStorage;
import cn.jiguang.core.JCoreTcpEngine;
import cn.jiguang.plugins.push.common.JLogger;
import cn.jpush.android.helper.JCoreHelper;
import cn.jpush.android.local.ActionHelper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import org.json.JSONObject;

public abstract class JThirdPlatFormInterface {
    private static JThirdPlatFormInterface fcmPlugin;
    private static boolean fcmRegistered;
    private static final int MAX_MESSAGE_BYTES = 65536;
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

    public static void initializeFcm(final Context context) {
        if (context == null || JCoreStorage.isPushStopped(context) || !JCoreStorage.isCollectionAuth(context)) {
            return;
        }
        JCoreTcpEngine.getInstance().executeAsync(new Runnable() {
            @Override
            public void run() {
                synchronized (JThirdPlatFormInterface.class) {
                    if (JCoreStorage.isPushStopped(context) || !JCoreStorage.isCollectionAuth(context)) {
                        return;
                    }
                    try {
                        if (fcmPlugin == null) {
                            // This optional source module is linked only by Google hosts.
                            fcmPlugin = (JThirdPlatFormInterface) Class.forName(
                                "com.onekey.jpush.fcm.FcmPushManager"
                            ).getDeclaredConstructor().newInstance();
                        }
                        if (!fcmPlugin.isSupport(context)) {
                            return;
                        }
                        if (!fcmRegistered) {
                            fcmPlugin.init(context);
                            fcmPlugin.register(context, new Bundle());
                            fcmRegistered = true;
                        }
                        fcmPlugin.getToken(context);
                        JLogger.i("FCM registration requested");
                    } catch (ClassNotFoundException absentPlugin) {
                        // Non-Google hosts intentionally do not include the optional plugin.
                    } catch (Exception | LinkageError unavailablePlugin) {
                        JLogger.w("FCM registration unavailable");
                    }
                }
            }
        });
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
        if (ACTION_PLUGIN_PALTFORM_ON_MESSAGING.equals(action)) {
            ActionHelper.getInstance().doAction(context, action, bundle);
            return;
        }
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
        message.platform = 8;
        message.displayForeground = "0";
        String decoded = decodeMessageJson(rawJson);
        if (decoded != null) {
            try {
                JSONObject json = new JSONObject(decoded);
                message.msgId = json.optString("_jmsgid_", json.optString("_j_msgid", ""));
                if (message.msgId.isEmpty()) {
                    message.msgId = json.optString("msg_id", "");
                }
                String overrideId = json.optString("override_msg_id", "");
                if (!overrideId.isEmpty()) {
                    message.msgId = overrideId;
                }
                message.notificationId = getNofiticationID(message.msgId, 0);
                message.notificationTitle = json.optString("n_title", json.optString("title", ""));
                message.notificationContent = json.optString("n_content", json.optString("alert", json.optString("content", "")));
                Object extras = json.opt("n_extras");
                if (extras == null) {
                    extras = json.opt("extras");
                }
                message.notificationExtras = extras != null ? extras.toString() : "";
                message.notificationChannelId = json.optString("n_channel_id", "");
                if (!JCoreStorage.isPushStopped(context) && JCoreStorage.isCollectionAuth(context)) {
                    message.displayForeground = json.optString("n_foreground", "1");
                }
            } catch (Exception invalidMessage) {
                JLogger.w("FCM payload rejected");
            }
        }
        return message;
    }

    public static String decodeMessageJson(String rawJson) {
        if (rawJson == null || rawJson.length() > MAX_MESSAGE_BYTES
            || rawJson.getBytes(StandardCharsets.UTF_8).length > MAX_MESSAGE_BYTES) {
            return null;
        }
        try {
            JSONObject json = new JSONObject(rawJson);
            if (!json.has("jg_c")) {
                return rawJson;
            }
            byte[] compressed = JCoreCrypto.base64Decode(json.getString("jg_c"));
            try (GZIPInputStream input = new GZIPInputStream(new ByteArrayInputStream(compressed));
                 ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[2048];
                int count;
                while ((count = input.read(buffer)) != -1) {
                    if (output.size() + count > MAX_MESSAGE_BYTES) {
                        return null;
                    }
                    output.write(buffer, 0, count);
                }
                return new String(output.toByteArray(), StandardCharsets.UTF_8);
            }
        } catch (Exception invalidMessage) {
            return null;
        }
    }
}
