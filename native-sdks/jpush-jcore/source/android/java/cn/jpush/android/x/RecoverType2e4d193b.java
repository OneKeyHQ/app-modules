package cn.jpush.android.x;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.Notification.Builder;
import android.content.Context;
import android.net.Uri;
import android.os.Build.VERSION;
import android.text.TextUtils;
import cn.jpush.android.ad.RecoverType9b4c5ae5;
import cn.jpush.android.helper.Logger;
import java.util.Locale;

public class RecoverType2e4d193b {
   public static void recoverMethod_a_f0554df3(Context var0) {
      recoverMethod_a_dc106f6d(var0, -1, 0);
      recoverMethod_a_dc106f6d(var0, 0, 7);
   }

   private static NotificationChannel recoverMethod_b_c62de8f1(Context var0, RecoverType2e4d193b.Membercf6bbb38 var1) {
      try {
         if (VERSION.SDK_INT < 26) {
            return null;
         }

         if (null == var1) {
            return null;
         }

         String var2 = var1.recoverField_a;
         if (!TextUtils.isEmpty(var2)) {
            NotificationManager var3 = (NotificationManager)var0.getSystemService("notification");
            NotificationChannel var4 = var3 != null ? var3.getNotificationChannel(var2) : null;
            if (var4 != null) {
               if (var1.recoverField_e) {
                  int var5 = recoverMethod_a_134621(var1.recoverField_c);
                  var4.setImportance(var5);
                  Logger.recoverMethod_dd_e925fa09("ChannelHelper", "channel has created: " + var2 + ",set importance:" + var5);
               }

               Logger.recoverMethod_dd_e925fa09("ChannelHelper", "channel has created: " + var2);
               return var4;
            }

            Logger.recoverMethod_dd_e925fa09("ChannelHelper", "not found channed id from user set,will use sdk default channel ID");
         }
      } catch (Throwable var6) {
         Logger.recoverMethod_e_e925fa09("ChannelHelper", "config channelID error,will use sdk default channel ID:" + var6);
      }

      return null;
   }

   public static void recoverMethod_a_24a4a06a(Context var0, RecoverType2e4d193b.Membercf6bbb38 var1) {
      if (RecoverType2e4d193c.recoverMethod_c_f0554df7(var0)) {
         Logger.recoverMethod_d_e925fa09("ChannelHelper", "in silence push time,change defaults to 0");
         var1.recoverField_a = "";
         var1.recoverField_f = 0;
         var1.recoverField_g = null;
      } else if (VERSION.SDK_INT >= 26) {
         NotificationChannel var2 = recoverMethod_b_c62de8f1(var0, var1);
         if (var2 != null) {
            var1.recoverField_a = var2.getId();
            var1.recoverField_b = var2.getName();
            var1.recoverField_d = var2.getImportance();
            var1.recoverField_c = recoverMethod_b_134621(var1.recoverField_d);
            var1.recoverField_h = var2.getSound();
            if (var1.recoverField_h != null) {
               var1.recoverField_g = var1.recoverField_h.getPath();
            }

            var1.recoverField_i = true;
            return;
         }
      }

      if (var1.recoverField_c == -2 || var1.recoverField_c == -1) {
         var1.recoverField_f = 0;
         var1.recoverField_g = null;
      } else if (var1.recoverField_c >= 0 && var1.recoverField_f == 0 && TextUtils.isEmpty(var1.recoverField_g)) {
         var1.recoverField_c = -1;
      }

      var1.recoverField_f &= 7;
      if (var1.recoverField_c == 2) {
         var1.recoverField_c = 1;
      }

      var1.recoverField_d = recoverMethod_a_134621(var1.recoverField_c);
      var1.recoverField_c = recoverMethod_b_134621(var1.recoverField_d);
      var1.recoverField_h = RecoverType2e4d193c.recoverMethod_a_561e3034(var0, var1.recoverField_g);
      if (var1.recoverField_h != null) {
         var1.recoverField_f &= -2;
      } else {
         var1.recoverField_g = null;
      }

      if (TextUtils.isEmpty(var1.recoverField_a)) {
         var1.recoverField_a = recoverMethod_a_91e54234(var1);
         if (VERSION.SDK_INT >= 26) {
            NotificationChannel var3 = recoverMethod_b_c62de8f1(var0, var1);
            if (var3 != null) {
               var1.recoverField_a = var3.getId();
               var1.recoverField_b = var3.getName();
               var1.recoverField_i = true;
               return;
            }
         }
      }

      if (TextUtils.isEmpty(var1.recoverField_b)) {
         if ("KG_channel_normal".equals(var1.recoverField_a) && "com.tencent.karaoke".equals(var0.getPackageName())) {
            var1.recoverField_b = "普通_K歌";
         } else {
            var1.recoverField_b = recoverMethod_a_fcba5f30(var0, var1.recoverField_c);
         }
      }

      Logger.recoverMethod_d_e925fa09(
         "ChannelHelper",
         "create channelId:"
            + var1.recoverField_a
            + ",channelName:"
            + var1.recoverField_b
            + ",soundUri:"
            + var1.recoverField_h
            + ",importance:"
            + var1.recoverField_d
            + ", hasImportance:"
            + var1.recoverField_e
      );
      var1.recoverField_i = recoverMethod_a_66f032f2(
         var0, var1.recoverField_a, var1.recoverField_b, var1.recoverField_d, var1.recoverField_f, var1.recoverField_h, var1.recoverField_e
      );
   }

   private static String recoverMethod_a_91e54234(RecoverType2e4d193b.Membercf6bbb38 var0) {
      String var1 = "JPush_" + var0.recoverField_d + "_" + var0.recoverField_f;
      if (var0.recoverField_h != null) {
         var1 = var1 + "_" + var0.recoverField_g;
      }

      return var1;
   }

   public static void recoverMethod_a_fb8bd7b9(Context var0, Builder var1, String var2, CharSequence var3, int var4, int var5, String var6, int var7) {
      RecoverType2e4d193b.Membercf6bbb38 var8 = new RecoverType2e4d193b.Membercf6bbb38(var2, var3, var4, var5, var6, var7);
      recoverMethod_a_24a4a06a(var0, var8);
      RecoverType2e4d193c.recoverMethod_a_f2a64de9(var0, var1, var8);
   }

   public static void recoverMethod_a_3cdf01a2(Context var0, Notification var1, String var2, CharSequence var3, int var4, int var5, String var6, int var7) {
      RecoverType2e4d193b.Membercf6bbb38 var8 = new RecoverType2e4d193b.Membercf6bbb38(var2, var3, var4, var5, var6, var7);
      recoverMethod_a_24a4a06a(var0, var8);
      RecoverType2e4d193c.recoverMethod_a_82e04d22(var1, var8);
   }

   private static String recoverMethod_a_fcba5f30(Context var0, int var1) {
      String var2;
      switch (var1) {
         case -2:
            var2 = "MIN";
            break;
         case -1:
            var2 = "LOW";
            break;
         case 0:
         default:
            var2 = "DEFAULT";
            break;
         case 1:
         case 2:
            var2 = "HIGH";
      }

      String var3 = "";

      try {
         String var4 = "jg_channel_name_p_" + var2.toLowerCase();
         int var5 = var0.getResources().getIdentifier(var4, "string", var0.getPackageName());
         if (var5 != 0) {
            var3 = var0.getResources().getString(var5);
            Logger.recoverMethod_dd_e925fa09("ChannelHelper", "found " + var3 + " from resource by name:" + var4);
         } else {
            Logger.recoverMethod_dd_e925fa09("ChannelHelper", "not found string value from resource by name:" + var4);
         }
      } catch (Throwable var8) {
         Logger.recoverMethod_ww_e925fa09("ChannelHelper", "get resource channel ID failed:" + var8.getMessage());
      }

      if (!TextUtils.isEmpty(var3)) {
         return var3;
      }

      boolean var10 = true;

      try {
         Locale var11 = Locale.getDefault();
         String var6 = var11.getLanguage();
         var10 = var6.contains("zh");
      } catch (Throwable var7) {
         Logger.recoverMethod_ww_e925fa09("ChannelHelper", "get language failed:" + var7.getMessage());
      }

      switch (var1) {
         case -2:
         case -1:
            var2 = var10 ? "不重要" : "LOW";
            break;
         case 0:
         default:
            var2 = var10 ? "普通" : "NORMAL";
            break;
         case 1:
         case 2:
            var2 = var10 ? "重要" : "HIGH";
      }

      return var2;
   }

   public static int recoverMethod_a_134621(int var0) {
      switch (var0) {
         case -2:
            return 1;
         case -1:
            return 2;
         case 0:
         default:
            return 3;
         case 1:
         case 2:
            return 4;
      }
   }

   public static int recoverMethod_b_134621(int var0) {
      switch (var0) {
         case 0:
         case 1:
            return -2;
         case 2:
            return -1;
         case 3:
         default:
            return 0;
         case 4:
         case 5:
            return 1;
      }
   }

   private static String recoverMethod_a_dc106f6d(Context var0, int var1, int var2) {
      int var3 = recoverMethod_a_134621(var1);
      String var4 = "JPush_" + var3 + "_" + var2;
      String var5 = recoverMethod_a_fcba5f30(var0, var1);
      Logger.recoverMethod_d_e925fa09("ChannelHelper", "channelId:" + var4 + ",channelName:" + var5 + ",importance:" + var3);
      return recoverMethod_a_66f032f2(var0, var4, var5, var3, var2, null, false) ? var4 : "";
   }

   private static boolean recoverMethod_a_66f032f2(Context var0, String var1, CharSequence var2, int var3, int var4, Uri var5, boolean var6) {
      if (VERSION.SDK_INT >= 26 && var0.getApplicationInfo().targetSdkVersion >= 26) {
         try {
            NotificationManager var7 = (NotificationManager)var0.getSystemService("notification");
            if (var7 == null) {
               Logger.recoverMethod_ee_e925fa09("ChannelHelper", "NotificationManager is null!");
               return false;
            }

            Logger.recoverMethod_dd_e925fa09("ChannelHelper", "prepare NotificationChannel, channelId=" + var1 + "");

            try {
               NotificationChannel var8 = var7.getNotificationChannel(var1);
               if (var8 != null) {
                  var8.setName(var2);
                  if (var6) {
                     var8.setImportance(var3);
                     Logger.recoverMethod_dd_e925fa09("ChannelHelper", "prepare NotificationChannel, channelId: " + var1 + "set importance:" + var3);
                  }

                  var7.createNotificationChannel(var8);
                  return true;
               }
            } catch (Throwable var15) {
               Logger.recoverMethod_w_e925fa09("ChannelHelper", "check channelID failed:" + var15.getMessage());
            }

            NotificationChannel var17 = new NotificationChannel(var1, var2, var3);

            try {
               boolean var9 = true;
               if (VERSION.SDK_INT >= 28) {
                  NotificationChannelGroup var10 = var7.getNotificationChannelGroup("JIGUANG_CHANNEL_GROUP");
                  if (var10 != null) {
                     var9 = false;
                  }
               }

               if (var9) {
                  NotificationChannelGroup var21 = new NotificationChannelGroup("JIGUANG_CHANNEL_GROUP", RecoverType9b4c5ae5.recoverMethod_b_e501eb0d(var0));
                  var7.createNotificationChannelGroup(var21);
               }

               var17.setGroup("JIGUANG_CHANNEL_GROUP");
            } catch (Throwable var14) {
               Logger.recoverMethod_ww_e925fa09("ChannelHelper", "config channel group failed:" + var14.getMessage());
            }

            try {
               boolean var18 = (var4 & 4) != 0;
               var17.enableLights(var18);
            } catch (Throwable var13) {
               Logger.recoverMethod_ww_e925fa09("ChannelHelper", "enableLights fail:" + var13);
            }

            try {
               boolean var19 = (var4 & 2) != 0;
               var17.enableVibration(var19);
            } catch (Throwable var12) {
               Logger.recoverMethod_ww_e925fa09("ChannelHelper", "enableVibration fail:" + var12);
            }

            try {
               if (var5 != null) {
                  var17.setSound(var5, null);
               } else {
                  boolean var20 = (var4 & 1) != 0;
                  if (!var20) {
                     var17.setSound(null, null);
                  }
               }
            } catch (Throwable var11) {
               Logger.recoverMethod_ww_e925fa09("ChannelHelper", "setSound fail:" + var11);
            }

            var7.createNotificationChannel(var17);
            return true;
         } catch (Throwable var16) {
            Logger.recoverMethod_ee_e925fa09("ChannelHelper", "new NotificationChannel fail:" + var16);
            return false;
         }
      } else {
         Logger.recoverMethod_d_e925fa09("ChannelHelper", "sdkversion < 26, no need use notificationChannel!");
         return false;
      }
   }

   public static class Membercf6bbb38 {
      public String recoverField_a;
      public CharSequence recoverField_b;
      public int recoverField_c;
      public int recoverField_d;
      public boolean recoverField_e;
      public int recoverField_f;
      public String recoverField_g;
      public Uri recoverField_h;
      public boolean recoverField_i;

      public Membercf6bbb38(String var1, CharSequence var2, int var3, int var4, String var5, int var6) {
         this.recoverField_a = var1;
         this.recoverField_b = var2;
         this.recoverField_c = var3;
         this.recoverField_f = var4;
         this.recoverField_g = var5;
         this.recoverField_d = var6;
         this.recoverField_e = -1 != this.recoverField_d;
      }
   }
}
