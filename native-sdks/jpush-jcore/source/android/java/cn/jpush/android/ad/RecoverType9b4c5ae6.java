package cn.jpush.android.ad;

import android.text.TextUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

public class RecoverType9b4c5ae6 {
   private static final Object recoverField_a = new Object();
   private static ConcurrentHashMap recoverField_b = new ConcurrentHashMap();

   public static SimpleDateFormat recoverMethod_a_5318097a(String var0) {
      ThreadLocal var1 = (ThreadLocal)recoverField_b.get(var0);
      if (var1 == null) {
         synchronized (recoverField_a) {
            var1 = (ThreadLocal)recoverField_b.get(var0);
            if (var1 == null) {
               var1 = new RecoverType9b4c5ae6.Memberf9a13e23(var0);
               recoverField_b.put(var0, var1);
            }
         }
      }

      return (SimpleDateFormat)var1.get();
   }

   public static long recoverMethod_b_aad3b1f3(String var0) {
      if (!TextUtils.isEmpty(var0)) {
         try {
            return recoverMethod_a_5318097a("yyyy-MM-dd HH:mm:ss").parse(var0).getTime();
         } catch (ParseException var2) {
         }
      }

      return -1L;
   }

   private static class Memberf9a13e23 extends ThreadLocal {
      private String recoverField_a;

      Memberf9a13e23(String var1) {
         this.recoverField_a = var1;
      }

      protected SimpleDateFormat recoverMethod_a_627ae644() {
         return new SimpleDateFormat(this.recoverField_a, Locale.ENGLISH);
      }

      // $VF: synthetic method
      @Override
      protected Object initialValue() {
         return this.recoverMethod_a_627ae644();
      }
   }
}
