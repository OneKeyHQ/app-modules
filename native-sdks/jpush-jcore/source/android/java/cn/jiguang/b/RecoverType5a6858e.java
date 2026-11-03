package cn.jiguang.b;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bj.RecoverTypeaf2b0403;
import cn.jiguang.bk.RecoverTypeaf2b07c4;
import cn.jiguang.bk.RecoverTypeaf2b07c9;
import cn.jiguang.bk.RecoverTypeaf2b07ce;
import cn.jiguang.bk.RecoverTypeaf2b07cf;
import cn.jiguang.d.RecoverType5a68d11;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.regex.PatternSyntaxException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecoverType5a6858e implements UncaughtExceptionHandler {
   private static int recoverField_b = 1048576;
   private UncaughtExceptionHandler recoverField_c = null;
   public boolean recoverField_a = true;
   private final Object recoverField_d = new Object();

   public static RecoverType5a6858e recoverMethod_a_4877eda2() {
      return RecoverType5a6858e.Member361b5ecb.recoverField_a;
   }

   private RecoverType5a6858e() {
      this.recoverField_a = recoverMethod_f_f0554df7(RecoverTypeaf2b07c4.recoverMethod_a_6263c3eb(null));
   }

   private static void recoverMethod_a_1a552341(Context var0, boolean var1) {
      try {
         SharedPreferences var2 = var0.getSharedPreferences("cn.jiguang.crash", 0);
         var2.edit().putBoolean("KEY_IS_INIT", var1).apply();
      } catch (Throwable var3) {
      }
   }

   private static boolean recoverMethod_f_f0554df7(Context var0) {
      try {
         SharedPreferences var1 = var0.getSharedPreferences("cn.jiguang.crash", 0);
         return var1.getBoolean("KEY_IS_INIT", true);
      } catch (Throwable var2) {
         return false;
      }
   }

   public void recoverMethod_b_9b75() {
      if (this.recoverField_c == null) {
         this.recoverField_c = Thread.getDefaultUncaughtExceptionHandler();
      }

      Thread.setDefaultUncaughtExceptionHandler(this);
   }

   public void recoverMethod_a_f0554df3(Context var1) {
      if (!this.recoverField_a) {
         this.recoverField_a = true;
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JPushCrashHandler", "init caughtException");
         var1 = RecoverTypeaf2b07c4.recoverMethod_a_6263c3eb(var1);
         recoverMethod_a_1a552341(var1, this.recoverField_a);
      }
   }

   public void recoverMethod_b_f0554df3(Context var1) {
      if (this.recoverField_a) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JPushCrashHandler", "stop caughtException");
         this.recoverField_a = false;
         var1 = RecoverTypeaf2b07c4.recoverMethod_a_6263c3eb(var1);
         recoverMethod_a_1a552341(var1, this.recoverField_a);
      }
   }

   public final void recoverMethod_a_1349ef(long var1) throws java.lang.InterruptedException {
      synchronized (this.recoverField_d) {
         long var4 = System.currentTimeMillis();
         long var6 = 0L;
         if (var1 < 0L) {
            throw new IllegalArgumentException("timeout value is negative");
         }

         if (var1 == 0L) {
            while (RecoverTypeaf2b0403.recoverMethod_b_aad3b203("FUTURE_TASK")) {
               this.recoverField_d.wait(0L);
            }
         } else {
            while (RecoverTypeaf2b0403.recoverMethod_b_aad3b203("FUTURE_TASK")) {
               long var8 = var1 - var6;
               if (var8 <= 0L) {
                  break;
               }

               this.recoverField_d.wait(var8);
               var6 = System.currentTimeMillis() - var4;
            }
         }
      }
   }

   @Override
   public void uncaughtException(Thread var1, Throwable var2) {
      if (this.recoverField_a) {
         RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("JPushCrashHandler", "enable crash report");
         this.recoverMethod_a_879483a0(var2);

         try {
            RecoverType5a6858f var3 = new RecoverType5a6858f();
            RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("FUTURE_TASK", var3);
            this.recoverMethod_a_1349ef(2000L);
         } catch (Throwable var4) {
            RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JPushCrashHandler", "report crash e:" + var4);
         }
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("JPushCrashHandler", "disable crash report");
      }

      if (this.recoverField_c != this && this.recoverField_c != null) {
         this.recoverField_c.uncaughtException(var1, var2);
      }
   }

   private void recoverMethod_a_879483a0(Throwable var1) {
      if (this.recoverField_a) {
         Context var2 = RecoverTypeaf2b07c4.recoverMethod_a_6263c3eb(null);
         if (var2 != null) {
            JSONArray var3 = this.recoverMethod_a_d28ad78e(var2, var1);
            RecoverTypeaf2b07c9.recoverMethod_a_3b358d1d("jpush_uncaughtexception_file", var3);
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JPushCrashHandler", "handleException failed: context is null");
         }
      }
   }

   public static JSONArray recoverMethod_c_5d5a71dd(Context var0) {
      return RecoverTypeaf2b07c9.recoverMethod_b_f96f9a51("jpush_uncaughtexception_file");
   }

   public static void recoverMethod_d_f0554df3(Context var0) {
      RecoverTypeaf2b07c9.recoverMethod_d_aad3b1ff("jpush_uncaughtexception_file");
   }

   private String recoverMethod_b_de092500(Throwable var1) {
      String var2 = var1.toString();
      Object var3 = null;

      try {
         var3 = var2.split(":");
         if (((Object[])var3).length > 1) {
            for (int var4 = ((Object[])var3).length - 1; var4 >= 0; var4--) {
               if (((String)((Object[])var3)[var4]).endsWith("Exception") || ((String)((Object[])var3)[var4]).endsWith("Error")) {
                  var2 = (String)((Object[])var3)[var4];
                  break;
               }
            }
         }
      } catch (NullPointerException var5) {
      } catch (PatternSyntaxException var6) {
      }

      return var2;
   }

   private JSONArray recoverMethod_a_5a359831(Context var1, JSONArray var2, int var3, Throwable var4) {
      long var5 = System.currentTimeMillis() + RecoverType5a68d11.recoverMethod_c_f0554de7(var1);
      StringWriter var7 = new StringWriter();
      PrintWriter var8 = new PrintWriter(var7);
      var4.printStackTrace(var8);
      String var9 = var7.toString();
      if (null == var2) {
         var2 = new JSONArray();
      }

      JSONObject var10 = null;

      try {
         for (int var11 = 0; var11 < var2.length(); var11++) {
            var10 = var2.optJSONObject(var11);
            if (null != var10) {
               String var12 = var10.getString("stacktrace");
               if (var9.equals(var12)) {
                  int var13 = var10.getInt("count");
                  var10.put("count", ++var13);
                  var10.put("crashtime", var5);
                  break;
               }
            }

            var10 = null;
         }

         if (var10 == null) {
            var10 = new JSONObject();
            var10.put("crashtime", var5);
            var10.put("stacktrace", var9);
            var10.put("message", this.recoverMethod_b_de092500(var4));
            var10.put("count", 1);
            var10.put("networktype", RecoverTypeaf2b07ce.recoverMethod_c_e501eb0d(var1));
            PackageInfo var21 = RecoverTypeaf2b07cf.recoverMethod_a_9537863d(var1, 1);
            if (var21 != null) {
               String var22 = var21.versionName == null ? "null" : var21.versionName;
               String var25 = var21.versionCode + "";
               var10.put("versionname", var22);
               var10.put("versioncode", var25);
            }

            if (var3 + var10.toString().length() < recoverField_b) {
               var2.put(var10);
            } else {
               int var23 = 0;
               long var26 = -1L;

               for (int var15 = 0; var15 < var2.length(); var15++) {
                  JSONObject var16 = var2.optJSONObject(var15);
                  if (null != var16) {
                     long var17 = var16.optLong("crashtime");
                     if (var26 == -1L || var17 < var26) {
                        var26 = var17;
                        var23 = var15;
                     }
                  }
               }

               var2.put(var23, var10);
            }
         }
      } catch (Throwable var19) {
      }

      return var2;
   }

   private JSONArray recoverMethod_a_d28ad78e(Context var1, Throwable var2) {
      JSONArray var3 = null;
      int var4 = 0;
      String var5 = RecoverTypeaf2b07c9.recoverMethod_e_e96ea081("jpush_uncaughtexception_file");
      if (!TextUtils.isEmpty(var5)) {
         try {
            var3 = new JSONArray(var5);
            var4 = var5.length();
         } catch (JSONException var7) {
         }
      }

      return this.recoverMethod_a_5a359831(var1, var3, var4, var2);
   }

   public void recoverMethod_e_f0554df3(Context var1) {
      if (var1 == null) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JPushCrashHandler", "Action - reportCrashLog context is null");
      } else if (RecoverType5a68d11.recoverMethod_a_f0554df7(var1)) {
         try {
            RecoverType5a6858f var2 = new RecoverType5a6858f();
            RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("FUTURE_TASK", var2);
         } catch (Throwable var3) {
            RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JPushCrashHandler", "report crash e:" + var3);
         }
      }
   }

   private static final class Member361b5ecb {
      private static final RecoverType5a6858e recoverField_a = new RecoverType5a6858e();
   }
}
