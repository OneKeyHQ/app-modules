package cn.jpush.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import cn.jiguang.internal.JConstants;
import cn.jiguang.internal.JCoreInternalHelper;

public class PushService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        JConstants.getAppContext(getApplicationContext());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            JCoreInternalHelper.getInstance().onEvent(this, "JCore", 2, true, intent.getAction(), intent.getExtras());
        }
        return Service.START_NOT_STICKY;
    }
}
