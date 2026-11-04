package cn.jpush.android.local;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Bundle;
import cn.jiguang.core.JCoreStorage;
import cn.jiguang.core.JCoreTcpEngine;
import cn.jiguang.core.JCoreWireCodec;
import cn.jpush.android.api.CmdMessage;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JThirdPlatFormInterface;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationCustomButton;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.api.TagAliasCallback;
import cn.jpush.android.data.JPushLocalNotification;
import cn.jpush.android.service.JNotifyActivity;
import cn.jpush.android.service.JPushMessageReceiver;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONObject;

public class ActionHelper {
    public static final String ACTION_RECEIVER_MESSAGE = "cn.jpush.android.intent.RECEIVER_MESSAGE";
    public static final String ACTION_RECEIVE_MESSAGE = "cn.jpush.android.intent.RECEIVE_MESSAGE";
    public static final String DEFAULT_CHANNEL_ID = "jpush_default_channel";
    public static final String DEFAULT_SILENT_CHANNEL_ID = "jpush_silent_channel";

    private static final String KEY_EVENT_TYPE = "jpush_event_type";
    private static final String KEY_SEQUENCE = "jpush_sequence";
    private static final String KEY_ERROR_CODE = "jpush_error_code";
    private static final String KEY_ALIAS = "jpush_alias";
    private static final String KEY_TAGS = "jpush_tags";
    private static final String KEY_CHECK_TAG = "jpush_check_tag";
    private static final String KEY_TAG_BIND_STATE = "jpush_tag_bind_state";
    private static final String KEY_MOBILE_NUMBER = "jpush_mobile_number";
    private static final String KEY_PROPS_MAP = "jpush_props_map";
    private static final String KEY_NOTIFICATION_MESSAGE = "jpush_notification_message";
    private static final String KEY_CUSTOM_MESSAGE = "jpush_custom_message";
    private static final String KEY_CUSTOM_BUTTON = "jpush_custom_button";

    private static volatile ActionHelper instance;
    private final List<JPushMessageReceiver> registeredReceivers = new CopyOnWriteArrayList<JPushMessageReceiver>();
    private final LinkedList<Integer> activeNotificationIds = new LinkedList<Integer>();
    private final ConcurrentHashMap<Integer, ScheduledFuture<?>> pendingLocalNotifications =
        new ConcurrentHashMap<Integer, ScheduledFuture<?>>();
    private final ScheduledExecutorService localNotificationScheduler =
        Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "jpush-local-notify");
                t.setDaemon(true);
                return t;
            }
        });

    private ActionHelper() {
    }

    public static ActionHelper getInstance() {
        if (instance == null) {
            synchronized (ActionHelper.class) {
                if (instance == null) {
                    instance = new ActionHelper();
                }
            }
        }
        return instance;
    }

    public void registerReceiver(JPushMessageReceiver receiver) {
        if (receiver != null && !registeredReceivers.contains(receiver)) {
            registeredReceivers.add(receiver);
        }
    }

    public void unregisterReceiver(JPushMessageReceiver receiver) {
        if (receiver != null) {
            registeredReceivers.remove(receiver);
        }
    }

    public void clearRegisteredReceivers() {
        registeredReceivers.clear();
    }

    public void init(Context context) {
        JPushConstants.init(context);
    }

    public void doAction(Context context, String action, Object data) {
        if (!(data instanceof Bundle) || JCoreStorage.isPushStopped(context) || !JCoreStorage.isCollectionAuth(context)) {
            return;
        }
        Bundle bundle = (Bundle) data;
        if ("third_action".equals(action)) {
            action = bundle.getString("third_key_action", "");
        }
        if (JThirdPlatFormInterface.ACTION_REGISTER_TOKEN.equals(action)) {
            String token = bundle.getString("token", "");
            byte platform = bundle.getByte("platform", (byte) 8);
            if (platform == 8 && !token.isEmpty()) {
                setThirdTokenWithCallBack(context, token, platform, false);
            }
            return;
        }
        if (JThirdPlatFormInterface.ACTION_SEND_EXCEPTION.equals(action)) {
            cn.jiguang.plugins.push.common.JLogger.w("FCM plugin reported an error");
            return;
        }
        String raw = bundle.getString("data", bundle.getString("JMessageExtra", ""));
        String decoded = JThirdPlatFormInterface.decodeMessageJson(raw);
        if (decoded == null) {
            return;
        }
        NotificationMessage message = JThirdPlatFormInterface.parseMessage(context, decoded);
        if (message.msgId == null || message.msgId.isEmpty()) {
            return;
        }
        message.notificationId = bundle.getInt("noti_id", message.notificationId);
        if (JThirdPlatFormInterface.ACTION_PLUGIN_PALTFORM_ON_MESSAGING.equals(action)) {
            try {
                JSONObject json = new JSONObject(decoded);
                if (json.has("message") && message.notificationContent.isEmpty()) {
                    CustomMessage custom = new CustomMessage();
                    custom.messageId = message.msgId;
                    custom.message = json.optString("message", "");
                    custom.title = message.notificationTitle;
                    custom.extra = message.notificationExtras;
                    custom.contentType = json.optString("content_type", "");
                    custom.platform = 8;
                    dispatchCustomMessageEvent(context, custom);
                } else {
                    if ("0".equals(message.displayForeground) && isApplicationForeground(context)) {
                        return;
                    }
                    postSystemNotification(context, message);
                    dispatchNotificationEvent(context, "notify_arrived", message);
                }
            } catch (Exception invalidMessage) {
                cn.jiguang.plugins.push.common.JLogger.w("FCM payload rejected");
            }
        } else if (JThirdPlatFormInterface.ACTION_NOTIFICATION_ARRIVED.equals(action)) {
            recordNotificationPosted(context, message.notificationId);
            dispatchNotificationEvent(context, "notify_arrived", message);
        } else if (JThirdPlatFormInterface.ACTION_NOTIFICATION_CLCKED.equals(action)) {
            dispatchNotificationEvent(context, "notify_opened", message);
            broadcastNotificationOpened(context, message);
        }
    }

    private boolean isApplicationForeground(Context context) {
        KeyguardManager keyguard = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if (keyguard != null && keyguard.isKeyguardLocked()) {
            return false;
        }
        ActivityManager.RunningAppProcessInfo process = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(process);
        return process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
    }

    public void doSingleAction(Context context, String action, Object data) {
    }

    public void onEvent(Thread thread, Throwable error) {
    }

    public void onActivityLifeCallback(Activity activity, String lifecycleEvent) {
    }

    public ProxyActivityAction getPopWinActivity(Context context) {
        return null;
    }

    public ProxyActivityAction getPushActivity(Context context) {
        return null;
    }

    public void handleNotificationIntent(Context context, Intent intent) {
        if (context == null || intent == null) {
            return;
        }
        try {
            String action = intent.getAction();
            if (action != null && !action.isEmpty()
                && !"cn.jpush.android.intent.JNotifyActivity".equals(action)
                && !JPushInterface.ACTION_NOTIFICATION_OPENED.equals(action)) {
                return;
            }
            String targetPkg = intent.getPackage();
            String appPkg = context.getPackageName();
            if (targetPkg != null && !targetPkg.isEmpty() && appPkg != null && !appPkg.isEmpty()
                && !appPkg.equals(targetPkg)) {
                return;
            }
            Serializable msg = intent.getSerializableExtra("multi_notification_msg");
            boolean fcmClick = !(msg instanceof NotificationMessage) && intent.hasExtra("JMessageExtra");
            if (fcmClick) {
                if (JCoreStorage.isPushStopped(context) || !JCoreStorage.isCollectionAuth(context)) {
                    return;
                }
                NotificationMessage message = JThirdPlatFormInterface.parseMessage(context, intent.getStringExtra("JMessageExtra"));
                if (message.msgId == null || message.msgId.isEmpty()) {
                    return;
                }
                msg = message;
            }
            if (msg instanceof NotificationMessage) {
                NotificationMessage notificationMessage = (NotificationMessage) msg;
                dispatchNotificationEvent(context, "notify_opened", notificationMessage);
                broadcastNotificationOpened(context, notificationMessage);
                if (fcmClick || notificationMessage.platform == 8) {
                    android.content.pm.PackageManager manager = context.getPackageManager();
                    Intent launcher = manager != null ? manager.getLaunchIntentForPackage(appPkg) : null;
                    if (launcher != null) {
                        launcher.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        context.startActivity(launcher);
                    }
                }
            }
        } catch (Throwable ignored) {
        }
    }

    public void broadcastNotificationOpened(Context context, NotificationMessage message) {
        Context app = JCoreStorage.getAppContext(context);
        if (app == null || message == null) {
            return;
        }
        try {
            Intent openIntent = new Intent(JPushInterface.ACTION_NOTIFICATION_OPENED);
            String pkg = app.getPackageName();
            if (pkg != null && !pkg.isEmpty()) {
                openIntent.setPackage(pkg);
                openIntent.addCategory(pkg);
            }
            openIntent.putExtra(JPushInterface.EXTRA_MSG_ID, message.msgId != null ? message.msgId : "");
            openIntent.putExtra(
                JPushInterface.EXTRA_NOTIFICATION_TITLE,
                message.notificationTitle != null ? message.notificationTitle : ""
            );
            openIntent.putExtra(
                JPushInterface.EXTRA_ALERT,
                message.notificationContent != null ? message.notificationContent : ""
            );
            openIntent.putExtra(
                JPushInterface.EXTRA_EXTRA,
                message.notificationExtras != null ? message.notificationExtras : ""
            );
            openIntent.putExtra(JPushInterface.EXTRA_NOTIFICATION_ID, message.notificationId);
            openIntent.putExtra(JPushInterface.EXTRA_NOTI_TYPE, message.notificationType);
            openIntent.putExtra("jpush_already_dispatched", true);
            app.sendBroadcast(openIntent);
        } catch (Throwable ignored) {
        }
    }

    public synchronized List<Integer> getActiveNotificationIds() {
        return new ArrayList<Integer>(activeNotificationIds);
    }

    public synchronized void removeActiveNotificationId(int notificationId) {
        ScheduledFuture<?> pending = pendingLocalNotifications.remove(Integer.valueOf(notificationId));
        if (pending != null) {
            pending.cancel(false);
        }
        activeNotificationIds.remove(Integer.valueOf(notificationId));
    }

    public synchronized void clearActiveNotificationIds() {
        for (ScheduledFuture<?> pending : pendingLocalNotifications.values()) {
            if (pending != null) {
                pending.cancel(false);
            }
        }
        pendingLocalNotifications.clear();
        activeNotificationIds.clear();
    }

    public synchronized void recordNotificationPosted(Context context, int notifyId) {
        activeNotificationIds.remove(Integer.valueOf(notifyId));
        activeNotificationIds.addLast(Integer.valueOf(notifyId));
        enforceLatestNotificationLimit(context, JCoreStorage.getLatestNotificationNumber(context));
    }

    public synchronized void enforceLatestNotificationLimit(Context context, int maxNum) {
        if (maxNum <= 0) {
            return;
        }
        Context app = JCoreStorage.getAppContext(context);
        NotificationManager nm = null;
        if (app != null) {
            try {
                nm = (NotificationManager) app.getSystemService(Context.NOTIFICATION_SERVICE);
            } catch (Throwable ignored) {
            }
        }
        while (activeNotificationIds.size() > maxNum) {
            Integer evictedId = activeNotificationIds.removeFirst();
            if (evictedId != null && nm != null) {
                try {
                    nm.cancel(evictedId.intValue());
                } catch (Throwable ignored) {
                }
            }
        }
    }

    public void postSystemNotification(Context context, NotificationMessage message) {
        if (message == null) {
            return;
        }
        Context app = JCoreStorage.getAppContext(context);
        Calendar now = Calendar.getInstance();
        int dayOfWeekZeroBased = now.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
        int hourOfDay = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        if (!JCoreStorage.isPushTimeAllowed(app, dayOfWeekZeroBased, hourOfDay)) {
            return;
        }
        boolean silenceActive = JCoreStorage.isInSilenceTime(app, hourOfDay, minute);
        int notifyId = message.notificationId != 0
            ? message.notificationId
            : (message.msgId != null ? Math.abs(message.msgId.hashCode()) : 1);
        recordNotificationPosted(app, notifyId);
        if (app == null) {
            return;
        }
        try {
            NotificationManager nm = (NotificationManager) app.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm == null) {
                return;
            }
            String channelId = (message.notificationChannelId != null && !message.notificationChannelId.isEmpty())
                ? message.notificationChannelId
                : (silenceActive ? DEFAULT_SILENT_CHANNEL_ID : DEFAULT_CHANNEL_ID);
            if (Build.VERSION.SDK_INT >= 26) {
                String channelName = JCoreStorage.getChannel(app);
                if (channelName == null || channelName.isEmpty()) {
                    channelName = "Notifications";
                }
                int importance = silenceActive
                    ? NotificationManager.IMPORTANCE_LOW
                    : NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
                if (silenceActive) {
                    channel.setSound(null, null);
                    channel.enableVibration(false);
                }
                nm.createNotificationChannel(channel);
            }

            Notification.Builder builder;
            if (Build.VERSION.SDK_INT >= 26) {
                builder = new Notification.Builder(app, channelId);
            } else {
                builder = new Notification.Builder(app);
            }
            if (silenceActive) {
                builder.setDefaults(0).setSound(null).setVibrate(null);
            }

            int smallIcon = android.R.drawable.ic_dialog_info;
            try {
                ApplicationInfo appInfo = app.getApplicationInfo();
                if (appInfo != null && appInfo.icon != 0) {
                    smallIcon = appInfo.icon;
                }
            } catch (Throwable ignored) {
            }

            builder.setSmallIcon(smallIcon)
                .setContentTitle(message.notificationTitle != null ? message.notificationTitle : "")
                .setContentText(message.notificationContent != null ? message.notificationContent : "")
                .setAutoCancel(true);

            Intent clickIntent = new Intent(app, JNotifyActivity.class);
            clickIntent.setAction("cn.jpush.android.intent.JNotifyActivity");
            clickIntent.putExtra("multi_notification_msg", message);
            clickIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (Build.VERSION.SDK_INT >= 23) {
                flags |= PendingIntent.FLAG_IMMUTABLE;
            }
            PendingIntent pendingIntent = PendingIntent.getActivity(app, message.notificationId, clickIntent, flags);
            if (pendingIntent != null) {
                builder.setContentIntent(pendingIntent);
            }

            Notification notification = builder.build();
            nm.notify(notifyId, notification);
        } catch (Throwable ignored) {
        }
    }

    public void handleDownlinkPushMessage(Context context, byte messageType, long messageId, String rawContent) {
        String content = rawContent != null ? rawContent : "";
        int lastNewline = content.lastIndexOf('\n');
        if (lastNewline >= 0 && lastNewline + 1 < content.length()) {
            content = content.substring(lastNewline + 1).trim();
        }
        String msgId = String.valueOf(messageId);
        String parsedMsgId = JCoreWireCodec.extractJsonString(content, "_j_msgid");
        if (parsedMsgId.isEmpty()) {
            parsedMsgId = JCoreWireCodec.extractJsonString(content, "msg_id");
        }
        if (!parsedMsgId.isEmpty()) {
            msgId = parsedMsgId;
        }

        String nTitle = JCoreWireCodec.extractJsonString(content, "n_title");
        if (nTitle.isEmpty()) {
            nTitle = JCoreWireCodec.extractJsonString(content, "notificationTitle");
        }
        String nContent = JCoreWireCodec.extractJsonString(content, "n_content");
        if (nContent.isEmpty()) {
            nContent = JCoreWireCodec.extractJsonString(content, "notificationContent");
        }
        if (nContent.isEmpty()) {
            nContent = JCoreWireCodec.extractJsonString(content, "alert");
        }

        boolean isCustom = (messageType == 2) || (nTitle.isEmpty() && nContent.isEmpty() && content.contains("\"message\""));
        if (!isCustom && (!nTitle.isEmpty() || !nContent.isEmpty() || messageType == 0 || messageType == 1)) {
            NotificationMessage notification = new NotificationMessage();
            notification.msgId = msgId;
            notification.notificationId = JCoreWireCodec.extractJsonInt(
                content,
                "notification_id",
                (int) (messageId & 0x7FFFFFFFL)
            );
            notification.notificationTitle = !nTitle.isEmpty()
                ? nTitle
                : JCoreWireCodec.extractJsonString(content, "title");
            notification.notificationContent = !nContent.isEmpty()
                ? nContent
                : JCoreWireCodec.extractJsonString(content, "content");
            notification.notificationExtras = extractJsonObjectAsString(content, "n_extras", "extras");
            notification.notificationType = JCoreWireCodec.extractJsonInt(
                content,
                "notification_type",
                JCoreWireCodec.extractJsonInt(content, "notificaion_type", 0)
            );
            notification.platform = JCoreWireCodec.extractJsonInt(content, "platform", 0);
            postSystemNotification(context, notification);
            dispatchNotificationEvent(context, "notify_arrived", notification);
            return;
        }

        CustomMessage custom = new CustomMessage();
        custom.messageId = msgId;
        custom.title = JCoreWireCodec.extractJsonString(content, "title");
        String msgBody = JCoreWireCodec.extractJsonString(content, "message");
        if (msgBody.isEmpty()) {
            msgBody = JCoreWireCodec.extractJsonString(content, "content");
        }
        if (msgBody.isEmpty() && !content.startsWith("{")) {
            msgBody = content;
        }
        custom.message = msgBody;
        custom.contentType = JCoreWireCodec.extractJsonString(content, "content_type");
        custom.extra = extractJsonObjectAsString(content, "extras", "n_extras");
        custom.platform = (byte) JCoreWireCodec.extractJsonInt(content, "platform", 0);
        dispatchCustomMessageEvent(context, custom);
    }

    private static String extractJsonObjectAsString(String json, String primaryKey, String fallbackKey) {
        if (json == null || json.isEmpty()) {
            return "{}";
        }
        try {
            JSONObject obj = new JSONObject(json);
            JSONObject nested = obj.optJSONObject(primaryKey);
            if (nested == null && fallbackKey != null) {
                nested = obj.optJSONObject(fallbackKey);
            }
            if (nested != null) {
                return nested.toString();
            }
            JSONObject mContent = obj.optJSONObject("m_content");
            if (mContent != null) {
                nested = mContent.optJSONObject(primaryKey);
                if (nested == null && fallbackKey != null) {
                    nested = mContent.optJSONObject(fallbackKey);
                }
                if (nested != null) {
                    return nested.toString();
                }
            }
        } catch (Throwable ignored) {
        }
        Map<String, String> map = JCoreWireCodec.extractJsonStringMap(json, primaryKey);
        if (map.isEmpty() && fallbackKey != null) {
            map = JCoreWireCodec.extractJsonStringMap(json, fallbackKey);
        }
        if (map.isEmpty()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!first) {
                sb.append(',');
            }
            first = false;
            sb.append('"').append(entry.getKey()).append("\":\"").append(entry.getValue()).append('"');
        }
        sb.append('}');
        return sb.toString();
    }

    public void setAliasAndTagsWithCallBack(
        final Context context,
        final String alias,
        final Set<String> tags,
        final TagAliasCallback callback,
        int protoType,
        int action
    ) {
        JCoreTcpEngine.getInstance().executeAsync(new Runnable() {
            @Override
            public void run() {
                if (alias != null) {
                    JCoreStorage.saveAlias(context, alias);
                }
                Set<String> validTags = JPushInterface.filterValidTags(tags);
                if (tags != null) {
                    JCoreStorage.saveTags(context, validTags);
                }
                if (callback != null) {
                    callback.gotResult(0, alias, validTags);
                }
            }
        });
    }

    private boolean shouldQueueOrWaitForServerAck() {
        JCoreTcpEngine engine = JCoreTcpEngine.getInstance();
        return engine.isRequireServerAckForOperations()
            || (!engine.isLoggedIn() && !engine.getCustomHost().isEmpty());
    }

    public void setAliasWithCallBack(
        final Context context,
        final int sequence,
        String alias,
        int protoType,
        final int action
    ) {
        final String previousAlias = JCoreStorage.getAlias(context);
        String resolvedAlias = alias != null ? alias : "";
        if (action == 2) {
            JCoreStorage.saveAlias(context, resolvedAlias);
        } else if (action == 3) {
            JCoreStorage.saveAlias(context, "");
            resolvedAlias = "";
        } else if (action == 5) {
            resolvedAlias = previousAlias;
        }
        final String effectiveAlias = resolvedAlias;

        byte[] wireBody = JCoreWireCodec.buildAliasOperationPayload(action, sequence, effectiveAlias);
        if (shouldQueueOrWaitForServerAck()) {
            JCoreTcpEngine.getInstance().enqueueOrSendRequest(
                context,
                JCoreWireCodec.CMD_ALIAS_OP,
                JCoreWireCodec.VER_ALIAS_OP,
                sequence,
                wireBody,
                JCoreTcpEngine.getInstance().getDefaultRequestTimeoutMs(),
                0,
                new JCoreTcpEngine.RequestCallback() {
                    @Override
                    public void onResult(JCoreWireCodec.DecodedFrame responseFrame, int statusCode) {
                        if (statusCode != 0 && (action == 2 || action == 3)) {
                            JCoreStorage.saveAlias(context, previousAlias);
                        }
                        String resultAlias = effectiveAlias;
                        if (statusCode == 0 && responseFrame != null && responseFrame.body != null && responseFrame.body.length > 0) {
                            String respJson = JCoreWireCodec.parseJsonCommandBody(responseFrame.body);
                            if (action == 5 && respJson.contains("\"alias\"")) {
                                String srvAlias = JCoreWireCodec.extractJsonString(respJson, "alias");
                                JCoreStorage.saveAlias(context, srvAlias);
                                resultAlias = srvAlias;
                            }
                        }
                        Intent intent = buildBaseReceiverIntent(context, "alias_op", sequence, statusCode);
                        intent.putExtra(KEY_ALIAS, resultAlias);
                        if (intent.getStringExtra(KEY_EVENT_TYPE) == null) {
                            JPushMessage msg = new JPushMessage();
                            msg.setSequence(sequence);
                            msg.setErrorCode(statusCode);
                            msg.setAlias(resultAlias);
                            for (JPushMessageReceiver receiver : registeredReceivers) {
                                receiver.onAliasOperatorResult(context, msg);
                            }
                        }
                        sendToReceiver(context, intent);
                    }
                }
            );
            return;
        }

        JCoreTcpEngine.getInstance().sendFrameRaw(
            JCoreWireCodec.CMD_ALIAS_OP,
            JCoreWireCodec.VER_ALIAS_OP,
            sequence,
            JCoreTcpEngine.getInstance().getSessionId(),
            JCoreStorage.getUid(context),
            wireBody
        );

        Intent intent = buildBaseReceiverIntent(context, "alias_op", sequence, 0);
        intent.putExtra(KEY_ALIAS, effectiveAlias);
        if (intent.getStringExtra(KEY_EVENT_TYPE) == null) {
            JPushMessage msg = new JPushMessage();
            msg.setSequence(sequence);
            msg.setErrorCode(0);
            msg.setAlias(effectiveAlias);
            for (JPushMessageReceiver receiver : registeredReceivers) {
                receiver.onAliasOperatorResult(context, msg);
            }
        }
        sendToReceiver(context, intent);
    }

    public void setTagsWithCallBack(
        final Context context,
        final int sequence,
        Set<String> tags,
        int protoType,
        final int action
    ) {
        final Set<String> validInput = JPushInterface.filterValidTags(tags);
        Set<String> stored = JCoreStorage.getTags(context);
        final Set<String> previousTags = new HashSet<String>(stored);

        if (action == 1) { // add
            stored.addAll(validInput);
            JCoreStorage.saveTags(context, stored);
        } else if (action == 2) { // set
            stored = new HashSet<String>(validInput);
            JCoreStorage.saveTags(context, stored);
        } else if (action == 3) { // delete
            stored.removeAll(validInput);
            JCoreStorage.saveTags(context, stored);
        } else if (action == 4) { // clean
            stored.clear();
            JCoreStorage.saveTags(context, stored);
        }
        final Set<String> finalStored = new HashSet<String>(stored);

        byte[] wireBody = JCoreWireCodec.buildTagOperationPayload(action, sequence, validInput);
        if (shouldQueueOrWaitForServerAck()) {
            JCoreTcpEngine.getInstance().enqueueOrSendRequest(
                context,
                JCoreWireCodec.CMD_TAG_OP,
                JCoreWireCodec.VER_TAG_OP,
                sequence,
                wireBody,
                JCoreTcpEngine.getInstance().getDefaultRequestTimeoutMs(),
                0,
                new JCoreTcpEngine.RequestCallback() {
                    @Override
                    public void onResult(JCoreWireCodec.DecodedFrame responseFrame, int statusCode) {
                        if (statusCode != 0 && action >= 1 && action <= 4) {
                            JCoreStorage.saveTags(context, previousTags);
                        }
                        dispatchTagResultIntent(context, sequence, action, statusCode, validInput, finalStored, responseFrame);
                    }
                }
            );
            return;
        }

        JCoreTcpEngine.getInstance().sendFrameRaw(
            JCoreWireCodec.CMD_TAG_OP,
            JCoreWireCodec.VER_TAG_OP,
            sequence,
            JCoreTcpEngine.getInstance().getSessionId(),
            JCoreStorage.getUid(context),
            wireBody
        );
        dispatchTagResultIntent(context, sequence, action, 0, validInput, finalStored, null);
    }

    private static boolean parseJsonBindFlag(String respJson, boolean defaultBind) {
        try {
            JSONObject obj = new JSONObject(respJson);
            Object raw = obj.opt("bind");
            if (raw == null) {
                raw = obj.opt("is_bind");
            }
            if (raw instanceof Boolean) {
                return ((Boolean) raw).booleanValue();
            }
            if (raw instanceof Number) {
                return ((Number) raw).intValue() != 0;
            }
            if (raw instanceof String) {
                String s = ((String) raw).trim();
                if ("true".equalsIgnoreCase(s) || "1".equals(s)) {
                    return true;
                }
                if ("false".equalsIgnoreCase(s) || "0".equals(s)) {
                    return false;
                }
            }
        } catch (Throwable ignored) {
        }
        return defaultBind;
    }

    private static Set<String> parseJsonTagsSet(String respJson) {
        Set<String> serverTags = new HashSet<String>();
        try {
            JSONObject obj = new JSONObject(respJson);
            JSONArray arr = obj.optJSONArray("tags");
            if (arr != null) {
                for (int i = 0; i < arr.length(); i++) {
                    String item = arr.optString(i, null);
                    if (item != null && !item.isEmpty()) {
                        serverTags.add(item);
                    }
                }
                return serverTags;
            }
        } catch (Throwable ignored) {
        }
        serverTags.addAll(JCoreWireCodec.extractJsonStringArray(respJson, "tags"));
        return serverTags;
    }

    private void dispatchTagResultIntent(
        Context context,
        int sequence,
        int action,
        int statusCode,
        Set<String> validInput,
        Set<String> stored,
        JCoreWireCodec.DecodedFrame responseFrame
    ) {
        String respJson = (statusCode == 0 && responseFrame != null && responseFrame.body != null && responseFrame.body.length > 0)
            ? JCoreWireCodec.parseJsonCommandBody(responseFrame.body)
            : "";
        if (action == 6) { // checkTagBindState
            String checkTag = validInput.isEmpty() ? "" : validInput.iterator().next();
            boolean isBind = !checkTag.isEmpty() && stored.contains(checkTag);
            if (statusCode == 0 && !respJson.isEmpty()
                && (respJson.contains("\"bind\"") || respJson.contains("\"is_bind\""))) {
                isBind = parseJsonBindFlag(respJson, isBind);
            }
            Intent checkIntent = buildBaseReceiverIntent(context, "check_tag_op", sequence, statusCode);
            checkIntent.putExtra(KEY_CHECK_TAG, checkTag);
            checkIntent.putExtra(KEY_TAG_BIND_STATE, isBind);
            if (checkIntent.getStringExtra(KEY_EVENT_TYPE) == null) {
                JPushMessage msg = new JPushMessage();
                msg.setSequence(sequence);
                msg.setErrorCode(statusCode);
                msg.setCheckTag(checkTag);
                msg.setTagCheckStateResult(isBind);
                msg.setTagCheckOperator(true);
                for (JPushMessageReceiver receiver : registeredReceivers) {
                    receiver.onCheckTagOperatorResult(context, msg);
                }
            }
            sendToReceiver(context, checkIntent);
            return;
        }

        Set<String> resultTags = (action == 5) ? stored : validInput;
        if (action == 5 && statusCode == 0 && !respJson.isEmpty() && respJson.contains("\"tags\"")) {
            Set<String> serverTags = parseJsonTagsSet(respJson);
            JCoreStorage.saveTags(context, serverTags);
            resultTags = serverTags;
        }
        Intent intent = buildBaseReceiverIntent(context, "tag_op", sequence, statusCode);
        intent.putStringArrayListExtra(KEY_TAGS, new ArrayList<String>(resultTags));
        if (intent.getStringExtra(KEY_EVENT_TYPE) == null) {
            JPushMessage msg = new JPushMessage();
            msg.setSequence(sequence);
            msg.setErrorCode(statusCode);
            msg.setTags(new HashSet<String>(resultTags));
            for (JPushMessageReceiver receiver : registeredReceivers) {
                receiver.onTagOperatorResult(context, msg);
            }
        }
        sendToReceiver(context, intent);
    }

    public void setPropertyWithCallBack(
        final Context context,
        final int sequence,
        final Map<String, Object> properties,
        int protoType,
        final int action
    ) {
        Map<String, Object> stored = JCoreStorage.getProperties(context);
        final Map<String, Object> previousProps = new HashMap<String, Object>(stored);
        if (action == 1 && properties != null) {
            stored.putAll(properties);
            JCoreStorage.saveProperties(context, stored);
        } else if (action == 2 && properties != null) {
            for (String key : properties.keySet()) {
                stored.remove(key);
            }
            JCoreStorage.saveProperties(context, stored);
        } else if (action == 3) {
            stored.clear();
            JCoreStorage.saveProperties(context, stored);
        }
        final Map<String, Object> finalStored = new HashMap<String, Object>(stored);

        byte[] wireBody = JCoreWireCodec.buildPropertyOperationPayload(action, sequence, properties);
        if (shouldQueueOrWaitForServerAck()) {
            JCoreTcpEngine.getInstance().enqueueOrSendRequest(
                context,
                JCoreWireCodec.CMD_PROPERTY_OP,
                JCoreWireCodec.VER_PROPERTY_OP,
                sequence,
                wireBody,
                JCoreTcpEngine.getInstance().getDefaultRequestTimeoutMs(),
                0,
                new JCoreTcpEngine.RequestCallback() {
                    @Override
                    public void onResult(JCoreWireCodec.DecodedFrame responseFrame, int statusCode) {
                        if (statusCode != 0) {
                            JCoreStorage.saveProperties(context, previousProps);
                        }
                        Intent intent = buildBaseReceiverIntent(context, "property_op", sequence, statusCode);
                        HashMap<String, Object> serializableProps = new HashMap<String, Object>();
                        if ((action == 1 || action == 2) && properties != null) {
                            serializableProps.putAll(properties);
                        } else {
                            serializableProps.putAll(statusCode == 0 ? finalStored : previousProps);
                        }
                        intent.putExtra(KEY_PROPS_MAP, serializableProps);
                        if (intent.getStringExtra(KEY_EVENT_TYPE) == null) {
                            JPushMessage msg = new JPushMessage();
                            msg.setSequence(sequence);
                            msg.setErrorCode(statusCode);
                            msg.setPros(serializableProps);
                            for (JPushMessageReceiver receiver : registeredReceivers) {
                                receiver.onPropertyOperatorResult(context, msg);
                            }
                        }
                        sendToReceiver(context, intent);
                    }
                }
            );
            return;
        }

        JCoreTcpEngine.getInstance().sendFrameRaw(
            JCoreWireCodec.CMD_PROPERTY_OP,
            JCoreWireCodec.VER_PROPERTY_OP,
            sequence,
            JCoreTcpEngine.getInstance().getSessionId(),
            JCoreStorage.getUid(context),
            wireBody
        );

        Intent intent = buildBaseReceiverIntent(context, "property_op", sequence, 0);
        HashMap<String, Object> serializableProps = new HashMap<String, Object>();
        if ((action == 1 || action == 2) && properties != null) {
            serializableProps.putAll(properties);
        } else {
            serializableProps.putAll(finalStored);
        }
        intent.putExtra(KEY_PROPS_MAP, serializableProps);
        if (intent.getStringExtra(KEY_EVENT_TYPE) == null) {
            JPushMessage msg = new JPushMessage();
            msg.setSequence(sequence);
            msg.setErrorCode(0);
            msg.setPros(serializableProps);
            for (JPushMessageReceiver receiver : registeredReceivers) {
                receiver.onPropertyOperatorResult(context, msg);
            }
        }
        sendToReceiver(context, intent);
    }

    public void setMobileNumberWithCallBack(final Context context, final int sequence, final String mobileNumber) {
        final String previousMobile = JCoreStorage.getMobileNumber(context);
        JCoreStorage.saveMobileNumber(context, mobileNumber);
        byte[] wireBody = JCoreWireCodec.buildMobileNumberPayload(sequence, mobileNumber);
        if (shouldQueueOrWaitForServerAck()) {
            JCoreTcpEngine.getInstance().enqueueOrSendRequest(
                context,
                JCoreWireCodec.CMD_MOBILE_NUMBER,
                JCoreWireCodec.VER_MOBILE_NUMBER,
                sequence,
                wireBody,
                JCoreTcpEngine.getInstance().getDefaultRequestTimeoutMs(),
                0,
                new JCoreTcpEngine.RequestCallback() {
                    @Override
                    public void onResult(JCoreWireCodec.DecodedFrame responseFrame, int statusCode) {
                        if (statusCode != 0) {
                            JCoreStorage.saveMobileNumber(context, previousMobile);
                        }
                        Intent intent = buildBaseReceiverIntent(context, "mobile_number_op", sequence, statusCode);
                        intent.putExtra(KEY_MOBILE_NUMBER, mobileNumber != null ? mobileNumber : "");
                        if (intent.getStringExtra(KEY_EVENT_TYPE) == null) {
                            JPushMessage msg = new JPushMessage();
                            msg.setSequence(sequence);
                            msg.setErrorCode(statusCode);
                            msg.setMobileNumber(mobileNumber != null ? mobileNumber : "");
                            for (JPushMessageReceiver receiver : registeredReceivers) {
                                receiver.onMobileNumberOperatorResult(context, msg);
                            }
                        }
                        sendToReceiver(context, intent);
                    }
                }
            );
            return;
        }

        JCoreTcpEngine.getInstance().sendFrameRaw(
            JCoreWireCodec.CMD_MOBILE_NUMBER,
            JCoreWireCodec.VER_MOBILE_NUMBER,
            sequence,
            JCoreTcpEngine.getInstance().getSessionId(),
            JCoreStorage.getUid(context),
            wireBody
        );

        Intent intent = buildBaseReceiverIntent(context, "mobile_number_op", sequence, 0);
        intent.putExtra(KEY_MOBILE_NUMBER, mobileNumber != null ? mobileNumber : "");
        if (intent.getStringExtra(KEY_EVENT_TYPE) == null) {
            JPushMessage msg = new JPushMessage();
            msg.setSequence(sequence);
            msg.setErrorCode(0);
            msg.setMobileNumber(mobileNumber != null ? mobileNumber : "");
            for (JPushMessageReceiver receiver : registeredReceivers) {
                receiver.onMobileNumberOperatorResult(context, msg);
            }
        }
        sendToReceiver(context, intent);
    }

    public void setThirdTokenWithCallBack(
        final Context context,
        final String token,
        final byte platform,
        final boolean unbind
    ) {
        byte[] wireBody = JCoreWireCodec.buildThirdTokenPayload(token, platform, unbind);
        JCoreTcpEngine.getInstance().enqueueOrSendRequest(
            context,
            JCoreWireCodec.CMD_THIRD_TOKEN,
            JCoreWireCodec.VER_THIRD_TOKEN,
            JCoreStorage.nextRid(),
            wireBody,
            JCoreTcpEngine.getInstance().getDefaultRequestTimeoutMs(),
            3,
            new JCoreTcpEngine.RequestCallback() {
                @Override
                public void onResult(JCoreWireCodec.DecodedFrame responseFrame, int statusCode) {
                    if (statusCode == 0) {
                        JCoreStorage.saveThirdToken(context, platform, unbind ? "" : token);
                    }
                    cn.jiguang.plugins.push.common.JLogger.i("Third token result platform=" + platform + " code=" + statusCode);
                    Intent intent = buildBaseReceiverIntent(context, "cmd_result", 0, statusCode);
                    intent.putExtra("cmd", 10000);
                    intent.putExtra("msg", unbind ? "unbind" : "bind");
                    intent.putExtra("token", token != null ? token : "");
                    intent.putExtra("platform", (int) platform);
                    sendToReceiver(context, intent);
                }
            }
        );
    }

    public void setPushStatusWithCallBack(Context context, int protoType, int action) {
        boolean stopped = (action == 0);
        byte[] wireBody = JCoreWireCodec.buildPushControlPayload(stopped);
        if (!stopped && shouldQueueOrWaitForServerAck()) {
            JCoreTcpEngine.getInstance().enqueueOrSendRequest(
                context,
                JCoreWireCodec.CMD_PUSH_CTRL,
                JCoreWireCodec.VER_PUSH_CTRL,
                JCoreStorage.nextRid(),
                wireBody,
                JCoreTcpEngine.getInstance().getDefaultRequestTimeoutMs(),
                0,
                null
            );
            return;
        }
        JCoreTcpEngine.getInstance().sendFrameRaw(
            JCoreWireCodec.CMD_PUSH_CTRL,
            JCoreWireCodec.VER_PUSH_CTRL,
            JCoreStorage.nextRid(),
            JCoreTcpEngine.getInstance().getSessionId(),
            JCoreStorage.getUid(context),
            wireBody
        );
    }

    public void dispatchRegisterEvent(Context context, String registrationId) {
        Intent intent = buildBaseReceiverIntent(context, "register", 0, 0);
        intent.putExtra("registration_id", registrationId != null ? registrationId : "");
        sendToReceiver(context, intent);
    }

    public void dispatchConnectedEvent(Context context, boolean connected) {
        Intent intent = buildBaseReceiverIntent(context, "connected", 0, 0);
        intent.putExtra("connected", connected);
        sendToReceiver(context, intent);
    }

    public void dispatchLocalNotification(final Context context, final JPushLocalNotification localNotification) {
        if (localNotification == null) {
            return;
        }
        final int notifyId = (int) localNotification.getNotificationId();
        long broadcastTime = localNotification.getBroadcastTime();
        long now = System.currentTimeMillis();
        long delayMs = 0L;
        if (broadcastTime > 1000000000000L) {
            delayMs = broadcastTime - now;
        } else if (broadcastTime > 0L) {
            delayMs = broadcastTime;
        }
        ScheduledFuture<?> previous = pendingLocalNotifications.remove(Integer.valueOf(notifyId));
        if (previous != null) {
            previous.cancel(false);
        }
        if (delayMs > 0L) {
            ScheduledFuture<?> future = localNotificationScheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    pendingLocalNotifications.remove(Integer.valueOf(notifyId));
                    deliverLocalNotificationNow(context, localNotification);
                }
            }, delayMs, TimeUnit.MILLISECONDS);
            pendingLocalNotifications.put(Integer.valueOf(notifyId), future);
            return;
        }
        deliverLocalNotificationNow(context, localNotification);
    }

    private void deliverLocalNotificationNow(Context context, JPushLocalNotification localNotification) {
        NotificationMessage message = new NotificationMessage();
        message.notificationId = (int) localNotification.getNotificationId();
        message.msgId = String.valueOf(localNotification.getNotificationId());
        message.notificationTitle = localNotification.getTitle() != null ? localNotification.getTitle() : "";
        message.notificationContent = localNotification.getContent() != null ? localNotification.getContent() : "";
        message.notificationExtras = localNotification.getExtras() != null ? localNotification.getExtras() : "";
        message.notificationType = 1;
        message.notificationPriority = localNotification.getPriority();
        message.notificationCategory = localNotification.getCategory();
        postSystemNotification(context, message);
        dispatchNotificationEvent(context, "notify_arrived", message);
    }

    public void dispatchNotificationEvent(Context context, String eventType, NotificationMessage message) {
        if (message == null) {
            return;
        }
        Intent intent = buildBaseReceiverIntent(context, eventType, 0, 0);
        intent.putExtra(KEY_NOTIFICATION_MESSAGE, message);
        sendToReceiver(context, intent);
    }

    public void dispatchCustomMessageEvent(Context context, CustomMessage message) {
        if (message == null) {
            return;
        }
        Intent intent = buildBaseReceiverIntent(context, "custom_msg", 0, 0);
        intent.putExtra(KEY_CUSTOM_MESSAGE, message);
        sendToReceiver(context, intent);
    }

    private Intent buildBaseReceiverIntent(Context context, String eventType, int sequence, int errorCode) {
        Intent intent = new Intent(ACTION_RECEIVER_MESSAGE);
        Context app = JCoreStorage.getAppContext(context);
        if (app != null) {
            try {
                String pkg = app.getPackageName();
                if (pkg != null && !pkg.isEmpty()) {
                    intent.setPackage(pkg);
                    intent.addCategory(pkg);
                }
            } catch (Throwable ignored) {
            }
        }
        intent.putExtra(KEY_EVENT_TYPE, eventType);
        intent.putExtra(KEY_SEQUENCE, sequence);
        intent.putExtra(KEY_ERROR_CODE, errorCode);
        return intent;
    }

    private void sendToReceiver(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        Context app = JCoreStorage.getAppContext(context);
        for (JPushMessageReceiver receiver : registeredReceivers) {
            try {
                onJPushMessageReceive(app != null ? app : context, receiver, intent);
            } catch (Throwable ignored) {
            }
        }
        if (app != null && registeredReceivers.isEmpty()) {
            try {
                app.sendBroadcast(intent);
            } catch (Throwable ignored) {
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void onJPushMessageReceive(Context context, JPushMessageReceiver receiver, Intent intent) {
        if (receiver == null || intent == null) {
            return;
        }
        String eventType = intent.getStringExtra(KEY_EVENT_TYPE);
        if (eventType == null) {
            return;
        }
        int sequence = intent.getIntExtra(KEY_SEQUENCE, 0);
        int errorCode = intent.getIntExtra(KEY_ERROR_CODE, 0);

        if ("register".equals(eventType)) {
            String regId = intent.getStringExtra("registration_id");
            receiver.onRegister(context, regId != null ? regId : "");
            return;
        }
        if ("connected".equals(eventType)) {
            boolean connected = intent.getBooleanExtra("connected", true);
            receiver.onConnected(context, connected);
            return;
        }
        if ("alias_op".equals(eventType)) {
            JPushMessage msg = new JPushMessage();
            msg.setSequence(sequence);
            msg.setErrorCode(errorCode);
            msg.setAlias(intent.getStringExtra(KEY_ALIAS));
            receiver.onAliasOperatorResult(context, msg);
            return;
        }
        if ("tag_op".equals(eventType)) {
            JPushMessage msg = new JPushMessage();
            msg.setSequence(sequence);
            msg.setErrorCode(errorCode);
            ArrayList<String> tagList = intent.getStringArrayListExtra(KEY_TAGS);
            msg.setTags(tagList != null ? new HashSet<String>(tagList) : new HashSet<String>());
            receiver.onTagOperatorResult(context, msg);
            return;
        }
        if ("check_tag_op".equals(eventType)) {
            JPushMessage msg = new JPushMessage();
            msg.setSequence(sequence);
            msg.setErrorCode(errorCode);
            msg.setCheckTag(intent.getStringExtra(KEY_CHECK_TAG));
            msg.setTagCheckStateResult(intent.getBooleanExtra(KEY_TAG_BIND_STATE, false));
            msg.setTagCheckOperator(true);
            receiver.onCheckTagOperatorResult(context, msg);
            return;
        }
        if ("property_op".equals(eventType)) {
            JPushMessage msg = new JPushMessage();
            msg.setSequence(sequence);
            msg.setErrorCode(errorCode);
            Serializable mapObj = intent.getSerializableExtra(KEY_PROPS_MAP);
            if (mapObj instanceof Map) {
                msg.setPros((Map<String, Object>) mapObj);
            }
            receiver.onPropertyOperatorResult(context, msg);
            return;
        }
        if ("mobile_number_op".equals(eventType)) {
            JPushMessage msg = new JPushMessage();
            msg.setSequence(sequence);
            msg.setErrorCode(errorCode);
            msg.setMobileNumber(intent.getStringExtra(KEY_MOBILE_NUMBER));
            receiver.onMobileNumberOperatorResult(context, msg);
            return;
        }
        if ("notify_arrived".equals(eventType)) {
            Serializable nMsg = intent.getSerializableExtra(KEY_NOTIFICATION_MESSAGE);
            if (nMsg instanceof NotificationMessage) {
                receiver.onNotifyMessageArrived(context, (NotificationMessage) nMsg);
            }
            return;
        }
        if ("notify_opened".equals(eventType)) {
            Serializable nMsg = intent.getSerializableExtra(KEY_NOTIFICATION_MESSAGE);
            if (nMsg instanceof NotificationMessage) {
                receiver.onNotifyMessageOpened(context, (NotificationMessage) nMsg);
            }
            return;
        }
        if ("notify_dismiss".equals(eventType)) {
            Serializable nMsg = intent.getSerializableExtra(KEY_NOTIFICATION_MESSAGE);
            if (nMsg instanceof NotificationMessage) {
                receiver.onNotifyMessageDismiss(context, (NotificationMessage) nMsg);
            }
            return;
        }
        if ("custom_msg".equals(eventType)) {
            Serializable cMsg = intent.getSerializableExtra(KEY_CUSTOM_MESSAGE);
            if (cMsg instanceof CustomMessage) {
                receiver.onMessage(context, (CustomMessage) cMsg);
            }
            return;
        }
        if ("inapp_show".equals(eventType)) {
            Serializable nMsg = intent.getSerializableExtra(KEY_NOTIFICATION_MESSAGE);
            if (nMsg instanceof NotificationMessage) {
                receiver.onInAppMessageShow(context, (NotificationMessage) nMsg);
            }
            return;
        }
        if ("inapp_click".equals(eventType)) {
            Serializable nMsg = intent.getSerializableExtra(KEY_NOTIFICATION_MESSAGE);
            if (nMsg instanceof NotificationMessage) {
                receiver.onInAppMessageClick(context, (NotificationMessage) nMsg);
            }
            return;
        }
        if ("notify_button_click".equals(eventType)) {
            Serializable btn = intent.getSerializableExtra(KEY_CUSTOM_BUTTON);
            if (btn instanceof NotificationCustomButton) {
                receiver.onNotifyButtonClick(context, (NotificationCustomButton) btn);
            }
            return;
        }
        if ("cmd_result".equals(eventType)) {
            int cmd = intent.getIntExtra("cmd", 0);
            String msgText = intent.getStringExtra("msg");
            receiver.onCommandResult(context, new CmdMessage(cmd, errorCode, msgText, intent.getExtras()));
        }
    }

    public void onJPushMessageReceive(Context context, String receiverClassName, String messageType, Bundle bundle) {
    }

    public void onMessage(Context context, CustomMessage message) {
    }

    public void onNotifyMessageArrived(Context context, NotificationMessage message) {
    }

    public void onNotifyMessageOpened(Context context, NotificationMessage message) {
    }

    public void onMultiAction(Context context, Intent intent) {
    }

    public void onInAppMessageShow(Context context, NotificationMessage message) {
    }

    public void onInAppMessageClick(Context context, NotificationMessage message) {
    }

    public boolean isSupportedCMD(String sdkType, int command) {
        return command == 3 || command == 10 || command == 26 || command == 27 || command == 28 || command == 29 || command == 38 || command == 40;
    }
}
