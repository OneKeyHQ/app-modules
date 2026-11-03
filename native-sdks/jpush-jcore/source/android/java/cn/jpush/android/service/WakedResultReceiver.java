package cn.jpush.android.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.Map;

public class WakedResultReceiver extends BroadcastReceiver {
   public static final String CONTEXT_KEY = "1";
   public static final String WAKE_TYPE_KEY = "2";

   @Override
   public final void onReceive(Context var1, Intent var2) {
   }

   public final void onWakeMap(Map var1) {
      try {
         Object var2 = var1.get("2");
         int var3 = -1;
         if (null != var2) {
            var3 = (Integer)var2;
         }

         this.onWake(var3);
         Object var4 = var1.get("1");
         Context var5 = null;
         if (null != var4) {
            var5 = (Context)var4;
         }

         this.onWake(var5, var3);
      } catch (Throwable var6) {
      }
   }

   public void onWake(int var1) {
   }

   public void onWake(Context var1, int var2) {
   }
}
