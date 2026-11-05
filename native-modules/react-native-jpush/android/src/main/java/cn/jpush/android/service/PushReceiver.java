package cn.jpush.android.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cn.jiguang.internal.JConstants;
import cn.jiguang.internal.JCoreInternalHelper;

public class PushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (context == null || intent == null) {
            return;
        }
        JConstants.getAppContext(context.getApplicationContext());
        JCoreInternalHelper.getInstance().onEvent(
            context.getApplicationContext(),
            "JCore",
            31,
            true,
            intent.getAction(),
            intent.getExtras(),
            intent
        );
    }
}
