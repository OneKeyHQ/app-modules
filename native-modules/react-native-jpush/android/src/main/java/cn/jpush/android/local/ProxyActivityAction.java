package cn.jpush.android.local;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;

public abstract class ProxyActivityAction {
    public abstract void onCreate(Activity activity, Bundle savedInstanceState);

    public abstract void onResume(Activity activity);

    public abstract void onPause(Activity activity);

    public abstract void onRestart(Activity activity);

    public abstract void onStop(Activity activity);

    public abstract void onDestroy(Activity activity);

    public abstract void onNewIntent(Activity activity, Intent intent);

    public abstract void onConfigurationChanged(Activity activity, Configuration newConfig);

    public abstract boolean onKeyDown(Activity activity, int keyCode, KeyEvent event);

    public abstract boolean onCreateOptionsMenu(Activity activity, Menu menu);

    public abstract void onBackPressed(Activity activity);

    public abstract void onEvent(Activity activity, String action, Bundle bundle);
}
