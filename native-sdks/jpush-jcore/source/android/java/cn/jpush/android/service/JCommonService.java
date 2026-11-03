package cn.jpush.android.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.text.TextUtils;
import cn.jiguang.android.IDataShare;
import cn.jiguang.internal.JConstants;
import cn.jiguang.internal.JCoreInternalHelper;

public class JCommonService extends Service {
    private static IDataShare.Stub mBinder;

    @Override
    public final void onCreate() {
        super.onCreate();
        JConstants.getAppContext(this.getApplicationContext());
        if (mBinder == null) {
            mBinder = new DataShare();
        }
    }

    @Override
    public final boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public final void onDestroy() {
        super.onDestroy();
    }

    @Override
    public final void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public final void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public final void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public final void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public final void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public final void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public final IBinder onBind(Intent intent) {
        if (mBinder == null) {
            mBinder = new DataShare();
        }
        return mBinder;
    }

    @Override
    public final int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && !TextUtils.isEmpty(intent.getAction())) {
            JCoreInternalHelper.getInstance().onEvent(this, "JCore", 2, true, intent.getAction(), intent.getExtras());
        }
        return Service.START_NOT_STICKY;
    }
}
