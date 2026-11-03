package cn.jiguang.ap;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.af.RecoverTypeaf2a809d;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.at.RecoverTypeaf2ab52c;
import cn.jiguang.at.RecoverTypeaf2ab52d;
import cn.jiguang.at.RecoverTypeaf2ab530;
import cn.jiguang.at.RecoverTypeaf2ab531;
import cn.jiguang.bb.RecoverTypeaf2ae5f9;
import cn.jiguang.bk.RecoverTypeaf2b07c4;
import cn.jiguang.c.RecoverType5a68950;
import cn.jiguang.internal.JConstants;

public class RecoverTypeaf2aa627 {
   private static boolean recoverField_a;

   public static synchronized void recoverMethod_a_f0554df3(Context var0) {
      try {
         int var1 = RecoverTypeaf2a809d.recoverMethod_a_9b68();
         int var2 = RecoverTypeaf2a809d.recoverMethod_b_9b68();
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreActionImpl", "custom:" + var1 + ",dynamic:" + var2);
         RecoverType5a68950.recoverMethod_a_d0b52086(var0, "core", var1, var2, RecoverType5a681cd.recoverField_c);
         RecoverTypeaf2ab52d.recoverMethod_b_f0554df3(RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d());
      } catch (Throwable var3) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JCoreActionImpl", "sdk type call failed:" + var3.getMessage());
      }
   }

   private static void recoverMethod_a_9b75() {
      if (!recoverField_a) {
         recoverField_a = true;
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
            "JCoreActionImpl", "init jcore impl ,version:" + RecoverType5a681cd.recoverField_b + ",local version:" + JConstants.SDK_VERSION_INT
         );
         RecoverTypeaf2ab52d.recoverMethod_a_f0554df7(RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d());
      }
   }

   public static void recoverMethod_a_d19840b7(Context var0, String var1, Bundle var2) {
      recoverMethod_a_9b75();
      if (TextUtils.isEmpty(var1)) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JCoreActionImpl", "handleAction Failed,action is empty");
      } else {
         boolean var3 = JConstants.isCallInit.get();
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreActionImpl", " isCallInit=" + var3);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreActionImpl", "handleAction action:" + var1);
         String var4 = "";
         if (var2 != null) {
            var4 = var2.getString("sdk_type");
         }

         if (var1.equals("a1")) {
            try {
               String var5 = var2 != null ? var2.getString("report_data") : null;
               RecoverTypeaf2aa628.recoverMethod_a_127ac70f(var0, var5);
            } catch (Throwable var7) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCoreActionImpl", "report failed:" + var7.getMessage());
            }
         } else if (var1.equals("a12")) {
            try {
               String var8 = var2 != null ? var2.getString("save_data") : null;
               RecoverTypeaf2ae5f9.recoverMethod_a_c69c3ecd().recoverMethod_a_aad3b1ff(var8);
            } catch (Throwable var6) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCoreActionImpl", "report failed:" + var6.getMessage());
            }
         } else if (var1.startsWith("tcp_")) {
            if (!var3) {
               return;
            }

            RecoverTypeaf2ab531.recoverMethod_a_c6965695().recoverMethod_a_d19840b7(var0, var1, var2);
         } else if (var1.equals("a2")) {
            if (!var3) {
               return;
            }

            RecoverTypeaf2ab530.recoverMethod_a_c6965676().recoverMethod_a_1a552341(var0, true);
         } else if (var1.equals("a3")) {
            RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_a_d19840b7(var0, var4, var2);
         } else if (var1.equals("a4")) {
            RecoverTypeaf2aa628.recoverMethod_a_69c77401(var0, var2);
         }
      }
   }
}
