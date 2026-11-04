package cn.jiguang.internal;

import android.content.Context;
import android.os.Bundle;
import cn.jiguang.core.JCoreStorage;
import cn.jiguang.core.JCoreTcpEngine;

public class JCoreInternalHelper {
    private static volatile JCoreInternalHelper instance;
    private volatile JCoreHelperAction helperAction;

    private JCoreInternalHelper() {
    }

    public static JCoreInternalHelper getInstance() {
        if (instance == null) {
            synchronized (JCoreInternalHelper.class) {
                if (instance == null) {
                    instance = new JCoreInternalHelper();
                }
            }
        }
        return instance;
    }

    public void setHelperAction(JCoreHelperAction action) {
        this.helperAction = action;
    }

    public Object onEvent(Context context, String sdkType, int cmd, String action, Bundle bundle, Object... extras) {
        return onEvent(context, sdkType, cmd, true, action, bundle, extras);
    }

    public Object onEvent(
        final Context context,
        final String sdkType,
        final int cmd,
        boolean async,
        final String action,
        final Bundle bundle,
        final Object... extras
    ) {
        JConstants.getAppContext(context);
        JCoreHelperAction custom = this.helperAction;
        if (custom != null) {
            return custom.onEvent(context, sdkType, cmd, action, bundle, extras);
        }
        Bundle result = JCoreTcpEngine.getInstance().handleEvent(context, sdkType, cmd, action, bundle, extras);
        if (result != null && result.containsKey("result")) {
            return result.get("result");
        }
        return result;
    }

    public Bundle directHandle(Context context, String sdkType, String action, Bundle bundle) {
        Context app = JConstants.getAppContext(context);
        JCoreHelperAction custom = this.helperAction;
        if (custom != null) {
            return custom.directHandle(app, sdkType, action, bundle);
        }
        Bundle out = new Bundle();
        out.putString("sdk_type", sdkType != null ? sdkType : "");
        out.putString("action", action != null ? action : "");
        if ("get_reg_id".equals(action)) {
            out.putString("reg_id", JCoreStorage.getRegistrationId(app));
        } else if ("sp_set".equals(action) && bundle != null) {
            String key = bundle.getString("key");
            if (key != null && !key.isEmpty()) {
                JCoreStorage.putSpValue(app, key, bundle.getString("value", ""));
                out.putBoolean("success", true);
            } else {
                for (String k : bundle.keySet()) {
                    Object v = bundle.get(k);
                    if (v != null) {
                        JCoreStorage.putSpValue(app, k, String.valueOf(v));
                    }
                }
                out.putBoolean("success", true);
            }
        } else if ("sp_get".equals(action) && bundle != null) {
            String key = bundle.getString("key");
            String defVal = bundle.getString("default", "");
            out.putString("value", JCoreStorage.getSpValue(app, key, defVal));
        } else if ("get_uid".equals(action)) {
            out.putLong("uid", JCoreStorage.getUid(app));
        }
        return out;
    }

    public Object commonMethod(Context context, String action, Object arg) {
        JConstants.getAppContext(context);
        JCoreHelperAction custom = this.helperAction;
        if (custom != null) {
            return custom.onCommonMethod(context, action, arg);
        }
        return null;
    }

    public synchronized void initLoad(Context context) {
        JConstants.getAppContext(context);
    }
}
