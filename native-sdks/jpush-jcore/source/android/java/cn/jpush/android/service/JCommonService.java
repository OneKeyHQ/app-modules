package cn.jpush.android.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.android.IDataShare;
import cn.jiguang.internal.JConstants;
import cn.jiguang.internal.JCoreInternalHelper;

public class JCommonService extends Service {
   private static final String TAG = "JCommonService";
   private static IDataShare.Membereeaed960 mBinder = null;

   @Override
   public final void onCreate() {
      super.onCreate();
      JConstants.getAppContext(this.getApplicationContext());
      if (mBinder == null) {
         mBinder = new DataShare();
      }
   }

   @Override
   public final boolean onUnbind(Intent var1) {
      return super.onUnbind(var1);
   }

   @Override
   public final void onDestroy() {
      super.onDestroy();
   }

   @Override
   public final void onConfigurationChanged(Configuration var1) {
      super.onConfigurationChanged(var1);
   }

   @Override
   public final void onLowMemory() {
      super.onLowMemory();
   }

   @Override
   public final void onRebind(Intent var1) {
      super.onRebind(var1);
   }

   @Override
   public final void onTaskRemoved(Intent var1) {
      super.onTaskRemoved(var1);
   }

   @Override
   public final void onTrimMemory(int var1) {
      super.onTrimMemory(var1);
   }

   @Override
   public final void onStart(Intent var1, int var2) {
      super.onStart(var1, var2);
   }

   @Override
   public final IBinder onBind(Intent var1) {
      return mBinder;
   }

   @Override
   public final int onStartCommand(Intent var1, int var2, int var3) {
      if (var1 != null && !TextUtils.isEmpty(var1.getAction())) {
         JCoreInternalHelper.getInstance().onEvent(this, "JCore", 2, true, var1.getAction(), var1.getExtras());
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCommonService", "onStartCommand intent is empty or action is empty");
      }

      return 2;
   }
}
