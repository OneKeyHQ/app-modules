package cn.jpush.android.f;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.os.Build.VERSION;
import android.text.TextUtils;
import cn.jiguang.push.asus.PushMessageReceiver;
import cn.jpush.android.a.RecoverType2e4cc2e5;
import cn.jpush.android.g.RecoverType2e4cd96a;
import cn.jpush.android.g.RecoverType2e4cd96c;
import cn.jpush.android.g.RecoverType2e4cd96d;
import cn.jpush.android.i.RecoverType2e4ce0ec;
import cn.jpush.android.i.RecoverType2e4ce0ed;
import cn.jpush.android.j.RecoverType2e4ce4ad;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import org.json.JSONObject;

public final class RecoverType2e4cd5ab {
   private static AtomicLong recoverField_f = new AtomicLong(0L);
   public static Context recoverField_a;
   public static String recoverField_b;
   public static String recoverField_c = null;
   public static boolean recoverField_d = false;
   public static String recoverField_e = "com.asus.as";
   private static RecoverType2e4cd96a recoverField_g;
   private static String recoverField_h;
   private static Boolean recoverField_i;
   private static ExecutorService recoverField_j = Executors.newSingleThreadExecutor();

   private static boolean recoverMethod_c_f0554df7(Context var0) {
      if (recoverField_i != null) {
         return recoverField_i;
      }

      if (var0 == null) {
         RecoverType2e4ce0ed.recoverMethod_g_e925fa09("Gobal", "context is null");
         return false;
      }

      Context var1 = var0;
      RecoverType2e4cd96d var10000;
      if (!RecoverType2e4ce4ad.recoverMethod_a_f0554df7(var0)) {
         RecoverType2e4ce0ed.recoverMethod_f_e925fa09("ConnectionFactory", "Manufacturer not match, give up create connection.");
         var10000 = null;
      } else if (!RecoverType2e4cd96c.recoverMethod_a_5ffc0101(var1, recoverField_e)) {
         RecoverType2e4ce0ed.recoverMethod_f_e925fa09(
            "ConnectionFactory", "cn.jpush.android.service.PushManagerService not exist in system, give up create connection."
         );
         var10000 = null;
      } else {
         var10000 = new RecoverType2e4cd96d();
      }

      recoverField_g = var10000;
      if (var10000 == null) {
         RecoverType2e4ce0ed.recoverMethod_g_e925fa09("Gobal", "unsupport this device.");
         return recoverField_i = false;
      }

      recoverField_c = (recoverField_a = var0.getApplicationContext()).getPackageName();
      RecoverType2e4cc2e5.recoverMethod_a_f0554df3(var0);
      var1 = var0;

      try {
         NotificationManager var2 = (NotificationManager)var1.getSystemService("notification");
         if (VERSION.SDK_INT >= 26 && var2.getNotificationChannel("asus_default_channel_id") == null) {
            RecoverType2e4ce0ed.recoverMethod_b_e925fa09("Gobal", "create default channel:默认");
            NotificationChannel var4 = new NotificationChannel("asus_default_channel_id", "默认", 3);
            var2.createNotificationChannel(var4);
         }
      } catch (Throwable var3) {
         RecoverType2e4ce0ed.recoverMethod_e_e925fa09("Gobal", "[initNotificationChannel] failed:" + var3.getMessage());
      }

      return recoverField_i = true;
   }

   public static boolean recoverMethod_a_f0554df7(Context var0) {
      return RecoverType2e4cd96c.recoverMethod_a_5ffc0101(var0, recoverField_e);
   }

   public static long recoverMethod_a_9b69() {
      long var0;
      if ((var0 = recoverField_f.incrementAndGet()) == Long.MAX_VALUE) {
         recoverField_f.set(0L);
      }

      return var0;
   }

   public static void recoverMethod_a_3bcbd2b7(Context var0, String var1, long var2, JSONObject var4) {
      if (recoverMethod_c_f0554df7(var0)) {
         recoverField_j.execute(new RecoverType2e4cd5ac(var1, var2, var4));
      }
   }

   public static String recoverMethod_b_e501eb0d(Context var0) {
      if (!TextUtils.isEmpty(recoverField_h)) {
         return recoverField_h;
      }

      try {
         Intent var1;
         (var1 = new Intent()).setAction("com.ups.push.PUSH_RESPONSE");
         var1.setPackage(var0.getPackageName());
         ComponentInfo var3;
         if ((var3 = RecoverType2e4ce4ad.recoverMethod_a_e3a6cbf6(var0, var0.getPackageName(), PushMessageReceiver.class)) != null) {
            recoverField_h = var3.name;
            String var5 = "found userServiceClass :" + recoverField_h + " by getComponentInfo";
            String var4 = "Gobal";
            RecoverType2e4ce0ec.recoverMethod_a_84873242("ASUS_PUSH", var4, false, 4, var5);
         }
      } catch (Throwable var2) {
         RecoverType2e4ce0ed.recoverMethod_e_e925fa09("Gobal", "not found PushMessageReceiver :" + var2.getMessage());
      }

      return recoverField_h;
   }

   // $VF: synthetic method
   static void recoverMethod_a_9abbf639(String var0, long var1, JSONObject var3) {
      JSONObject var15 = var3;
      long var5 = var1;
      var0 = var0;
      if (recoverField_g != null ? !recoverField_g.recoverMethod_b_9b79() : true) {
         RecoverType2e4ce0ed.recoverMethod_a_e925fa09("Gobal", "connect has disconnect, will reconnect.");
         if (recoverField_g != null) {
            recoverField_g.recoverMethod_a_9b79();
         } else {
            boolean var10000 = false;
         }
      }

      if (var15 == null) {
         var15 = new JSONObject();
      }

      try {
         var15.put("JPUSH_PACKAGE", recoverField_c);
         if (var5 <= 0L) {
            var5 = recoverMethod_a_9b69();
         }

         var15.put("JPUSH_RID", var5);
         String var16 = var15.toString();
         RecoverType2e4ce0ed.recoverMethod_c_e925fa09("Gobal", "cmd:" + var0 + ",data:" + var16);
         String var17 = var16;
         var0 = var0;
         if (recoverField_g != null) {
            recoverField_g.recoverMethod_a_e925fa09(var0, var17);
         }
      } catch (Throwable var12) {
         var12.printStackTrace();
      }
   }
}
