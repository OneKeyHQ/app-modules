package cn.jiguang.bk;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build.VERSION;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.g.RecoverType5a69853;
import java.net.Inet6Address;
import java.net.InetAddress;

public class RecoverTypeaf2b07ce {
   public static String recoverField_a = "";
   private static String recoverField_b = "";

   public static boolean recoverMethod_a_9b79() {
      return recoverMethod_a_134632(10000);
   }

   private static boolean recoverMethod_a_134632(int var0) {
      ConnectivityManager var1 = recoverMethod_b_79f2a562();
      if (VERSION.SDK_INT < 23) {
         return recoverMethod_a_eb6c925f(var1, var0);
      }

      Network var2 = var1.getActiveNetwork();
      if (var2 == null) {
         return recoverMethod_a_eb6c925f(var1, var0);
      }

      NetworkCapabilities var3 = var1.getNetworkCapabilities(var2);
      if (var3 == null) {
         return recoverMethod_a_eb6c925f(var1, var0);
      }

      boolean var4 = var3.hasCapability(12);
      boolean var5 = var3.hasCapability(16);
      if (var4 && var5) {
         byte var6;
         switch (var0) {
            case 0:
               var6 = 0;
               break;
            case 1:
               var6 = 1;
               break;
            default:
               return true;
         }

         return var3.hasTransport(var6) || recoverMethod_a_eb6c925f(var1, var0);
      } else {
         return recoverMethod_a_eb6c925f(var1, var0);
      }
   }

   private static boolean recoverMethod_b_134632(int var0) {
      return var0 == 0 || var0 == 4 || var0 == 5 || var0 == 2 || var0 == 6 || var0 == 3;
   }

   private static boolean recoverMethod_a_eb6c925f(ConnectivityManager var0, int var1) {
      NetworkInfo var2 = var0.getActiveNetworkInfo();
      if (var2 == null || !var2.isConnected()) {
         return false;
      } else if (10000 == var1) {
         return true;
      } else {
         return 0 == var1 ? recoverMethod_b_134632(var2.getType()) : var2.getType() == var1;
      }
   }

   public static ConnectivityManager recoverMethod_b_79f2a562() {
      Context var0 = RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d();
      if (var0 == null) {
         return null;
      } else {
         return !RecoverTypeaf2b07d0.recoverMethod_a_5ffc0101(var0, "android.permission.ACCESS_NETWORK_STATE")
            ? null
            : (ConnectivityManager)var0.getSystemService("connectivity");
      }
   }

   public static boolean recoverMethod_c_9b79() {
      try {
         ConnectivityManager var0 = recoverMethod_b_79f2a562();
         if (var0 == null) {
            return false;
         }

         if (VERSION.SDK_INT >= 23) {
            LinkProperties var1 = var0.getLinkProperties(var0.getActiveNetwork());
            if (var1 != null) {
               for (LinkAddress var4 : var1.getLinkAddresses()) {
                  InetAddress var5 = var4.getAddress();
                  if (var5 instanceof Inet6Address) {
                     return true;
                  }
               }
            }
         }
      } catch (Throwable var6) {
      }

      return false;
   }

   public static String recoverMethod_d_73cf11cb() {
      ConnectivityManager var0 = recoverMethod_b_79f2a562();
      if (var0 == null) {
         return null;
      }

      boolean var1 = false;
      boolean var2 = false;
      boolean var3 = false;
      if (VERSION.SDK_INT >= 23) {
         Network var4 = var0.getActiveNetwork();
         if (var4 == null) {
            return null;
         }

         NetworkCapabilities var5 = var0.getNetworkCapabilities(var4);
         if (var5 == null) {
            return null;
         }

         if (var5.hasTransport(3)) {
            var1 = true;
         }

         if (var5.hasTransport(1)) {
            var2 = true;
         }

         if (var5.hasTransport(0)) {
            var3 = true;
         }
      } else {
         NetworkInfo var6 = var0.getActiveNetworkInfo();
         if (var6 == null) {
            return null;
         }

         int var7 = var6.getType();
         switch (var7) {
            case 0:
               var3 = true;
               break;
            case 1:
               var2 = true;
               break;
            case 9:
               var1 = true;
         }
      }

      if (var1) {
         return "ethernet";
      } else if (var2) {
         return "wifi";
      } else {
         return var3 ? "cellular" : null;
      }
   }

   public static String recoverMethod_a_e501eb0d(Context var0) {
      if (!recoverMethod_e_9b79()) {
         return "unknown";
      }

      ConnectivityManager var1 = recoverMethod_b_79f2a562();
      if (var1 == null) {
         return "unknown";
      }

      if (RecoverTypeaf2b07ca.recoverMethod_a_5d527815("getCurrentNetType")) {
         return recoverField_a;
      }

      String var2 = "unknown";

      try {
         NetworkInfo var3 = var1.getActiveNetworkInfo();
         if (var3 != null) {
            if (var3.getType() == 1) {
               var2 = "wifi";
            } else if (var3.getType() == 0) {
               var2 = recoverMethod_c_47921032(var3.getSubtype());
            }
         }

         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetworkUtils", "getCurrentNetType by api type=" + var2);
      } catch (Throwable var4) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("NetworkUtils", "getCurrentNetType failed, " + var4.getMessage());
      }

      if (TextUtils.isEmpty(var2) || TextUtils.equals(var2, "unknown")) {
         var2 = RecoverTypeaf2b07d6.recoverMethod_b_e501eb0d(var0);
      }

      recoverField_a = var2;
      return var2;
   }

   private static String recoverMethod_c_47921032(int var0) {
      RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("NetworkUtils", "getNetworkClass networkType:" + var0);
      switch (var0) {
         case 1:
         case 2:
         case 4:
         case 7:
         case 11:
            return "2g";
         case 3:
         case 5:
         case 6:
         case 8:
         case 9:
         case 10:
         case 12:
         case 14:
         case 15:
            return "3g";
         case 13:
            return "4g";
         default:
            switch (var0) {
               case 16:
                  return "2g";
               case 17:
                  return "3g";
               case 18:
               case 19:
                  return "4g";
               case 20:
                  return "5g";
               default:
                  return "unknown";
            }
      }
   }

   public static int recoverMethod_b_f0554de6(Context var0) {
      String var1 = recoverMethod_a_e501eb0d(var0);
      return recoverMethod_a_aad3b1f2(var1);
   }

   private static int recoverMethod_a_aad3b1f2(String var0) {
      if (!TextUtils.isEmpty(var0)) {
         if ("wifi".equalsIgnoreCase(var0)) {
            return 1;
         }

         if ("2g".equals(var0)) {
            return 2;
         }

         if ("3g".equals(var0)) {
            return 3;
         }

         if ("4g".equals(var0)) {
            return 4;
         }

         if ("5g".equals(var0)) {
            return 5;
         }
      }

      return 0;
   }

   public static String recoverMethod_c_e501eb0d(Context var0) {
      if (!recoverMethod_e_9b79()) {
         return "";
      }

      ConnectivityManager var1 = recoverMethod_b_79f2a562();
      if (var1 == null) {
         return "";
      }

      if (RecoverTypeaf2b07ca.recoverMethod_a_4cfd00d7("getNetworkType", 600000L)) {
         return recoverField_b;
      }

      try {
         NetworkInfo var2 = var1.getActiveNetworkInfo();
         if (null == var2) {
            return "unknown";
         }

         String var3 = var2.getTypeName();
         String var4 = var2.getSubtypeName();
         if (null == var3) {
            var3 = "unknown";
         } else if (!RecoverTypeaf2b07d5.recoverMethod_a_aad3b203(var4)) {
            var3 = var3 + "," + var4;
         }

         recoverField_b = var3.toLowerCase();
      } catch (Throwable var5) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("NetworkUtils", "getNetworkType failed, " + var5.getMessage());
         return "unknown";
      }

      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetworkUtils", "getNetworkType final networkType: " + recoverField_b);
      return recoverField_b;
   }

   private static boolean recoverMethod_e_9b79() {
      return RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_d_134632(1504);
   }
}
