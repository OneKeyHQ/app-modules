package cn.jpush.android.aa;

import android.content.Context;
import android.text.TextUtils;
import cn.jpush.android.ad.RecoverType9b4c5aea;
import cn.jpush.android.cache.Key;
import cn.jpush.android.cache.RecoverTypea0793a19;
import cn.jpush.android.helper.JCoreHelper;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.helper.RecoverType8b455831;
import java.util.Map;

public class RecoverType9b4c4fa1 {
   private Context recoverField_a;
   private RecoverType9b4c4fa2 recoverField_b;

   private RecoverType9b4c4fa1() {
   }

   public static final RecoverType9b4c4fa1 recoverMethod_a_d8c30de5() {
      return RecoverType9b4c4fa1.Memberf976f01f.recoverField_a;
   }

   public void recoverMethod_a_f0554df3(Context var1) {
      if (this.recoverField_a == null || this.recoverField_b == null) {
         this.recoverField_a = var1.getApplicationContext();
         this.recoverField_b = new RecoverType9b4c4fa2(this.recoverField_a);
      }
   }

   public void recoverMethod_a_5ffc00fd(Context var1, String var2) {
      this.recoverMethod_a_f0554df3(var1);
      String var3 = "";
      switch (var2) {
         case "clear_by_id":
            var3 = "JPushInterface#clearNotificationById";
            break;
         case "clear_all_notify":
            var3 = "JPushInterface#clearAllNotifications";
            break;
         case "set_mobile":
            var3 = "JPushInterface#setMobileNumber";
            break;
         case "add_local_notify":
            var3 = "JPushInterface#addLocalNotification";
            break;
         case "rm_local_notify":
            var3 = "JPushInterface#removeLocalNotification";
            break;
         case "clear_local_notify":
            var3 = "JPushInterface#clearLocalNotifications";
            break;
         case "geo_interval":
            var3 = "JPushInterface#setGeofenceInterval";
            break;
         case "geo_fence_max_num":
            var3 = "JPushInterface#setMaxGeofenceNumber";
            break;
         case "delete_geo_fence":
            var3 = "JPushInterface#deleteGeofence";
            break;
         case "go_settings":
            var3 = "JPushInterface#goToAppNotificationSettings";
            break;
         case "is_enable":
            var3 = "JPushInterface#isNotificationEnabled";
            break;
         case "on_check":
            var3 = "JPushMessageReceiver#onNotificationSettingsCheck";
      }

      if (!TextUtils.isEmpty(var3)) {
         Logger.recoverMethod_d_e925fa09("JPushApiStatistics", "  statistic api =" + var3);
         JCoreHelper.normalExecutor(this.recoverField_a, null, new RecoverType9b4c4fa1.Memberf976f01e(var3));
      }
   }

   public void recoverMethod_b_9b75() {
      long var1 = (Long)RecoverTypea0793a19.get(this.recoverField_a, Key.LastReportApiStatisticTime());
      long var3 = System.currentTimeMillis();
      long var5 = Math.abs(var3 - var1);
      if (var1 != 0L && var5 < 86400000L) {
         Logger.recoverMethod_dd_e925fa09(
            "JPushApiStatistics", "Less 24 hours since the last report so return. lastTime=" + var1 + " currentTime=" + var3 + " diffTime=" + var5
         );
      } else {
         JCoreHelper.normalExecutor(this.recoverField_a, null, new RecoverType9b4c4fa1.Memberf976f01e(""));
      }
   }

   class Memberf976f01e extends RecoverType9b4c5aea {
      private String recoverField_b;

      public Memberf976f01e(String var2) {
         this.recoverField_b = var2;
      }

      @Override
      public void recoverMethod_a_9b75() {
         try {
            if (!TextUtils.isEmpty(this.recoverField_b)) {
               RecoverType9b4c4fa1.this.recoverField_b.recoverMethod_a_aad3b1ff(this.recoverField_b);
            } else {
               Map var1 = RecoverType9b4c4fa1.this.recoverField_b.recoverMethod_a_604efcea();
               if (var1 != null && var1.size() > 0) {
                  RecoverTypea0793a19.set(RecoverType9b4c4fa1.this.recoverField_a, Key.LastReportApiStatisticTime().set(System.currentTimeMillis()));
                  boolean var2 = RecoverType8b455831.recoverMethod_a_f08e8936(RecoverType9b4c4fa1.this.recoverField_a, var1);
                  if (var2) {
                     RecoverType9b4c4fa1.this.recoverField_b.recoverMethod_b_9b75();
                  }
               }
            }
         } catch (Throwable var3) {
         }
      }
   }

   private static class Memberf976f01f {
      private static final RecoverType9b4c4fa1 recoverField_a = new RecoverType9b4c4fa1();
   }
}
