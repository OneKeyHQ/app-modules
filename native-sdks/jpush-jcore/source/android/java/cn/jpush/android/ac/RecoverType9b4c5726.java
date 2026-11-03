package cn.jpush.android.ac;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import cn.jpush.android.d.RecoverType2e4cce2a;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.helper.RecoverType8b455831;
import cn.jpush.android.x.RecoverType2e4d193c;
import cn.jpush.android.x.RecoverType2e4d193e;
import java.util.LinkedList;

public class RecoverType9b4c5726 {
   public static int recoverField_a = 0;

   public static void recoverMethod_a_87826923(Context var0, String var1, String var2, int var3, byte var4, int var5) {
      if (var0 == null) {
         Logger.recoverMethod_ww_e925fa09("PluginPlatformsNotificationHelper", "context was null");
      } else if (TextUtils.isEmpty(var1)) {
         Logger.recoverMethod_ww_e925fa09("PluginPlatformsNotificationHelper", "content was null");
      } else {
         Logger.recoverMethod_v_e925fa09("PluginPlatformsNotificationHelper", "message content:" + var1);
         RecoverType2e4cce2a var6 = recoverMethod_a_39f271f4(var0, var1, var2);
         Logger.recoverMethod_v_e925fa09("PluginPlatformsNotificationHelper", "entity:" + var6);
         if (var6 == null) {
            Logger.recoverMethod_ww_e925fa09("PluginPlatformsNotificationHelper", "entity was null");
         } else if (TextUtils.isEmpty(var6.recoverField_d)) {
            Logger.recoverMethod_ww_e925fa09("PluginPlatformsNotificationHelper", "message id was empty");
         } else {
            var6.recoverField_ai = var4;
            var6.recoverField_e = var3;
            switch (var5) {
               case 0:
                  recoverMethod_c_f39a58c1(var0, var6, var2, var3);
                  LinkedList var7 = new LinkedList();
                  var7.add(var6.recoverField_d);
                  RecoverType2e4d193e.recoverMethod_a_4eab7a9f(var0, var7, 1);
                  break;
               case 1:
                  recoverMethod_a_f39a58c1(var0, var6, var2, var3);
                  break;
               case 2:
                  recoverMethod_b_f39a58c1(var0, var6, var2, var3);
            }
         }
      }
   }

   public static void recoverMethod_b_87826923(Context var0, String var1, String var2, int var3, byte var4, int var5) {
      if (var0 == null) {
         Logger.recoverMethod_ww_e925fa09("PluginPlatformsNotificationHelper", "customButton context was null");
      } else if (TextUtils.isEmpty(var1)) {
         Logger.recoverMethod_ww_e925fa09("PluginPlatformsNotificationHelper", "customButton content was null");
      } else {
         Logger.recoverMethod_v_e925fa09("PluginPlatformsNotificationHelper", "customButton content:" + var1);
         RecoverType2e4cce2a.Memberce51f067 var6 = RecoverType2e4cce2a.Memberce51f067.recoverMethod_a_14ff071f(var1);
         Logger.recoverMethod_v_e925fa09("PluginPlatformsNotificationHelper", "customButton:" + var6);
         if (var6 == null) {
            Logger.recoverMethod_ww_e925fa09("PluginPlatformsNotificationHelper", "customButton was null");
         } else if (TextUtils.isEmpty(var6.recoverField_a)) {
            Logger.recoverMethod_ww_e925fa09("PluginPlatformsNotificationHelper", "customButton id was empty");
         } else {
            switch (var5) {
               case 3:
                  recoverMethod_a_c2ba8db(var0, var6);
                  LinkedList var7 = new LinkedList();
                  var7.add(var6.recoverField_a);
                  RecoverType2e4d193e.recoverMethod_a_4eab7a9f(var0, var7, 1);
            }
         }
      }
   }

   public static RecoverType2e4cce2a recoverMethod_a_39f271f4(Context var0, String var1, String var2) {
      return RecoverType2e4cce2a.recoverMethod_a_39f271f4(var0, var1, var2);
   }

   private static void recoverMethod_a_f39a58c1(Context var0, RecoverType2e4cce2a var1, String var2, int var3) {
      Logger.recoverMethod_v_e925fa09("PluginPlatformsNotificationHelper", "Action - onNotificationMessageArrived");
      RecoverType2e4d193c.recoverMethod_a_74ff4fff(var0, "cn.jpush.android.intent.NOTIFICATION_ARRIVED", var1, null);
      RecoverType8b455831.recoverMethod_a_2ef42797(var1.recoverField_d, var2, var1.recoverField_ai, 1018L, var0);
   }

   private static void recoverMethod_b_f39a58c1(Context var0, RecoverType2e4cce2a var1, String var2, int var3) {
      Logger.recoverMethod_v_e925fa09("PluginPlatformsNotificationHelper", "Action - onNotificationMessageUnShow in foreground");
      var1.recoverField_e = var3;
      RecoverType2e4d193c.recoverMethod_a_74ff4fff(var0, "cn.jpush.android.intent.NOTIFICATION_UN_SHOW", var1, null);
      RecoverType8b455831.recoverMethod_a_2ef42797(var1.recoverField_d, var2, var1.recoverField_ai, 1060L, var0);
   }

   private static void recoverMethod_c_f39a58c1(Context var0, RecoverType2e4cce2a var1, String var2, int var3) {
      Logger.recoverMethod_v_e925fa09("PluginPlatformsNotificationHelper", "Action - onNotificationMessageClick");
      if (!var1.recoverField_Y) {
         RecoverType2e4d193c.recoverMethod_a_74ff4fff(var0, "cn.jpush.android.intent.NOTIFICATION_OPENED", var1, null);
      } else if (var1.recoverField_ai == 8) {
         recoverMethod_a_ae3b7eb8(var0, var1);
      }
   }

   private static void recoverMethod_a_c2ba8db(Context var0, RecoverType2e4cce2a.Memberce51f067 var1) {
      Logger.recoverMethod_v_e925fa09("PluginPlatformsNotificationHelper", "Action - onCustomButtonClick");
      Intent var2 = new Intent();
      var2.putExtra("custom_button", var1.recoverMethod_a_a7dbfe3().toString());
      RecoverType2e4d193c.recoverMethod_a_74ff4fff(var0, "cn.jpush.android.intent.NOTIFICATION_OPENED_CUSTOM_BUTTON", null, var2);
   }

   private static void recoverMethod_a_ae3b7eb8(Context var0, RecoverType2e4cce2a var1) {
      Intent var2 = null;
      if (2 == var1.recoverField_ad) {
         var2 = RecoverType2e4d193c.recoverMethod_b_555de491(var0, "cn.jpush.android.intent.NOTIFICATION_OPENED", var1);
      } else {
         var2 = RecoverType2e4d193c.recoverMethod_a_555de491(var0, "cn.jpush.android.intent.NOTIFICATION_OPENED", var1);
      }

      if (var2 != null) {
         var2.addFlags(268435456);
         var0.getApplicationContext().startActivity(var2);
      }
   }
}
