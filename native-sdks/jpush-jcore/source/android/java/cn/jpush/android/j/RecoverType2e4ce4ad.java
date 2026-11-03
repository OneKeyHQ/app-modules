package cn.jpush.android.j;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.Context;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.text.TextUtils;
import cn.jpush.android.i.RecoverType2e4ce0ed;

public final class RecoverType2e4ce4ad {
   private static String recoverField_a = "";
   private static String recoverField_b = "";
   private static final String recoverField_c = "".toLowerCase();

   public static boolean recoverMethod_a_f0554df7(Context var0) {
      if (var0 == null) {
         RecoverType2e4ce0ed.recoverMethod_e_e925fa09("AndroidUtils", "context was null");
         return false;
      }

      String var3 = null;

      try {
         var3 = Build.MANUFACTURER;
         RecoverType2e4ce0ed.recoverMethod_a_e925fa09("AndroidUtils", "MANUFACTURER:" + var3);
      } catch (Throwable var2) {
         RecoverType2e4ce0ed.recoverMethod_e_e925fa09("AndroidUtils", "get MANUFACTURER failed - error:" + var2);
      }

      if (TextUtils.isEmpty(var3)) {
         RecoverType2e4ce0ed.recoverMethod_e_e925fa09("AndroidUtils", "MANUFACTURER was empty");
         return false;
      } else {
         return TextUtils.isEmpty(recoverField_c) ? true : TextUtils.equals(recoverField_c, var3.toLowerCase());
      }
   }

   public static ComponentInfo recoverMethod_a_e3a6cbf6(Context var0, String var1, Class var2) {
      if (var0 != null && !TextUtils.isEmpty(var1)) {
         try {
            byte var3 = 0;
            if (Service.class.isAssignableFrom(var2)) {
               var3 = 4;
            } else if (BroadcastReceiver.class.isAssignableFrom(var2)) {
               var3 = 2;
            } else if (Activity.class.isAssignableFrom(var2)) {
               var3 = 1;
            } else if (ContentProvider.class.isAssignableFrom(var2)) {
               var3 = 8;
            }

            PackageInfo var7 = var0.getPackageManager().getPackageInfo(var1, var3);
            ComponentInfo[] var8;
            switch (var3) {
               case 1:
                  var8 = var7.activities;
                  break;
               case 2:
                  var8 = var7.receivers;
                  break;
               case 3:
               case 5:
               case 6:
               case 7:
               default:
                  var8 = null;
                  break;
               case 4:
                  var8 = var7.services;
                  break;
               case 8:
                  var8 = var7.providers;
            }

            if (var8 == null) {
               return null;
            }

            ComponentInfo[] var9;
            int var10 = ((Object[])(var9 = var8)).length;

            for (int var11 = 0; var11 < var10; var11++) {
               ComponentInfo var4 = var9[var11];

               try {
                  if (var2.isAssignableFrom(Class.forName(var4.name))) {
                     return var4;
                  }
               } catch (Throwable var5) {
                  RecoverType2e4ce0ed.recoverMethod_d_e925fa09("AndroidUtils", "check component e:" + var5);
               }
            }
         } catch (Throwable var6) {
            RecoverType2e4ce0ed.recoverMethod_e_e925fa09("AndroidUtils", "hasComponent error:" + var6);
         }

         return null;
      } else {
         RecoverType2e4ce0ed.recoverMethod_d_e925fa09(
            "AndroidUtils", "Action - hasComponent, invalide param, context:" + var0 + ",packageName:" + var1 + ",cls:" + var2
         );
         return null;
      }
   }
}
