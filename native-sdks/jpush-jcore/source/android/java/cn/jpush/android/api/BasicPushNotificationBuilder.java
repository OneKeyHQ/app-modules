package cn.jpush.android.api;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.Notification.Builder;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build.VERSION;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.x.RecoverType2e4d193c;

public class BasicPushNotificationBuilder extends DefaultPushNotificationBuilder {
   public int notificationDefaults = -2;
   public int notificationFlags = 16;
   public int statusBarDrawable = 0;
   public String developerArg0 = "developerArg0";
   protected Context recoverField_a;

   public BasicPushNotificationBuilder(Context var1) {
      if (null == var1) {
         throw new IllegalArgumentException("NULL context");
      }

      this.recoverField_a = var1;

      try {
         ApplicationInfo var2 = var1.getPackageManager().getApplicationInfo(var1.getPackageName(), 0);
         if (null == var2) {
            Logger.recoverMethod_ee_e925fa09("BasicPushNotificationBu", "JPush get NULL appInfo.");
            return;
         }

         this.statusBarDrawable = var2.icon;
      } catch (Throwable var3) {
         Logger.recoverMethod_ww_e925fa09("BasicPushNotificationBu", "[statusBarDrawable] JPush get NULL appInfo.");
      }
   }

   @Override
   public String getDeveloperArg0() {
      return this.developerArg0;
   }

   @TargetApi(11)
   @Override
   Notification getNotification(Builder var1) {
      if (this.notificationDefaults != -2) {
         var1.setDefaults(this.notificationDefaults);
      }

      var1.setSmallIcon(this.statusBarDrawable);
      RecoverType2e4d193c.recoverMethod_a_a37e39a6(
         this.context, this.notificationMessage.customButtons, var1, this.notificationMessage.msgId, (byte)this.notificationMessage.platform
      );
      Notification var2;
      if (VERSION.SDK_INT >= 16) {
         var2 = var1.build();
      } else {
         var2 = var1.getNotification();
      }

      var2.flags = 1 | this.notificationFlags;
      return var2;
   }

   @Override
   void resetNotificationParams(Notification var1) {
      var1.defaults = this.notificationDefaults;
      var1.flags = this.notificationFlags;
      var1.icon = this.statusBarDrawable;
   }

   @Override
   public String toString() {
      return "basic_____" + this.recoverMethod_a_73cf11cb();
   }

   String recoverMethod_a_73cf11cb() {
      return this.notificationDefaults + "_____" + this.notificationFlags + "_____" + this.statusBarDrawable + "_____" + this.developerArg0;
   }

   void recoverMethod_a_3231c38a(String[] var1) {
      this.notificationDefaults = Integer.parseInt(var1[1]);
      this.notificationFlags = Integer.parseInt(var1[2]);
      this.statusBarDrawable = Integer.parseInt(var1[3]);
      if (var1.length >= 5) {
         this.developerArg0 = var1[4];
      }
   }

   public static PushNotificationBuilder parseFromPreference(Context var0, String var1) {
      String[] var2 = var1.split("_____");
      String var3 = var2[0];
      BasicPushNotificationBuilder var4 = null;
      if ("basic".equals(var3)) {
         var4 = new BasicPushNotificationBuilder(var0);
      } else if ("custom".equals(var3)) {
         var4 = new CustomPushNotificationBuilder(var0);
      } else {
         var4 = new BasicPushNotificationBuilder(var0);
      }

      var4.recoverMethod_a_3231c38a(var2);
      return var4;
   }
}
