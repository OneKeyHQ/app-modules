package cn.jiguang.at;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bk.RecoverTypeaf2b07c2;
import cn.jiguang.bk.RecoverTypeaf2b07c4;
import cn.jpush.android.service.AlarmReceiver;

public class RecoverTypeaf2ab52b {
   public static void recoverMethod_a_f0554df3(Context var0) {
      var0 = RecoverTypeaf2b07c4.recoverMethod_a_6263c3eb(var0);
      if (VERSION.SDK_INT >= 31) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("AlarmHelper", "sdk is android 12, return");
      } else {
         try {
            Intent var1 = new Intent(var0, AlarmReceiver.class);
            PendingIntent var2 = PendingIntent.getBroadcast(var0, 0, var1, 67108864);
            AlarmManager var3 = (AlarmManager)var0.getSystemService("alarm");
            var3.cancel(var2);
         } catch (Throwable var4) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("AlarmHelper", "Cancel heartbeat alarm failed.");
         }
      }
   }

   public static void recoverMethod_b_f0554df3(Context var0) {
      var0 = RecoverTypeaf2b07c4.recoverMethod_a_6263c3eb(var0);
      if (VERSION.SDK_INT >= 31) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("AlarmHelper", "sdk is android 12, return");
      } else if (!RecoverTypeaf2b07c2.recoverMethod_b_f0554df7(var0)) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("AlarmHelper", "auto wakeup is closed");
      } else {
         RecoverTypeaf2ab531.recoverMethod_a_c6965695().recoverMethod_e_9b75();
         long var1 = RecoverTypeaf2ab531.recoverMethod_a_c6965695().recoverMethod_h_9b68() * 1000;
         long var3 = System.currentTimeMillis() + var1;
         RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("AlarmHelper", "Reset heartbeat alarm, wait " + var1 + "ms.");

         try {
            Intent var5 = new Intent(var0, AlarmReceiver.class);
            PendingIntent var6 = PendingIntent.getBroadcast(var0, 0, var5, 67108864);
            AlarmManager var7 = (AlarmManager)var0.getSystemService("alarm");
            if (VERSION.SDK_INT >= 19) {
               var7.setWindow(0, var3, 0L, var6);
            } else {
               var7.setInexactRepeating(0, var3, var1, var6);
            }
         } catch (Throwable var8) {
            RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("AlarmHelper", "can't trigger alarm cause by exception:" + var8);
         }
      }
   }
}
