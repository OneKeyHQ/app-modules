package cn.jpush.android.x;

import android.content.Context;
import cn.jpush.android.helper.Logger;

public class RecoverType2e4d1941 {
   public static int recoverMethod_a_f0554de6(Context var0) {
      int var1 = var0.getResources().getIdentifier("jpush_notification_icon", "drawable", var0.getPackageName());
      if (0 == var1) {
         try {
            var1 = var0.getPackageManager().getApplicationInfo(var0.getPackageName(), 0).icon;
            Logger.recoverMethod_i_e925fa09("ResourceHelper", "JPush.mPackageInconId == 0 ?, get icon from application info.");
         } catch (Throwable var3) {
            Logger.recoverMethod_ee_1765c98c("ResourceHelper", "failed to get application info and icon.", var3);
         }
      }

      return var1;
   }
}
