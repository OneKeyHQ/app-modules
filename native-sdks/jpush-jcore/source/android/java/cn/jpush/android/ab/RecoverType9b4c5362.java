package cn.jpush.android.ab;

import android.content.Context;
import cn.jiguang.api.JCoreManager;
import cn.jpush.android.cache.Key;
import cn.jpush.android.cache.RecoverTypea0793a19;
import cn.jpush.android.cache.RecoverTypea2146d45;
import cn.jpush.android.helper.JCoreHelper;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.local.ActionHelper;
import cn.jpush.android.x.RecoverType2e4d193c;

public class RecoverType9b4c5362 {
   public static String recoverField_a = "JPushStatusUtils";
   private static boolean recoverField_b = false;

   public static void recoverMethod_a_304871f2(Context var0, long var1, int var3) {
      Logger.recoverMethod_d_e925fa09(recoverField_a, "dealCallBack code=" + var3);
      RecoverType9b4c5364.Memberf9850f21 var4 = RecoverType9b4c5364.recoverMethod_a_d8c38282().recoverMethod_a_2d280f22(var0, var1, var3);
      int var5 = -1;
      if (var4 != null) {
         var5 = var4.recoverField_b;
      }

      recoverMethod_a_3047fd93(var0, var3, var5);
   }

   public static void recoverMethod_a_3047fd93(Context var0, int var1, int var2) {
      if (var1 == 0) {
         if (0 == var2) {
            if (recoverField_b) {
               recoverField_b = false;
               RecoverTypea0793a19.set(var0, Key.PushStatusSyncTime().set(System.currentTimeMillis()));
            } else {
               RecoverType2e4d193c.recoverMethod_a_4abe089d(var0, 2007, var1, "set success");
            }

            RecoverTypea2146d45.recoverMethod_a_1a54e370(var0, 1);
            JCoreManager.onEvent(var0, "JPUSH", 0, null, null);
            ActionHelper.getInstance().doSingleAction(var0, "third_stop", null);
         } else if (1 == var2) {
            RecoverType2e4d193c.recoverMethod_a_4abe089d(var0, 2006, var1, "set success");
         }
      } else if (0 == var2) {
         RecoverType2e4d193c.recoverMethod_a_4abe089d(var0, 2007, var1, "set fail");
      } else if (1 == var2) {
         RecoverType2e4d193c.recoverMethod_a_4abe089d(var0, 2006, var1, "set fail");
      }
   }

   public static void recoverMethod_a_f0554df3(Context var0) {
      try {
         long var1 = System.currentTimeMillis();
         long var3 = (Long)RecoverTypea0793a19.get(var0, Key.PushStatusSyncTime());
         long var5 = Math.abs(var1 - var3);
         if (var5 > -1702967296L) {
            boolean var7 = RecoverTypea2146d45.recoverMethod_e_f0554df7(var0);
            Logger.recoverMethod_d_e925fa09(recoverField_a, "push current status  =" + (var7 ? "stop " : "resume"));
            if (var7) {
               recoverField_b = true;
               JCoreHelper.runActionWithService(var0, "JPUSH", "resume", null);
               ActionHelper.getInstance().setPushStatusWithCallBack(var0, 4, 0);
            }
         }
      } catch (Throwable var8) {
         Logger.recoverMethod_d_e925fa09(recoverField_a, "syncCurrentStatus throwable=" + var8);
      }
   }
}
