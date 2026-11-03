package cn.jpush.android.ad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Build.VERSION;

public class RecoverType9b4c5aee {
   public static void recoverMethod_a_b0740a13(Context var0, BroadcastReceiver var1, IntentFilter var2, String var3, Handler var4) {
      if (VERSION.SDK_INT >= 33) {
         var0.getApplicationContext().registerReceiver(var1, var2, var3, var4, 2);
      } else {
         var0.getApplicationContext().registerReceiver(var1, var2, var3, var4);
      }
   }
}
