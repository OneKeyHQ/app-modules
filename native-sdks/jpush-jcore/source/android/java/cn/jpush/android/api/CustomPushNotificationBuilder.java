package cn.jpush.android.api;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.widget.RemoteViews;
import cn.jpush.android.helper.Logger;

public class CustomPushNotificationBuilder extends BasicPushNotificationBuilder {
   public int layoutTimeId;
   public int layout;
   public int layoutIconId;
   public int layoutTitleId;
   public int layoutContentId;
   public int layoutIconDrawable = 0;

   CustomPushNotificationBuilder(Context var1) {
      super(var1);
   }

   public CustomPushNotificationBuilder(Context var1, int var2, int var3, int var4, int var5, int var6) {
      super(var1);
      this.layout = var2;
      this.layoutTitleId = var4;
      this.layoutContentId = var5;
      this.layoutTimeId = var6;
      this.layoutIconId = var3;

      try {
         ApplicationInfo var7 = var1.getPackageManager().getApplicationInfo(var1.getPackageName(), 0);
         if (null == var7) {
            Logger.recoverMethod_ee_e925fa09("CustomPushNotificationB", "JPush get NULL appInfo.");
            return;
         }

         this.layoutIconDrawable = var7.icon;
      } catch (Throwable var8) {
         Logger.recoverMethod_ww_e925fa09("CustomPushNotificationB", "[statusBarDrawable] JPush get NULL appInfo.");
      }
   }

   public CustomPushNotificationBuilder(Context var1, int var2, int var3, int var4, int var5) {
      super(var1);
      this.layout = var2;
      this.layoutIconId = var3;
      this.layoutTitleId = var4;
      this.layoutContentId = var5;
   }

   @Override
   RemoteViews buildContentView(String var1, String var2) {
      RemoteViews var3 = new RemoteViews(this.recoverField_a.getPackageName(), this.layout);
      var3.setTextViewText(this.layoutTitleId, var2);
      var3.setImageViewResource(this.layoutIconId, this.layoutIconDrawable);
      var3.setTextViewText(this.layoutContentId, var1);
      if (this.layoutTimeId != 0) {
         var3.setLong(this.layoutTimeId, "setTime", System.currentTimeMillis());
      }

      return var3;
   }

   @Override
   public String toString() {
      return "custom_____" + this.recoverMethod_a_73cf11cb();
   }

   @Override
   String recoverMethod_a_73cf11cb() {
      return super.recoverMethod_a_73cf11cb()
         + "_____"
         + this.layout
         + "_____"
         + this.layoutIconId
         + "_____"
         + this.layoutTitleId
         + "_____"
         + this.layoutContentId
         + "_____"
         + this.layoutIconDrawable
         + "_____"
         + this.layoutTimeId;
   }

   @Override
   void recoverMethod_a_3231c38a(String[] var1) {
      super.recoverMethod_a_3231c38a(var1);
      this.layout = Integer.parseInt(var1[5]);
      this.layoutIconId = Integer.parseInt(var1[6]);
      this.layoutTitleId = Integer.parseInt(var1[7]);
      this.layoutContentId = Integer.parseInt(var1[8]);
      this.layoutIconDrawable = Integer.parseInt(var1[9]);
      if (var1.length == 11) {
         this.layoutTimeId = Integer.parseInt(var1[10]);
      }
   }
}
