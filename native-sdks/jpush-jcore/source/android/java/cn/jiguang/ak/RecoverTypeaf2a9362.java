package cn.jiguang.ak;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import cn.jiguang.ai.RecoverTypeaf2a8be2;
import cn.jiguang.al.RecoverTypeaf2a9723;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bk.RecoverTypeaf2b07cb;
import cn.jiguang.i.RecoverType5a69fd7;
import java.util.ArrayList;

public class RecoverTypeaf2a9362 {
   private static final ArrayList recoverField_a = new ArrayList();
   private static final ArrayList recoverField_b = new ArrayList();
   private static boolean recoverField_c;
   private static Boolean recoverField_d;
   private static final String recoverField_e = "Xiaomi".toLowerCase();

   public static void recoverMethod_a_1a552341(Context var0, boolean var1) {
      try {
         if (recoverField_c == var1) {
            return;
         }

         recoverField_c = var1;
         recoverMethod_a_78b907d2(
            var0, var1, "cn.jpush.android.intent.DaemonService", Class.forName("cn.jpush.android.service.DaemonService", false, var0.getClassLoader())
         );
         recoverMethod_b_cf7cfe1c(var0, var1, Class.forName("cn.jpush.android.service.DownloadProvider", false, var0.getClassLoader()));
         recoverMethod_a_cf7cfe1c(var0, var1, Class.forName("cn.jpush.android.service.DActivity", false, var0.getClassLoader()));
         recoverMethod_a_cf7cfe1c(var0, var1, Class.forName("cn.jpush.android.service.BActivity", false, var0.getClassLoader()));
      } catch (Throwable var3) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JComponentHelper", "setComponentEnable failed, " + var3.getMessage());
      }
   }

   private static void recoverMethod_a_cf7cfe1c(Context var0, boolean var1, Class var2) {
      try {
         if (var0 == null) {
            RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JComponentHelper", "context is null, give up setComponentEnabled");
            return;
         }

         if (var2 == null) {
            return;
         }

         PackageManager var3 = var0.getApplicationContext().getPackageManager();
         if (var3 == null) {
            RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JComponentHelper", "PackageManager is null, give up setComponentEnabled");
            return;
         }

         int var4 = var1 ? 1 : 2;
         ComponentName var5 = new ComponentName(var0, var2);
         int var6 = var3.getComponentEnabledSetting(var5);
         if (var6 == var4) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JComponentHelper", var5.getClassName() + " enabled is :" + var1 + ", no need repeat set.");
            return;
         }

         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JComponentHelper", var5 + " setActivityEnabledSetting newState: " + var4);
         var3.setComponentEnabledSetting(var5, var4, 1);
      } catch (Throwable var7) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JComponentHelper", "setActivityEnabled throwable:" + var7.getMessage());
      }
   }

   private static void recoverMethod_a_78b907d2(Context var0, boolean var1, String var2, Class var3) {
      try {
         if (var0 == null) {
            RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JComponentHelper", "context is null, give up setComponentEnabled");
            return;
         }

         PackageManager var4 = var0.getApplicationContext().getPackageManager();
         if (var4 == null) {
            RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JComponentHelper", "PackageManager is null, give up setComponentEnabled");
            return;
         }

         String var5 = var0.getPackageName();
         int var6 = var1 ? 1 : 2;
         Intent var7 = new Intent();
         var7.setPackage(var5);
         var7.setAction(var2);
         ResolveInfo var8 = var4.resolveService(var7, 512);
         if (null != var8) {
            ServiceInfo var9 = var8.serviceInfo;
            if (var9 == null) {
               return;
            }

            String var10 = var9.name;
            if (TextUtils.isEmpty(var10)) {
               return;
            }

            try {
               Class var11 = Class.forName(var10);
               if (var11 == null) {
                  return;
               }

               if (var3.isAssignableFrom(var11)) {
                  ComponentName var12 = new ComponentName(var9.packageName, var9.name);
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JComponentHelper", var12 + " setComponentEnabledSetting newState: " + var6);
                  int var13 = var4.getComponentEnabledSetting(var12);
                  if (var13 == var6) {
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JComponentHelper", "DaemonService  enabled is :" + var1 + ", no need repeat set.");
                     return;
                  }

                  var4.setComponentEnabledSetting(var12, var6, 1);
               } else {
                  RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JComponentHelper", "give up setting, as " + var10 + " is not extend from: " + var3.getName());
               }
            } catch (ClassNotFoundException var14) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JComponentHelper", "cant't find service class:" + var10);
            }
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JComponentHelper", "cant't find DaemonService");
         }
      } catch (Throwable var15) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JComponentHelper", "setServiceEnabled throwable:" + var15.getMessage());
      }
   }

   private static void recoverMethod_b_cf7cfe1c(Context var0, boolean var1, Class var2) {
      try {
         if (var0 == null) {
            RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JComponentHelper", "context is null, give up setComponentEnabled");
            return;
         }

         PackageManager var3 = var0.getApplicationContext().getPackageManager();
         if (var3 == null) {
            RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JComponentHelper", "PackageManager is null, give up setComponentEnabled");
            return;
         }

         int var4 = var1 ? 1 : 2;
         ComponentName var5 = new ComponentName(var0, var2);
         int var6 = var3.getComponentEnabledSetting(var5);
         if (var6 == var4) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JComponentHelper", var5.getClassName() + " enabled is :" + var1 + ", no need repeat set.");
            return;
         }

         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JComponentHelper", var5 + " setDownloadProviderEnabledSetting newState: " + var4);
         var3.setComponentEnabledSetting(var5, var4, 1);
      } catch (Throwable var7) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JComponentHelper", "setContentProviderEnabled throwable:" + var7.getMessage());
      }
   }

   static RecoverTypeaf2a8be2 recoverMethod_a_35f2ae(Context var0, PackageManager var1, String var2, String var3) {
      if (var1 != null && !TextUtils.isEmpty(var2)) {
         try {
            int var4 = var1.checkPermission(var2 + ".permission.JPUSH_MESSAGE", var2);
            ApplicationInfo var5 = var1.getApplicationInfo(var2, 128);
            if (var5 != null && var5.metaData != null) {
               String var6 = RecoverTypeaf2a9723.recoverMethod_a_e501eb0d(var0);
               Intent var7 = new Intent();
               var7.setClassName(var2, "cn.jpush.android.service.PushService");
               ResolveInfo var8 = var1.resolveService(var7, 0);
               boolean var9 = null != var8;
               if (0 == var4 && var9 && !TextUtils.isEmpty(var6) && var6.length() == 24) {
                  RecoverTypeaf2a8be2 var10 = new RecoverTypeaf2a8be2(var2, var3, var5.targetSdkVersion);
                  var10.recoverField_d = var2 + ".DownloadProvider";
                  return var10;
               }
            }
         } catch (NameNotFoundException var11) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JComponentHelper", "checkWhetherToStart exception:" + var11.toString());
         } catch (Throwable var12) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JComponentHelper", "checkWhetherToStart throwable:" + var12.getMessage());
         }

         return null;
      } else {
         return null;
      }
   }

   public static boolean recoverMethod_a_f0554df7(Context var0) {
      if (recoverField_d != null) {
         return recoverField_d;
      }

      if (recoverMethod_b_f0554df7(var0)) {
         recoverField_d = true;
      } else {
         recoverField_d = false;
      }

      return recoverField_d;
   }

   private static boolean recoverMethod_b_f0554df7(Context var0) {
      try {
         String var1 = RecoverType5a69fd7.Member367e0ad4.recoverField_b;
         if (!TextUtils.isEmpty(var1) && TextUtils.equals(recoverField_e, var1.toLowerCase())) {
            return true;
         }
      } catch (Throwable var2) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JComponentHelper", "get MANUFACTURER failed - error:" + var2);
      }

      return false;
   }

   private static boolean recoverMethod_a_aad3b203(String var0) {
      boolean var1 = recoverField_a.contains(var0);
      RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("JComponentHelper", "package[" + var0 + "] need check activity enable state, " + !var1);
      return !var1;
   }

   public static boolean recoverMethod_a_3441485e(Context var0, String var1, Intent var2, boolean var3) {
      try {
         PackageManager var4 = var0.getPackageManager();
         if (var4 == null) {
            return false;
         }

         var2.setPackage(var1);
         if (!var3) {
            var2.setAction("cn.jpush.android.intent.DActivity");
            var2.addCategory(var1);
         }

         ResolveInfo var5 = var4.resolveActivity(var2, 0);
         if (var5 == null) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JComponentHelper", "getDActivity resolveInfo was null from:" + var2);
            return false;
         }

         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
            "JComponentHelper",
            "target actvity name:"
               + var5.activityInfo.name
               + ", theme:"
               + var5.activityInfo.theme
               + ", exported:"
               + var5.activityInfo.exported
               + ", enable: "
               + var5.activityInfo.enabled
         );
         if (var5.activityInfo.exported && (!recoverMethod_a_aad3b203(var1) || var5.activityInfo.enabled)) {
            if (!recoverField_b.contains(var1) && var5.activityInfo.theme != 16973840) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09(
                  "JComponentHelper", var5.activityInfo.name + "activity theme must config as @android:style/Theme.Translucent.NoTitleBar"
               );
               return false;
            } else {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JComponentHelper", "dIntent:" + var2);
               return true;
            }
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JComponentHelper", "activity muse be exported and enabled");
            return false;
         }
      } catch (Throwable var6) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JComponentHelper", "get deeplink activity error#" + var6);
         return false;
      }
   }

   static {
      recoverField_a.add(
         RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{122, 103, 123, 63, 67, 79, 113, 125, 56, 127, 85, 87, 106, 107, 122, 120, 85, 78, 109})
      );
      recoverField_b.add(
         RecoverTypeaf2b07cb.recoverMethod_b_79834524(
            new byte[]{
               107,
               121,
               124,
               30,
               84,
               124,
               102,
               117,
               116,
               94,
               84,
               55,
               105,
               120,
               117,
               66,
               79,
               112,
               108,
               56,
               96,
               65,
               68,
               118,
               127,
               120,
               125,
               95,
               65,
               125,
               109,
               100
            }
         )
      );
   }
}
