package cn.jpush.android.v;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import cn.jpush.android.ad.RecoverType9b4c5aea;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.cache.RecoverTypea2146d45;
import cn.jpush.android.data.JPushLocalNotification;
import cn.jpush.android.helper.JCoreHelper;
import cn.jpush.android.helper.Logger;

public class RecoverType2e4d11b9 {
   private static volatile RecoverType2e4d11b9 recoverField_a = null;
   private Handler recoverField_b = null;
   private Context recoverField_c = null;
   private String recoverField_d = "";
   private static final Object recoverField_e = new Object();

   private RecoverType2e4d11b9(Context var1) {
      Logger.recoverMethod_d_e925fa09("JPushLocalNotificationCenter", "Constructor : JPushLocalNotificationCenter");
      this.recoverField_b = new Handler(Looper.getMainLooper());
      this.recoverField_c = var1;
      this.recoverField_d = this.recoverField_c.getPackageName();
   }

   public static RecoverType2e4d11b9 recoverMethod_a_4af8d359(Context var0) {
      Logger.recoverMethod_v_e925fa09("JPushLocalNotificationCenter", "getInstance");
      if (recoverField_a == null) {
         synchronized (recoverField_e) {
            if (recoverField_a == null) {
               recoverField_a = new RecoverType2e4d11b9(var0);
            }
         }
      }

      return recoverField_a;
   }

   public synchronized void recoverMethod_b_f0554df3(Context var1) {
      Logger.recoverMethod_dd_e925fa09("JPushLocalNotificationCenter", "clear all local notification ");
      int[] var2 = RecoverType2e4d11ba.recoverMethod_a_4af8d378(var1).recoverMethod_a_12d40f();
      if (var2 != null && var2.length > 0) {
         Logger.recoverMethod_d_e925fa09("JPushLocalNotificationCenter", " success");

         for (int var6 : var2) {
            JPushInterface.clearNotificationById(this.recoverField_c, var6);
         }
      }
   }

   public synchronized boolean recoverMethod_a_1a54e735(Context var1, long var2) {
      Logger.recoverMethod_dd_e925fa09("JPushLocalNotificationCenter", "remove LocalNotification ");

      try {
         var2 = (int)var2;
         RecoverType2e4d11ba var4 = RecoverType2e4d11ba.recoverMethod_a_4af8d378(var1);
         RecoverType2e4d11bb var5 = var4.recoverMethod_a_c9571536(var2, 0);
         if (var5 != null) {
            Logger.recoverMethod_d_e925fa09("JPushLocalNotificationCenter", "remove local count : " + var5.recoverMethod_b_9b68());
            var4.recoverMethod_a_1349e2(var2);
            JPushInterface.clearNotificationById(this.recoverField_c, (int)var2);
            return true;
         } else {
            return false;
         }
      } catch (Exception var6) {
         Logger.recoverMethod_e_e925fa09("JPushLocalNotificationCenter", "exception:" + var6.getMessage());
         return false;
      }
   }

   public synchronized void recoverMethod_a_9131a9a(Context var1, JPushLocalNotification var2) {
      Logger.recoverMethod_dd_e925fa09("JPushLocalNotificationCenter", "add LocalNotification");
      long var3 = System.currentTimeMillis();
      long var5 = var2.getBroadcastTime() - var3;
      if (RecoverTypea2146d45.recoverMethod_e_f0554df7(var1)) {
         Logger.recoverMethod_d_e925fa09("JPushLocalNotificationCenter", "push has stopped");
      }

      long var7 = var2.getNotificationId();

      try {
         RecoverType2e4d11ba var9 = RecoverType2e4d11ba.recoverMethod_a_4af8d378(var1);
         RecoverType2e4d11bb var10 = var9.recoverMethod_a_c9571536(var7, 0);
         if (var10 != null) {
            var9.recoverMethod_b_e0d72012(var7, 1, 0, 0, var2.toJSON(), var2.getBroadcastTime(), var3);
         } else {
            var9.recoverMethod_a_e0d72012(var7, 1, 0, 0, var2.toJSON(), var2.getBroadcastTime(), var3);
         }
      } catch (Exception var11) {
         Logger.recoverMethod_e_e925fa09("JPushLocalNotificationCenter", "exception:" + var11.getMessage());
      }

      if (var5 < 300000L) {
         this.recoverMethod_a_48775eee(var2.getNotificationId(), var5, 0);
      }
   }

   public synchronized void recoverMethod_c_f0554df3(Context var1) {
      Logger.recoverMethod_d_e925fa09("JPushLocalNotificationCenter", "init LocalNotification");
      RecoverType2e4d11ba var2 = RecoverType2e4d11ba.recoverMethod_a_4af8d378(var1);
      if (var2.recoverMethod_a_138603(false)) {
         Cursor var3 = null;

         try {
            long var4 = System.currentTimeMillis();
            var3 = var2.recoverMethod_a_479cc43c(var4, 300000L);
            if (var3.moveToFirst()) {
               do {
                  RecoverType2e4d11bb var6 = RecoverType2e4d11ba.recoverMethod_a_cfbf928(var3);
                  if (var6 != null) {
                     this.recoverMethod_a_48775eee(var6.recoverMethod_a_9b69(), var6.recoverMethod_f_9b69() - var4, 0);
                  }
               } while (var3.moveToNext());
            }
         } catch (Exception var10) {
            Logger.recoverMethod_e_e925fa09("JPushLocalNotificationCenter", "init LocalNotification cast expt:" + var10);
         } finally {
            if (var3 != null) {
               var3.close();
            }

            var2.recoverMethod_b_1385ff(false);
         }
      } else {
         Logger.recoverMethod_e_e925fa09("JPushLocalNotificationCenter", "init LocalNotification cast expt: db open failed");
      }
   }

   public void recoverMethod_d_f0554df3(final Context var1) {
      Logger.recoverMethod_d_e925fa09("JPushLocalNotificationCenter", "LocalNotification onHeartBeat");
      JCoreHelper.futureExecutor(var1, "JPushLocalNotificationCenter", new RecoverType9b4c5aea("JPushLocationNotificationCenter#onHeartBeat") {
         @Override
         public void recoverMethod_a_9b75() {
            RecoverType2e4d11b9.this.recoverMethod_e_f0554df3(var1);
            RecoverType2e4d11b9.this.recoverMethod_c_f0554df3(var1);
         }
      });
   }

   private synchronized void recoverMethod_e_f0554df3(Context var1) {
      Logger.recoverMethod_d_e925fa09("JPushLocalNotificationCenter", "triggerLNKillProcess");
      RecoverType2e4d11ba var2 = RecoverType2e4d11ba.recoverMethod_a_4af8d378(var1);
      if (var2.recoverMethod_a_138603(false)) {
         Cursor var3 = null;

         try {
            long var4 = System.currentTimeMillis();
            var3 = var2.recoverMethod_a_56cbbcdd(1, var4);
            if (var3.moveToFirst()) {
               do {
                  RecoverType2e4d11bb var6 = RecoverType2e4d11ba.recoverMethod_a_cfbf928(var3);
                  if (var6 != null) {
                     this.recoverMethod_a_5ffc00fd(var1, var6.recoverMethod_d_73cf11cb());
                     var2.recoverMethod_b_e0d72012(
                        var6.recoverMethod_a_9b69(), 0, 0, 0, var6.recoverMethod_d_73cf11cb(), var6.recoverMethod_f_9b69(), var6.recoverMethod_e_9b69()
                     );
                  }
               } while (var3.moveToNext());
            }
         } catch (Exception var10) {
            Logger.recoverMethod_ww_e925fa09("JPushLocalNotificationCenter", "triggerLNKillProcess: " + var10.getMessage());
         } finally {
            if (var3 != null) {
               var3.close();
            }

            var2.recoverMethod_b_1385ff(false);
         }
      } else {
         Logger.recoverMethod_e_e925fa09("JPushLocalNotificationCenter", "init LocalNotification cast expt: db open failed");
      }
   }

   private synchronized void recoverMethod_a_48775eee(final long var1, long var3, int var5) {
      Logger.recoverMethod_d_e925fa09("JPushLocalNotificationCenter", "LocalNotification scheduleReadiedLN");
      if (this.recoverField_b != null) {
         Runnable var6 = new Runnable() {
            @Override
            public void run() {
               try {
                  RecoverType2e4d11ba var1x = RecoverType2e4d11ba.recoverMethod_a_4af8d378(RecoverType2e4d11b9.this.recoverField_c);
                  RecoverType2e4d11bb var2 = var1x.recoverMethod_a_c9571536(var1, 0);
                  if (var2 != null) {
                     if (var2.recoverMethod_c_9b68() == 1) {
                        Logger.recoverMethod_d_e925fa09("JPushLocalNotificationCenter", "remove ");
                        var1x.recoverMethod_b_e0d72012(
                           var2.recoverMethod_a_9b69(), 0, 1, 0, var2.recoverMethod_d_73cf11cb(), var2.recoverMethod_f_9b69(), var2.recoverMethod_e_9b69()
                        );
                     } else if (var2.recoverMethod_b_9b68() > 1) {
                        Logger.recoverMethod_d_e925fa09("JPushLocalNotificationCenter", "repeat add");
                        var1x.recoverMethod_b_e0d72012(
                           var2.recoverMethod_a_9b69(),
                           var2.recoverMethod_b_9b68() - 1,
                           0,
                           0,
                           var2.recoverMethod_d_73cf11cb(),
                           var2.recoverMethod_f_9b69(),
                           var2.recoverMethod_e_9b69()
                        );
                     } else if (var2.recoverMethod_b_9b68() == 1) {
                        Logger.recoverMethod_d_e925fa09("JPushLocalNotificationCenter", "send broadcast");
                        long var3x = System.currentTimeMillis();
                        if (var2.recoverMethod_f_9b69() > var3x) {
                           Logger.recoverMethod_d_e925fa09("JPushLocalNotificationCenter", "repeat add");
                        } else {
                           RecoverType2e4d11b9.this.recoverMethod_a_5ffc00fd(RecoverType2e4d11b9.this.recoverField_c, var2.recoverMethod_d_73cf11cb());
                           var1x.recoverMethod_b_e0d72012(
                              var2.recoverMethod_a_9b69(), 0, 0, 0, var2.recoverMethod_d_73cf11cb(), var2.recoverMethod_f_9b69(), var2.recoverMethod_e_9b69()
                           );
                        }
                     } else {
                        Logger.recoverMethod_d_e925fa09("JPushLocalNotificationCenter", "already triggered");
                     }
                  }
               } catch (Exception var5x) {
                  Logger.recoverMethod_e_e925fa09("JPushLocalNotificationCenter", "exception:" + var5x.getMessage());
               }
            }
         };
         if (var3 <= 0L) {
            Logger.recoverMethod_d_e925fa09("JPushLocalNotificationCenter", "post right now ");
            this.recoverField_b.post(var6);
         } else {
            Logger.recoverMethod_d_e925fa09("JPushLocalNotificationCenter", "post delayed : " + var3);
            this.recoverField_b.postDelayed(var6, var3);
         }
      }
   }

   public void recoverMethod_a_5ffc00fd(Context var1, String var2) {
      Logger.recoverMethod_d_e925fa09("JPushLocalNotificationCenter", "start LocalNotification broadCastToPushService");
      Bundle var3 = new Bundle();
      var3.putString("local_notify_msg", var2);
      JCoreHelper.runActionWithService(var1, "JPUSH", "show_local_notify", var3);
      Logger.recoverMethod_d_e925fa09("JPushLocalNotificationCenter", "end LocalNotification broadCastToPushService");
   }
}
