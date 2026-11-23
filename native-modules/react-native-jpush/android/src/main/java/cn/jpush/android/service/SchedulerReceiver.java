package cn.jpush.android.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cn.jiguang.internal.JConstants;

public class SchedulerReceiver extends BroadcastReceiver {
    public static final String DELAY_NOTIFY = "delay_notify";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context != null) {
            JConstants.getAppContext(context.getApplicationContext());
        }
    }
}
