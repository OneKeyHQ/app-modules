package cn.jiguang.internal;

import android.content.Context;
import android.text.TextUtils;
import cn.jiguang.api.JAction;
import cn.jiguang.api.JActionExtra;
import cn.jiguang.api.JDispatchAction;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONObject;

public class ActionManager {
    private static volatile ActionManager instance;
    private final ConcurrentHashMap<String, JDispatchAction> dispatchActions = new ConcurrentHashMap<String, JDispatchAction>();
    private final ConcurrentHashMap<String, JAction> actions = new ConcurrentHashMap<String, JAction>();
    private final ConcurrentHashMap<String, JActionExtra> extraActions = new ConcurrentHashMap<String, JActionExtra>();

    public static ActionManager getInstance() {
        if (instance == null) {
            synchronized (ActionManager.class) {
                if (instance == null) {
                    instance = new ActionManager();
                }
            }
        }
        return instance;
    }

    public static void addAction(String sdkType, String className) {
        if (TextUtils.isEmpty(sdkType) || TextUtils.isEmpty(className)) {
            return;
        }
        try {
            Object instanceObj = Class.forName(className).getDeclaredConstructor().newInstance();
            if (instanceObj instanceof JDispatchAction) {
                getInstance().dispatchActions.put(sdkType, (JDispatchAction) instanceObj);
            } else if (instanceObj instanceof JAction) {
                getInstance().actions.put(sdkType, (JAction) instanceObj);
            } else if (instanceObj instanceof JActionExtra) {
                getInstance().extraActions.put(sdkType, (JActionExtra) instanceObj);
            }
        } catch (Throwable ignored) {
        }
    }

    public JDispatchAction getJDispatchAction(String sdkType) {
        return sdkType != null ? dispatchActions.get(sdkType) : null;
    }

    public boolean wrapSdkVersionInfo(JSONObject target) {
        return target != null;
    }

    public static boolean wrapSdkTypeVersionInfo(JSONObject target) {
        return target != null;
    }

    public void handleMessage(Context context, String sdkType, Object message) {
        JDispatchAction action = getJDispatchAction(sdkType);
        if (action != null) {
            action.handleMessage(context, sdkType, message);
        }
    }
}
