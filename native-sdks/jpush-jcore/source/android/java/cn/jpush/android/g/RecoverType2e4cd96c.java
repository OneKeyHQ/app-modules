package cn.jpush.android.g;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import cn.jpush.android.i.RecoverType2e4ce0ed;

public final class RecoverType2e4cd96c {
   public static boolean recoverMethod_a_5ffc0101(Context var0, String var1) {
      PackageManager var5 = var0.getPackageManager();
      ServiceInfo[] var2 = null;

      try {
         String var3;
         PackageInfo var6;
         if (!(var3 = (var6 = var5.getPackageInfo(var1, 4)).sharedUserId).equals("android.uid.system")) {
            RecoverType2e4ce0ed.recoverMethod_e_e925fa09("ConnectionFactory", var1 + " is not system app,sharedUserId:" + var3);
            return false;
         }

         var2 = var6.services;
      } catch (NameNotFoundException var4) {
      }

      if (var2 == null) {
         RecoverType2e4ce0ed.recoverMethod_a_e925fa09("ConnectionFactory", "not found service by:" + var1);
         return false;
      }

      for (int var7 = 0; var7 < var2.length; var7++) {
         RecoverType2e4ce0ed.recoverMethod_a_e925fa09("ConnectionFactory", "serviceInfos:" + var2[var7].name);
         if ("cn.jpush.android.service.PushManagerService".equalsIgnoreCase(var2[var7].name)) {
            return true;
         }
      }

      return false;
   }
}
