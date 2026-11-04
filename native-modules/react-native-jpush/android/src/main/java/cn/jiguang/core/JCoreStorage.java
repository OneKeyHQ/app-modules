package cn.jiguang.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONObject;

public final class JCoreStorage {
    private static final String PREFS_NAME = "cn.jiguang.sdk.clean.prefs";
    private static final String KEY_APP_KEY = "app_key";
    private static final String KEY_CHANNEL = "channel";
    private static final String KEY_UID = "uid";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REG_ID = "registration_id";
    private static final String KEY_DEVICE_ID = "device_id";
    private static final String KEY_SERVER_REGISTERED = "server_registered";
    private static final String KEY_PUSH_STOPPED = "push_stopped";
    private static final String KEY_COLLECTION_AUTH = "collection_auth";
    private static final String KEY_DEBUG_MODE = "debug_mode";
    private static final String KEY_TAGS = "tags";
    private static final String KEY_ALIAS = "alias";
    private static final String KEY_PROPERTIES = "properties_json";
    private static final String KEY_MOBILE_NUMBER = "mobile_number";
    private static final String KEY_CONNECTED = "tcp_connected";
    private static final String KEY_COUNTRY_CODE = "country_code";
    private static final String KEY_LOCAL_LOG_ENABLED = "local_log_enabled";
    private static final String KEY_SIS_RAW_JSON = "sis_raw_json";
    private static final String KEY_SIS_IPS = "sis_ips_list";
    private static final String KEY_SIS_SSL_IPS = "sis_ssl_ips_list";
    private static final String KEY_SIS_UDP_IPS = "sis_udp_ips_list";
    private static final String KEY_SIS_HTTP_REPORT = "sis_http_report_list";
    private static final String KEY_SIS_HTTPS_REPORT = "sis_https_report_list";
    private static final String KEY_SIS_TCP_REPORT = "sis_tcp_report_list";
    private static final String KEY_SIS_CERT_VER = "sis_cert_ver";
    private static final String KEY_SIS_DATA_REPORT = "sis_data_report";
    private static final String KEY_FAILED_IPS = "sis_failed_ips";
    private static final String KEY_LAST_GOOD_IP = "sis_last_good_ip";
    private static final String KEY_THIRD_TOKEN_PREFIX = "third_token_";
    private static final String KEY_LATEST_NOTIFICATION_NUM = "latest_notification_num";
    private static final String KEY_PUSH_TIME_CONFIGURED = "push_time_configured";
    private static final String KEY_PUSH_TIME_DAYS = "push_time_days";
    private static final String KEY_PUSH_TIME_START_HOUR = "push_time_start_hour";
    private static final String KEY_PUSH_TIME_END_HOUR = "push_time_end_hour";
    private static final String KEY_SILENCE_TIME_ENABLED = "silence_time_enabled";
    private static final String KEY_SILENCE_START_HOUR = "silence_start_hour";
    private static final String KEY_SILENCE_START_MIN = "silence_start_min";
    private static final String KEY_SILENCE_END_HOUR = "silence_end_hour";
    private static final String KEY_SILENCE_END_MIN = "silence_end_min";

    private static final AtomicInteger REQUEST_ID = new AtomicInteger(100);
    private static final Map<String, Object> MEMORY_FALLBACK = new ConcurrentHashMap<String, Object>();
    private static final StringBuilder LOCAL_LOG_BUFFER = new StringBuilder();
    private static volatile Context sAppContext;
    private static volatile boolean sDebugMode = false;

    private JCoreStorage() {
    }

    private static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static void setAppContext(Context context) {
        if (context != null && sAppContext == null) {
            try {
                Context app = context.getApplicationContext();
                sAppContext = app != null ? app : context;
            } catch (Throwable ignored) {
                sAppContext = context;
            }
        }
    }

    public static Context getAppContext(Context fallback) {
        if (sAppContext != null) {
            return sAppContext;
        }
        if (fallback != null) {
            setAppContext(fallback);
            return sAppContext;
        }
        return null;
    }

    private static SharedPreferences prefs(Context context) {
        Context resolved = getAppContext(context);
        if (resolved == null) {
            return null;
        }
        try {
            return resolved.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        } catch (Throwable ignored) {
            return null;
        }
    }

    public static String getSpValue(Context context, String key, String defaultVal) {
        if (isEmpty(key)) {
            return defaultVal;
        }
        return getStringPref(context, key, defaultVal);
    }

    public static void putSpValue(Context context, String key, String value) {
        if (isEmpty(key)) {
            return;
        }
        putStringPref(context, key, value);
    }

    private static String getStringPref(Context context, String key, String defaultVal) {
        Object mem = MEMORY_FALLBACK.get(key);
        if (mem instanceof String) {
            return (String) mem;
        }
        SharedPreferences sp = prefs(context);
        if (sp != null) {
            try {
                String v = sp.getString(key, null);
                if (v != null) {
                    MEMORY_FALLBACK.put(key, v);
                    return v;
                }
            } catch (Throwable ignored) {
            }
        }
        return defaultVal;
    }

    private static void putStringPref(Context context, String key, String value) {
        String safe = value != null ? value : "";
        MEMORY_FALLBACK.put(key, safe);
        SharedPreferences sp = prefs(context);
        if (sp != null) {
            try {
                sp.edit().putString(key, safe).apply();
            } catch (Throwable ignored) {
            }
        }
    }

    private static long getLongPref(Context context, String key, long defaultVal) {
        Object mem = MEMORY_FALLBACK.get(key);
        if (mem instanceof Number) {
            return ((Number) mem).longValue();
        }
        SharedPreferences sp = prefs(context);
        if (sp != null) {
            try {
                long v = sp.getLong(key, Long.MIN_VALUE);
                if (v != Long.MIN_VALUE) {
                    MEMORY_FALLBACK.put(key, v);
                    return v;
                }
            } catch (Throwable ignored) {
            }
        }
        return defaultVal;
    }

    private static void putLongPref(Context context, String key, long value) {
        MEMORY_FALLBACK.put(key, value);
        SharedPreferences sp = prefs(context);
        if (sp != null) {
            try {
                sp.edit().putLong(key, value).apply();
            } catch (Throwable ignored) {
            }
        }
    }

    private static boolean getBooleanPref(Context context, String key, boolean defaultVal) {
        Object mem = MEMORY_FALLBACK.get(key);
        if (mem instanceof Boolean) {
            return (Boolean) mem;
        }
        SharedPreferences sp = prefs(context);
        if (sp != null) {
            try {
                return sp.getBoolean(key, defaultVal);
            } catch (Throwable ignored) {
            }
        }
        return defaultVal;
    }

    private static void putBooleanPref(Context context, String key, boolean value) {
        MEMORY_FALLBACK.put(key, value);
        SharedPreferences sp = prefs(context);
        if (sp != null) {
            try {
                sp.edit().putBoolean(key, value).apply();
            } catch (Throwable ignored) {
            }
        }
    }

    public static int nextRid() {
        int val = REQUEST_ID.incrementAndGet();
        if (val <= 0 || val >= 65535) {
            REQUEST_ID.set(100);
            return 100;
        }
        return val;
    }

    public static void setDebugMode(boolean debug) {
        sDebugMode = debug;
        putBooleanPref(null, KEY_DEBUG_MODE, debug);
    }

    public static boolean isDebugMode() {
        return getBooleanPref(null, KEY_DEBUG_MODE, sDebugMode);
    }

    public static void setCollectionAuth(Context context, boolean auth) {
        putBooleanPref(context, KEY_COLLECTION_AUTH, auth);
    }

    public static boolean isCollectionAuth(Context context) {
        return getBooleanPref(context, KEY_COLLECTION_AUTH, true);
    }

    public static String getAppKey(Context context) {
        String cached = getStringPref(context, KEY_APP_KEY, "");
        if (!isEmpty(cached)) {
            return cached;
        }
        Context resolved = getAppContext(context);
        if (resolved != null) {
            try {
                ApplicationInfo info = resolved.getPackageManager()
                    .getApplicationInfo(resolved.getPackageName(), PackageManager.GET_META_DATA);
                Bundle meta = info != null ? info.metaData : null;
                if (meta != null) {
                    String key = meta.getString("JPUSH_APPKEY");
                    if (!isEmpty(key)) {
                        if (key.contains("=")) {
                            key = key.substring(key.indexOf('=') + 1).trim();
                        }
                        putStringPref(context, KEY_APP_KEY, key);
                        return key;
                    }
                }
            } catch (Throwable ignored) {
            }
        }
        return "";
    }

    public static void setAppKey(Context context, String appKey) {
        if (!isEmpty(appKey)) {
            putStringPref(context, KEY_APP_KEY, appKey);
        }
    }

    public static String getChannel(Context context) {
        String cached = getStringPref(context, KEY_CHANNEL, "");
        if (!isEmpty(cached)) {
            return cached;
        }
        Context resolved = getAppContext(context);
        if (resolved != null) {
            try {
                ApplicationInfo info = resolved.getPackageManager()
                    .getApplicationInfo(resolved.getPackageName(), PackageManager.GET_META_DATA);
                Bundle meta = info != null ? info.metaData : null;
                if (meta != null) {
                    String channel = meta.getString("JPUSH_CHANNEL");
                    if (!isEmpty(channel)) {
                        return channel;
                    }
                }
            } catch (Throwable ignored) {
            }
        }
        return "developer-default";
    }

    public static void setChannel(Context context, String channel) {
        if (channel != null) {
            putStringPref(context, KEY_CHANNEL, channel);
        }
    }

    public static void setCountryCode(Context context, String countryCode) {
        putStringPref(context, KEY_COUNTRY_CODE, countryCode != null ? countryCode : "");
    }

    public static String getCountryCode(Context context) {
        return getStringPref(context, KEY_COUNTRY_CODE, "");
    }

    public static void enableSDKLocalLog(Context context, boolean enable, boolean uploadToServer) {
        putBooleanPref(context, KEY_LOCAL_LOG_ENABLED, enable);
        if (enable) {
            appendLocalLog("SDKLocalLog enabled (upload=" + uploadToServer + ")");
        }
    }

    public static synchronized void appendLocalLog(String line) {
        if (line != null && !line.isEmpty()) {
            if (LOCAL_LOG_BUFFER.length() > 0) {
                LOCAL_LOG_BUFFER.append('\n');
            }
            LOCAL_LOG_BUFFER.append(line);
        }
    }

    public static synchronized String readNewLogs(Context context) {
        String logs = LOCAL_LOG_BUFFER.toString();
        LOCAL_LOG_BUFFER.setLength(0);
        return logs;
    }

    public static synchronized void ensureRegistration(Context context) {
        String deviceId = getStringPref(context, KEY_DEVICE_ID, "");
        if (!isEmpty(deviceId)) {
            return;
        }

        Context resolved = getAppContext(context);
        String seed = "";
        if (resolved != null) {
            try {
                seed = Settings.Secure.getString(resolved.getContentResolver(), Settings.Secure.ANDROID_ID);
            } catch (Throwable ignored) {
            }
        }
        if (isEmpty(seed)) {
            seed = UUID.randomUUID().toString();
        }
        String hash = JCoreCrypto.md5Hex(getAppKey(context) + ":" + seed);
        deviceId = hash.substring(0, Math.min(24, hash.length()));
        putStringPref(context, KEY_DEVICE_ID, deviceId);
    }

    public static synchronized void updateRegistration(Context context, long uid, String password, String regId) {
        updateRegistration(context, uid, password, regId, null);
    }

    public static synchronized void updateRegistration(
        Context context,
        long uid,
        String password,
        String regId,
        String deviceId
    ) {
        if (uid > 0L) {
            putLongPref(context, KEY_UID, uid);
        }
        if (!isEmpty(password)) {
            putStringPref(context, KEY_PASSWORD, password);
        }
        if (!isEmpty(regId)) {
            putStringPref(context, KEY_REG_ID, regId);
        }
        if (!isEmpty(deviceId)) {
            putStringPref(context, KEY_DEVICE_ID, deviceId);
        }
    }

    public static synchronized void clearRegistration(Context context) {
        putBooleanPref(context, KEY_SERVER_REGISTERED, false);
        putLongPref(context, KEY_UID, 0L);
        putStringPref(context, KEY_PASSWORD, "");
        putStringPref(context, KEY_REG_ID, "");
    }

    public static boolean isServerRegistered(Context context) {
        return getBooleanPref(context, KEY_SERVER_REGISTERED, false);
    }

    public static void setServerRegistered(Context context, boolean registered) {
        putBooleanPref(context, KEY_SERVER_REGISTERED, registered);
    }

    public static long getUid(Context context) {
        return getLongPref(context, KEY_UID, 0L);
    }

    public static String getPassword(Context context) {
        return getStringPref(context, KEY_PASSWORD, "");
    }

    public static String getRegistrationId(Context context) {
        return getStringPref(context, KEY_REG_ID, "");
    }

    public static String getDeviceId(Context context) {
        ensureRegistration(context);
        return getStringPref(context, KEY_DEVICE_ID, "");
    }

    public static boolean isPushStopped(Context context) {
        return getBooleanPref(context, KEY_PUSH_STOPPED, false);
    }

    public static void setPushStopped(Context context, boolean stopped) {
        putBooleanPref(context, KEY_PUSH_STOPPED, stopped);
    }

    public static boolean isConnected(Context context) {
        return getBooleanPref(context, KEY_CONNECTED, false);
    }

    public static void setConnected(Context context, boolean connected) {
        putBooleanPref(context, KEY_CONNECTED, connected);
    }

    public static void saveSisConfig(Context context, String rawJson) {
        if (isEmpty(rawJson)) {
            return;
        }
        putStringPref(context, KEY_SIS_RAW_JSON, rawJson);
        saveCsvSet(context, KEY_SIS_IPS, JCoreWireCodec.extractJsonStringArray(rawJson, "ips"));
        saveCsvSet(context, KEY_SIS_SSL_IPS, JCoreWireCodec.extractJsonStringArray(rawJson, "ssl_ips"));
        saveCsvSet(context, KEY_SIS_UDP_IPS, JCoreWireCodec.extractJsonStringArray(rawJson, "sis_ips"));
        saveCsvSet(context, KEY_SIS_HTTP_REPORT, JCoreWireCodec.extractJsonStringArray(rawJson, "http_report"));
        saveCsvSet(context, KEY_SIS_HTTPS_REPORT, JCoreWireCodec.extractJsonStringArray(rawJson, "https_report"));
        saveCsvSet(context, KEY_SIS_TCP_REPORT, JCoreWireCodec.extractJsonStringArray(rawJson, "tcp_report"));
        putLongPref(context, KEY_SIS_CERT_VER, JCoreWireCodec.extractJsonInt(rawJson, "cert_ver", 1));
        putBooleanPref(context, KEY_SIS_DATA_REPORT, rawJson.contains("\"data_report\":true") || rawJson.contains("\"data_report\":1"));
    }

    public static void resetSisCache(Context context) {
        putStringPref(context, KEY_SIS_RAW_JSON, "");
        putStringPref(context, KEY_SIS_IPS, "");
        putStringPref(context, KEY_SIS_SSL_IPS, "");
        putStringPref(context, KEY_SIS_UDP_IPS, "");
        putStringPref(context, KEY_LAST_GOOD_IP, "");
    }

    public static List<String> getSisSslIps(Context context) {
        return loadCsvList(context, KEY_SIS_SSL_IPS);
    }

    public static List<String> getSisIps(Context context) {
        return loadCsvList(context, KEY_SIS_IPS);
    }

    public static List<String> getSisUdpIps(Context context) {
        return loadCsvList(context, KEY_SIS_UDP_IPS);
    }

    public static List<String> getSisHttpReportIps(Context context) {
        return loadCsvList(context, KEY_SIS_HTTP_REPORT);
    }

    public static List<String> getSisHttpsReportIps(Context context) {
        return loadCsvList(context, KEY_SIS_HTTPS_REPORT);
    }

    public static int getSisCertVer(Context context) {
        return (int) getLongPref(context, KEY_SIS_CERT_VER, 1L);
    }

    public static boolean getSisDataReport(Context context) {
        return getBooleanPref(context, KEY_SIS_DATA_REPORT, false);
    }

    public static void setLastGoodIp(Context context, String ipPort) {
        putStringPref(context, KEY_LAST_GOOD_IP, ipPort != null ? ipPort : "");
    }

    public static String getLastGoodIp(Context context) {
        return getStringPref(context, KEY_LAST_GOOD_IP, "");
    }

    public static synchronized void recordFailedIp(Context context, String ipPort) {
        if (isEmpty(ipPort)) {
            return;
        }
        Set<String> set = new LinkedHashSet<String>(loadCsvList(context, KEY_FAILED_IPS));
        set.add(ipPort);
        saveCsvSet(context, KEY_FAILED_IPS, set);
    }

    public static synchronized List<String> getFailedIps(Context context) {
        return loadCsvList(context, KEY_FAILED_IPS);
    }

    public static synchronized void clearFailedIps(Context context) {
        putStringPref(context, KEY_FAILED_IPS, "");
    }

    private static void saveCsvSet(Context context, String key, Collection<String> items) {
        if (items == null || items.isEmpty()) {
            putStringPref(context, key, "");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String item : items) {
            if (isEmpty(item)) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(item.trim());
        }
        putStringPref(context, key, sb.toString());
    }

    private static List<String> loadCsvList(Context context, String key) {
        String raw = getStringPref(context, key, "");
        if (isEmpty(raw)) {
            return Collections.emptyList();
        }
        String[] parts = raw.split(",");
        List<String> out = new ArrayList<String>(parts.length);
        for (String p : parts) {
            String trimmed = p.trim();
            if (!trimmed.isEmpty()) {
                out.add(trimmed);
            }
        }
        return out;
    }

    public static void saveThirdToken(Context context, byte platform, String token) {
        putStringPref(context, KEY_THIRD_TOKEN_PREFIX + (platform & 0xFF), token != null ? token : "");
    }

    public static String getThirdToken(Context context, byte platform) {
        return getStringPref(context, KEY_THIRD_TOKEN_PREFIX + (platform & 0xFF), "");
    }

    @SuppressWarnings("unchecked")
    public static Set<String> getTags(Context context) {
        Object mem = MEMORY_FALLBACK.get(KEY_TAGS);
        if (mem instanceof Set) {
            return new HashSet<String>((Set<String>) mem);
        }
        SharedPreferences sp = prefs(context);
        if (sp != null) {
            try {
                Set<String> saved = sp.getStringSet(KEY_TAGS, null);
                if (saved != null) {
                    return new HashSet<String>(saved);
                }
            } catch (Throwable ignored) {
            }
        }
        return new HashSet<String>();
    }

    public static void saveTags(Context context, Set<String> tags) {
        Set<String> copy = tags != null ? new HashSet<String>(tags) : Collections.<String>emptySet();
        MEMORY_FALLBACK.put(KEY_TAGS, copy);
        SharedPreferences sp = prefs(context);
        if (sp != null) {
            try {
                sp.edit().putStringSet(KEY_TAGS, copy).apply();
            } catch (Throwable ignored) {
            }
        }
    }

    public static String getAlias(Context context) {
        return getStringPref(context, KEY_ALIAS, "");
    }

    public static void saveAlias(Context context, String alias) {
        putStringPref(context, KEY_ALIAS, alias != null ? alias : "");
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getProperties(Context context) {
        Object mem = MEMORY_FALLBACK.get(KEY_PROPERTIES);
        if (mem instanceof Map) {
            return new HashMap<String, Object>((Map<String, Object>) mem);
        }
        Map<String, Object> result = new HashMap<String, Object>();
        String raw = getStringPref(context, KEY_PROPERTIES, "");
        if (isEmpty(raw)) {
            return result;
        }
        try {
            JSONObject json = new JSONObject(raw);
            Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                String k = keys.next();
                result.put(k, json.opt(k));
            }
        } catch (Throwable ignored) {
        }
        return result;
    }

    public static void saveProperties(Context context, Map<String, Object> props) {
        if (props == null || props.isEmpty()) {
            MEMORY_FALLBACK.remove(KEY_PROPERTIES);
            SharedPreferences sp = prefs(context);
            if (sp != null) {
                try {
                    sp.edit().remove(KEY_PROPERTIES).apply();
                } catch (Throwable ignored) {
                }
            }
            return;
        }
        MEMORY_FALLBACK.put(KEY_PROPERTIES, new HashMap<String, Object>(props));
        try {
            JSONObject json = new JSONObject(props);
            putStringPref(context, KEY_PROPERTIES, json.toString());
        } catch (Throwable ignored) {
        }
    }

    public static String getMobileNumber(Context context) {
        return getStringPref(context, KEY_MOBILE_NUMBER, "");
    }

    public static void saveMobileNumber(Context context, String mobileNumber) {
        putStringPref(context, KEY_MOBILE_NUMBER, mobileNumber != null ? mobileNumber : "");
    }

    public static void setLatestNotificationNumber(Context context, int maxNum) {
        if (maxNum > 0) {
            putLongPref(context, KEY_LATEST_NOTIFICATION_NUM, maxNum);
        }
    }

    public static int getLatestNotificationNumber(Context context) {
        long val = getLongPref(context, KEY_LATEST_NOTIFICATION_NUM, 5L);
        return val > 0L ? (int) val : 5;
    }

    public static void setPushTime(Context context, Set<Integer> days, int startHour, int endHour) {
        if (days == null) {
            putBooleanPref(context, KEY_PUSH_TIME_CONFIGURED, false);
            putStringPref(context, KEY_PUSH_TIME_DAYS, "");
            return;
        }
        if (startHour < 0 || startHour > 23 || endHour < 0 || endHour > 23 || endHour < startHour) {
            return;
        }
        List<String> validDays = new ArrayList<String>();
        for (Integer day : days) {
            if (day != null && day >= 0 && day <= 6) {
                validDays.add(String.valueOf(day));
            }
        }
        putBooleanPref(context, KEY_PUSH_TIME_CONFIGURED, true);
        saveCsvSet(context, KEY_PUSH_TIME_DAYS, validDays);
        putLongPref(context, KEY_PUSH_TIME_START_HOUR, startHour);
        putLongPref(context, KEY_PUSH_TIME_END_HOUR, endHour);
    }

    public static boolean isPushTimeAllowed(Context context, int dayOfWeekZeroBasedSunday, int hourOfDay) {
        if (!getBooleanPref(context, KEY_PUSH_TIME_CONFIGURED, false)) {
            return true;
        }
        List<String> allowedDays = loadCsvList(context, KEY_PUSH_TIME_DAYS);
        if (!allowedDays.contains(String.valueOf(dayOfWeekZeroBasedSunday))) {
            return false;
        }
        int startHour = (int) getLongPref(context, KEY_PUSH_TIME_START_HOUR, 0L);
        int endHour = (int) getLongPref(context, KEY_PUSH_TIME_END_HOUR, 23L);
        return hourOfDay >= startHour && hourOfDay <= endHour;
    }

    public static void setSilenceTime(
        Context context,
        int startHour,
        int startMinute,
        int endHour,
        int endMinute
    ) {
        if (startHour == 0 && startMinute == 0 && endHour == 0 && endMinute == 0) {
            putBooleanPref(context, KEY_SILENCE_TIME_ENABLED, false);
            return;
        }
        if (startHour < 0 || startHour > 23 || startMinute < 0 || startMinute > 59
            || endHour < 0 || endHour > 23 || endMinute < 0 || endMinute > 59) {
            return;
        }
        putBooleanPref(context, KEY_SILENCE_TIME_ENABLED, true);
        putLongPref(context, KEY_SILENCE_START_HOUR, startHour);
        putLongPref(context, KEY_SILENCE_START_MIN, startMinute);
        putLongPref(context, KEY_SILENCE_END_HOUR, endHour);
        putLongPref(context, KEY_SILENCE_END_MIN, endMinute);
    }

    public static boolean isInSilenceTime(Context context, int hourOfDay, int minute) {
        if (!getBooleanPref(context, KEY_SILENCE_TIME_ENABLED, false)) {
            return false;
        }
        int startHour = (int) getLongPref(context, KEY_SILENCE_START_HOUR, 0L);
        int startMin = (int) getLongPref(context, KEY_SILENCE_START_MIN, 0L);
        int endHour = (int) getLongPref(context, KEY_SILENCE_END_HOUR, 0L);
        int endMin = (int) getLongPref(context, KEY_SILENCE_END_MIN, 0L);

        int currentTotal = hourOfDay * 60 + minute;
        int startTotal = startHour * 60 + startMin;
        int endTotal = endHour * 60 + endMin;
        if (startTotal == endTotal) {
            return false;
        }
        if (startTotal < endTotal) {
            return currentTotal >= startTotal && currentTotal <= endTotal;
        }
        return currentTotal >= startTotal || currentTotal <= endTotal;
    }
}
