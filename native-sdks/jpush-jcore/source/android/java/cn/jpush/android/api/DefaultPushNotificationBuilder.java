package cn.jpush.android.api;

import android.app.Notification;
import android.app.Notification.BigPictureStyle;
import android.app.Notification.BigTextStyle;
import android.app.Notification.Builder;
import android.app.Notification.InboxStyle;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.widget.RemoteViews;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.x.RecoverType2e4d193b;
import cn.jpush.android.x.RecoverType2e4d193c;
import cn.jpush.android.x.RecoverType2e4d1941;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.json.JSONObject;

public class DefaultPushNotificationBuilder implements PushNotificationBuilder {
   private static final String TAG = "DefaultPushNotificationBuilder";
   protected Context context;
   public static final int NOTI_STYLE_BIG_TEXT = 1;
   public static final int NOTI_STYLE_INBOX = 2;
   public static final int NOTI_STYLE_BIG_PICTURE = 3;
   protected NotificationMessage notificationMessage;

   @Override
   public Notification buildNotification(Map var1) {
      if (this.context == null) {
         Logger.recoverMethod_w_e925fa09("DefaultPushNotificationBuilder", "Can't find valid context when build notification.");
         return null;
      }

      String var2 = null;

      try {
         var2 = this.context.getApplicationInfo().loadLabel(this.context.getPackageManager()).toString();
      } catch (Throwable var25) {
      }

      String var3 = "";
      String var4 = "";
      String var5 = "";
      int var6 = 0;
      int var7 = -1;
      String var8 = "";
      String var9 = "";
      int var10 = -1;
      String var11 = "";
      String var12 = "";
      String var13 = "";
      if (var1.containsKey("cn.jpush.android.MSG_ID")) {
         var13 = (String)var1.get("cn.jpush.android.MSG_ID");
      }

      if (var1.containsKey("cn.jpush.android.ALERT")) {
         var3 = (String)var1.get("cn.jpush.android.ALERT");
      }

      if (TextUtils.isEmpty(var3)) {
         Logger.recoverMethod_ww_e925fa09("DefaultPushNotificationBuilder", "No notification content to show. Give up.");
         return null;
      }

      if (var1.containsKey("cn.jpush.android.NOTIFICATION_SMALL_ICON")) {
         var11 = (String)var1.get("cn.jpush.android.NOTIFICATION_SMALL_ICON");
      }

      if (var1.containsKey("cn.jpush.android.NOTIFICATION_LARGE_ICON")) {
         var12 = (String)var1.get("cn.jpush.android.NOTIFICATION_LARGE_ICON");
      }

      if (var1.containsKey("cn.jpush.android.NOTIFICATION_CONTENT_TITLE")) {
         var2 = (String)var1.get("cn.jpush.android.NOTIFICATION_CONTENT_TITLE");
      }

      if (var1.containsKey("cn.jpush.android.BIG_TEXT")) {
         var4 = (String)var1.get("cn.jpush.android.BIG_TEXT");
      }

      if (var1.containsKey("cn.jpush.android.INBOX")) {
         var5 = (String)var1.get("cn.jpush.android.INBOX");
      }

      if (var1.containsKey("cn.jpush.android.NOTI_PRIORITY")) {
         var6 = Integer.parseInt((String)var1.get("cn.jpush.android.NOTI_PRIORITY"));
      }

      if (var1.containsKey("cn.jpush.android.NOTI_IMPORTANCE")) {
         var7 = Integer.parseInt((String)var1.get("cn.jpush.android.NOTI_IMPORTANCE"));
      }

      if (var1.containsKey("cn.jpush.android.NOTI_CATEGORY")) {
         var8 = (String)var1.get("cn.jpush.android.NOTI_CATEGORY");
      }

      if (var1.containsKey("cn.jpush.android.BIG_PIC_PATH")) {
         var9 = (String)var1.get("cn.jpush.android.BIG_PIC_PATH");
      }

      if (var1.containsKey("cn.jpush.android.ALERT_TYPE")) {
         var10 = Integer.parseInt((String)var1.get("cn.jpush.android.ALERT_TYPE"));
      }

      if (var10 < -1 || var10 > 7) {
         var10 = -1;
      }

      RemoteViews var14 = this.buildContentView(var3, var2);
      if (VERSION.SDK_INT >= 11) {
         Builder var29 = new Builder(this.context);
         var29.setContentTitle(var2).setContentText(var3).setTicker(var3);
         int var16 = RecoverType2e4d1941.recoverMethod_a_f0554de6(this.context);
         if (var16 != 0) {
            Logger.recoverMethod_dd_e925fa09("DefaultPushNotificationBuilder", "setSmallIcon with resId:" + var16);
            var29.setSmallIcon(var16);
         } else {
            Logger.recoverMethod_ww_e925fa09(
               "DefaultPushNotificationBuilder", "not found small icon in your app,will setSmallIcon with android.R.color.transparent "
            );
            var29.setSmallIcon(17170445);
         }

         Object var17 = RecoverType2e4d193c.recoverMethod_b_b2278fd5(this.context, var12);
         if (VERSION.SDK_INT >= 23) {
            if (var17 instanceof Icon) {
               var29.setLargeIcon((Icon)var17);
            } else if (var17 instanceof Bitmap) {
               var29.setLargeIcon((Bitmap)var17);
            }
         }

         if (VERSION.SDK_INT >= 17) {
            var29.setShowWhen(true);
         }

         RecoverType2e4d193b.recoverMethod_a_fb8bd7b9(
            this.context, var29, this.notificationMessage != null ? this.notificationMessage.notificationChannelId : "", "", var6, var10, null, var7
         );
         if (VERSION.SDK_INT >= 16) {
            if (var14 == null) {
               if (!TextUtils.isEmpty(var4)) {
                  BigTextStyle var18 = new BigTextStyle();
                  var18.bigText(var4);
                  var29.setStyle(var18);
               }

               if (!TextUtils.isEmpty(var5)) {
                  InboxStyle var30 = new InboxStyle();

                  try {
                     TreeMap var19 = new TreeMap();
                     JSONObject var20 = new JSONObject(var5);
                     Iterator var21 = var20.keys();

                     while (var21.hasNext()) {
                        String var22 = (String)var21.next();
                        String var23 = var20.optString(var22);
                        var19.put(var22, var23);
                     }

                     for (Object recoveryElement_168 : var19.values()) {
                        String var34 = (String)recoveryElement_168;
                        var30.addLine(var34);
                     }

                     var30.setSummaryText(" + " + var20.length() + " new messages");
                  } catch (Throwable var26) {
                     Logger.recoverMethod_ee_e925fa09("DefaultPushNotificationBuilder", "Set inbox style error: " + var26.getMessage());
                  }

                  var29.setStyle(var30);
               }

               if (!TextUtils.isEmpty(var9)) {
                  Logger.recoverMethod_i_e925fa09("DefaultPushNotificationBuilder", "Set notification BPS with picture path:" + var9);

                  try {
                     BigPictureStyle var31 = new BigPictureStyle();
                     Bitmap var32 = BitmapFactory.decodeFile(var9);
                     var31.bigPicture(var32);
                     var29.setStyle(var31);
                  } catch (Throwable var24) {
                     Logger.recoverMethod_ww_e925fa09("DefaultPushNotificationBuilder", "Create big picture style failed.");
                  }
               }
            }

            if (var6 != 0) {
               if (var6 == 1) {
                  var29.setPriority(1);
               } else if (var6 == 2) {
                  var29.setPriority(2);
               } else if (var6 == -1) {
                  var29.setPriority(-1);
               } else if (var6 == -2) {
                  var29.setPriority(-2);
               } else {
                  var29.setPriority(0);
               }
            }

            if (!TextUtils.isEmpty(var8) && VERSION.SDK_INT >= 21) {
               var29.setCategory(var8);
            }
         }

         if (null != var14) {
            var29.setContent(var14);
            if (this.context.getApplicationInfo().targetSdkVersion >= 31 && VERSION.SDK_INT >= 24) {
               var29.setCustomBigContentView(var14);
            }
         } else {
            Logger.recoverMethod_i_e925fa09("DefaultPushNotificationBuilder", " Use default notification view! ");
         }

         var29.setDefaults(var10);
         return this.getNotification(var29);
      } else {
         Notification var15 = new Notification(RecoverType2e4d1941.recoverMethod_a_f0554de6(this.context), var3, System.currentTimeMillis());
         this.resetNotificationParams(var15);
         var15.defaults = var10;
         if (null != var14) {
            var15.contentView = var14;
         } else {
            RecoverType2e4d193c.recoverMethod_a_d55e17cb(var15, this.context, var2, var3, null);
         }

         return var15;
      }
   }

   @Override
   public Notification buildNotification(Context var1, NotificationMessage var2) {
      this.context = var1;
      this.notificationMessage = var2;
      HashMap var3 = new HashMap();
      if (var2 != null) {
         Logger.recoverMethod_d_e925fa09(
            "DefaultPushNotificationBuilder",
            "notificationContent:"
               + var2.notificationContent
               + "\nnotificationTitle"
               + var2.notificationTitle
               + "\nnotificationStyle:"
               + var2.notificationStyle
               + "\nnotificationPriority:"
               + var2.notificationPriority
               + "\nnotificationBigText:"
               + var2.notificationBigText
               + "\nnotificationBigPicPath:"
               + var2.notificationBigPicPath
               + "\nnotificationInbox:"
               + var2.notificationInbox
               + "\nnotificationCategory:"
               + var2.notificationCategory
               + "\nnotificationAlertType:"
               + var2.notificationAlertType
               + "\nnotificationSmallIcon:"
               + var2.notificationSmallIcon
               + "\nnotificationLargeIcon:"
               + var2.notificationLargeIcon
               + "\nextraJson:"
               + var2.notificationExtras
         );
         var3.put("cn.jpush.android.MSG_ID", var2.msgId);
         var3.put("cn.jpush.android.ALERT", var2.notificationContent);
         var3.put("cn.jpush.android.ALERT_TYPE", var2.notificationAlertType + "");
         if (!TextUtils.isEmpty(var2.notificationTitle)) {
            var3.put("cn.jpush.android.NOTIFICATION_CONTENT_TITLE", var2.notificationTitle);
         }

         if (!TextUtils.isEmpty(var2.notificationExtras)) {
            var3.put("cn.jpush.android.EXTRA", var2.notificationExtras);
         }

         if (var2.notificationStyle == 1 && !TextUtils.isEmpty(var2.notificationBigText)) {
            var3.put("cn.jpush.android.BIG_TEXT", var2.notificationBigText);
         } else if (var2.notificationStyle == 2 && !TextUtils.isEmpty(var2.notificationInbox)) {
            var3.put("cn.jpush.android.INBOX", var2.notificationInbox);
         } else if (var2.notificationStyle == 3 && !TextUtils.isEmpty(var2.notificationBigPicPath)) {
            var3.put("cn.jpush.android.BIG_PIC_PATH", var2.notificationBigPicPath);
         }

         if (var2.notificationPriority != 0) {
            var3.put("cn.jpush.android.NOTI_PRIORITY", var2.notificationPriority + "");
         }

         if (var2.notificationImportance != -1) {
            var3.put("cn.jpush.android.NOTI_IMPORTANCE", var2.notificationImportance + "");
         }

         if (!TextUtils.isEmpty(var2.notificationCategory)) {
            var3.put("cn.jpush.android.NOTI_CATEGORY", var2.notificationCategory);
         }

         if (!TextUtils.isEmpty(var2.notificationSmallIcon)) {
            var3.put("cn.jpush.android.NOTIFICATION_SMALL_ICON", var2.notificationSmallIcon);
         }

         if (!TextUtils.isEmpty(var2.notificationLargeIcon)) {
            var3.put("cn.jpush.android.NOTIFICATION_LARGE_ICON", var2.notificationLargeIcon);
         }
      }

      return this.buildNotification(var3);
   }

   @Override
   public String getDeveloperArg0() {
      return null;
   }

   RemoteViews buildContentView(String var1, String var2) {
      return null;
   }

   void resetNotificationParams(Notification var1) {
   }

   Notification getNotification(Builder var1) {
      try {
         RecoverType2e4d193c.recoverMethod_a_a37e39a6(
            this.context, this.notificationMessage.customButtons, var1, this.notificationMessage.msgId, (byte)this.notificationMessage.platform
         );
         return VERSION.SDK_INT >= 16 ? var1.build() : var1.getNotification();
      } catch (Throwable var3) {
         Logger.recoverMethod_ww_1765c98c("DefaultPushNotificationBuilder", "Build notification error:", var3);
         return null;
      }
   }

   @Override
   public String toString() {
      return "";
   }
}
