package cn.jiguang.bk;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build.VERSION;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import java.util.ArrayList;
import java.util.List;

public class RecoverTypeaf2b07d0 {
   public static List recoverMethod_a_c98d58e3(Context var0, List var1) {
      if (var1 != null && var1.size() != 0) {
         ArrayList var2 = new ArrayList();

         for (Object recoveryElement_17 : var1) {
            String var4 = (String)recoveryElement_17;
            if (!recoverMethod_a_5ffc0101(var0, var4)) {
               var2.add(var4);
            }
         }

         return var2;
      } else {
         return null;
      }
   }

   public static boolean recoverMethod_a_5ffc0101(Context var0, String var1) {
      boolean var2 = false;

      try {
         if (VERSION.SDK_INT >= 23) {
            if (var0.getApplicationInfo().targetSdkVersion >= 23) {
               int var3 = var0.checkSelfPermission(var1);
               var2 = var3 == 0;
            } else {
               String var7 = AppOpsManager.permissionToOp(var1);
               if (var7 == null) {
                  var2 = true;
               } else {
                  AppOpsManager var4 = (AppOpsManager)var0.getSystemService("appops");
                  int var5 = var4.noteProxyOpNoThrow(var7, var0.getPackageName());
                  var2 = var5 == 0;
               }
            }
         } else {
            var2 = true;
         }
      } catch (Throwable var6) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("PermissionUtils", "checkPermission error:" + var6.getMessage());
      }

      return var2;
   }

   public static boolean recoverMethod_b_5ffc0101(Context var0, String var1) {
      if (null != var0 && !TextUtils.isEmpty(var1)) {
         try {
            PackageInfo var2 = RecoverTypeaf2b07cf.recoverMethod_a_9537863d(var0, 4096);
            if (null != var2) {
               String[] var3 = var2.requestedPermissions;

               for (String var7 : var3) {
                  if (var1.equals(var7)) {
                     return true;
                  }
               }
            }
         } catch (Throwable var8) {
            RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("PermissionUtils", "hasPermissionDefined error:" + var8.getMessage());
         }

         return false;
      } else {
         throw new IllegalArgumentException("empty params");
      }
   }
}
