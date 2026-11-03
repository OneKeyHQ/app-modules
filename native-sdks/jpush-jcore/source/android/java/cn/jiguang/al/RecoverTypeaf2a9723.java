package cn.jiguang.al;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Pair;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.bk.RecoverTypeaf2b07cf;
import cn.jiguang.bk.RecoverTypeaf2b07d0;
import cn.jiguang.bk.RecoverTypeaf2b07d5;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import cn.jiguang.internal.JConstants;
import java.util.ArrayList;
import java.util.Locale;

public class RecoverTypeaf2a9723 {
   private static String recoverField_a = "";
   private static String recoverField_b = null;
   private static Boolean recoverField_c;
   private static Pair recoverField_d;
   private static final ArrayList recoverField_e = new ArrayList();
   private static final ArrayList recoverField_f = new ArrayList();
   private static final ArrayList recoverField_g = new ArrayList();
   private static final ArrayList recoverField_h = new ArrayList();

   public static String recoverMethod_a_7fee7477(Bundle var0, String var1) {
      if (var0 == null) {
         return null;
      }

      Object var2 = var0.get(var1);
      return var2 == null ? null : var2.toString();
   }

   public static String recoverMethod_a_e501eb0d(Context var0) {
      try {
         if (recoverField_c == null && !TextUtils.isEmpty(JConstants.APP_KEY)) {
            return JConstants.APP_KEY;
         }
      } catch (Throwable var2) {
         recoverField_c = false;
      }

      recoverMethod_b_e501eb0d(var0);
      return recoverField_a;
   }

   public static String recoverMethod_b_e501eb0d(Context var0) {
      if (!TextUtils.isEmpty(recoverField_a)) {
         return recoverField_a;
      }

      if (TextUtils.isEmpty(recoverField_a)) {
         try {
            if (var0 != null) {
               ApplicationInfo var1 = var0.getPackageManager().getApplicationInfo(var0.getPackageName(), 128);
               String var2 = "";
               if (var1 != null && var1.metaData != null) {
                  var2 = recoverMethod_a_7fee7477(var1.metaData, "JPUSH_APPKEY");
                  if (!TextUtils.isEmpty(var2)) {
                     var2 = var2.toLowerCase(Locale.getDefault());
                  }
               }

               if (!TextUtils.isEmpty("") && !TextUtils.isEmpty(var2)) {
                  String var3 = "".toLowerCase(Locale.getDefault());
                  if (var3.contains(var2)) {
                     recoverField_a = var2;
                  } else {
                     RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("CheckManifestHelper", "[key-step]appkey " + var2 + " not in limitAppkeys:" + var3);
                     RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09(
                        "CheckManifestHelper",
                        "[key-step]You use the customized sdk, but your appkey not in valid appkey list. If you want to enable it, please contact us."
                     );
                  }
               } else {
                  recoverField_a = var2;
               }
            } else {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("CheckManifestHelper", "[getAppKey] context is null");
            }
         } catch (Throwable var4) {
         }
      }

      return recoverField_a;
   }

   public static void recoverMethod_a_5ffc00fd(Context var0, String var1) {
      recoverField_b = var1;
      RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_O_48794abf().recoverMethod_a_4e9722a3(var1));
      recoverMethod_c_e501eb0d(var0);
   }

   public static String recoverMethod_c_e501eb0d(Context var0) {
      if (recoverField_b == null) {
         try {
            if (var0 != null) {
               recoverField_b = RecoverType5a68d10.recoverMethod_c_e501eb0d(var0);
               if (recoverField_b != null) {
                  RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("CheckManifestHelper", "get option channel - " + recoverField_b);
               } else {
                  ApplicationInfo var1 = var0.getPackageManager().getApplicationInfo(var0.getPackageName(), 128);
                  if (var1 != null && var1.metaData != null) {
                     recoverField_b = recoverMethod_a_7fee7477(var1.metaData, "JPUSH_CHANNEL");
                     if (!TextUtils.isEmpty(recoverField_b)) {
                        recoverField_b = RecoverTypeaf2b07d5.recoverMethod_b_e96ea081(recoverField_b);
                     }
                  }

                  RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("CheckManifestHelper", "manifest:channel - " + recoverField_b);
               }
            }
         } catch (Throwable var2) {
         }
      }

      return recoverField_b;
   }

   public static Pair recoverMethod_d_49a4c97d(Context var0) {
      if (recoverField_d == null) {
         try {
            PackageInfo var1 = RecoverTypeaf2b07cf.recoverMethod_a_9537863d(var0, 0);
            if (null != var1) {
               String var2 = var1.versionName;
               byte[] var3 = var2.getBytes("UTF-8");
               if (var3.length > 30) {
                  var2 = new String(var3, 0, 30);
               }

               long var4 = recoverMethod_a_56ff7e40(var1);
               recoverField_d = new Pair(var2, var4);
            } else {
               RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("CheckManifestHelper", "NO versionCode or versionName defined in manifest.");
            }
         } catch (Throwable var6) {
            RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("CheckManifestHelper", "NO versionCode or versionName defined in manifest.");
         }
      }

      return recoverField_d;
   }

   public static long recoverMethod_a_56ff7e40(PackageInfo var0) {
      return VERSION.SDK_INT >= 28 ? var0.getLongVersionCode() : recoverMethod_b_56ff7e40(var0);
   }

   private static long recoverMethod_b_56ff7e40(PackageInfo var0) {
      return var0.versionCode;
   }

   public static int recoverMethod_e_f0554de6(Context var0) {
      String var1 = recoverMethod_a_e501eb0d(var0);
      if (TextUtils.isEmpty(var1)) {
         RecoverTypeaf2a9ae9.recoverMethod_n_e925fa09("CheckManifestHelper", "errorcode:10001,metadata: JCore appKey - not defined in manifest");
         RecoverTypeaf2b07c1.recoverMethod_a_9f8491a6(var0, " 未在manifest中配置AppKey", -1);
         return 10001;
      } else if (var1.length() != 24) {
         RecoverTypeaf2a9ae9.recoverMethod_n_e925fa09(
            "CheckManifestHelper", "errorcode:1008,Invalid appKey : " + var1 + ", Please get your Appkey from JIGUANG web console!"
         );
         String var2 = " AppKey:" + var1 + " 是无效的AppKey,请确认与JIGUANG web端的AppKey一致";
         RecoverTypeaf2b07c1.recoverMethod_a_9f8491a6(var0, var2, -1);
         return 1008;
      } else {
         return 0;
      }
   }

   public static int recoverMethod_f_f0554de6(Context var0) {
      recoverMethod_c_e501eb0d(var0);
      if (RecoverTypeaf2a9725.recoverMethod_a_c692b321().recoverMethod_g_9b79() || RecoverTypeaf2a9725.recoverMethod_a_c692b321().recoverMethod_f_9b79()) {
         String var1 = var0.getPackageName() + RecoverType5a681cd.recoverField_a;
         if (!RecoverTypeaf2b07d0.recoverMethod_b_5ffc0101(var0, var1)) {
            RecoverTypeaf2a9ae9.recoverMethod_m_e925fa09("CheckManifestHelper", "The permission should be defined - " + var1);
            return 1001;
         }

         recoverField_f.add(var1);
      }

      for (Object recoveryElement_190 : recoverField_f) {
         String var2 = (String)recoveryElement_190;
         if (!RecoverTypeaf2b07d0.recoverMethod_a_5ffc0101(var0, var2)) {
            RecoverTypeaf2a9ae9.recoverMethod_m_e925fa09("CheckManifestHelper", "The permissoin is required - " + var2);
            return 1002;
         }
      }

      for (Object recoveryElement_197 : recoverField_g) {
         String var7 = (String)recoveryElement_197;
         if (!RecoverTypeaf2b07d0.recoverMethod_b_5ffc0101(var0, var7)) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("CheckManifestHelper", "We recommend you add the permission - " + var7);
         }
      }

      for (Object recoveryElement_203 : recoverField_h) {
         String var8 = (String)recoveryElement_203;
         if (!RecoverTypeaf2b07d0.recoverMethod_b_5ffc0101(var0, var8)) {
            RecoverTypeaf2a9ae9.recoverMethod_k_e925fa09(
               "CheckManifestHelper", "We recommend you add the permission - " + var8 + ",Otherwise set badge number will failed"
            );
         }
      }

      for (Object recoveryElement_211 : recoverField_e) {
         String var9 = (String)recoveryElement_211;
         if (!RecoverTypeaf2b07d0.recoverMethod_b_5ffc0101(var0, var9)) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09(
               "CheckManifestHelper", "We recommend you add the permission - " + var9 + ", otherwise you can not locate the devices."
            );
         }
      }

      return 0;
   }

   static {
      recoverField_f.add("android.permission.INTERNET");
      recoverField_f.add("android.permission.ACCESS_NETWORK_STATE");
      recoverField_h.add("com.huawei.android.launcher.permission.CHANGE_BADGE");
      recoverField_h.add("com.vivo.notification.permission.BADGE_ICON");
      recoverField_h.add("com.hihonor.android.launcher.permission.CHANGE_BADGE");
   }
}
