package cn.jiguang.al;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.os.Bundle;
import android.text.TextUtils;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa627;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.bk.RecoverTypeaf2b07c4;
import cn.jpush.android.service.JCommonService;

public class RecoverTypeaf2a9726 {
   private static String recoverField_a;
   private static String recoverField_b;

   private RecoverTypeaf2a9726() {
   }

   public static RecoverTypeaf2a9726 recoverMethod_a_c692b340() {
      return RecoverTypeaf2a9726.Member8ee16a63.recoverField_a;
   }

   public void recoverMethod_a_d19840b7(Context var1, String var2, Bundle var3) {
      try {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
            "JCommonServiceHelper", "onAction action:" + var2 + " bundle:" + (var3 == null ? "null" : var3.toString())
         );
         String var4 = recoverMethod_b_e501eb0d(var1);
         if (TextUtils.isEmpty(var4)) {
            RecoverType5a681cd.recoverMethod_b_d19840b7(var1, var2, var3);
            return;
         }

         RecoverTypeaf2a9727.recoverMethod_a_c692b35f().recoverMethod_a_e26f8bf1(var1, var4, var2, var3);
      } catch (Throwable var5) {
         RecoverTypeaf2a9ae9.recoverMethod_f_1765c98c("JCommonServiceHelper", "onAction failed", var5);
      }
   }

   public void recoverMethod_b_d19840b7(Context var1, String var2, Bundle var3) {
      try {
         RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09(
            "JCommonServiceHelper", "callAction action:" + var2 + " bundle:" + (var3 == null ? "null" : var3.toString())
         );
         var1 = RecoverTypeaf2b07c4.recoverMethod_a_6263c3eb(var1);
         RecoverTypeaf2aa627.recoverMethod_a_d19840b7(var1, var2, var3);
      } catch (Throwable var5) {
         RecoverTypeaf2a9ae9.recoverMethod_f_1765c98c("JCommonServiceHelper", "callAction failed", var5);
      }
   }

   public static String recoverMethod_a_e501eb0d(Context var0) {
      if (recoverField_b != null) {
         return recoverField_b;
      } else {
         String var1 = recoverMethod_b_e501eb0d(var0);
         if (TextUtils.isEmpty(var1)) {
            recoverField_b = var0.getPackageName();
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCommonServiceHelper", "user serviceProcess is curProcessName:" + recoverField_b);
            return recoverField_b;
         } else {
            recoverField_b = RecoverTypeaf2b07c1.recoverMethod_a_a4ea6d43(var0, var1);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCommonServiceHelper", "user serviceProcess is:" + recoverField_b);
            return recoverField_b;
         }
      }
   }

   public static String recoverMethod_b_e501eb0d(Context var0) {
      try {
         if (recoverField_a != null) {
            return recoverField_a;
         }

         Intent var1 = new Intent();
         var1.setAction("cn.jiguang.user.service.action");
         var1.setPackage(var0.getPackageName());
         String var2 = RecoverTypeaf2b07c1.recoverMethod_a_aec4ad66(var0, var1, "");
         if (!TextUtils.isEmpty(var2)) {
            Class var3 = Class.forName(var2);
            if (JCommonService.class.isAssignableFrom(var3)) {
               recoverField_a = var2;
               RecoverTypeaf2a9ae9.recoverMethod_h_e925fa09("JCommonServiceHelper", "found userServiceClass :" + recoverField_a + " by getCommonServiceNames");
            }
         }
      } catch (Throwable var4) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCommonServiceHelper", "getUserServiceClass failed:" + var4);
      }

      if (TextUtils.isEmpty(recoverField_a)) {
         ComponentInfo var5 = RecoverTypeaf2b07c1.recoverMethod_a_e3a6cbf6(var0, var0.getPackageName(), JCommonService.class);
         if (var5 != null) {
            recoverField_a = var5.name;
            RecoverTypeaf2a9ae9.recoverMethod_h_e925fa09("JCommonServiceHelper", "found userServiceClass :" + recoverField_a + " by getComponentInfo");
         }
      }

      if (TextUtils.isEmpty(recoverField_a)) {
         recoverField_a = "";
      }

      return recoverField_a;
   }

   private static final class Member8ee16a63 {
      private static final RecoverTypeaf2a9726 recoverField_a = new RecoverTypeaf2a9726();
   }
}
