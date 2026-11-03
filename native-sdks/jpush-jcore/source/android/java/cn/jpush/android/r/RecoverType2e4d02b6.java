package cn.jpush.android.r;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import cn.jpush.android.ad.RecoverType9b4c5af2;
import cn.jpush.android.d.RecoverType2e4cce2a;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.p.RecoverType2e4cfb33;
import cn.jpush.android.s.RecoverType2e4d0676;
import cn.jpush.android.t.RecoverType2e4d0a37;
import cn.jpush.android.u.RecoverType2e4d0df9;
import java.util.HashMap;

public class RecoverType2e4d02b6 {
   private static HashMap recoverField_a = new HashMap();
   private static HashMap recoverField_b = new HashMap();

   public static RecoverType2e4d0a37 recoverMethod_a_2b43953e(Context var0, int var1) {
      int var2 = var0.getResources().getConfiguration().orientation;
      Logger.recoverMethod_d_e925fa09("InAppHelper", "current orientation state: " + var2 + " type=" + var1);
      if (var2 < 0) {
         Logger.recoverMethod_d_e925fa09("InAppHelper", "[getLayoutConfig] get screen orientation failed, curOrientation: " + var2);
         return null;
      }

      HashMap var3 = null;
      if (var2 == 2) {
         var3 = (HashMap)recoverField_b.get(var1);
      } else if (var2 == 1) {
         var3 = (HashMap)recoverField_a.get(var1);
      }

      Logger.recoverMethod_d_e925fa09("InAppHelper", "current map: " + var3 + " type=" + var1);
      if (var3 != null && var3.size() != 0) {
         return (RecoverType2e4d0a37)var3.get(var2);
      }

      Logger.recoverMethod_d_e925fa09("InAppHelper", "current is null ");
      return null;
   }

   public static void recoverMethod_a_1934cb4b(Context var0, RecoverType2e4cce2a var1, int var2) {
      recoverField_a.clear();
      recoverField_b.clear();
      RecoverType2e4d0a37 var3 = recoverMethod_b_3d90b443(var0, var1, var2);
      HashMap var4 = new HashMap();
      var4.put(1, var3);
      recoverField_a.put(var2, var4);
      var3 = recoverMethod_c_3d90b443(var0, var1, var2);
      HashMap var5 = new HashMap();
      var5.put(2, var3);
      recoverField_b.put(var2, var5);
   }

   private static RecoverType2e4d0a37 recoverMethod_b_3d90b443(Context var0, RecoverType2e4cce2a var1, int var2) {
      boolean var3 = RecoverType2e4d0df9.recoverMethod_c_134632(var2);
      if (40 != var2 && !var3) {
         return new RecoverType2e4d0a37().recoverMethod_e_710a9b0c(-1).recoverMethod_f_710a9b0c(-1);
      }

      DisplayMetrics var4 = RecoverType9b4c5af2.recoverMethod_a_f8f56096(var0);
      int var5 = var3 ? -2 : RecoverType9b4c5af2.recoverMethod_a_1a54e363(var0, 94);
      return new RecoverType2e4d0a37()
         .recoverMethod_a_4cb3dc2a(0.3F)
         .recoverMethod_b_4cb3dc2a(0.3F)
         .recoverMethod_a_710a9b0c((int)(0.5F * var4.heightPixels))
         .recoverMethod_b_710a9b0c((int)(0.9F * var4.widthPixels))
         .recoverMethod_e_710a9b0c(-1)
         .recoverMethod_f_710a9b0c(var5)
         .recoverMethod_c_710a9b0c(var1.recoverField_aH == 0 ? 48 : 80)
         .recoverMethod_d_710a9b0c(65824)
         .recoverMethod_a_710a9b0c((int)(0.5F * var4.heightPixels))
         .recoverMethod_b_710a9b0c((int)(0.9F * var4.widthPixels))
         .recoverMethod_a_ad11720f(Boolean.TRUE)
         .recoverMethod_a_1bb8e7f6(true)
         .recoverMethod_b_1bb8e7f6(true)
         .recoverMethod_b_ad11720f(true)
         .recoverMethod_a_fcf248a3(var1.recoverField_bb * 1000)
         .recoverMethod_b_fcf248a3(500.0F)
         .recoverMethod_c_fcf248a3(500.0F);
   }

   private static RecoverType2e4d0a37 recoverMethod_c_3d90b443(Context var0, RecoverType2e4cce2a var1, int var2) {
      boolean var3 = RecoverType2e4d0df9.recoverMethod_c_134632(var2);
      if (40 != var2 && !var3) {
         return new RecoverType2e4d0a37().recoverMethod_e_710a9b0c(-1).recoverMethod_f_710a9b0c(-1);
      }

      DisplayMetrics var4 = RecoverType9b4c5af2.recoverMethod_a_f8f56096(var0);
      int var5 = var4.heightPixels;
      int var6 = var3 ? -2 : RecoverType9b4c5af2.recoverMethod_a_1a54e363(var0, 94);
      return new RecoverType2e4d0a37()
         .recoverMethod_a_4cb3dc2a(0.3F)
         .recoverMethod_b_4cb3dc2a(0.3F)
         .recoverMethod_a_710a9b0c((int)(0.5F * var4.heightPixels))
         .recoverMethod_b_710a9b0c((int)(0.9F * var4.widthPixels))
         .recoverMethod_c_710a9b0c(var1.recoverField_aH == 0 ? 48 : 80)
         .recoverMethod_d_710a9b0c(65824)
         .recoverMethod_e_710a9b0c(var5)
         .recoverMethod_f_710a9b0c(var6)
         .recoverMethod_a_ad11720f(Boolean.TRUE)
         .recoverMethod_a_1bb8e7f6(true)
         .recoverMethod_b_1bb8e7f6(true)
         .recoverMethod_b_ad11720f(true)
         .recoverMethod_a_fcf248a3(var1.recoverField_bb * 1000)
         .recoverMethod_b_fcf248a3(500.0F)
         .recoverMethod_c_fcf248a3(500.0F);
   }

   private static long recoverMethod_a_f0554de7(Context var0) {
      long var1 = 0L;

      try {
         String var3 = RecoverType2e4cfb33.recoverMethod_b_e501eb0d(var0);
         if (!TextUtils.isEmpty(var3)) {
            String[] var4 = var3.split(",");
            long var5 = Long.parseLong(var4[0]);
            int var7 = Integer.parseInt(var4[1]);
            Logger.recoverMethod_d_e925fa09("InAppHelper", "checkUnionTopBannerTime, topBanner exposureTime: " + var5 + ", server exposureTime: " + var7);
            long var8 = System.currentTimeMillis() - (var5 + 3000L);
            var8 = var8 < 0L ? 0L : var8;
            var1 = var8 >= var7 ? 0L : var7 - var8;
            Logger.recoverMethod_d_e925fa09("InAppHelper", "checkUnionTopBannerTime, should delay time: " + var1 + ", leftTime: " + var8);
         }
      } catch (Throwable var10) {
      }

      return var1;
   }

   public static long recoverMethod_a_3048b1b7(Context var0, long var1, boolean var3) {
      long var4 = Math.max(System.currentTimeMillis() - RecoverType2e4d0676.recoverMethod_a_9b69(), 0L);
      long var6 = Math.max(var1, 0L);
      long var8 = recoverMethod_a_f0554de7(var0);
      long var10 = var3 ? (var6 > var4 ? var6 - var4 : 0L) : var6;
      long var12 = Math.max(var8, var10);
      Logger.recoverMethod_d_e925fa09(
         "InAppHelper",
         "getDelayDisplayTime - stayForegroundTime: "
            + var4
            + ", delayDisTime: "
            + var6
            + ", unionDelayTime: "
            + var8
            + ", foreDelayTime: "
            + var10
            + ", realDelayTime: "
            + var12
      );
      if (var12 > 0L) {
         Logger.recoverMethod_d_e925fa09("InAppHelper", "notify inapp should delay to show, delay time: " + var12 + ", server delay time: " + var6);
      } else {
         Logger.recoverMethod_d_e925fa09("InAppHelper", "notify inapp display directory, stay foreground time: " + var4 + ", server delay time: " + var6);
      }

      return var12;
   }

   public static void recoverMethod_a_1a54e731(Context var0, long var1) {
      String var3 = System.currentTimeMillis() + "," + var1 * 1000L;
      RecoverType2e4cfb33.recoverMethod_a_5ffc00fd(var0, var3);
   }

   public static long recoverMethod_b_1a54e725(Context var0, long var1) {
      try {
         long var3 = RecoverType2e4cfb33.recoverMethod_a_f0554de7(var0);
         Logger.recoverMethod_d_e925fa09("InAppHelper", "getIntervalTime lastShowMessageTime=" + var3);
         if (var3 <= 0L) {
            return var1;
         } else {
            long var5 = System.currentTimeMillis();
            long var7 = Math.abs(var5 - var3);
            Logger.recoverMethod_d_e925fa09("InAppHelper", "getIntervalTime currentTimeMillis=" + var5);
            Logger.recoverMethod_d_e925fa09("InAppHelper", "getIntervalTime diff=" + var7);
            if (var7 > var1) {
               Logger.recoverMethod_d_e925fa09("InAppHelper", "getIntervalTime return value= 0");
               return 0L;
            } else {
               Logger.recoverMethod_d_e925fa09("InAppHelper", "getIntervalTime return value= " + (var1 - var7));
               return var1 - var7;
            }
         }
      } catch (Throwable var9) {
         Logger.recoverMethod_d_e925fa09("InAppHelper", "getIntervalTime return intervalTime= " + var1);
         return var1;
      }
   }
}
