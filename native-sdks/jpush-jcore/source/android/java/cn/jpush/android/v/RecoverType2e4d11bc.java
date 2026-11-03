package cn.jpush.android.v;

import android.content.Context;
import android.os.Bundle;
import cn.jpush.android.data.JPushLocalNotification;
import cn.jpush.android.helper.JCoreHelper;

public class RecoverType2e4d11bc {
   public static void recoverMethod_a_9131a9a(Context var0, JPushLocalNotification var1) {
      Bundle var2 = new Bundle();
      var2.putSerializable("local_notify", var1);
      JCoreHelper.runActionWithService(var0, "JPUSH", "add_local_notify", var2);
   }

   public static void recoverMethod_a_1a54e731(Context var0, long var1) {
      Bundle var3 = new Bundle();
      var3.putSerializable("local_notify_ID", var1);
      JCoreHelper.runActionWithService(var0, "JPUSH", "rm_local_notify", var3);
   }

   public static void recoverMethod_a_f0554df3(Context var0) {
      Bundle var1 = new Bundle();
      JCoreHelper.runActionWithService(var0, "JPUSH", "clear_local_notify", var1);
   }
}
