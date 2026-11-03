package cn.jiguang.bk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Build.VERSION;

public class RecoverTypeaf2b07d1 {
   public static Intent recoverMethod_a_6169dcd2(Context var0, BroadcastReceiver var1, IntentFilter var2, String var3, Handler var4, boolean var5) {
      return VERSION.SDK_INT >= 33
         ? var0.getApplicationContext().registerReceiver(var1, var2, var3, var4, var5 ? 2 : 4)
         : var0.getApplicationContext().registerReceiver(var1, var2, var3, var4);
   }

   public static Intent recoverMethod_a_81560598(Context var0, BroadcastReceiver var1, IntentFilter var2, boolean var3) {
      return VERSION.SDK_INT >= 33
         ? var0.getApplicationContext().registerReceiver(var1, var2, var3 ? 2 : 4)
         : var0.getApplicationContext().registerReceiver(var1, var2);
   }
}
