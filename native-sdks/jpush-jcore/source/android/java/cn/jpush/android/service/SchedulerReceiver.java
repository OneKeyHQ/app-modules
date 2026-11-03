package cn.jpush.android.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cn.jpush.android.helper.JCoreHelper;
import cn.jpush.android.helper.Logger;

public class SchedulerReceiver extends BroadcastReceiver {
   private static final String TAG = "SchedulerReceiver";
   public static final String DELAY_NOTIFY = "delay_notify";

   @Override
   public void onReceive(Context var1, Intent var2) {
      try {
         Logger.recoverMethod_dd_e925fa09("SchedulerReceiver", "onReceive action=" + var2.getAction());
         if ("delay_notify".equals(var2.getAction())) {
            JCoreHelper.runActionWithService(var1, "JPUSH", "delay_notify", null);
         }
      } catch (Throwable var4) {
         Logger.recoverMethod_w_e925fa09("SchedulerReceiver", "onReceive e:" + var4);
      }
   }
}
