package cn.jpush.android.ad;

import android.content.Context;
import android.text.TextUtils;
import cn.jpush.android.helper.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Comparator;

public class RecoverType9b4c5ae7 {
   public static Object recoverMethod_a_bc5e8086(File var0) throws java.io.FileNotFoundException, java.io.IOException , java.lang.ClassNotFoundException {
      if (var0 != null && var0.exists() && !var0.isDirectory()) {
         ObjectInputStream var1 = null;

         try {
            var1 = new ObjectInputStream(new FileInputStream(var0));
            return var1.readObject();
         } finally {
            RecoverType9b4c5af2.recoverMethod_a_d17885d6(var1);
         }
      } else {
         return null;
      }
   }

   public static void recoverMethod_b_4ad1f42c(File var0) {
      if (var0 != null && !var0.exists()) {
         File var1 = var0.getParentFile();
         if (var1 != null && !var1.exists()) {
            var1.mkdirs();
         }

         try {
            var0.createNewFile();
         } catch (Throwable var3) {
         }
      }
   }

   public static boolean recoverMethod_a_aad3b203(String var0) {
      if (TextUtils.isEmpty(var0)) {
         return false;
      }

      try {
         File var1 = new File(var0);
         return var1.exists();
      } catch (Throwable var2) {
         Logger.recoverMethod_w_e925fa09("FileUtils", "FileUtils e:" + var2);
         return false;
      }
   }

   public static File recoverMethod_a_d4d80730(Context var0, String var1) {
      if (var0 != null) {
         File var2 = var0.getFilesDir();
         if (var2 != null) {
            return new File(var2, var1);
         }
      }

      Logger.recoverMethod_w_e925fa09("FileUtils", "can't get file :" + var1);
      return null;
   }

   public static String recoverMethod_b_a4ea6d43(Context var0, String var1) {
      try {
         File var2 = recoverMethod_a_d4d80730(var0, var1);
         if (var2 == null) {
            Logger.recoverMethod_w_e925fa09("FileUtils", var1 + " can't be null");
            return "";
         }

         if (var2.isFile()) {
            var2.delete();
         }

         if (!var2.exists()) {
            var2.mkdirs();
         }

         return var2.getAbsolutePath() + File.separator;
      } catch (Throwable var3) {
         return "";
      }
   }

   public static String recoverMethod_c_a4ea6d43(Context var0, String var1) {
      try {
         if (RecoverType9b4c5ae5.recoverMethod_a_9b79()) {
         }

         recoverMethod_a_142ec09d(var0, "rich", 10, null);
         return recoverMethod_b_a4ea6d43(var0, "rich" + File.separator + var1);
      } catch (Throwable var3) {
         return "";
      }
   }

   public static void recoverMethod_d_5ffc00fd(Context var0, String var1) {
      try {
         String var2 = recoverMethod_a_a5a07872(var0, "j_in_app_" + var1, 0, false);
         File var3 = new File(var2);
         recoverMethod_e_4ad1f430(var3);
      } catch (Throwable var4) {
         Logger.recoverMethod_ee_e925fa09("FileUtils", "delete file throwable" + var4);
      }
   }

   public static String recoverMethod_a_39e223ba(Context var0, String var1, int var2) {
      return recoverMethod_a_a5a07872(var0, var1, var2, true);
   }

   public static String recoverMethod_a_a5a07872(Context var0, String var1, int var2, boolean var3) {
      try {
         if (var0 == null) {
            return null;
         }

         String var4 = "";
         Object var5 = null;
         if (RecoverType9b4c5ae5.recoverMethod_a_9b79()) {
         }

         var4 = recoverMethod_b_a4ea6d43(var0, var1 + File.separator);
         if (var3) {
            recoverMethod_a_142ec09d(var0, var1, var2, (File)var5);
         }

         return var4;
      } catch (Throwable var6) {
         return "";
      }
   }

   public static String recoverMethod_e_a4ea6d43(Context var0, String var1) {
      try {
         Object var2 = null;
         if (RecoverType9b4c5ae5.recoverMethod_a_9b79()) {
         }

         var2 = var0.getFilesDir().getAbsolutePath();
         String var3 = var2 + File.separator + var1;
         File var4 = new File(var3);
         if (var4.exists()) {
            return var3;
         }

         Logger.recoverMethod_ww_e925fa09("FileUtils", "Can't find developer picture resource in SDCard.");
         return "";
      } catch (Throwable var5) {
         Logger.recoverMethod_ww_e925fa09("FileUtils", "Get developer picture resource failed." + var5);
         return "";
      }
   }

   public static String recoverMethod_a_e7a3948b(String var0, boolean var1) {
      if (TextUtils.isEmpty(var0)) {
         return "";
      }

      int var2 = var0.indexOf("?");
      if (var2 > 0) {
         var0 = var0.substring(0, var2);
         if (TextUtils.isEmpty(var0)) {
            return "";
         }
      }

      return var1 ? "j_in_app_image_" + System.currentTimeMillis() : var0.substring(var0.lastIndexOf("/") + 1);
   }

   public static String recoverMethod_c_af215df4(File var0) {
      byte[] var1 = recoverMethod_d_f6c9231(var0);
      if (var1 == null) {
         return null;
      }

      try {
         return recoverMethod_d_e96ea081(new String(var1, "UTF-8"));
      } catch (UnsupportedEncodingException var3) {
         return null;
      }
   }

   public static byte[] recoverMethod_d_f6c9231(File var0) {
      if (var0 != null && var0.exists() && !var0.isDirectory()) {
         FileInputStream var1 = null;

         try {
            var1 = new FileInputStream(var0);
            return RecoverType9b4c5af2.recoverMethod_a_b328ee5f(var1);
         } catch (Throwable var7) {
            return null;
         } finally {
            RecoverType9b4c5af2.recoverMethod_a_d17885d6(var1);
         }
      } else {
         return null;
      }
   }

   private static String recoverMethod_d_e96ea081(String var0) {
      try {
         if (TextUtils.isEmpty(var0)) {
            return var0;
         } else {
            return recoverMethod_b_aad3b203(var0) ? recoverMethod_c_e96ea081(var0.substring("j-c-o-r-e-".length())) : var0;
         }
      } catch (Throwable var2) {
         return var0;
      }
   }

   public static boolean recoverMethod_b_aad3b203(String var0) {
      try {
         if (TextUtils.isEmpty(var0)) {
            return false;
         }

         char[] var1 = var0.toCharArray();
         char[] var2 = new char["j-c-o-r-e-".length()];
         if (var1.length <= "j-c-o-r-e-".length()) {
            return false;
         }

         for (int var3 = 0; var3 < "j-c-o-r-e-".length(); var3++) {
            var2[var3] = (char)(var1[var3] ^ 2000);
         }

         return "j-c-o-r-e-".equals(new String(var2));
      } catch (Throwable var4) {
         return false;
      }
   }

   public static boolean recoverMethod_a_e925fa0d(String var0, String var1) {
      return recoverMethod_a_3b99f9ef(var0, var1, true);
   }

   public static boolean recoverMethod_a_3b99f9ef(String var0, String var1, boolean var2) {
      try {
         if (var2) {
            var1 = recoverMethod_e_e96ea081(var1);
         }

         byte[] var3 = var1 != null ? var1.getBytes("UTF-8") : null;
         return recoverMethod_b_44c5a30a(var0, var3);
      } catch (Exception var4) {
         Logger.recoverMethod_w_e925fa09("FileUtils", "getBytes exception:" + var4);
         return false;
      }
   }

   private static String recoverMethod_e_e96ea081(String var0) {
      try {
         StringBuilder var1 = new StringBuilder(var0);
         var1.insert(0, "j-c-o-r-e-");
         return recoverMethod_c_e96ea081(var1.toString());
      } catch (Throwable var2) {
         return var0;
      }
   }

   public static String recoverMethod_c_e96ea081(String var0) {
      try {
         if (TextUtils.isEmpty(var0)) {
            return var0;
         }

         char[] var1 = var0.toCharArray();

         for (int var2 = 0; var2 < var1.length; var2++) {
            var1[var2] = (char)(var1[var2] ^ 2000);
         }

         return new String(var1);
      } catch (Throwable var3) {
         return var0;
      }
   }

   private static boolean recoverMethod_b_44c5a30a(String var0, byte[] var1) {
      if (TextUtils.isEmpty(var0)) {
         return false;
      }

      if (var1 == null) {
         var1 = new byte[0];
      }

      FileOutputStream var2 = null;

      try {
         File var3 = new File(var0);
         recoverMethod_b_4ad1f42c(var3);
         var2 = new FileOutputStream(var3);
         var2.write(var1);
         return true;
      } catch (Exception var8) {
         Logger.recoverMethod_w_e925fa09("FileUtils", "save to file exception:" + var8 + " path = " + var0);
         return false;
      } finally {
         RecoverType9b4c5af2.recoverMethod_a_d17885d6(var2);
      }
   }

   public static boolean recoverMethod_b_e925fa0d(String var0, String var1) {
      Logger.recoverMethod_v_e925fa09("FileUtils", "action:createHtmlFile - filePath:" + var0 + ", content:" + var1);
      return !TextUtils.isEmpty(var1) ? recoverMethod_a_3b99f9ef(var0, var1, false) : false;
   }

   public static boolean recoverMethod_a_44c5a30a(String var0, byte[] var1) {
      return var1 != null && var1.length > 0 ? recoverMethod_b_44c5a30a(var0, var1) : false;
   }

   private static void recoverMethod_a_142ec09d(Context var0, String var1, int var2, File var3) {
      File var4 = var3;
      if (var3 == null) {
         var4 = recoverMethod_a_d4d80730(var0, var1);
      }

      if (var4 != null) {
         if (var4.exists() && var4.isDirectory()) {
            File[] var5 = var4.listFiles();
            if (var5 != null && var5.length > var2) {
               Arrays.sort(var5, new Comparator() {
                  public int recoverMethod_a_7c424096(File var1, File var2x) {
                     if (var1.lastModified() > var2x.lastModified()) {
                        return -1;
                     } else {
                        return var1.lastModified() < var2x.lastModified() ? 1 : 0;
                     }
                  }

                  // $VF: synthetic method
                  @Override
                  public int compare(Object var1, Object var2) {
                     return this.recoverMethod_a_7c424096((File)var1, (File)var2);
                  }
               });
               recoverMethod_e_4ad1f430(var5[var5.length - 1]);
            }
         }
      }
   }

   public static boolean recoverMethod_e_4ad1f430(File var0) {
      try {
         if (!var0.exists()) {
            return false;
         }

         if (var0.isFile()) {
            return var0.delete();
         }

         String[] var1 = var0.list();
         if (var1 != null) {
            for (String var5 : var1) {
               File var6 = new File(var0, var5);
               if (var6.isDirectory()) {
                  recoverMethod_e_4ad1f430(var6);
               } else {
                  var6.delete();
               }
            }
         }

         return var0.delete();
      } catch (Exception var7) {
         Logger.recoverMethod_e_e925fa09("FileUtils", "Delete dir error");
         return false;
      }
   }
}
