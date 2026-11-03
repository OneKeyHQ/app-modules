package cn.jpush.android.ad;

import android.app.Application;
import android.content.Context;
import android.os.Build.VERSION;
import android.text.TextUtils;
import cn.jpush.android.helper.Logger;
import java.io.FileInputStream;
import java.lang.reflect.Method;

public class RecoverType9b4c5aed {
   private static String recoverField_a;

   public static String recoverMethod_a_e501eb0d(Context var0) {
      if (!TextUtils.isEmpty(recoverField_a)) {
         return recoverField_a;
      }

      recoverField_a = recoverMethod_a_73cf11cb();
      if (!TextUtils.isEmpty(recoverField_a)) {
         return recoverField_a;
      }

      recoverField_a = recoverMethod_b_73cf11cb();
      if (!TextUtils.isEmpty(recoverField_a)) {
         return recoverField_a;
      }

      recoverField_a = recoverMethod_c_73cf11cb();
      recoverField_a = !TextUtils.isEmpty(recoverField_a) ? recoverField_a : "";
      return recoverField_a;
   }

   private static String recoverMethod_a_73cf11cb() {
      return VERSION.SDK_INT >= 28 ? Application.getProcessName() : null;
   }

   private static String recoverMethod_b_73cf11cb() {
      String var0 = null;

      try {
         Method var1 = Class.forName("android.app.ActivityThread", false, Application.class.getClassLoader()).getDeclaredMethod("currentProcessName");
         var1.setAccessible(true);
         Object var2 = var1.invoke(null);
         if (var2 instanceof String) {
            var0 = (String)var2;
         }
      } catch (Throwable var3) {
         Logger.recoverMethod_w_e925fa09("ProcessUtil", "getCurrentProcessNameByActivityThread failed, " + var3.getMessage());
      }

      return var0;
   }

   private static String recoverMethod_c_73cf11cb() {
      FileInputStream var0 = null;

      try {
         String var1 = "/proc/self/cmdline";
         var0 = new FileInputStream(var1);
         byte[] var2 = new byte[256];
         int var3 = 0;

         int var4;
         while ((var4 = var0.read()) > 0 && var3 < var2.length) {
            var2[var3++] = (byte)var4;
         }

         if (var3 > 0) {
            return new String(var2, 0, var3, "UTF-8");
         }
      } catch (Throwable var16) {
      } finally {
         try {
            if (var0 != null) {
               var0.close();
            }
         } catch (Throwable var15) {
         }
      }

      return "";
   }
}
