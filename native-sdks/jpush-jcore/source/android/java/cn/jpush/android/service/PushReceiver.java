package cn.jpush.android.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cn.jiguang.al.RecoverTypeaf2a972a;
import cn.jiguang.al.RecoverTypeaf2a972b;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.api.JCoreManager;
import cn.jiguang.internal.JConstants;

public class PushReceiver extends BroadcastReceiver {
   private static final String TAG = "PushReceiver";

   @Override
   public void onReceive(Context var1, Intent var2) {
      try {
         if (RecoverTypeaf2a972a.recoverMethod_b_9b79()) {
            RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("PushReceiver", "[key-step]Your sdk is disabled already, no receiver task can be handled.");
            return;
         }

         if (RecoverTypeaf2a972b.recoverMethod_a_9b79()) {
            RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("PushReceiver", "[key-step]Your sdk is limited already, no receiver task can be handled.");
            return;
         }

         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("PushReceiver", "onReceive:" + var2.getAction());
         if (!JConstants.isCallInit.get()) {
            RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("PushReceiver", "please call init");
            return;
         }

         String var3 = var2.getStringExtra("sdktype");
         JCoreManager.onEvent(var1.getApplicationContext(), var3, 31, null, null, var2);
      } catch (Throwable var4) {
      }
   }
}
