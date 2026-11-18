package cn.jpush.android.local;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import cn.jiguang.core.JCoreStorage;
import cn.jpush.android.api.NotificationMessage;
import java.util.Locale;
import org.json.JSONObject;

/** Resolves the JPush 5.9.0 channel identity so Android can retain user settings on upgrade. */
public final class LegacyNotificationChannel {
    private static final String GROUP_ID = "JIGUANG_CHANNEL_GROUP";
    private static final int ALERT_MASK =
        Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS;

    private LegacyNotificationChannel() {}

    public static void readOptions(NotificationMessage message, JSONObject payload) {
        message.notificationChannelId = payload.optString("n_channel_id", "");
        message.notificationPriority = payload.optInt("n_priority", Notification.PRIORITY_DEFAULT);
        message.notificationAlertType = payload.optInt("n_alert_type", Notification.DEFAULT_ALL);
        message.notificationSound = payload.optString("n_sound", "");
        JSONObject importance = payload.optJSONObject("n_importance");
        String key = Build.MANUFACTURER == null ? null : manufacturerKey(Build.MANUFACTURER);
        message.notificationImportance = importance == null || key == null ? -1 : importance.optInt(key, -1);
    }

    @TargetApi(26)
    static String resolve(Context context, NotificationManager manager, NotificationMessage message, boolean silenceActive) {
        String channelId = silenceActive ? null : message.notificationChannelId;
        if (!TextUtils.isEmpty(channelId) && manager.getNotificationChannel(channelId) != null) {
            return channelId;
        }

        int priority = message.notificationPriority;
        if (message.notificationImportance != -1) {
            priority = Math.min(priority, priorityForImportance(message.notificationImportance));
        }
        int defaults = silenceActive ? 0 : message.notificationAlertType;
        String soundName = silenceActive ? null : message.notificationSound;
        if (priority == Notification.PRIORITY_MIN || priority == Notification.PRIORITY_LOW) {
            defaults = 0;
            soundName = null;
        } else if (priority >= Notification.PRIORITY_DEFAULT && defaults == 0 && TextUtils.isEmpty(soundName)) {
            priority = Notification.PRIORITY_LOW;
        }
        defaults &= ALERT_MASK;

        int importance = importanceForPriority(priority);
        Uri sound = resolveSound(context, soundName);
        if (sound != null) {
            defaults &= ~Notification.DEFAULT_SOUND;
        }
        if (TextUtils.isEmpty(channelId)) {
            channelId = "JPush_" + importance + "_" + defaults;
            if (sound != null) {
                channelId += "_" + soundName;
            }
            if (manager.getNotificationChannel(channelId) != null) {
                return channelId;
            }
        }

        String channelName = JCoreStorage.getChannel(context);
        NotificationChannel channel = new NotificationChannel(
            channelId, TextUtils.isEmpty(channelName) ? "Notifications" : channelName, importance
        );
        if (Build.VERSION.SDK_INT < 28 || manager.getNotificationChannelGroup(GROUP_ID) == null) {
            CharSequence appName = context.getApplicationInfo().loadLabel(context.getPackageManager());
            manager.createNotificationChannelGroup(new NotificationChannelGroup(GROUP_ID, appName));
        }
        channel.setGroup(GROUP_ID);
        channel.enableLights((defaults & Notification.DEFAULT_LIGHTS) != 0);
        channel.enableVibration((defaults & Notification.DEFAULT_VIBRATE) != 0);
        if (sound != null || (defaults & Notification.DEFAULT_SOUND) == 0) {
            channel.setSound(sound, null);
        }
        manager.createNotificationChannel(channel);
        return channelId;
    }

    private static Uri resolveSound(Context context, String name) {
        if (!TextUtils.isEmpty(name)
            && context.getResources().getIdentifier(name, "raw", context.getPackageName()) != 0) {
            return Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + name);
        }
        return null;
    }

    private static int importanceForPriority(int priority) {
        switch (priority) {
            case Notification.PRIORITY_MIN: return NotificationManager.IMPORTANCE_MIN;
            case Notification.PRIORITY_LOW: return NotificationManager.IMPORTANCE_LOW;
            case Notification.PRIORITY_HIGH:
            case Notification.PRIORITY_MAX: return NotificationManager.IMPORTANCE_HIGH;
            default: return NotificationManager.IMPORTANCE_DEFAULT;
        }
    }

    private static int priorityForImportance(int importance) {
        switch (importance) {
            case NotificationManager.IMPORTANCE_NONE:
            case NotificationManager.IMPORTANCE_MIN: return Notification.PRIORITY_MIN;
            case NotificationManager.IMPORTANCE_LOW: return Notification.PRIORITY_LOW;
            case NotificationManager.IMPORTANCE_HIGH:
            case NotificationManager.IMPORTANCE_MAX: return Notification.PRIORITY_HIGH;
            default: return Notification.PRIORITY_DEFAULT;
        }
    }

    private static String manufacturerKey(String manufacturer) {
        switch (manufacturer.toLowerCase(Locale.ROOT)) {
            case "google": return "fc";
            case "huawei": return "hw";
            case "honor": return "hn";
            case "meizu": return "mz";
            case "vivo": return "vi";
            case "oppo":
            case "realme":
            case "oneplus": return "op";
            case "xiaomi":
            case "blackshark": return "xm";
            default: return null;
        }
    }
}
