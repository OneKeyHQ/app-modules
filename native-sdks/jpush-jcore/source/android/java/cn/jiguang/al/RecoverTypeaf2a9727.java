package cn.jiguang.al;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.android.IDataShare;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.internal.ActionManager;
import cn.jiguang.internal.JCoreInternalHelper;
import cn.jpush.android.service.DataShare;
import cn.jpush.android.service.JCommonService;
import cn.jpush.android.service.PushReceiver;
import java.util.HashMap;
import java.util.Map;

public class RecoverTypeaf2a9727 {
   private static Map recoverField_a = new HashMap();

   public static RecoverTypeaf2a9727 recoverMethod_a_c692b35f() {
      return RecoverTypeaf2a9727.Member8ee16e24.recoverField_a;
   }

   private RecoverTypeaf2a9727() {
   }

   public boolean recoverMethod_a_e26f8bf1(Context var1, String var2, String var3, Bundle var4) {
      String var5 = var2 + "." + var3;
      String var6 = null;
      Class var7 = null;

      try {
         var7 = Class.forName(var2);
         if (Service.class.isAssignableFrom(var7)) {
            var6 = RecoverTypeaf2b07c1.recoverMethod_a_a4ea6d43(var1, var2);
            RecoverTypeaf2a9728.Member8ee171e6.recoverMethod_a_6cb381c0().recoverMethod_a_3144747b(var1, var2, var3, var4, var6);
         } else if (BroadcastReceiver.class.isAssignableFrom(var7)) {
            var6 = RecoverTypeaf2b07c1.recoverMethod_b_a4ea6d43(var1, var2);
         }
      } catch (Throwable var10) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JMessenger", var5 + " meet e:" + var10);
      }

      try {
         Bundle var8 = this.recoverMethod_a_1785417b(var1, var2, var3, var4, var6);
         if (var8 != null) {
            return true;
         }

         if (this.recoverMethod_a_79599764(var1, var7, var3, var4)) {
            return true;
         }
      } catch (Throwable var9) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JMessenger", "send, " + var5 + " meet e:" + var9);
      }

      RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JMessenger", "send all failed, " + var5);
      return false;
   }

   public Bundle recoverMethod_a_1785417b(Context var1, String var2, String var3, Bundle var4, String var5) {
      if (!TextUtils.isEmpty(var5)) {
         String var6 = RecoverTypeaf2b07c1.recoverMethod_a_e501eb0d(var1);
         if (!TextUtils.isEmpty(var6)) {
            if (var6.equals(var5)) {
               return this.recoverMethod_b_f41a771(var1, var2, var3, var4);
            }

            Bundle var7 = this.recoverMethod_a_97aea2b9(var2, var3, var4, var5);
            if (var7 != null) {
               return var7;
            }

            var7 = this.recoverMethod_b_1785417b(var1, var2, var3, var4, var5);
            if (var7 != null) {
               return var7;
            }
         }
      }

      return null;
   }

   private Bundle recoverMethod_a_97aea2b9(String var1, String var2, Bundle var3, String var4) {
      String var5 = var1 + "." + var2;

      try {
         IDataShare var6 = DataShare.getInstance(var4);
         if (var6 != null) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JMessenger", "sendByAidl, " + var5);
            if ("INTERNAL_API".equals(var1)) {
               return var6.execute(var1, var2, var3);
            }

            if (RecoverTypeaf2a9728.Member8ee171e6.recoverMethod_a_6cb381c0().recoverMethod_a_df065b4d(var3, var4)) {
               RecoverTypeaf2a9728.Member8ee171e6.recoverMethod_a_6cb381c0().recoverMethod_a_19005124(var6, var4);
            } else {
               var6.onAction(var1, var2, var3);
            }

            return new Bundle();
         }

         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JMessenger", var4 + "'s aidl not found, " + var5);
      } catch (Throwable var7) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JMessenger", "sendByAidl, " + var5 + ", e=" + var7);
      }

      return null;
   }

   private Bundle recoverMethod_b_1785417b(Context var1, String var2, String var3, Bundle var4, String var5) {
      String var6 = var2 + "." + var3;

      try {
         if (VERSION.SDK_INT >= 11) {
            String var7 = recoverMethod_a_a4ea6d43(var1, var5);
            if (!TextUtils.isEmpty(var7)) {
               Uri var8 = Uri.parse(var7);
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JMessenger", "sendByProvider, uri=" + var7 + ", " + var6);
               if (RecoverTypeaf2a9728.Member8ee171e6.recoverMethod_a_6cb381c0().recoverMethod_a_df065b4d(var4, var5)) {
                  return RecoverTypeaf2a9728.Member8ee171e6.recoverMethod_a_6cb381c0().recoverMethod_a_f46529c2(var1, var8, var5);
               }

               return var1.getContentResolver().call(var8, var2, var3, var4);
            }

            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JMessenger", "uri is null, check provider config");
         }
      } catch (Throwable var9) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JMessenger", "provider call:" + var9);
      }

      return null;
   }

   private static String recoverMethod_a_a4ea6d43(Context var0, String var1) {
      String var2 = (String)recoverField_a.get(var1);
      if (!TextUtils.isEmpty(var2)) {
         return var2;
      } else {
         String var3 = var0.getPackageName() + ".DataProvider";
         ProviderInfo var4 = RecoverTypeaf2b07c1.recoverMethod_a_49ff051d(var0, var0.getPackageName(), var3);
         String var5 = var4 != null ? var4.processName : null;
         if (var1.equals(var5)) {
            var2 = "content://" + var3 + "/";
            recoverField_a.put(var1, var2);
            return var2;
         } else {
            var3 = var0.getPackageName() + ".DownloadProvider";
            var4 = RecoverTypeaf2b07c1.recoverMethod_a_49ff051d(var0, var0.getPackageName(), var3);
            var5 = var4 != null ? var4.processName : null;
            if (var1.equals(var5)) {
               var2 = "content://" + var3 + "/";
               recoverField_a.put(var1, var2);
               return var2;
            } else {
               return "";
            }
         }
      }
   }

   private boolean recoverMethod_a_79599764(Context var1, Class var2, String var3, Bundle var4) {
      if (var2 == null) {
         return false;
      }

      String var5 = var2.getName() + "." + var3;

      try {
         Intent var6 = new Intent();
         var6.setClass(var1, var2);
         var6.setAction(var3);
         var6.setPackage(var1.getPackageName());
         if (var4 != null) {
            var6.putExtras(var4);
         }

         if (BroadcastReceiver.class.isAssignableFrom(var2)) {
            var1.sendBroadcast(var6);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JMessenger", "sendByReceiver, " + var5);
            return true;
         }

         if (Service.class.isAssignableFrom(var2)) {
            ComponentName var7 = var1.startService(var6);
            if (var7 != null) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JMessenger", "sendByService, " + var5);
               return true;
            }

            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JMessenger", "startService, " + var5 + ", can't find component");
         }
      } catch (Throwable var8) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JMessenger", "sendByComponent, " + var5 + " e=" + var8);
      }

      return false;
   }

   public Bundle recoverMethod_b_f41a771(Context var1, String var2, String var3, Bundle var4) {
      try {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JMessenger", "directHandle, " + var2 + "." + var3);
         if ("INTERNAL_API".equals(var2)) {
            return RecoverTypeaf2a9729.recoverMethod_a_c692b39d().recoverMethod_b_fa0ea5f9(var3, var4);
         }

         Class var5 = Class.forName(var2);
         if (!BroadcastReceiver.class.isAssignableFrom(var5) && !Service.class.isAssignableFrom(var5)) {
            RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JMessenger", "directHandle, not support " + var2 + "." + var3);
            return null;
         }

         if (JCommonService.class.isAssignableFrom(var5)) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JMessenger", "JCommonService handle succeed," + var2 + "." + var3);
            JCoreInternalHelper.getInstance().onEvent(var1, "JCore", 2, true, var3, var4);
            return new Bundle();
         }

         Intent var6 = new Intent(var3);
         var6.setClass(var1, var5);
         if (var4 != null) {
            var6.putExtras(var4);
         }

         var6.setPackage(var1.getPackageName());
         var6.addCategory(var1.getPackageName());
         if (PushReceiver.class.isAssignableFrom(var5)) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JMessenger", "PushReceiver handle succeed," + var2 + "." + var3);
            String var7 = var4 != null ? var4.getString("sdktype") : null;
            ActionManager.getInstance().handleMessage(var1, var7, var6);
         } else if (BroadcastReceiver.class.isAssignableFrom(var5)) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JMessenger", "Receiver onReceive," + var2 + "." + var3);
            ((BroadcastReceiver)var5.newInstance()).onReceive(var1, var6);
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JMessenger", "Service onStartCommand," + var2 + "." + var3);
            ((Service)var5.newInstance()).onStartCommand(var6, 0, 0);
         }

         return new Bundle();
      } catch (Throwable var8) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JMessenger", "directHandle e:" + var8);
         return null;
      }
   }

   private static final class Member8ee16e24 {
      private static final RecoverTypeaf2a9727 recoverField_a = new RecoverTypeaf2a9727();
   }
}
