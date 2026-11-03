package cn.jpush.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import cn.jiguang.ak.RecoverTypeaf2a9368;
import cn.jiguang.am.RecoverTypeaf2a9ae9;

public class DaemonService extends Service {
   private static final String TAG = "DaemonService";

   @Override
   public void onCreate() {
      super.onCreate();
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("DaemonService", "action onCreate");
   }

   @Override
   public void onDestroy() {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("DaemonService", "action onDestroy");
      super.onDestroy();
   }

   @Override
   public final IBinder onBind(Intent var1) {
      RecoverTypeaf2a9368.recoverMethod_a_cf277f22(this.getApplicationContext(), var1 != null ? var1.getExtras() : null, 2);
      return null;
   }

   @Override
   public final int onStartCommand(Intent var1, int var2, int var3) {
      RecoverTypeaf2a9368.recoverMethod_a_cf277f22(this.getApplicationContext(), var1 != null ? var1.getExtras() : null, 1);
      return 2;
   }
}
