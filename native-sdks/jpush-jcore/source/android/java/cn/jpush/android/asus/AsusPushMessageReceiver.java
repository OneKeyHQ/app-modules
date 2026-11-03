package cn.jpush.android.asus;

import android.content.Context;
import cn.jiguang.push.asus.PushMessageReceiver;
import cn.jpush.android.ac.RecoverType9b4c5727;
import cn.jpush.android.c.RecoverType2e4cca66;

public class AsusPushMessageReceiver extends PushMessageReceiver {
   @Override
   public void recoverMethod_a_5ffc00fd(Context var1, String var2) {
      RecoverType9b4c5727.recoverMethod_a_d8c3f71f().recoverMethod_a_e7235ab3(var1, (byte)6, var2);
   }

   @Override
   public void recoverMethod_a_ac8548fc(Context var1, RecoverType2e4cca66 var2) {
      if (var2 != null) {
         RecoverTypefa94ae11.recoverMethod_a_1a54e370(var1, var2.recoverMethod_a_9b68());
      }
   }
}
