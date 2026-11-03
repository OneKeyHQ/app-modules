package cn.jpush.android.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.Map;

public class WakedResultReceiver extends BroadcastReceiver {
    public static final String CONTEXT_KEY = "1";
    public static final String WAKE_TYPE_KEY = "2";

    @Override
    public final void onReceive(Context context, Intent intent) {
    }

    public void onWake(int wakeType) {
    }

    public void onWake(Context context, int wakeType) {
    }

    public final void onWakeMap(Map<String, Object> wakeInfo) {
    }
}
