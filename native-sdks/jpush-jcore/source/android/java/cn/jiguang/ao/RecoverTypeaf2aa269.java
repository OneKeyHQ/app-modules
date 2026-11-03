package cn.jiguang.ao;

import android.content.Context;
import android.text.TextUtils;
import cn.jiguang.al.RecoverTypeaf2a9726;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.bj.RecoverTypeaf2b0403;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.bk.RecoverTypeaf2b07c9;
import cn.jiguang.bk.RecoverTypeaf2b07cd;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import cn.jiguang.internal.ActionManager;
import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONObject;

public class RecoverTypeaf2aa269 {
   private static String recoverField_a = null;
   private static String recoverField_b = null;
   private static ConcurrentHashMap recoverField_c = new ConcurrentHashMap();
   private static final AtomicBoolean recoverField_d = new AtomicBoolean(false);
   private static boolean recoverField_e = false;

   public static FileFilter recoverMethod_a_b1291960() {
      return new FileFilter() {
         @Override
         public boolean accept(File var1) {
            return var1.getName().length() != 24 && !var1.getName().equals("0");
         }
      };
   }

   public static void recoverMethod_a_f0554df3(Context var0) {
      RecoverTypeaf2b07c9.Member90883e46 var1 = new RecoverTypeaf2b07c9.Member90883e46(false, true, "jpush_stat_history", 1);
      String var2 = RecoverType5a68d10.recoverMethod_e_e501eb0d(var0);
      File[] var3 = RecoverTypeaf2b07c9.recoverMethod_a_51736ac4(var0.getFilesDir(), var1, recoverMethod_a_b1291960());
      if (var3 != null) {
         for (File var7 : var3) {
            try {
               File var8 = new File(var7, "nowrap");
               RecoverTypeaf2b07c9.recoverMethod_b_4ad1f430(var8);
               File var9 = new File(var7.getParent() + File.separator + var2 + File.separator + var7.getName());
               if (!var9.getParentFile().exists()) {
                  var9.getParentFile().mkdirs();
               }

               var7.renameTo(var9);
            } catch (Throwable var10) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("updateByAppKey", "e=" + var10);
            }
         }
      }
   }

   public static void recoverMethod_a_2213544c(Context var0, String... var1) {
      FileFilter[] var2;
      if (var1 != null && var1.length != 0) {
         var2 = new FileFilter[var1.length + 1];

         for (int var3 = 0; var3 < var1.length; var3++) {
            var2[var3 + 1] = RecoverTypeaf2b07c9.Member90883e46.recoverMethod_a_de38896a(var1[var3]);
         }
      } else {
         var2 = new FileFilter[1];
      }

      var2[0] = new RecoverTypeaf2b07c9.Member90883e46(false, true, "jpush_stat_history", 1);
      File[] var8 = RecoverTypeaf2b07c9.recoverMethod_a_51736ac4(var0.getFilesDir(), var2);
      if (var8 != null) {
         for (File var7 : var8) {
            RecoverTypeaf2b07c9.recoverMethod_b_4ad1f430(var7);
         }
      }
   }

   private static synchronized String recoverMethod_d_e501eb0d(Context var0) {
      if (!TextUtils.isEmpty(recoverField_b)) {
         return recoverField_b;
      }

      if (recoverField_a == null) {
         String var1 = RecoverTypeaf2b07c1.recoverMethod_a_e501eb0d(var0);
         if (!TextUtils.isEmpty(var1)) {
            String var2 = var0.getPackageName();
            if (var1.equals(var2)) {
               recoverField_a = "";
            } else {
               recoverField_a = var1.replaceFirst(var0.getPackageName() + ":", "_");
            }
         }
      }

      File var3 = RecoverTypeaf2b07c9.recoverMethod_c_3b83896e(recoverField_a != null ? "jpush_stat_history" + recoverField_a : "jpush_stat_history");
      if (var3 != null) {
         recoverField_b = var3.getAbsolutePath();
         return recoverField_b;
      } else {
         return "jpush_stat_history" + recoverField_a;
      }
   }

   private static String recoverMethod_e_e501eb0d(Context var0) {
      String var1 = RecoverType5a68d10.recoverMethod_e_e501eb0d(var0);
      if (TextUtils.isEmpty(var1)) {
         var1 = "0";
      }

      return recoverMethod_d_e501eb0d(var0) + File.separator + var1;
   }

   public static File recoverMethod_a_d4d80730(Context var0, String var1) {
      String var2 = recoverMethod_e_e501eb0d(var0) + var1 + File.separator + UUID.randomUUID().toString();
      return new File(var2);
   }

   public static File recoverMethod_a_d8504dda(Context var0, String var1, JSONObject var2, boolean var3) {
      File var4 = recoverMethod_a_d4d80730(var0, var1);
      if (var3) {
         recoverMethod_b_4ad1f42c(var4);
      }

      RecoverTypeaf2b07c9.recoverMethod_b_d9caeafa(var4, var2.toString());
      return var4;
   }

   public static void recoverMethod_a_4ad1f42c(File var0) {
      if (var0 != null) {
         recoverField_c.remove(var0);
      }
   }

   private static void recoverMethod_b_4ad1f42c(File var0) {
      if (var0 != null) {
         recoverField_c.put(var0, true);
      }
   }

   public static void recoverMethod_a_1385ff(boolean var0) {
      recoverField_e = var0;
   }

   private static boolean recoverMethod_b_9b79() {
      return recoverField_e;
   }

   public static void recoverMethod_b_f0554df3(final Context var0) {
      if (recoverField_d.get()) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ReportHistory", "isRunning, no need report");
      } else {
         recoverMethod_a_1385ff(false);
         if (!TextUtils.isEmpty(RecoverTypeaf2a9726.recoverMethod_b_e501eb0d(var0))
            && var0.getPackageName().equals(RecoverTypeaf2b07c1.recoverMethod_a_e501eb0d(var0))) {
            RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("MAJOR_TASK", new RecoverTypeaf2b0401("ReportHistory#submitReportAll") {
               @Override
               public void recoverMethod_a_9b75() {
                  RecoverTypeaf2aa269.recoverMethod_f_f0554df3(var0);
               }
            });
         } else {
            RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("NORMAL_TASK", new RecoverTypeaf2b0401("ReportHistory#submitReportAll") {
               @Override
               public void recoverMethod_a_9b75() {
                  RecoverTypeaf2aa269.recoverMethod_f_f0554df3(var0);
               }
            });
         }
      }
   }

   private static void recoverMethod_f_f0554df3(Context var0) {
      try {
         recoverField_d.set(true);
         File[] var1 = RecoverTypeaf2b07c9.recoverMethod_a_a68c0411(recoverMethod_e_e501eb0d(var0), RecoverTypeaf2b07c9.Member90883e46.recoverField_b);
         if (var1 == null) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ReportHistory", "no history, no report");
         } else {
            for (File var5 : var1) {
               if (recoverMethod_b_9b79()) {
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ReportHistory", "history, report is Tcp and http Fail");
                  return;
               }

               int var6 = recoverMethod_c_e205721d(var0, var5);
               if (var6 != 0 && var6 != 1) {
                  return;
               }
            }
         }
      } catch (Throwable var10) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("ReportHistory", "run upload exception=" + var10);
      } finally {
         recoverField_d.set(false);
      }
   }

   private static int recoverMethod_a_e205721d(Context var0, File var1) {
      if ((Long)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_B_48794abf()) == 0L) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ReportHistory", "can't get uid, skip upload history");
         return -2;
      }

      File[] var2 = RecoverTypeaf2b07c9.recoverMethod_a_51736ac4(var1, RecoverTypeaf2b07c9.Member90883e46.recoverField_a);
      if (var2 != null && var2.length != 0) {
         LinkedList var3 = new LinkedList();

         for (File var7 : var2) {
            if (!Boolean.TRUE.equals(recoverField_c.get(var7))) {
               var3.add(var7);
            }
         }

         if (var3.isEmpty()) {
            return 0;
         }

         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ReportHistory", "process space=" + recoverMethod_e_af215df4(var1) + " history[" + var3.size() + "]");
         Collections.sort(var3, new Comparator() {
            public int recoverMethod_a_7c424096(File var1, File var2x) {
               long var3x = var1.lastModified() - var2x.lastModified();
               return var3x < 0L ? 1 : (var3x == 0L ? 0 : -1);
            }

            // $VF: synthetic method
            @Override
            public int compare(Object var1, Object var2) {
               return this.recoverMethod_a_7c424096((File)var1, (File)var2);
            }
         });

         for (Object recoveryElement_245 : var3) {
            File var11 = (File)recoveryElement_245;
            try {
               JSONObject var12 = RecoverTypeaf2aa268.recoverMethod_a_588a971a(var11);
               if (var12 == null) {
                  RecoverTypeaf2b07c9.recoverMethod_b_4ad1f430(var11);
               } else {
                  recoverMethod_b_4ad1f42c(var11);
                  Set var13 = RecoverTypeaf2aa26b.recoverMethod_a_b00d7198(var12);
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ReportHistory", "report history types=" + var13);
                  int var8 = RecoverTypeaf2aa26b.recoverMethod_a_c08b9df7(var0, var13, var12, var11, null);
                  if (var8 != 0 && var8 != 1) {
                     return var8;
                  }
               }
            } catch (Throwable var9) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ReportHistory", "upload e:" + var9);
            }
         }

         return 0;
      } else {
         return 0;
      }
   }

   private static void recoverMethod_c_4ad1f42c(File var0) {
      File[] var1 = RecoverTypeaf2b07c9.recoverMethod_a_51736ac4(var0, RecoverTypeaf2b07c9.Member90883e46.recoverField_a);
      if (var1 != null && var1.length > 1) {
         List var2 = Arrays.asList(var1);
         Collections.sort(var2, new Comparator() {
            public int recoverMethod_a_7c424096(File var1, File var2x) {
               long var3 = var1.lastModified() - var2x.lastModified();
               return var3 < 0L ? 1 : (var3 == 0L ? 0 : -1);
            }

            // $VF: synthetic method
            @Override
            public int compare(Object var1, Object var2) {
               return this.recoverMethod_a_7c424096((File)var1, (File)var2);
            }
         });
         int var3 = 0;

         for (Object recoveryElement_288 : var2) {
            File var5 = (File)recoveryElement_288;
            var3 = (int)(var3 + var5.length());
            if (var3 > 1048576) {
               RecoverTypeaf2b07c9.recoverMethod_b_4ad1f430(var5);
            }
         }

         if (var2.size() < var1.length) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
               "ReportHistory", "trim space history=" + recoverMethod_e_af215df4(var0) + "," + var1.length + " to " + var2.size()
            );
         }
      }
   }

   private static void recoverMethod_d_4ad1f42c(File var0) {
      try {
         File[] var1 = RecoverTypeaf2b07c9.recoverMethod_a_51736ac4(new File(var0, "tmp"), RecoverTypeaf2b07c9.Member90883e46.recoverField_a);
         if (var1 == null || var1.length == 0) {
            return;
         }

         LinkedList var2 = new LinkedList();

         for (File var6 : var1) {
            if (!Boolean.TRUE.equals(recoverField_c.get(var6))) {
               var2.add(var6);
            }
         }

         if (var2.isEmpty()) {
            return;
         }

         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ReportHistory", "process space=" + recoverMethod_e_af215df4(var0) + " tmp[" + var2.size() + "]");
         Set var13 = recoverMethod_c_6051c6a4();
         HashMap var14 = new HashMap();
         File[] var15 = RecoverTypeaf2b07c9.recoverMethod_a_51736ac4(var0, RecoverTypeaf2b07c9.Member90883e46.recoverField_a);
         RecoverTypeaf2aa268 var16 = RecoverTypeaf2aa268.recoverMethod_a_b74b45dc(RecoverTypeaf2b07c9.recoverMethod_a_51027336(var15), var13);
         if (var16 != null) {
            String var7 = String.valueOf(var16.recoverField_b);
            var14.put(var7, var16);
         }

         for (Object recoveryElement_332 : var2) {
            File var8 = (File)recoveryElement_332;
            RecoverTypeaf2aa268 var9 = RecoverTypeaf2aa268.recoverMethod_a_b74b45dc(var8, var13);
            if (var9 != null) {
               String var10 = String.valueOf(var9.recoverField_b);
               RecoverTypeaf2aa268 var11 = (RecoverTypeaf2aa268)var14.get(var10);
               if (var11 == null) {
                  var14.put(var10, var9);
               } else if (!var11.recoverMethod_a_a6e15dff(var9, var0)) {
                  var14.put(var10, var9);
               }
            }
         }

         for (Object recoveryElement_345 : var14.values()) {
            RecoverTypeaf2aa268 var19 = (RecoverTypeaf2aa268)recoveryElement_345;
            var19.recoverMethod_a_a6e15dff(null, var0);
         }
      } catch (Throwable var12) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ReportHistory", "processTmp e:" + var12);
      }
   }

   private static void recoverMethod_b_e205722a(Context var0, File var1) {
      try {
         JSONObject var2 = RecoverTypeaf2aa26b.recoverMethod_a_16ce4761(var0);
         if (RecoverTypeaf2b07cd.recoverMethod_c_be7fc16d(var2)) {
            return;
         }

         File[] var3 = RecoverTypeaf2b07c9.recoverMethod_a_51736ac4(new File(var1, "nowrap"), RecoverTypeaf2b07c9.Member90883e46.recoverField_a);
         if (var3 == null || var3.length == 0) {
            return;
         }

         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ReportHistory", "process space=" + recoverMethod_e_af215df4(var1) + " nowrap[" + var3.length + "]");
         File var4 = new File(var1, "tmp");
         RecoverTypeaf2aa268 var5 = RecoverTypeaf2aa268.recoverMethod_a_e1c46bdb(var3[0], var2);

         for (int var6 = 1; var6 < var3.length; var6++) {
            RecoverTypeaf2aa268 var7 = RecoverTypeaf2aa268.recoverMethod_a_e1c46bdb(var3[var6], var2);
            if (var7 != null) {
               if (var5 == null) {
                  var5 = var7;
               } else if (!var5.recoverMethod_a_a6e15dff(var7, var4)) {
                  var5 = var7;
               }
            }
         }

         if (var5 != null) {
            var5.recoverMethod_a_a6e15dff(null, var4);
         }
      } catch (Throwable var8) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ReportHistory", "processNowrap e:" + var8);
      }
   }

   private static int recoverMethod_c_e205721d(Context var0, File var1) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ReportHistory", "upload space=" + recoverMethod_e_af215df4(var1));
      recoverMethod_b_e205722a(var0, var1);
      recoverMethod_d_4ad1f42c(var1);
      int var2 = recoverMethod_a_e205721d(var0, var1);
      recoverMethod_c_4ad1f42c(var1);
      return var2;
   }

   private static String recoverMethod_e_af215df4(File var0) {
      try {
         File var1 = var0.getParentFile();
         return var1.getParentFile().getName() + File.separator + var1.getName() + File.separator + var0.getName();
      } catch (Throwable var2) {
         return var0.getAbsolutePath();
      }
   }

   private static Set recoverMethod_c_6051c6a4() {
      HashSet var0 = new HashSet();
      var0.add("uid");
      var0.add("app_key");
      var0.add("sdk_ver");
      var0.add("channel");
      var0.add("app_version");
      JSONObject var1 = new JSONObject();
      ActionManager.getInstance().wrapSdkVersionInfo(var1);
      Iterator var2 = var1.keys();

      while (var2.hasNext()) {
         var0.add((String)var2.next());
      }

      return var0;
   }
}
