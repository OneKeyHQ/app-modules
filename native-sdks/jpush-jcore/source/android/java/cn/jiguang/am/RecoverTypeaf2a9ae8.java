package cn.jiguang.am;

import android.content.Context;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import cn.jiguang.bj.RecoverTypeaf2b0403;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.bk.RecoverTypeaf2b07c4;
import cn.jiguang.bk.RecoverTypeaf2b07c9;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class RecoverTypeaf2a9ae8 {
   public static String recoverField_a = "jpush_log";
   public static String recoverField_b = "internal_debug_log.txt";
   private static final SimpleDateFormat recoverField_c = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
   private static final Object recoverField_d = new Object();

   public static void recoverMethod_a_2d6f8978(String var0, String var1, String var2, String var3, Throwable var4) {
      if (null == var2 || var2.trim().equals("")) {
         var2 = "Logger";
      }

      if (null != var3) {
         try {
            StringBuilder var5 = new StringBuilder();
            String var6 = recoverField_c.format(new Date());
            String var7 = RecoverTypeaf2b07c1.recoverMethod_a_e501eb0d(RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d());
            int var8 = Process.myPid();
            long var9 = Thread.currentThread().getId();
            String[] var11 = var3.split("\n");
            boolean var12 = true;

            for (int var13 = 0; var13 < var11.length; var13++) {
               String var14 = var11[var13];
               if (!var12) {
                  var5.append("\n");
               }

               var5.append((CharSequence)var6)
                  .append(" ")
                  .append(var8)
                  .append("-")
                  .append(var9)
                  .append(" ")
                  .append(var7)
                  .append(" ")
                  .append(var1)
                  .append(" ")
                  .append(var0)
                  .append(" ")
                  .append("[")
                  .append(var2)
                  .append("] ")
                  .append(var14);
               var12 = false;
            }

            if (var4 != null) {
               StringWriter var20 = new StringWriter();
               PrintWriter var21 = new PrintWriter(var20);

               try {
                  var4.printStackTrace(var21);
               } catch (Throwable var18) {
                  try {
                     var21.close();
                  } catch (Throwable var17) {
                     var18.addSuppressed(var17);
                  }

                  throw var18;
               }

               var21.close();
               String var22 = var20.toString();
               var5.append("\n");
               var5.append(var22);
            }

            if (!TextUtils.isEmpty(var5)) {
               recoverMethod_a_aad3b1ff(var5.toString().trim());
            }
         } catch (Throwable var19) {
            Log.w("Logger", "log2File call failed: " + var19.getMessage());
         }
      }
   }

   private static void recoverMethod_a_aad3b1ff(final String var0) {
      try {
         RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("ONCE_TASK", new Runnable() {
            @Override
            public void run() {
               try {
                  synchronized (RecoverTypeaf2a9ae8.recoverField_d) {
                     Context var2 = RecoverTypeaf2b07c4.recoverMethod_a_6263c3eb(null);
                     if (var2 == null) {
                        return;
                     }

                     String var3 = var2.getFilesDir() + File.separator + RecoverTypeaf2a9ae8.recoverField_a + File.separator;
                     File var4 = new File(var3 + RecoverTypeaf2a9ae8.recoverField_b);
                     File var5 = var4.getParentFile();
                     if (!var5.exists()) {
                        var5.mkdirs();
                     }

                     if (!var4.exists()) {
                        var4.createNewFile();
                        RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("Logger", "create log file: " + var4.getName());
                     }

                     RecoverTypeaf2a9ae8.recoverMethod_b_4ad1f42c(var4);
                     RecoverTypeaf2b07c9.recoverMethod_a_d9caeafa(var4, var0 + "\n");
                  }
               } catch (Throwable var8) {
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("Logger", "write logs to file error is " + var8.getMessage());
               }
            }
         });
      } catch (Throwable var2) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("Logger", "saveLogs error is " + var2.getMessage());
      }
   }

   public static void recoverMethod_a_9b75() {
      try {
         Context var0 = RecoverTypeaf2b07c4.recoverMethod_a_6263c3eb(null);
         if (var0 != null && !RecoverTypeaf2a9ae7.recoverMethod_c_f0554df7(var0)) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("Logger", "Skip clearing log files due to active references");
            return;
         }

         String var1 = var0.getFilesDir() + File.separator + recoverField_a + File.separator;
         RecoverTypeaf2b07c9.recoverMethod_d_aad3b1ff(var1);
      } catch (Throwable var2) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("Logger", "clearLogFiles error is " + var2.getMessage());
      }
   }

   private static void recoverMethod_b_4ad1f42c(File var0) {
      try {
         if (var0.exists() && var0.length() > 10485760L) {
            synchronized (recoverField_d) {
               if (var0.exists() && var0.length() > 10485760L) {
                  RecoverTypeaf2b07c9.recoverMethod_b_4ad1f430(var0);
                  var0.createNewFile();
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("Logger", "Log file exceeded max size, rotated");
               }
            }
         }
      } catch (Exception var4) {
         Log.w("Logger", "Failed to rotate log file: " + var4.getMessage());
      }
   }
}
