package cn.jpush.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PushService extends Service {
   private static final String TAG = "PushService";

   @Override
   public IBinder onBind(Intent var1) {
      return null;
   }

   @Override
   public int onStartCommand(Intent var1, int var2, int var3) {
      return 2;
   }
}
