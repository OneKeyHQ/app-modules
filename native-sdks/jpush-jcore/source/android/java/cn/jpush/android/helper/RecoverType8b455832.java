package cn.jpush.android.helper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import cn.jpush.android.ad.RecoverType9b4c5aec;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.cache.RecoverTypea2146d45;
import cn.jpush.android.local.JPushConstants;
import cn.jpush.android.z.RecoverType2e4d20be;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RecoverType8b455832 {
   private static ConcurrentLinkedQueue recoverField_a = new ConcurrentLinkedQueue();
   private ConcurrentHashMap recoverField_b = new ConcurrentHashMap();
   private static final Object recoverField_c = new Object();
   private static RecoverType8b455832 recoverField_d;

   public static synchronized RecoverType8b455832 recoverMethod_a_e76c89f4() {
      if (recoverField_d == null) {
         synchronized (recoverField_c) {
            if (recoverField_d == null) {
               recoverField_d = new RecoverType8b455832();
            }
         }
      }

      return recoverField_d;
   }

   public void recoverMethod_a_3f52113a(Context var1, int var2, String var3) {
      Logger.recoverMethod_dd_e925fa09("MobileNumberHelper", "action - setMobileNubmer, sequence:" + var2 + ",mobileNumber:" + var3);
      Bundle var4 = new Bundle();
      var4.putInt("sequence", var2);
      var4.putString("mobile", var3);
      JCoreHelper.runActionWithService(var1, "JPUSH", "set_mobile", var4);
   }

   public void recoverMethod_a_69c77401(Context var1, Bundle var2) {
      if (var2 == null) {
         Logger.recoverMethod_ww_e925fa09("MobileNumberHelper", "[setMobileNum] bundle is bull");
      } else {
         int var3 = var2.getInt("sequence", 0);
         String var4 = var2.getString("mobile");
         String var5 = RecoverTypea2146d45.recoverMethod_f_e501eb0d(var1);
         Logger.recoverMethod_dd_e925fa09("MobileNumberHelper", "action:setMobileNum sequence:" + var3 + ",mobileNumber:" + var4 + ",lastMobileNumber:" + var5);
         if (var5 != null && TextUtils.equals(var4, var5)) {
            Logger.recoverMethod_dd_e925fa09("MobileNumberHelper", "already set this mobile number");
            this.recoverMethod_a_4abe089d(var1, var3, JPushInterface.Member6357d7ea.SUCCESS, var4);
         } else if (RecoverTypea2146d45.recoverMethod_b_f0554de6(var1) == 1) {
            this.recoverMethod_a_4abe089d(var1, var3, JPushInterface.Member6357d7ea.PUSH_STOPED, var4);
         } else {
            if (var5 != null) {
               RecoverTypea2146d45.recoverMethod_a_5ffc00fd(var1, null);
            }

            int var6 = recoverMethod_a_1349e2(System.currentTimeMillis());
            if (var6 != 0) {
               if (var6 == 1) {
                  Logger.recoverMethod_w_e925fa09("MobileNumberHelper", "set mobile number too soon,over 3 times in 10s");
               } else {
                  Logger.recoverMethod_w_e925fa09("MobileNumberHelper", "set mobile number failed,time shaft error，please try again");
               }

               this.recoverMethod_a_4abe089d(
                  var1, var3, var6 == 1 ? JPushInterface.Member6357d7ea.INVOKE_TOO_SOON : JPushInterface.Member6357d7ea.INCORRECT_TIME, var4
               );
            } else {
               int var7 = RecoverType9b4c5aec.recoverMethod_c_aad3b1f2(var4);
               if (var7 != 0) {
                  Logger.recoverMethod_dd_e925fa09("MobileNumberHelper", "Invalid mobile number: " + var4 + ", will not set mobile number this time.");
                  this.recoverMethod_a_4abe089d(var1, var3, var7, var4);
               } else {
                  byte[] var8 = RecoverType2e4d20be.recoverMethod_a_afa28ebe(var4);
                  long var9 = RecoverType8b455834.recoverMethod_a_9b69();
                  this.recoverField_b.put(var9, new RecoverType8b455832.Membercf50186f(var3, var4));
                  JCoreHelper.sendRequest(var1, "JPUSH", 26, 1, var9, 0L, var8);
               }
            }
         }
      }
   }

   public void recoverMethod_a_4abe089d(Context var1, int var2, int var3, String var4) {
      try {
         Logger.recoverMethod_d_e925fa09("MobileNumberHelper", "Action - onResult,sequence:" + var2 + ",code:" + var3 + ",mobileNumber:" + var4);
         String var5 = JPushConstants.getMessageReceiverClass(var1);
         Intent var6 = new Intent();
         if (!TextUtils.isEmpty(var5)) {
            var6 = new Intent(var1, Class.forName(var5));
            var6.setAction("mobile_result");
            var6.putExtra("sequence", var2);
            var6.putExtra("code", var3);
            var6.putExtra("mobile", var4);
         } else {
            Logger.recoverMethod_d_e925fa09("MobileNumberHelper", "not found messageReceiver");
         }

         RecoverType8b45582f.recoverMethod_a_c4d2ecb0(var1, var6);
      } catch (Throwable var7) {
         Logger.recoverMethod_ww_e925fa09("MobileNumberHelper", "onResult error:" + var7);
      }
   }

   private static int recoverMethod_a_1349e2(long var0) {
      if (recoverField_a.size() < 3) {
         recoverField_a.offer(var0);
         return 0;
      }

      long var2 = (Long)recoverField_a.element();
      long var4 = var0 - var2;
      if (var4 < 0L) {
         recoverField_a.clear();
         return 2;
      }

      if (var4 <= 10000L) {
         return 1;
      }

      while (recoverField_a.size() >= 3) {
         recoverField_a.poll();
      }

      recoverField_a.offer(var0);
      return 0;
   }

   public void recoverMethod_a_304871f2(Context var1, long var2, int var4) {
      if (this.recoverField_b.size() != 0) {
         RecoverType8b455832.Membercf50186f var5 = (RecoverType8b455832.Membercf50186f)this.recoverField_b.remove(var2);
         Logger.recoverMethod_d_e925fa09("MobileNumberHelper", "mobileBean:" + var5);
         if (var5 != null) {
            if (var4 == 0) {
               RecoverTypea2146d45.recoverMethod_a_5ffc00fd(var1, var5.recoverField_b);
            } else if (var4 == 11) {
               var4 = JPushInterface.Member6357d7ea.ERROR_CODE_INTERNEL_SERVER_ERROR;
            } else if (var4 == 10) {
               var4 = JPushInterface.Member6357d7ea.ERROR_CODE_INVALID_MOBILENUMBER;
            }

            this.recoverMethod_a_4abe089d(var1, var5.recoverField_a, var4, var5.recoverField_b);
         } else {
            Logger.recoverMethod_d_e925fa09("MobileNumberHelper", "#unexcepted, do not find mobile number request cache");
         }
      }
   }

   public JPushMessage recoverMethod_a_ec80629a(Context var1, Intent var2) {
      JPushMessage var3 = null;
      if (var2 != null) {
         try {
            int var4 = var2.getIntExtra("sequence", -1);
            int var5 = var2.getIntExtra("code", -1);
            String var6 = var2.getStringExtra("mobile");
            var3 = new JPushMessage();
            var3.setSequence(var4);
            var3.setErrorCode(var5);
            var3.setMobileNumber(var6);
         } catch (Throwable var7) {
            Logger.recoverMethod_w_e925fa09("MobileNumberHelper", "parese mobile number response to JPushMessage failed, error:" + var7);
         }
      }

      return var3;
   }

   private class Membercf50186f {
      public int recoverField_a;
      public String recoverField_b;

      public Membercf50186f(int var2, String var3) {
         this.recoverField_a = var2;
         this.recoverField_b = var3;
      }

      @Override
      public String toString() {
         return "MobileBean{sequence=" + this.recoverField_a + ", mobileNumber='" + this.recoverField_b + '\'' + '}';
      }
   }
}
