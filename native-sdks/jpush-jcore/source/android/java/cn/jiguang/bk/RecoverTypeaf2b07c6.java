package cn.jiguang.bk;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

public class RecoverTypeaf2b07c6 {
   private static final Object recoverField_b = new Object();
   private static ConcurrentHashMap recoverField_c = new ConcurrentHashMap();
   public static String recoverField_a = "yyyyMMdd_HHmm";
   private static final TimeZone recoverField_d = TimeZone.getTimeZone("UTC");
   private static final ThreadLocal recoverField_e = new ThreadLocal() {
      protected SimpleDateFormat recoverMethod_a_627ae644() {
         SimpleDateFormat var1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ROOT);
         var1.setTimeZone(RecoverTypeaf2b07c6.recoverField_d);
         return var1;
      }

      // $VF: synthetic method
      @Override
      protected Object initialValue() {
         return this.recoverMethod_a_627ae644();
      }
   };

   public static String recoverMethod_a_73cf11cb() {
      return recoverMethod_a_5318097a("yyyyMMdd HH:mm:ss.SSS").format(new Date());
   }

   public static SimpleDateFormat recoverMethod_a_5318097a(String var0) {
      ThreadLocal var1 = (ThreadLocal)recoverField_c.get(var0);
      if (var1 == null) {
         synchronized (recoverField_b) {
            var1 = (ThreadLocal)recoverField_c.get(var0);
            if (var1 == null) {
               var1 = new RecoverTypeaf2b07c6.Member90883303(var0);
               recoverField_c.put(var0, var1);
            }
         }
      }

      return (SimpleDateFormat)var1.get();
   }

   public static String recoverMethod_b_73cf11cb() {
      return recoverMethod_a_5318097a("yyyy-MM-dd_HH:mm:ss").format(new Date());
   }

   public static long recoverMethod_a_134622(int var0) {
      Calendar var1 = Calendar.getInstance();
      var1.roll(6, var0);
      return var1.getTime().getTime();
   }

   public static Date recoverMethod_c_a911e122() {
      Calendar var0 = Calendar.getInstance(recoverField_d);
      return var0.getTime();
   }

   public static String recoverMethod_a_b044460a(Date var0) {
      DateFormat var1 = (DateFormat)recoverField_e.get();
      return var1.format(var0);
   }

   private static class Member90883303 extends ThreadLocal {
      private String recoverField_a;

      Member90883303(String var1) {
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
