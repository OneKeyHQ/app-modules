package cn.jpush.android.s;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import cn.jpush.android.ad.RecoverType9b4c5ae5;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.local.JPushConstants;
import cn.jpush.android.o.RecoverType2e4cf772;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicLong;

public class RecoverType2e4d0676 {
   private static int recoverField_a = 0;
   private static WeakReference recoverField_b;
   private static String recoverField_c = "";
   private String recoverField_d;
   private static AtomicLong recoverField_e = new AtomicLong(System.currentTimeMillis());

   public static long recoverMethod_a_9b69() {
      return recoverField_e.get();
   }

   public static Activity recoverMethod_a_2b1cd295(Context var0) {
      return recoverMethod_b_2b1cd295(var0);
   }

   private static Activity recoverMethod_b_2b1cd295(Context var0) {
      try {
         if (recoverField_b != null) {
            Logger.recoverMethod_d_e925fa09("InAppLifeListener", "mActivity is null, try to user weakActivity: " + recoverField_b);
            Activity var1 = (Activity)recoverField_b.get();
            if (var1 != null && !var1.isFinishing()) {
               Logger.recoverMethod_dd_e925fa09("InAppLifeListener", "use weak activity, " + ((Activity)recoverField_b.get()).getLocalClassName());
               return var1;
            }
         }

         Activity var3 = RecoverType9b4c5ae5.recoverMethod_k_2b1cd295(var0);
         if (var3 != null && !var3.isFinishing()) {
            Logger.recoverMethod_dd_e925fa09("InAppLifeListener", "use current stack activity, " + var3.getLocalClassName());
            recoverField_b = new WeakReference(var3);
         }
      } catch (Throwable var2) {
         Logger.recoverMethod_w_e925fa09("InAppLifeListener", "getActivityInternal error, " + var2.getMessage());
      }

      return null;
   }

   public void recoverMethod_a_f9c5dd07(Activity var1) {
      recoverField_b = new WeakReference(var1);
   }

   public void recoverMethod_b_f9c5dd07(Activity var1) {
      if (var1 == null) {
         Logger.recoverMethod_w_e925fa09("InAppLifeListener", "onActivityStarted activity is null");
      } else {
         try {
            String var2 = var1.getClass().getName();
            Logger.recoverMethod_d_e925fa09(
               "InAppLifeListener",
               "[onActivityStarted], gLatestActivityName: " + recoverField_c + ", activityTaskCount: " + recoverField_a + " currentActivity: " + var2
            );
            this.recoverMethod_a_f9c5dd07(var1);
            Context var3 = JPushConstants.getAppContext(var1);
            String var4 = var3.getPackageName();
            Logger.recoverMethod_d_e925fa09("InAppLifeListener", "mLastPkg=" + this.recoverField_d + " currentPkg=" + var4);
            if (recoverField_a == 0) {
               Logger.recoverMethod_d_e925fa09("InAppLifeListener", "is foreground, change foreground state");
               recoverField_e.set(System.currentTimeMillis());
               boolean var5 = true;
               RecoverType2e4cf772.recoverMethod_a_cd353c7e().recoverMethod_a_157a5ecb(var3, var5, var2);
            } else if (!TextUtils.equals(var2, recoverField_c) && TextUtils.equals(var4, this.recoverField_d)) {
               Logger.recoverMethod_d_e925fa09("InAppLifeListener", "use enter new activity,show inapp message");
               RecoverType2e4cf772.recoverMethod_a_cd353c7e().recoverMethod_a_157a5ecb(var3, true, var2);
            }

            recoverField_c = var2;
            this.recoverField_d = var4;
            recoverField_a++;
         } catch (Throwable var6) {
            Logger.recoverMethod_w_e925fa09("InAppLifeListener", "onActivityStarted error, " + var6.getMessage());
         }
      }
   }

   public void recoverMethod_c_f9c5dd07(Activity var1) {
      this.recoverMethod_a_f9c5dd07(var1);
   }

   public void recoverMethod_d_f9c5dd07(Activity var1) {
      if (null != var1 && (recoverField_b == null || recoverField_b.get() != null)) {
         try {
            if (recoverField_b != null && recoverField_b.get() == var1) {
               recoverField_b = new WeakReference(null);
            }
         } catch (Throwable var3) {
            Logger.recoverMethod_w_e925fa09("InAppLifeListener", " onActivityDestroyed error, " + var3.getMessage());
         }
      } else {
         Logger.recoverMethod_w_e925fa09("InAppLifeListener", "onActivityPaused activity is null");
      }
   }

   public void recoverMethod_e_f9c5dd07(Activity var1) {
      if (null == var1) {
         Logger.recoverMethod_w_e925fa09("InAppLifeListener", "onActivityStopped activity is null");
      } else {
         try {
            String var2 = var1.getClass().getName();
            Logger.recoverMethod_d_e925fa09(
               "InAppLifeListener",
               "[onActivityStopped], curClzName: " + var2 + ", latestCurClzName: " + recoverField_c + ", activityTaskCount: " + recoverField_a
            );
            RecoverType2e4cf772.recoverMethod_a_cd353c7e().recoverMethod_a_5ffc00fd(var1.getApplicationContext(), var2);
            if (recoverField_a > 0) {
               recoverField_a--;
            }

            if (recoverField_a == 0 && !recoverField_c.equals(var2)) {
               recoverField_a++;
            }
         } catch (Throwable var3) {
            Logger.recoverMethod_w_e925fa09("InAppLifeListener", "onActivityStopped error, " + var3.getMessage());
         }
      }
   }

   public void recoverMethod_f_f9c5dd07(Activity var1) {
      Logger.recoverMethod_d_e925fa09("InAppLifeListener", "[onActivityDestroyed]");
      if (var1 == null) {
         Logger.recoverMethod_w_e925fa09("InAppLifeListener", "onActivityDestroyed activity is null");
      } else {
         try {
            RecoverType2e4cf772.recoverMethod_a_cd353c7e().recoverMethod_a_5ffc00fd(var1.getApplicationContext(), var1.getClass().getCanonicalName());
         } catch (Throwable var3) {
            Logger.recoverMethod_w_e925fa09("InAppLifeListener", "onActivityDestroyed error, " + var3.getMessage());
         }
      }
   }
}
