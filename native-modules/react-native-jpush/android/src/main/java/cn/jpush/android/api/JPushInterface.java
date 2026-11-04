package cn.jpush.android.api;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import cn.jiguang.api.JCoreManager;
import cn.jiguang.core.JCoreStorage;
import cn.jiguang.core.JCoreTcpEngine;
import cn.jiguang.core.JCoreWireCodec;
import cn.jpush.android.data.JPushCollectControl;
import cn.jpush.android.data.JPushConfig;
import cn.jpush.android.data.JPushLocalNotification;
import cn.jpush.android.local.ActionHelper;
import cn.jpush.android.local.JPushConstants;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.json.JSONObject;

public class JPushInterface {
    public static final int DEFAULT_NOTIFICATION_NUM = 5;
    public static final String PUSH_MESSAGE_PERMISSION_POSTFIX = ".permission.JPUSH_MESSAGE";

    public static final String EXTRA_APP_KEY = "cn.jpush.android.APPKEY";
    public static final String EXTRA_CONNECTION_CHANGE = "cn.jpush.android.CONNECTION_CHANGE";
    public static final String EXTRA_REGISTRATION_ID = "cn.jpush.android.REGISTRATION_ID";
    public static final String EXTRA_STATUS = "cn.jpush.android.STATUS";
    public static final String EXTRA_PUSH_ID = "cn.jpush.android.PUSH_ID";
    public static final String EXTRA_MSG_ID = "cn.jpush.android.MSG_ID";
    public static final String EXTRA_TITLE = "cn.jpush.android.TITLE";
    public static final String EXTRA_MESSAGE = "cn.jpush.android.MESSAGE";
    public static final String EXTRA_CONTENT_TYPE = "cn.jpush.android.CONTENT_TYPE";
    public static final String EXTRA_RICHPUSH_FILE_PATH = "cn.jpush.android.FILE_PATH";
    public static final String EXTRA_RICHPUSH_FILE_TYPE = "cn.jpush.android.FILE_TYPE";
    public static final String EXTRA_RICHPUSH_HTML_PATH = "cn.jpush.android.HTML_PATH";
    public static final String EXTRA_RICHPUSH_HTML_RES = "cn.jpush.android.HTML_RES";
    public static final String EXTRA_NOTIFICATION_DEVELOPER_ARG0 = "cn.jpush.android.NOTIFICATION_DEVELOPER_ARG0";
    public static final String EXTRA_NOTIFICATION_TITLE = "cn.jpush.android.NOTIFICATION_CONTENT_TITLE";
    public static final String EXTRA_NOTIFICATION_SMALL_ICON = "cn.jpush.android.NOTIFICATION_SMALL_ICON";
    public static final String EXTRA_NOTIFICATION_LARGE_ICON = "cn.jpush.android.NOTIFICATION_LARGE_ICON";
    public static final String EXTRA_ALERT = "cn.jpush.android.ALERT";
    public static final String EXTRA_BIG_TEXT = "cn.jpush.android.BIG_TEXT";
    public static final String EXTRA_INBOX = "cn.jpush.android.INBOX";
    public static final String EXTRA_NOTI_PRIORITY = "cn.jpush.android.NOTI_PRIORITY";
    public static final String EXTRA_NOTI_IMPORTANCE = "cn.jpush.android.NOTI_IMPORTANCE";
    public static final String EXTRA_NOTI_CATEGORY = "cn.jpush.android.NOTI_CATEGORY";
    public static final String EXTRA_BIG_PIC_PATH = "cn.jpush.android.BIG_PIC_PATH";
    public static final String EXTRA_ALERT_TYPE = "cn.jpush.android.ALERT_TYPE";
    public static final String EXTRA_EXTRA = "cn.jpush.android.EXTRA";
    public static final String EXTRA_NOTIFICATION_ID = "cn.jpush.android.NOTIFICATION_ID";
    public static final String EXTRA_ACTIVITY_PARAM = "cn.jpush.android.ACTIVITY_PARAM";
    public static final String EXTRA_NOTIFICATION_ACTION_EXTRA = "cn.jpush.android.NOTIFIACATION_ACTION_EXTRA";
    public static final String EXTRA_NOTIFICATION_URL = "cn.jpush.android.NOTIFICATION_URL";
    public static final String EXTRA_NOTI_TYPE = "cn.jpush.android.NOTIFICATION_TYPE";
    public static final String EXTRA_TYPE_PLATFORM = "cn.jpush.android.TYPE_PLATFORM";

    public static final String ACTION_REGISTRATION_ID = "cn.jpush.android.intent.REGISTRATION";
    public static final String ACTION_MESSAGE_RECEIVED = "cn.jpush.android.intent.MESSAGE_RECEIVED";
    public static final String ACTION_NOTIFICATION_RECEIVED = "cn.jpush.android.intent.NOTIFICATION_RECEIVED";
    public static final String ACTION_NOTIFICATION_OPENED = "cn.jpush.android.intent.NOTIFICATION_OPENED";
    public static final String ACTION_NOTIFICATION_CLICK_ACTION = "cn.jpush.android.intent.NOTIFICATION_CLICK_ACTION";
    public static final String ACTION_NOTIFICATION_CLICK_ACTION_PROXY = "cn.jpush.android.intent.NOTIFICATION_CLICK_ACTION_PROXY";
    public static final String ACTION_NOTIFICATION_RECEIVED_PROXY = "cn.jpush.android.intent.NOTIFICATION_RECEIVED_PROXY";
    public static final String ACTION_IN_APP_MSG_SHOW = "cn.jpush.android.intent.IN_APP_MSG_SHOW";
    public static final String ACTION_IN_APP_MSG_CLICK = "cn.jpush.android.intent.IN_APP_MSG_CLICK";
    public static final String ACTION_STATUS = "cn.jpush.android.intent.STATUS";
    public static final String ACTION_CONNECTION_CHANGE = "cn.jpush.android.intent.CONNECTION";
    public static final String ACTION_RICHPUSH_CALLBACK = "cn.jpush.android.intent.ACTION_RICHPUSH_CALLBACK";

    static {
        JCoreManager.addDispatchAction(JPushConstants.SDK_TYPE, JPushConstants.DISPATCH_ACTION_CLASS);
    }

    public static void init(Context context) {
        if (context == null) {
            return;
        }
        JPushConstants.init(context);
        ActionHelper.getInstance().init(context);
        JCoreManager.init(context);
    }

    public static void init(Context context, JPushConfig config) {
        if (context == null) {
            return;
        }
        if (config != null && config.getjAppKey() != null && !config.getjAppKey().isEmpty()) {
            JCoreStorage.setAppKey(context, config.getjAppKey());
        }
        init(context);
    }

    public static void setDebugMode(boolean enable) {
        JCoreManager.setDebugMode(enable);
    }

    public static void stopPush(Context context) {
        JCoreStorage.setPushStopped(context, true);
        JCoreTcpEngine.getInstance().stop(context);
        ActionHelper.getInstance().setPushStatusWithCallBack(context, 4, 0);
    }

    public static void resumePush(Context context) {
        JCoreStorage.setPushStopped(context, false);
        JCoreManager.init(context);
        ActionHelper.getInstance().setPushStatusWithCallBack(context, 4, 1);
    }

    public static boolean isPushStopped(Context context) {
        return JCoreStorage.isPushStopped(context);
    }

    public static String getRegistrationID(Context context) {
        return JCoreStorage.getRegistrationId(context);
    }

    public static String getUdid(Context context) {
        return JCoreStorage.getDeviceId(context);
    }

    public static void setChannel(Context context, String channel) {
        JCoreStorage.setChannel(context, channel);
    }

    public static void setLatestNotificationNumber(Context context, int maxNum) {
        if (maxNum <= 0) {
            return;
        }
        JCoreStorage.setLatestNotificationNumber(context, maxNum);
        ActionHelper.getInstance().enforceLatestNotificationLimit(context, maxNum);
    }

    public static void setPushTime(Context context, Set<Integer> days, int startHour, int endHour) {
        JCoreStorage.setPushTime(context, days, startHour, endHour);
    }

    public static void setSilenceTime(Context context, int startHour, int startMinute, int endHour, int endMinute) {
        JCoreStorage.setSilenceTime(context, startHour, startMinute, endHour, endMinute);
    }

    public static void setDefaultPushNotificationBuilder(DefaultPushNotificationBuilder builder) {
    }

    public static void setPushNotificationBuilder(Integer id, DefaultPushNotificationBuilder builder) {
    }

    public static Set<String> filterValidTags(Set<String> tags) {
        if (tags == null) {
            return new HashSet<String>();
        }
        Set<String> valid = new HashSet<String>();
        for (String tag : tags) {
            if (tag != null && !tag.isEmpty() && tag.length() <= 40) {
                valid.add(tag);
            }
        }
        return valid;
    }

    public static String getStringTags(Set<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String tag : tags) {
            if (tag == null) {
                continue;
            }
            if (!first) {
                sb.append(',');
            }
            first = false;
            sb.append(tag);
        }
        return sb.toString();
    }

    @Deprecated
    public static void setAliasAndTags(Context context, String alias, Set<String> tags) {
        setAliasAndTags(context, alias, tags, null);
    }

    @Deprecated
    public static void setAliasAndTags(Context context, String alias, Set<String> tags, TagAliasCallback callback) {
        ActionHelper.getInstance().setAliasAndTagsWithCallBack(context, alias, tags, callback, 0, 0);
    }

    @Deprecated
    public static void setTags(Context context, Set<String> tags, TagAliasCallback callback) {
        setAliasAndTags(context, null, tags, callback);
    }

    @Deprecated
    public static void setAlias(Context context, String alias, TagAliasCallback callback) {
        setAliasAndTags(context, alias, null, callback);
    }

    public static void setAlias(Context context, int sequence, String alias) {
        ActionHelper.getInstance().setAliasWithCallBack(context, sequence, alias, 2, 2);
    }

    public static void deleteAlias(Context context, int sequence) {
        ActionHelper.getInstance().setAliasWithCallBack(context, sequence, null, 2, 3);
    }

    public static void getAlias(Context context, int sequence) {
        ActionHelper.getInstance().setAliasWithCallBack(context, sequence, null, 2, 5);
    }

    public static void setTags(Context context, int sequence, Set<String> tags) {
        ActionHelper.getInstance().setTagsWithCallBack(context, sequence, tags, 1, 2);
    }

    public static void addTags(Context context, int sequence, Set<String> tags) {
        ActionHelper.getInstance().setTagsWithCallBack(context, sequence, tags, 1, 1);
    }

    public static void deleteTags(Context context, int sequence, Set<String> tags) {
        ActionHelper.getInstance().setTagsWithCallBack(context, sequence, tags, 1, 3);
    }

    public static void cleanTags(Context context, int sequence) {
        ActionHelper.getInstance().setTagsWithCallBack(context, sequence, null, 1, 4);
    }

    public static void getAllTags(Context context, int sequence) {
        ActionHelper.getInstance().setTagsWithCallBack(context, sequence, null, 1, 5);
    }

    public static void checkTagBindState(Context context, int sequence, String tag) {
        HashSet<String> tags = new HashSet<String>();
        if (tag != null && !tag.isEmpty()) {
            tags.add(tag);
        }
        ActionHelper.getInstance().setTagsWithCallBack(context, sequence, tags, 1, 6);
    }

    public static void setMobileNumber(Context context, int sequence, String mobileNumber) {
        ActionHelper.getInstance().setMobileNumberWithCallBack(context, sequence, mobileNumber);
    }

    public static void setProperties(Context context, int sequence, Map<String, Object> properties) {
        ActionHelper.getInstance().setPropertyWithCallBack(context, sequence, properties, 3, 1);
    }

    public static void deleteProperties(Context context, int sequence, Map<String, Object> properties) {
        ActionHelper.getInstance().setPropertyWithCallBack(context, sequence, properties, 3, 2);
    }

    public static void cleanProperties(Context context, int sequence) {
        ActionHelper.getInstance().setPropertyWithCallBack(context, sequence, null, 3, 3);
    }

    public static void setBadgeNumber(Context context, int badgeCount) {
    }

    public static void addLocalNotification(Context context, JPushLocalNotification notification) {
        if (context == null || notification == null) {
            return;
        }
        ActionHelper.getInstance().dispatchLocalNotification(context, notification);
    }

    public static void removeLocalNotification(Context context, long notificationId) {
        clearNotificationById(context, (int) notificationId);
    }

    public static void clearLocalNotifications(Context context) {
        clearAllNotifications(context);
    }

    public static void clearNotificationById(Context context, int notificationId) {
        ActionHelper.getInstance().removeActiveNotificationId(notificationId);
        if (context == null) {
            return;
        }
        try {
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) {
                nm.cancel(notificationId);
            }
        } catch (Throwable ignored) {
        }
    }

    public static void clearAllNotifications(Context context) {
        ActionHelper.getInstance().clearActiveNotificationIds();
        if (context == null) {
            return;
        }
        try {
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) {
                nm.cancelAll();
            }
        } catch (Throwable ignored) {
        }
    }

    public static void reportNotificationOpened(Context context, String msgId) {
    }

    public static void reportNotificationOpened(Context context, String msgId, byte platform) {
    }

    public static void reportNotificationOpened(Context context, String msgId, byte platform, String extra) {
    }

    public static void reportMessageOpened(Context context, String msgId, byte platform) {
    }

    public static void onResume(Context context) {
    }

    public static void onPause(Context context) {
    }

    public static void onFragmentResume(Context context, String fragmentName) {
    }

    public static void onFragmentPause(Context context, String fragmentName) {
    }

    public static void onKillProcess(Context context) {
    }

    public static void setStatisticsEnable(boolean enable) {
    }

    public static void setStatisticsSessionTimeout(long timeoutSeconds) {
    }

    public static void initCrashHandler(Context context) {
    }

    public static void stopCrashHandler(Context context) {
    }

    public static void setGeofenceInterval(Context context, long intervalMinutes) {
    }

    public static void setMaxGeofenceNumber(Context context, int maxNumber) {
    }

    public static void deleteGeofence(Context context, String geofenceId) {
    }

    public static void setGeofenceEnable(Context context, boolean enable) {
    }

    public static void setLinkMergeEnable(Context context, boolean enable) {
    }

    public static void setHbPeriodEnable(Context context, boolean enable) {
    }

    public static void setSmartPushEnable(Context context, boolean enable) {
    }

    public static void setDataInsightsEnable(Context context, boolean enable) {
    }

    public static void setPowerSaveMode(Context context, boolean enable) {
    }

    public static void setCollectControl(Context context, JPushCollectControl control) {
    }

    public static void setLbsEnable(Context context, boolean enable) {
    }

    public static void setDaemonEnable(Context context, boolean enable) {
    }

    public static void setAutoWakeupEnable(Context context, boolean enable) {
    }

    public static void setThirdToken(Context context, String token) {
        setThirdToken(context, token, (byte) 0, false);
    }

    public static void setThirdToken(Context context, String token, byte platform, boolean unbind) {
        if (token != null && !token.isEmpty()) {
            ActionHelper.getInstance().setThirdTokenWithCallBack(context, token, platform, unbind);
        }
    }

    public static void setUserId(Context context, String userId) {
    }

    public static void registerByKey(Context context, Bundle config) {
    }

    public static void pullInAppMessage(Context context, int sequence) {
    }

    public static void pullInAppMessage(Context context, int sequence, int type) {
    }

    public static void pullInAppMessage(Context context, int sequence, String adSlot) {
    }

    public static void pullInAppMessageWithParams(Context context, InAppSlotParams params) {
    }

    public static void triggerInAppMsgByEvent(Context context, int sequence, String eventName) {
    }

    public static void checkSupportVas(Context context, int type) {
    }

    public static void setNotificationCallBackEnable(Context context, boolean enable) {
    }

    public static void si(Context context, Bundle bundle) {
    }

    public static void attachBaseContext(Context context) {
    }

    public static JSONObject getSdkVersionJson() {
        return new JSONObject();
    }

    public static int isNotificationEnabled(Context context) {
        if (context == null) {
            return -1;
        }
        try {
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null && Build.VERSION.SDK_INT >= 24) {
                return nm.areNotificationsEnabled() ? 1 : 0;
            }
            return 1;
        } catch (Throwable ignored) {
            return -1;
        }
    }

    public static void goToAppNotificationSettings(Context context) {
        if (context == null) {
            return;
        }
        try {
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT >= 26) {
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            } else {
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.fromParts("package", context.getPackageName(), null));
            }
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        } catch (Throwable ignored) {
        }
    }

    public static void requestPermission(Context context) {
        JCoreManager.requestPermission(context);
    }

    public static void requestRequiredPermission(Activity activity) {
        JCoreManager.requestPermission(activity);
    }

    public static class ErrorCode {
        public static int SUCCESS = 0;
        public static int UNKNOWN_ERROR = 6001;
        public static int TIMEOUT = 6002;
        public static int INVALID_ALIAS = 6003;
        public static int TOO_LONG_ALIAS = 6004;
        public static int INVALID_TAGS = 6005;
        public static int TOO_LONG_TAGS = 6006;
        public static int TOO_MANY_TAGS = 6007;
        public static int TOO_LONG_ENTRIES = 6008;
        public static int TOO_MANY_ELEMENTS = 6011;
        public static int INVOKE_TOO_SOON = 6012;
        public static int INVALID_JSON = 6010;
        public static int PUSH_STOPPED = 6012;
        public static int INVALID_MOBILE_NUMBER = 6023;
        public static int INTERNAL_ERROR = 6024;
    }
}
