package cn.jiguang.plugins.push.helper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import cn.jiguang.plugins.push.JPushModule;
import cn.jiguang.plugins.push.common.JConstants;
import cn.jiguang.plugins.push.common.JLogger;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.api.NotificationCustomButton;

public class JPushHelper {

    public interface NitroEventListener {
        void onEvent(String eventName, String payloadJson);
    }

    private static final class PendingEvent {
        final String eventName;
        final WritableMap params;
        final Map<String, Object> mapParams;

        PendingEvent(String eventName, WritableMap params) {
            this.eventName = eventName;
            this.params = params;
            this.mapParams = null;
        }

        PendingEvent(String eventName, Map<String, Object> mapParams) {
            this.eventName = eventName;
            this.params = null;
            this.mapParams = mapParams != null
                ? new HashMap<String, Object>(mapParams)
                : new HashMap<String, Object>();
        }
    }

    private static final class PendingNitroEvent {
        final String eventName;
        final String payloadJson;

        PendingNitroEvent(String eventName, String payloadJson) {
            this.eventName = eventName;
            this.payloadJson = payloadJson;
        }
    }

    private static final AtomicInteger NEXT_NITRO_LISTENER_ID = new AtomicInteger(1);
    private static final Map<Integer, NitroEventListener> NITRO_LISTENERS =
        new ConcurrentHashMap<Integer, NitroEventListener>();
    private static final List<PendingEvent> PENDING_EVENTS =
        new CopyOnWriteArrayList<PendingEvent>();
    private static final List<PendingNitroEvent> PENDING_NITRO_EVENTS =
        new CopyOnWriteArrayList<PendingNitroEvent>();

    public static int registerNitroListener(NitroEventListener listener) {
        if (listener == null) {
            return 0;
        }
        int id = NEXT_NITRO_LISTENER_ID.getAndIncrement();
        NITRO_LISTENERS.put(Integer.valueOf(id), listener);
        flushPendingNitroEvents();
        return id;
    }

    public static void unregisterNitroListener(int id) {
        if (id <= 0) {
            return;
        }
        NITRO_LISTENERS.remove(Integer.valueOf(id));
    }

    public static void clearNitroListeners() {
        NITRO_LISTENERS.clear();
        PENDING_NITRO_EVENTS.clear();
        PENDING_EVENTS.clear();
    }

    private static Set<ReactApplicationContext> getTargetContexts() {
        Set<ReactApplicationContext> targets = new LinkedHashSet<ReactApplicationContext>();
        Set<ReactApplicationContext> activeContexts = JPushModule.getActiveReactContexts();
        if (activeContexts != null) {
            for (ReactApplicationContext ctx : activeContexts) {
                if (ctx != null) {
                    targets.add(ctx);
                }
            }
        }
        if (JPushModule.reactContext != null) {
            targets.add(JPushModule.reactContext);
        }
        return targets;
    }

    private static boolean isEmpty(CharSequence s) {
        return s == null || s.length() == 0;
    }

    public static void flushPendingNitroEvents() {
        if (PENDING_NITRO_EVENTS.isEmpty() || NITRO_LISTENERS.isEmpty()) {
            return;
        }
        List<PendingNitroEvent> snapshot = new ArrayList<PendingNitroEvent>(PENDING_NITRO_EVENTS);
        PENDING_NITRO_EVENTS.clear();
        for (PendingNitroEvent pending : snapshot) {
            if (pending == null || isEmpty(pending.eventName)) {
                continue;
            }
            for (NitroEventListener listener : NITRO_LISTENERS.values()) {
                if (listener != null) {
                    try {
                        listener.onEvent(pending.eventName, pending.payloadJson);
                    } catch (Throwable inner) {
                        JLogger.e("flushPendingNitroEvents error:" + inner.getMessage());
                    }
                }
            }
        }
    }

    public static void flushPendingEvents() {
        flushPendingNitroEvents();
        if (PENDING_EVENTS.isEmpty()) {
            return;
        }
        Set<ReactApplicationContext> targets = getTargetContexts();
        if (targets.isEmpty()) {
            return;
        }
        List<PendingEvent> snapshot = new ArrayList<PendingEvent>(PENDING_EVENTS);
        PENDING_EVENTS.clear();
        for (PendingEvent pending : snapshot) {
            if (pending != null && !isEmpty(pending.eventName)) {
                int index = 0;
                int total = targets.size();
                WritableMap baseWritable = pending.params;
                if (baseWritable == null && pending.mapParams != null) {
                    try {
                        baseWritable = mapFromHashMap(pending.mapParams);
                    } catch (Throwable ignored) {
                    }
                }
                for (ReactApplicationContext ctx : targets) {
                    try {
                        WritableMap payload = (total > 1 && index > 0)
                            ? cloneWritableMap(baseWritable)
                            : baseWritable;
                        index++;
                        ctx.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                            .emit(pending.eventName, payload);
                    } catch (Throwable inner) {
                        JLogger.e("flushPendingEvents error:" + inner.getMessage());
                    }
                }
            }
        }
    }

    public static void sendEventMap(String eventName, Map<String, Object> params) {
        if (isEmpty(eventName)) {
            return;
        }
        try {
            String payloadJson = mapToJsonString(params);
            if (!NITRO_LISTENERS.isEmpty()) {
                for (NitroEventListener listener : NITRO_LISTENERS.values()) {
                    if (listener != null) {
                        try {
                            listener.onEvent(eventName, payloadJson);
                        } catch (Throwable inner) {
                            JLogger.e("sendEventMap nitro listener error:" + inner.getMessage());
                        }
                    }
                }
            } else {
                if (PENDING_NITRO_EVENTS.size() >= 64) {
                    PENDING_NITRO_EVENTS.remove(0);
                }
                PENDING_NITRO_EVENTS.add(new PendingNitroEvent(eventName, payloadJson));
            }

            Set<ReactApplicationContext> targets = getTargetContexts();
            boolean deliveredLegacy = false;
            if (!targets.isEmpty()) {
                try {
                    WritableMap writable = mapFromHashMap(
                        params != null ? params : new HashMap<String, Object>()
                    );
                    int index = 0;
                    int total = targets.size();
                    for (ReactApplicationContext ctx : targets) {
                        WritableMap payload = (total > 1 && index > 0) ? cloneWritableMap(writable) : writable;
                        index++;
                        ctx.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, payload);
                        deliveredLegacy = true;
                    }
                } catch (Throwable ignored) {
                }
            }
            if (!deliveredLegacy) {
                if (PENDING_EVENTS.size() >= 64) {
                    PENDING_EVENTS.remove(0);
                }
                PENDING_EVENTS.add(new PendingEvent(eventName, params));
            }
        } catch (Throwable throwable) {
            JLogger.e("sendEventMap error:" + throwable.getMessage());
        }
    }

    private static String mapToJsonString(Map<String, Object> params) {
        JSONObject obj = new JSONObject();
        if (params == null || params.isEmpty()) {
            return obj.toString();
        }
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getKey() != null) {
                try {
                    obj.put(entry.getKey(), convertValueForJson(entry.getValue()));
                } catch (Throwable ignored) {
                }
            }
        }
        return obj.toString();
    }

    public static void sendEvent(String eventName, WritableMap params) {
        if (isEmpty(eventName)) {
            return;
        }
        try {
            String payloadJson = readableMapToJsonObject(params).toString();
            if (!NITRO_LISTENERS.isEmpty()) {
                for (NitroEventListener listener : NITRO_LISTENERS.values()) {
                    if (listener != null) {
                        try {
                            listener.onEvent(eventName, payloadJson);
                        } catch (Throwable inner) {
                            JLogger.e("sendEvent nitro listener error:" + inner.getMessage());
                        }
                    }
                }
            } else {
                if (PENDING_NITRO_EVENTS.size() >= 64) {
                    PENDING_NITRO_EVENTS.remove(0);
                }
                PENDING_NITRO_EVENTS.add(new PendingNitroEvent(eventName, payloadJson));
            }

            Set<ReactApplicationContext> targets = getTargetContexts();
            boolean deliveredLegacy = false;
            int index = 0;
            int total = targets.size();
            for (ReactApplicationContext ctx : targets) {
                try {
                    WritableMap payload = (total > 1 && index > 0) ? cloneWritableMap(params) : params;
                    index++;
                    ctx.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, payload);
                    deliveredLegacy = true;
                } catch (Throwable inner) {
                    JLogger.e("sendEvent error:" + inner.getMessage());
                }
            }

            if (!deliveredLegacy) {
                if (PENDING_EVENTS.size() >= 64) {
                    PENDING_EVENTS.remove(0);
                }
                PENDING_EVENTS.add(new PendingEvent(eventName, cloneWritableMap(params)));
            }
        } catch (Throwable throwable) {
            JLogger.e("sendEvent error:" + throwable.getMessage());
        }
    }

    public static JSONObject readableMapToJsonObject(ReadableMap source) {
        JSONObject result = new JSONObject();
        if (source == null) {
            return result;
        }
        try {
            HashMap<String, Object> raw = source.toHashMap();
            if (raw != null) {
                for (Map.Entry<String, Object> entry : raw.entrySet()) {
                    String key = entry.getKey();
                    if (key == null) {
                        continue;
                    }
                    result.put(key, convertValueForJson(entry.getValue()));
                }
            }
        } catch (Throwable ignored) {
        }
        return result;
    }

    public static JSONArray readableArrayToJsonArray(ReadableArray source) {
        JSONArray arr = new JSONArray();
        if (source == null) {
            return arr;
        }
        try {
            for (int i = 0; i < source.size(); i++) {
                arr.put(source.getString(i));
            }
        } catch (Throwable ignored) {
        }
        return arr;
    }

    private static boolean isJsonNull(Object val) {
        return val == null || val == JSONObject.NULL || (JSONObject.NULL != null && JSONObject.NULL.equals(val));
    }

    private static Object convertValueForJson(Object val) {
        if (isJsonNull(val)) {
            return JSONObject.NULL != null ? JSONObject.NULL : null;
        }
        if (val instanceof String || val instanceof Boolean || val instanceof Number
            || val instanceof JSONObject || val instanceof JSONArray) {
            return val;
        }
        if (val instanceof ReadableMap) {
            return readableMapToJsonObject((ReadableMap) val);
        }
        if (val instanceof Map) {
            JSONObject obj = new JSONObject();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) val).entrySet()) {
                if (entry.getKey() != null) {
                    try {
                        obj.put(String.valueOf(entry.getKey()), convertValueForJson(entry.getValue()));
                    } catch (Throwable ignored) {
                    }
                }
            }
            return obj;
        }
        if (val instanceof ReadableArray) {
            return readableArrayToJsonArray((ReadableArray) val);
        }
        if (val instanceof List) {
            JSONArray arr = new JSONArray();
            for (Object item : (List<?>) val) {
                arr.put(convertValueForJson(item));
            }
            return arr;
        }
        return String.valueOf(val);
    }

    @SuppressWarnings("unchecked")
    private static WritableMap cloneWritableMap(ReadableMap source) {
        if (source == null) {
            return null;
        }
        try {
            HashMap<String, Object> raw = source.toHashMap();
            if (raw == null) {
                return (source instanceof WritableMap) ? (WritableMap) source : Arguments.createMap();
            }
            return mapFromHashMap(raw);
        } catch (Throwable ignored) {
            return (source instanceof WritableMap) ? (WritableMap) source : null;
        }
    }

    @SuppressWarnings("unchecked")
    private static WritableMap mapFromHashMap(Map<String, Object> raw) {
        WritableMap copy = Arguments.createMap();
        for (Map.Entry<String, Object> entry : raw.entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();
            if (key == null || val == null) {
                continue;
            }
            if (val instanceof String) {
                copy.putString(key, (String) val);
            } else if (val instanceof Boolean) {
                copy.putBoolean(key, ((Boolean) val).booleanValue());
            } else if (val instanceof Number) {
                copy.putInt(key, ((Number) val).intValue());
            } else if (val instanceof ReadableMap) {
                copy.putMap(key, cloneWritableMap((ReadableMap) val));
            } else if (val instanceof Map) {
                copy.putMap(key, mapFromHashMap((Map<String, Object>) val));
            } else if (val instanceof ReadableArray) {
                ReadableArray arr = (ReadableArray) val;
                WritableArray arrCopy = Arguments.createArray();
                for (int i = 0; i < arr.size(); i++) {
                    arrCopy.pushString(arr.getString(i));
                }
                copy.putArray(key, arrCopy);
            } else if (val instanceof List) {
                List<?> list = (List<?>) val;
                WritableArray arrCopy = Arguments.createArray();
                for (Object item : list) {
                    if (item != null) {
                        arrCopy.pushString(String.valueOf(item));
                    }
                }
                copy.putArray(key, arrCopy);
            } else {
                copy.putString(key, String.valueOf(val));
            }
        }
        return copy;
    }

    public static Map<String, Object> convertNotificationToMapObject(String eventType, NotificationMessage message) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(JConstants.NOTIFICATION_EVENT_TYPE, eventType != null ? eventType : "");
        if (message != null) {
            map.put(JConstants.MESSAGE_ID, message.msgId != null ? message.msgId : "");
            map.put(JConstants.TITLE, message.notificationTitle != null ? message.notificationTitle : "");
            map.put(JConstants.CONTENT, message.notificationContent != null ? message.notificationContent : "");
            convertExtrasToMap(message.notificationExtras, map);
        }
        return map;
    }

    public static Map<String, Object> convertInAppMessageToMapObject(String eventType, NotificationMessage message) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(JConstants.INAPP_MESSAGE_EVENT, eventType != null ? eventType : "");
        if (message != null) {
            map.put(JConstants.MESSAGE_ID, message.msgId != null ? message.msgId : "");
            map.put(JConstants.TITLE, message.inAppMsgTitle != null ? message.inAppMsgTitle : "");
            map.put(JConstants.CONTENT, message.inAppMsgContentBody != null ? message.inAppMsgContentBody : "");
            map.put(JConstants.INAPPCLICKACTION, message.inAppClickAction != null ? message.inAppClickAction : "");
            map.put(JConstants.INAPPEXTRAS, message.inAppExtras != null ? message.inAppExtras : "");
            map.put(JConstants.INAPPSHOWTARGET, message.inAppShowTarget != null ? message.inAppShowTarget : "");
        }
        return map;
    }

    public static Map<String, Object> convertNotificationBundleToMapObject(String eventType, Bundle bundle) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(JConstants.NOTIFICATION_EVENT_TYPE, eventType != null ? eventType : "");
        if (bundle != null) {
            map.put(JConstants.MESSAGE_ID, bundle.getString("cn.jpush.android.MSG_ID", ""));
            map.put(JConstants.TITLE, bundle.getString("cn.jpush.android.NOTIFICATION_CONTENT_TITLE", ""));
            map.put(JConstants.CONTENT, bundle.getString("cn.jpush.android.ALERT", ""));
            convertExtrasToMap(bundle.getString("cn.jpush.android.EXTRA", ""), map);
        }
        return map;
    }

    public static Map<String, Object> convertCustomMessageToMapObject(CustomMessage customMessage) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (customMessage != null) {
            map.put(JConstants.MESSAGE_ID, customMessage.messageId != null ? customMessage.messageId : "");
            map.put(JConstants.TITLE, customMessage.title != null ? customMessage.title : "");
            map.put(JConstants.CONTENT, customMessage.message != null ? customMessage.message : "");
            convertExtrasToMap(customMessage.extra, map);
        }
        return map;
    }

    public static Map<String, Object> convertNotificationCustomButtonToMapObject(
        NotificationCustomButton notificationCustomButton
    ) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (notificationCustomButton != null) {
            map.put("msgId", notificationCustomButton.a != null ? notificationCustomButton.a : "");
            map.put("platform", Integer.valueOf(notificationCustomButton.b));
            map.put("name", notificationCustomButton.c != null ? notificationCustomButton.c : "");
            map.put("actionType", Integer.valueOf(notificationCustomButton.d));
            map.put("action", notificationCustomButton.e != null ? notificationCustomButton.e : "");
            map.put("data", notificationCustomButton.f != null ? notificationCustomButton.f : "");
        }
        return map;
    }

    public static Map<String, Object> convertJPushMessageToMapObject(int type, JPushMessage message) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (message == null) {
            return map;
        }
        map.put(JConstants.CODE, Integer.valueOf(message.getErrorCode()));
        map.put(JConstants.SEQUENCE, Integer.valueOf(message.getSequence()));
        switch (type) {
            case 1:
                Set<String> tags = message.getTags();
                List<String> tagsList = new ArrayList<String>();
                if (tags == null || tags.isEmpty()) {
                    JLogger.d("tags is empty");
                } else {
                    for (String tag : tags) {
                        if (tag != null) {
                            tagsList.add(tag);
                        }
                    }
                }
                map.put(JConstants.TAGS, tagsList);
                break;
            case 2:
                map.put(JConstants.TAG_ENABLE, Boolean.valueOf(message.getTagCheckStateResult()));
                map.put(JConstants.TAG, message.getCheckTag() != null ? message.getCheckTag() : "");
                break;
            case 3:
                map.put(JConstants.ALIAS, message.getAlias() != null ? message.getAlias() : "");
                break;
            case 4:
                Map<String, Object> prosMap = new HashMap<String, Object>();
                if (message.getPros() != null) {
                    for (Map.Entry<String, Object> entry : message.getPros().entrySet()) {
                        if (entry.getKey() != null && entry.getValue() != null) {
                            prosMap.put(entry.getKey(), String.valueOf(entry.getValue()));
                        }
                    }
                }
                map.put(JConstants.PROPERTIES, prosMap);
                break;
        }
        return map;
    }

    public static void convertExtrasToMap(String extras, Map<String, Object> targetMap) {
        if (targetMap == null || isEmpty(extras) || "{}".equals(extras)) {
            return;
        }
        try {
            Map<String, Object> extrasMap = new HashMap<String, Object>();
            JSONObject jsonObject = new JSONObject(extras);
            Iterator<String> it = jsonObject.keys();
            while (it.hasNext()) {
                String key = it.next();
                if (key == null) {
                    continue;
                }
                Object val = jsonObject.opt(key);
                String value = !isJsonNull(val) ? String.valueOf(val) : "";
                extrasMap.put(key, value);
            }
            targetMap.put(JConstants.EXTRAS, extrasMap);
        } catch (Throwable throwable) {
            JLogger.w("convertExtrasToMap error");
        }
    }

    public static WritableMap convertNotificationToMap(String eventType, NotificationMessage message) {
        WritableMap writableMap = Arguments.createMap();
        writableMap.putString(JConstants.NOTIFICATION_EVENT_TYPE, eventType);
        if (message != null) {
            writableMap.putString(JConstants.MESSAGE_ID, message.msgId);
            writableMap.putString(JConstants.TITLE, message.notificationTitle);
            writableMap.putString(JConstants.CONTENT, message.notificationContent);
            convertExtras(message.notificationExtras, writableMap);
        }
        return writableMap;
    }
    public static WritableMap convertInAppMessageToMap(String eventType, NotificationMessage message) {
        WritableMap writableMap = Arguments.createMap();
        writableMap.putString(JConstants.INAPP_MESSAGE_EVENT, eventType);
        if (message != null) {
            writableMap.putString(JConstants.MESSAGE_ID, message.msgId);
            writableMap.putString(JConstants.TITLE, message.inAppMsgTitle);
            writableMap.putString(JConstants.CONTENT, message.inAppMsgContentBody);
            writableMap.putString(JConstants.INAPPCLICKACTION, message.inAppClickAction);
            writableMap.putString(JConstants.INAPPEXTRAS, message.inAppExtras);
            writableMap.putString(JConstants.INAPPSHOWTARGET, message.inAppShowTarget);
        }
        return writableMap;
    }

    public static WritableMap convertNotificationBundleToMap(String eventType, Bundle bundle) {
        WritableMap writableMap = Arguments.createMap();
        writableMap.putString(JConstants.NOTIFICATION_EVENT_TYPE, eventType);
        if (bundle != null) {
            writableMap.putString(JConstants.MESSAGE_ID, bundle.getString("cn.jpush.android.MSG_ID",""));
            writableMap.putString(JConstants.TITLE, bundle.getString("cn.jpush.android.NOTIFICATION_CONTENT_TITLE",""));
            writableMap.putString(JConstants.CONTENT, bundle.getString("cn.jpush.android.ALERT",""));
            convertExtras(bundle.getString("cn.jpush.android.EXTRA",""), writableMap);
        }
        return writableMap;
    }

    public static WritableMap convertCustomMessage(CustomMessage customMessage) {
        WritableMap writableMap = Arguments.createMap();
        if (customMessage != null) {
            writableMap.putString(JConstants.MESSAGE_ID, customMessage.messageId);
            writableMap.putString(JConstants.TITLE, customMessage.title);
            writableMap.putString(JConstants.CONTENT, customMessage.message);
            convertExtras(customMessage.extra, writableMap);
        }
        return writableMap;
    }

    public static WritableMap convertNotificationCustomButtonToMap(NotificationCustomButton notificationCustomButton) {
        WritableMap writableMap = Arguments.createMap();
        try {
            if (notificationCustomButton != null) {
                writableMap.putString("msgId", notificationCustomButton.a);
                writableMap.putInt("platform", notificationCustomButton.b);
                writableMap.putString("name", notificationCustomButton.c);
                writableMap.putInt("actionType", notificationCustomButton.d);
                writableMap.putString("action", notificationCustomButton.e);
                writableMap.putString("data", notificationCustomButton.f);
            }
            return writableMap;
         } catch (Throwable throwable) {
            JLogger.w("convertNotificationCustomButtonToMap error");
            return writableMap;
        }
    }

    public static WritableMap convertJPushMessageToMap(int type, JPushMessage message) {
        WritableMap writableMap = Arguments.createMap();
        if (message == null) {
            return writableMap;
        }
        writableMap.putInt(JConstants.CODE, message.getErrorCode());
        writableMap.putInt(JConstants.SEQUENCE, message.getSequence());
        switch (type) {
            case 1:
                Set<String> tags = message.getTags();
                WritableArray tagsArray = Arguments.createArray();
                if(tags==null || tags.isEmpty()){
                    JLogger.d("tags is empty");
                }else {
                    for (String tag : tags) {
                        tagsArray.pushString(tag);
                    }
                }
                writableMap.putArray(JConstants.TAGS, tagsArray);
                break;
            case 2:
                writableMap.putBoolean(JConstants.TAG_ENABLE, message.getTagCheckStateResult());
                writableMap.putString(JConstants.TAG, message.getCheckTag());
                break;
            case 3:
                writableMap.putString(JConstants.ALIAS, message.getAlias());
                break;
            case 4:
                WritableMap prosMap = Arguments.createMap();
                if (message.getPros() != null) {
                    for (java.util.Map.Entry<String, Object> entry : message.getPros().entrySet()) {
                        if (entry.getKey() != null && entry.getValue() != null) {
                            prosMap.putString(entry.getKey(), String.valueOf(entry.getValue()));
                        }
                    }
                }
                writableMap.putMap(JConstants.PROPERTIES, prosMap);
                break;
        }
        return writableMap;
    }

    public static void convertExtras(String extras, WritableMap writableMap) {
        if (isEmpty(extras) || extras.equals("{}")) return;
        try {
            WritableMap extrasMap = Arguments.createMap();
            JSONObject jsonObject = new JSONObject(extras);
            Iterator<String> it = jsonObject.keys();
            while (it.hasNext()) {
                String key = it.next();
                if (key == null) {
                    continue;
                }
                Object val = jsonObject.opt(key);
                String value = !isJsonNull(val) ? String.valueOf(val) : "";
                extrasMap.putString(key, value);
            }
            writableMap.putMap(JConstants.EXTRAS, extrasMap);
        } catch (Throwable throwable) {
            JLogger.w("convertExtras error");
        }
    }

    public static void launchApp(Context context) {
        if (context == null) {
            return;
        }
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            if (intent == null) {
                return;
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }catch (Throwable throwable){
            JLogger.e("");
        }
    }

}
