package cn.jiguang.y;

import android.text.TextUtils;

public class RecoverType5a6dbe5 {
   public static boolean recoverMethod_a_9b79() {
      String var0 = System.getProperty("ro.build.freeme.label");
      return !TextUtils.isEmpty(var0) && var0.equalsIgnoreCase("FREEMEOS");
   }

   public static boolean recoverMethod_b_9b79() {
      String var0 = System.getProperty("ro.ssui.product");
      return !TextUtils.isEmpty(var0) && !var0.equalsIgnoreCase("unknown");
   }
}
