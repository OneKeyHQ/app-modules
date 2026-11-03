package cn.jiguang.az;

import android.content.Context;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ao.RecoverTypeaf2aa26b;
import cn.jiguang.api.ReportCallBack;
import cn.jiguang.ba.RecoverTypeaf2ae239;
import cn.jiguang.ba.RecoverTypeaf2ae23f;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.bj.RecoverTypeaf2b0403;
import cn.jiguang.bk.RecoverTypeaf2b07c4;
import cn.jiguang.bk.RecoverTypeaf2b07c6;
import cn.jiguang.bk.RecoverTypeaf2b07c9;
import cn.jiguang.d.RecoverType5a68d11;
import java.io.File;
import java.io.FilenameFilter;
import java.lang.Thread.UncaughtExceptionHandler;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecoverTypeaf2acbb8 implements UncaughtExceptionHandler {
   private static RecoverTypeaf2acbb8 recoverField_a = new RecoverTypeaf2acbb8();
   private UncaughtExceptionHandler recoverField_b = null;
   private String recoverField_c;
   private RecoverTypeaf2acbb7 recoverField_d = new RecoverTypeaf2acbb7();

   private RecoverTypeaf2acbb8() {
   }

   public static RecoverTypeaf2acbb8 recoverMethod_a_c69910ee() {
      return recoverField_a;
   }

   public void recoverMethod_a_f0554df3(final Context var1) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SentryCrashHandler", "init");
      this.recoverField_d.recoverMethod_b_8497ceb2();
      RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("FUTURE_TASK", new RecoverTypeaf2b0401() {
         @Override
         public void recoverMethod_a_9b75() {
            try {
               RecoverTypeaf2ae23f var1x = RecoverTypeaf2acbb8.this.recoverField_d.recoverMethod_a_c69bcb47();
               JSONObject var2 = new JSONObject();
               var2.put("sentry_envelope_header", RecoverTypeaf2acbb4.recoverMethod_a_e86c016d(false));
               JSONArray var3 = new JSONArray();
               JSONObject var4 = new JSONObject();
               var4.put("sentry_envelope_item_header", RecoverTypeaf2acbb4.recoverMethod_a_fd5e2d6d("session"));
               var4.put("sentry_envelope_item", RecoverTypeaf2acbb4.recoverMethod_a_e6f7d4fd(var1, var1x));
               var3.put(var4);
               var2.put("sentry_envelope_items", var3);
               var2.put("type", "sentry_crash_log");
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SentryCrashHandler", "session is " + var2.toString());
               RecoverTypeaf2acbb8.this.recoverField_c = RecoverTypeaf2acbb8.this.recoverMethod_c_73cf11cb();
               RecoverTypeaf2b07c9.recoverMethod_a_27ad0523(RecoverTypeaf2acbb8.this.recoverField_c, var2);
               RecoverTypeaf2acbb8.this.recoverMethod_a_d97c5eb5(var1, var2, RecoverTypeaf2acbb8.this.recoverField_c);
            } catch (Throwable var5) {
            }
         }
      });
   }

   public void recoverMethod_b_9b75() {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SentryCrashHandler", "setUncaughtExceptionHandler");
      if (this.recoverField_b == null) {
         this.recoverField_b = Thread.getDefaultUncaughtExceptionHandler();
      }

      Thread.setDefaultUncaughtExceptionHandler(this);
   }

   @Override
   public void uncaughtException(Thread var1, Throwable var2) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SentryCrashHandler", "uncaughtException");
      this.recoverMethod_a_879483a0(this.recoverMethod_b_cbc271b4(var1, var2));
      if (this.recoverField_b != this && this.recoverField_b != null) {
         this.recoverField_b.uncaughtException(var1, var2);
      }
   }

   private Throwable recoverMethod_b_cbc271b4(Thread var1, Throwable var2) {
      RecoverTypeaf2ae239 var3 = new RecoverTypeaf2ae239();
      var3.recoverMethod_a_e8e24db8(false);
      var3.recoverMethod_a_aad3b1ff("UncaughtExceptionHandler");
      return new RecoverTypeaf2acbb3(var3, var2, var1);
   }

   public void recoverMethod_a_77d15a4f(Thread var1, Throwable var2) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SentryCrashHandler", "handleException catch exception");
      this.recoverMethod_a_879483a0(var2);
   }

   public void recoverMethod_a_879483a0(Throwable var1) {
      Context var2 = RecoverTypeaf2b07c4.recoverMethod_a_6263c3eb(null);
      if (null == var2) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("SentryCrashHandler", "handleException context is null");
      } else {
         RecoverTypeaf2acbb4.recoverMethod_b_554ebe2(var2, var1);
         if (RecoverTypeaf2acbb9.recoverMethod_a_9b79()) {
            try {
               JSONObject var3 = this.recoverMethod_a_47aa97d0(var2, var1);
               if (var3 == null) {
                  return;
               }

               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SentryCrashHandler", "crash log is : " + var3.toString());
               this.recoverField_c = this.recoverMethod_c_73cf11cb();
               RecoverTypeaf2b07c9.recoverMethod_a_27ad0523(this.recoverField_c, var3);
               this.recoverMethod_a_d97c5eb5(var2, var3, this.recoverField_c);
            } catch (Throwable var4) {
            }
         }
      }
   }

   private String recoverMethod_c_73cf11cb() {
      return "sentry" + File.separator + RecoverTypeaf2b07c6.recoverMethod_a_73cf11cb() + ".envelope";
   }

   private JSONObject recoverMethod_a_47aa97d0(Context var1, Throwable var2) {
      try {
         JSONObject var3 = new JSONObject();
         var3.put("type", "sentry_crash_log");
         JSONArray var4 = new JSONArray();
         JSONObject var5 = new JSONObject();
         JSONObject var6 = RecoverTypeaf2acbb4.recoverMethod_a_47aa97d0(var1, var2);
         if (var6 != null && var6.length() != 0) {
            var5.put("sentry_envelope_item_header", RecoverTypeaf2acbb4.recoverMethod_a_fd5e2d6d("event"));
            var5.put("sentry_envelope_item", var6);
            var4.put(var5);
            var3.put("sentry_envelope_header", RecoverTypeaf2acbb4.recoverMethod_a_e86c016d(true));
            JSONObject var7 = new JSONObject();
            RecoverTypeaf2ae23f var8 = this.recoverField_d.recoverMethod_a_c69bcb47();
            var8.recoverMethod_a_8b85531e(RecoverTypeaf2ae23f.Member8ffb533c.recoverField_c, "", true);
            var7.put("sentry_envelope_item_header", RecoverTypeaf2acbb4.recoverMethod_a_fd5e2d6d("session"));
            var7.put("sentry_envelope_item", RecoverTypeaf2acbb4.recoverMethod_a_e6f7d4fd(var1, var8));
            var4.put(var7);
            var3.put("sentry_envelope_items", var4);
            return var3;
         } else {
            return null;
         }
      } catch (Throwable var9) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("SentryCrashHandler", "generalJSONLog error : " + var9.getMessage());
         return null;
      }
   }

   public void recoverMethod_b_f0554df3(final Context var1) {
      RecoverTypeaf2b0403.recoverMethod_a_63b8aba6("DELAY_TASK", new RecoverTypeaf2b0401() {
         @Override
         public void recoverMethod_a_9b75() {
            File var1x = new File(var1.getFilesDir().getAbsolutePath() + File.separator + "sentry");
            if (var1x != null && var1x.exists() && var1x.isDirectory() && var1x.canWrite() && var1x.canRead()) {
               File[] var2 = var1x.listFiles(new FilenameFilter() {
                  @Override
                  public boolean accept(File var1x, String var2x) {
                     return var2x.endsWith(".envelope");
                  }
               });

               for (File var6 : var2) {
                  String var7 = "sentry" + File.separator + var6.getName();
                  JSONObject var8 = RecoverTypeaf2b07c9.recoverMethod_a_fd5e2d6d(var7);
                  if (var8 != null) {
                     RecoverTypeaf2acbb8.this.recoverMethod_a_d97c5eb5(var1, var8, var7);
                  }
               }
            }
         }
      }, 10000);
   }

   public void recoverMethod_a_d97c5eb5(final Context var1, final JSONObject var2, final String var3) {
      if (var1 == null) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("SentryCrashHandler", "Action - reportCrashLog context is null");
      } else if (RecoverType5a68d11.recoverMethod_a_f0554df7(var1)) {
         try {
            RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("FUTURE_TASK", new RecoveredReportTask(var1, var2, var3));
         } catch (Throwable var5) {
            RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("SentryCrashHandler", "report crash e:" + var5);
         }
      }
   }

   // A named task lets the nested callback capture the parent, as in the original class.
   class RecoveredReportTask extends RecoverTypeaf2b0401 {
      final Context recoverField_a;
      final JSONObject recoverField_b;
      final String recoverField_c;

      RecoveredReportTask(Context context, JSONObject report, String path) {
         this.recoverField_a = context;
         this.recoverField_b = report;
         this.recoverField_c = path;
      }

      @Override
      public void recoverMethod_a_9b75() {
         RecoverTypeaf2aa26b.recoverMethod_a_6892eae2(recoverField_a, recoverField_b, new ReportCallBack() {
            @Override
            public void onFinish(int result) {
               if (result == 0) {
                  RecoverTypeaf2b07c9.recoverMethod_d_aad3b1ff(RecoveredReportTask.this.recoverField_c);
               }
            }
         });
      }
   }

}
