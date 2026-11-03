package cn.jpush.android.support;

import android.content.Context;
import android.text.TextUtils;
import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.MultiActionsNotificationBuilder;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.api.PushNotificationBuilder;
import cn.jpush.android.cache.RecoverTypea2146d45;
import cn.jpush.android.helper.Logger;

public class JPushSupport {
   private static final Integer recoverField_a = 0;

   public static PushNotificationBuilder getNotificationBuilder(Context var0, NotificationMessage var1) {
      try {
         int var2 = var1.notificationBuilderId;
         if (var2 < 1) {
            var2 = recoverField_a;
         }

         String var3 = RecoverTypea2146d45.recoverMethod_d_a4ea6d43(var0, var2 + "");
         if (TextUtils.isEmpty(var3)) {
            Logger.recoverMethod_d_e925fa09("JPushSupport", "not found custom notification");
            return null;
         }

         Logger.recoverMethod_ii_e925fa09("JPushSupport", "get customBuilder:" + var3);
         PushNotificationBuilder var4 = null;
         if (!var3.startsWith("basic") && !var3.startsWith("custom")) {
            var4 = MultiActionsNotificationBuilder.parseFromPreference(var0, var3);
         } else {
            var4 = BasicPushNotificationBuilder.parseFromPreference(var0, var3);
         }

         return var4;
      } catch (Throwable var5) {
         Logger.recoverMethod_ww_e925fa09("JPushSupport", "getNotification failed:" + var5.getMessage());
         return null;
      }
   }
}
