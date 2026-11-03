package cn.jiguang.bk;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.g.RecoverType5a69853;
import java.lang.reflect.InvocationTargetException;

public class RecoverTypeaf2b07d6 {
   private static int recoverField_a = 0;
   private static String recoverField_b = "";
   private static String recoverField_c = "";

   public static int recoverMethod_a_f0554de6(Context var0) {
      try {
         if (!RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_d_134632(1504)) {
            return 0;
         }

         if (RecoverTypeaf2b07ca.recoverMethod_a_5d527815("getIntNetworkType")) {
            return recoverField_a;
         }

         TelephonyManager var1 = (TelephonyManager)var0.getSystemService("phone");
         recoverField_a = var1.getNetworkType();
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TeleonyManagerUtils", "getNetworkType int by api");
      } catch (Throwable var2) {
         Log.e("TeleonyManagerUtils", "getIntNetworkType failed: " + var2.getMessage());
         return 0;
      }

      return recoverField_a;
   }

   public static String recoverMethod_b_e501eb0d(Context var0) {
      String var1 = "";

      try {
         int var2 = recoverMethod_a_f0554de6(var0);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TeleonyManagerUtils", "step2 - networkType:" + var2);
         Class[] var3 = new Class[]{int.class};
         Object[] var4 = new Object[]{var2};
         Object var5 = RecoverTypeaf2b07d2.recoverMethod_a_92542ccb(TelephonyManager.class, "getNetworkClass", var4, var3);
         if ((Integer)var5 == 0) {
            var1 = "unknown";
         } else if ((Integer)var5 == 1) {
            var1 = "2g";
         } else if ((Integer)var5 == 2) {
            var1 = "3g";
         } else if ((Integer)var5 == 3) {
            var1 = "4g";
         }

         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TeleonyManagerUtils", "step2 - type:" + var1);
      } catch (NoSuchMethodException var6) {
      } catch (IllegalAccessException var7) {
      } catch (InvocationTargetException var8) {
      } catch (Exception var9) {
      }

      return TextUtils.isEmpty(var1) ? "unknown" : var1;
   }

   public static void recoverMethod_a_9b75() {
      recoverField_b = "";
      RecoverTypeaf2b07ce.recoverField_a = "";
      recoverField_c = "";
   }

   public static String recoverMethod_a_5e799b7f(Context var0, boolean var1) {
      if (!var1 && !RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_d_134632(1506)) {
         return "";
      }

      if (!RecoverTypeaf2b07d0.recoverMethod_a_5ffc0101(var0, "android.permission.ACCESS_NETWORK_STATE")) {
         return "";
      }

      if (RecoverTypeaf2b07ca.recoverMethod_a_5d527815("getNetworkOperator")) {
         return recoverField_b;
      }

      try {
         TelephonyManager var2 = (TelephonyManager)var0.getSystemService("phone");
         recoverField_b = var2.getNetworkOperator();
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TeleonyManagerUtils", "getNetworkOperator by api");
      } catch (Throwable var3) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("TeleonyManagerUtils", "getNetworkOperator failed, " + var3.getMessage());
      }

      return recoverField_b;
   }

   public static String recoverMethod_b_5e799b7f(Context var0, boolean var1) {
      if (!var1 && !RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_d_134632(1507)) {
         return "";
      }

      if (!RecoverTypeaf2b07d0.recoverMethod_a_5ffc0101(var0, "android.permission.ACCESS_NETWORK_STATE")) {
         return "";
      }

      if (RecoverTypeaf2b07ca.recoverMethod_a_5d527815("getNetworkOperatorName")) {
         return recoverField_c;
      }

      try {
         TelephonyManager var2 = (TelephonyManager)var0.getSystemService("phone");
         recoverField_c = var2.getNetworkOperatorName();
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TeleonyManagerUtils", "getNetworkOperatorName by api");
      } catch (Throwable var3) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("TeleonyManagerUtils", "getNetworkOperatorName failed, " + var3.getMessage());
      }

      return recoverField_c;
   }
}
