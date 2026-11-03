package cn.jpush.android.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import cn.jiguang.al.RecoverTypeaf2a972a;
import cn.jiguang.al.RecoverTypeaf2a972b;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.api.JCoreManager;
import cn.jiguang.at.RecoverTypeaf2ab52b;
import cn.jiguang.bk.RecoverTypeaf2b07c2;
import cn.jiguang.internal.JConstants;

public class AlarmReceiver extends BroadcastReceiver {
   private static final String TAG = "AlarmReceiver";

   @Override
   public void onReceive(Context var1, Intent var2) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("AlarmReceiver", "onReceive");
      if (RecoverTypeaf2a972a.recoverMethod_b_9b79()) {
         RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("AlarmReceiver", "[key-step]Your sdk is disabled already, no receiver task can be handled.");
      } else if (RecoverTypeaf2a972b.recoverMethod_a_9b79()) {
         RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("AlarmReceiver", "[key-step]Your sdk is limited already, no receiver task can be handled.");
      } else if (VERSION.SDK_INT >= 31) {
         RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("AlarmReceiver", "sdk is android 12, return");
      } else if (!RecoverTypeaf2b07c2.recoverMethod_b_f0554df7(var1)) {
         RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("AlarmReceiver", "auto wakeup is closed");
      } else if (!JConstants.isCallInit.get()) {
         RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("AlarmReceiver", "please call init");
      } else {
         RecoverTypeaf2ab52b.recoverMethod_b_f0554df3(var1);
         JCoreManager.onEvent(var1, "JCore", 10, "a2", null);
      }
   }
}
