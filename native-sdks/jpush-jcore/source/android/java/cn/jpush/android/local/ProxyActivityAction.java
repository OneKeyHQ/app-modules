package cn.jpush.android.local;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;

public abstract class ProxyActivityAction {
   public abstract boolean onCreateOptionsMenu(Activity var1, Menu var2);

   public abstract void onConfigurationChanged(Activity var1, Configuration var2);

   public abstract boolean onKeyDown(Activity var1, int var2, KeyEvent var3);

   public abstract void onCreate(Activity var1, Bundle var2);

   public abstract void onStart(Activity var1, Intent var2);

   public abstract void onNewIntent(Activity var1, Intent var2);

   public abstract void onRestart(Activity var1);

   public abstract void onResume(Activity var1);

   public abstract void onPause(Activity var1);

   public abstract void onStop(Activity var1);

   public abstract void onDestroy(Activity var1);

   public abstract void onBackPressed(Activity var1);

   public abstract void onEvent(Activity var1, String var2, Bundle var3);
}
