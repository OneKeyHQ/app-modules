package cn.jpush.android.x;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.text.TextUtils;
import cn.jpush.android.ad.RecoverType9b4c5ae6;
import cn.jpush.android.cache.Key;
import cn.jpush.android.cache.RecoverTypea0793a19;
import cn.jpush.android.d.RecoverType2e4cce2a;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.service.SchedulerReceiver;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecoverType2e4d193f {
   private static volatile RecoverType2e4d193f recoverField_a;
   private LinkedList recoverField_b;
   private LinkedList recoverField_c;
   private final Comparator recoverField_d = new Comparator() {
      public int recoverMethod_a_2575504e(RecoverType2e4d193f.Membercf6bca3c var1, RecoverType2e4d193f.Membercf6bca3c var2) {
         return var1.recoverField_a < var2.recoverField_a ? -1 : (var1.recoverField_a == var2.recoverField_a ? 0 : 1);
      }

      // $VF: synthetic method
      @Override
      public int compare(Object var1, Object var2) {
         return this.recoverMethod_a_2575504e((RecoverType2e4d193f.Membercf6bca3c)var1, (RecoverType2e4d193f.Membercf6bca3c)var2);
      }
   };

   private RecoverType2e4d193f() {
   }

   public static RecoverType2e4d193f recoverMethod_a_cd395451() {
      if (recoverField_a == null) {
         synchronized (RecoverType2e4d193f.class) {
            if (recoverField_a == null) {
               recoverField_a = new RecoverType2e4d193f();
            }
         }
      }

      return recoverField_a;
   }

   public void recoverMethod_a_f0554df3(Context var1) {
      this.recoverField_b = new LinkedList();
      if (null == this.recoverField_c) {
         this.recoverField_c = new LinkedList();
      }

      String var2 = (String)RecoverTypea0793a19.get(var1, Key.NotiSchedule());
      if (!TextUtils.isEmpty(var2)) {
         try {
            JSONArray var3 = new JSONArray(var2);

            for (int var4 = 0; var4 < var3.length(); var4++) {
               this.recoverField_b.add(new RecoverType2e4d193f.Membercf6bca3c(var3.getJSONObject(var4)));
            }
         } catch (Throwable var5) {
         }
      }

      if (this.recoverField_c.size() > 0) {
         this.recoverField_b.addAll(this.recoverField_c);
         this.recoverField_c.clear();
         Collections.sort(this.recoverField_b, this.recoverField_d);
      }
   }

   public void recoverMethod_b_f0554df3(Context var1) {
      Object var2 = null;
      if (this.recoverField_b != null) {
         JSONArray var3 = new JSONArray();

         for (Object recoveryElement_83 : this.recoverField_b) {
            RecoverType2e4d193f.Membercf6bca3c var5 = (RecoverType2e4d193f.Membercf6bca3c)recoveryElement_83;
            if (var5.recoverField_e) {
               this.recoverField_c.add(var5);
            } else {
               var3.put(var5.recoverMethod_a_a7dbfe3());
            }
         }

         var2 = var3.toString();
         Collections.sort(this.recoverField_b, this.recoverField_d);
         RecoverTypea0793a19.set(var1, Key.NotiSchedule().set(var2));
      }
   }

   public synchronized void recoverMethod_a_2b7802e6(Context var1, RecoverType2e4d193f.Membercf6bca3c var2) {
      this.recoverMethod_a_f0554df3(var1);
      LinkedList var3 = new LinkedList();
      long var4 = System.currentTimeMillis();
      boolean var6 = var2 != null;

      for (Object recoveryElement_103 : this.recoverField_b) {
         RecoverType2e4d193f.Membercf6bca3c var8 = (RecoverType2e4d193f.Membercf6bca3c)recoveryElement_103;
         if (var6 && var8.recoverField_a > var2.recoverField_a) {
            var6 = false;
            recoverMethod_a_87f3160c(var1, var3, var4, var2);
         }

         recoverMethod_a_87f3160c(var1, var3, var4, var8);
      }

      if (var6) {
         recoverMethod_a_87f3160c(var1, var3, var4, var2);
      }

      this.recoverField_b = var3;
      this.recoverMethod_b_f0554df3(var1);
      if (!var3.isEmpty()) {
         this.recoverMethod_b_2b7802e6(var1, (RecoverType2e4d193f.Membercf6bca3c)var3.getFirst());
      }
   }

   private static void recoverMethod_a_87f3160c(Context var0, LinkedList var1, long var2, RecoverType2e4d193f.Membercf6bca3c var4) {
      if (var4 != null) {
         if (var4.recoverField_c != null && TextUtils.equals(var4.recoverField_c.recoverField_K, "ssp")) {
            Logger.recoverMethod_d_e925fa09("NotificationScheduler", "ssp notification message");
         } else {
            if (var4.recoverField_a <= var2) {
               if (var4.recoverField_b) {
                  if (var4.recoverField_c != null
                     && RecoverType2e4d193e.recoverMethod_a_7555e60(
                        var0, var4.recoverField_c.recoverField_d, var4.recoverField_c.recoverField_h, var4.recoverField_c.recoverField_a
                     )) {
                     Logger.recoverMethod_d_e925fa09("NotificationScheduler", "item: " + var4 + " already cancel");
                     return;
                  }

                  long var5 = RecoverType9b4c5ae6.recoverMethod_b_aad3b1f3(var4.recoverField_c.recoverField_aw);
                  int var7 = RecoverType2e4d193c.recoverMethod_a_73eedead(var4.recoverField_c);
                  if (var5 <= 0L) {
                     Logger.recoverMethod_d_e925fa09("NotificationScheduler", "handleNotification:" + var4);
                     RecoverType2e4d193c.Membercf6bbef9.recoverMethod_a_ae3b7eb8(var0, var4.recoverField_c);
                  } else if (var5 <= var2) {
                     Logger.recoverMethod_d_e925fa09("NotificationScheduler", "cancelNotification:" + var4);
                     RecoverType2e4d193c.recoverMethod_d_1a54e370(var0, var7);
                  } else {
                     Logger.recoverMethod_d_e925fa09("NotificationScheduler", "handleNotification:" + var4);
                     RecoverType2e4d193c.Membercf6bbef9.recoverMethod_a_ae3b7eb8(var0, var4.recoverField_c);
                     var1.add(new RecoverType2e4d193f.Membercf6bca3c(var5, var7));
                  }
               } else {
                  Logger.recoverMethod_d_e925fa09("NotificationScheduler", "cancelNotification:" + var4);
                  RecoverType2e4d193c.recoverMethod_d_1a54e370(var0, var4.recoverField_d);
               }
            } else {
               var1.add(var4);
            }
         }
      }
   }

   private void recoverMethod_b_2b7802e6(Context var1, RecoverType2e4d193f.Membercf6bca3c var2) {
      try {
         Logger.recoverMethod_d_e925fa09("NotificationScheduler", "schedule item=" + var2);
         Intent var3 = new Intent(var1, SchedulerReceiver.class);
         var3.setAction("delay_notify");
         PendingIntent var4 = PendingIntent.getBroadcast(var1, 1000, var3, 67108864);
         AlarmManager var5 = (AlarmManager)var1.getSystemService("alarm");
         if (var5 != null) {
            if (VERSION.SDK_INT >= 19) {
               var5.setWindow(0, var2.recoverField_a, 300L, var4);
            } else {
               var5.set(0, var2.recoverField_a, var4);
            }

            Logger.recoverMethod_d_e925fa09(
               "NotificationScheduler",
               "setAlarm at=" + RecoverType9b4c5ae6.recoverMethod_a_5318097a("yyyy-MM-dd HH:mm:ss").format(new Date(var2.recoverField_a))
            );
         }
      } catch (Throwable var6) {
         Logger.recoverMethod_ww_e925fa09("NotificationScheduler", "can't trigger alarm cause by exception:" + var6);
      }
   }

   static class Membercf6bca3c {
      private long recoverField_a;
      private boolean recoverField_b;
      private RecoverType2e4cce2a recoverField_c;
      private int recoverField_d;
      private boolean recoverField_e;

      public Membercf6bca3c(JSONObject var1) {
         try {
            this.recoverField_a = var1.getLong("operationTime");
            this.recoverField_b = var1.optBoolean("showOrDismiss");
            String var2 = var1.optString("pushEntity");
            if (!TextUtils.isEmpty(var2)) {
               this.recoverField_c = RecoverType2e4cce2a.recoverMethod_b_365d807c(var2);
            }

            this.recoverField_d = var1.optInt("notifyId");
         } catch (Throwable var3) {
         }
      }

      public Membercf6bca3c(long var1, RecoverType2e4cce2a var3, boolean var4) {
         this.recoverField_a = var1;
         this.recoverField_b = true;
         this.recoverField_c = var3;
         this.recoverField_e = var4;
      }

      public Membercf6bca3c(long var1, int var3) {
         this.recoverField_a = var1;
         this.recoverField_b = false;
         this.recoverField_d = var3;
      }

      @Override
      public String toString() {
         return "Item{operationTime="
            + this.recoverField_a
            + ", showOrDismiss="
            + this.recoverField_b
            + ", pushEntity="
            + this.recoverField_c
            + ", notifyId="
            + this.recoverField_d
            + ", isDelayByInapp="
            + this.recoverField_e
            + '}';
      }

      public JSONObject recoverMethod_a_a7dbfe3() {
         try {
            JSONObject var1 = new JSONObject();
            var1.put("operationTime", this.recoverField_a);
            var1.put("showOrDismiss", this.recoverField_b);
            if (this.recoverField_b) {
               var1.put("pushEntity", this.recoverField_c != null ? this.recoverField_c.recoverMethod_c_73cf11cb() : null);
            } else {
               var1.put("notifyId", this.recoverField_d);
            }

            return var1;
         } catch (Throwable var2) {
            return null;
         }
      }
   }
}
