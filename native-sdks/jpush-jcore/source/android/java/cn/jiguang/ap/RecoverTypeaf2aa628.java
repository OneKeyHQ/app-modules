package cn.jiguang.ap;

import android.content.Context;
import android.os.Bundle;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ao.RecoverTypeaf2aa269;
import cn.jiguang.ao.RecoverTypeaf2aa26b;
import cn.jiguang.api.JCoreManager;
import cn.jiguang.at.RecoverTypeaf2ab531;
import cn.jiguang.bj.RecoverTypeaf2b0403;
import cn.jiguang.bk.RecoverTypeaf2b07c8;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.d.RecoverType5a68d11;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import cn.jiguang.internal.JConstants;
import cn.jiguang.m.RecoverType5a6aed9;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONObject;

public class RecoverTypeaf2aa628 {
   public static int recoverField_a = 0;
   public static AtomicBoolean recoverField_b = new AtomicBoolean();

   public static void recoverMethod_a_d97c5eb5(Context var0, JSONObject var1, String var2) {
      JSONObject var3 = RecoverType5a68d10.recoverMethod_a_1b76323(var0, var1, var2);
      RecoverTypeaf2aa26b.recoverMethod_a_127ac70f(var0, var3);
   }

   public static void recoverMethod_b_d97c5eb5(Context var0, JSONObject var1, String var2) {
      JSONObject var3 = RecoverType5a68d10.recoverMethod_a_1b76323(var0, var1, var2);
      JCoreManager.onEvent(var0, "JCore", 14, null, null, var3);
   }

   public static void recoverMethod_a_a4fc3973(Runnable var0) {
      RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("NORMAL_TASK", var0);
   }

   public static void recoverMethod_b_a4fc3973(Runnable var0) {
      RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("SCHEDULE_TASK", var0);
   }

   public static void recoverMethod_c_a4fc3973(Runnable var0) {
      RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("MAJOR_TASK", var0);
   }

   public static void recoverMethod_d_a4fc3973(Runnable var0) {
      RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("NORMAL_TASK", var0);
   }

   public static void recoverMethod_e_a4fc3973(Runnable var0) {
      RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("FUTURE_TASK", var0);
   }

   public static void recoverMethod_a_fa8b67f0(Runnable var0, int var1) {
      RecoverTypeaf2b0403.recoverMethod_a_63b8aba6("DELAY_TASK", var0, var1);
   }

   public static void recoverMethod_a_3b017c5(Context var0, long var1, String var3, String var4) {
      RecoverType5a690d2.recoverMethod_b_abcb8d40(
         var0,
         RecoverType5a690d1.recoverMethod_B_48794abf().recoverMethod_a_4e9722a3(var1),
         RecoverType5a690d1.recoverMethod_E_48794abf().recoverMethod_a_4e9722a3(var3),
         RecoverType5a690d1.recoverMethod_C_48794abf().recoverMethod_a_4e9722a3(var4),
         RecoverType5a690d1.recoverMethod_D_48794abf().recoverMethod_a_4e9722a3(System.currentTimeMillis())
      );
   }

   public static void recoverMethod_a_5ffc00fd(Context var0, String var1) {
      RecoverTypeaf2b07c8.recoverMethod_a_5ffc00fd(var0, var1);
   }

   public static void recoverMethod_a_1a54e731(Context var0, long var1) {
      if (var1 > 0L) {
         RecoverType5a68d11.recoverMethod_b_1a54e731(var0, var1);
      }
   }

   public static void recoverMethod_a_f0554df3(Context var0) {
      RecoverType5a690d2.recoverMethod_a_5ffc00fd(var0, "cn.jiguang.sdk.user.profile");
   }

   public static void recoverMethod_a_127ac70f(Context var0, Object var1) {
      RecoverTypeaf2aa26b.recoverMethod_a_127ac70f(var0, var1);
   }

   public static void recoverMethod_b_f0554df3(Context var0) {
      RecoverTypeaf2aa269.recoverMethod_a_f0554df3(var0);
   }

   public static void recoverMethod_a_1a54e370(Context var0, int var1) {
      RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_G_48794abf().recoverMethod_a_4e9722a3(var1));
   }

   public static void recoverMethod_a_69c77401(Context var0, Bundle var1) {
      if (var1 != null) {
         recoverField_b.set(var1.getBoolean("foreground"));
         recoverField_a = var1.getInt("user_type");
         if (recoverField_a == -1) {
            recoverField_a = recoverField_b.get() ? 1 : 0;
         }

         boolean var2 = JConstants.isCallInit.get();
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
            "JCoreManagerInternal", "changeForeGroundStat:" + recoverField_b.get() + ", userActiveType:" + recoverField_a + " isCallInit=" + var2
         );
         if (recoverField_b.get()) {
            if (!var2) {
               return;
            }

            if (RecoverTypeaf2ab531.recoverMethod_a_c6965695().recoverMethod_f_9b68() >= 120) {
               RecoverTypeaf2ab531.recoverMethod_a_c6965695().recoverMethod_i_9b75();
            }
         }

         RecoverType5a6aed9.recoverMethod_a_b00c10b9(var0, "foreground_state_change", var1);
      }
   }
}
