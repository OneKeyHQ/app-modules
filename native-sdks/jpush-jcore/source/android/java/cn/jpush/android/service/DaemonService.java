package cn.jpush.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DaemonService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }
}
