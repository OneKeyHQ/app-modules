package cn.jiguang.bk;

import android.os.Build.VERSION;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bg.RecoverTypeaf2af8c1;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecoverTypeaf2b07c9 {
   private static final byte[] recoverField_a = recoverMethod_j_e96ea081("j-c-o-r-e-").getBytes(StandardCharsets.UTF_8);
   private static final ConcurrentHashMap recoverField_b = new ConcurrentHashMap();

   public static void recoverMethod_a_4ad1f42c(File var0) {
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

   public static boolean recoverMethod_a_d9caeafa(File var0, String var1) {
      if (var0 == null || var0.isDirectory()) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("FileUtils", "File should not be null or a directory");
         return false;
      }

      if (TextUtils.isEmpty(var1)) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("FileUtils", "Append data should not be empty");
         return false;
      }

      try {
         recoverMethod_a_4ad1f42c(var0);
         BufferedWriter var2 = new BufferedWriter(new FileWriter(var0, true));

         boolean var3;
         try {
            var2.write(var1);
            var2.flush();
            var3 = true;
         } catch (Throwable var6) {
            try {
               var2.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }

            throw var6;
         }

         var2.close();
         return var3;
      } catch (IOException var7) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("FileUtils", "Failed to append to file: " + var0.getAbsolutePath() + ". Exception: " + var7.getMessage());
         return false;
      }
   }

   public static boolean recoverMethod_a_27ad0523(String var0, JSONObject var1) {
      return recoverMethod_a_e925fa0d(var0, var1 != null ? var1.toString() : null);
   }

   public static boolean recoverMethod_a_3b358d1d(String var0, JSONArray var1) {
      return recoverMethod_a_e925fa0d(var0, var1 != null ? var1.toString() : null);
   }

   public static JSONObject recoverMethod_a_fd5e2d6d(String var0) {
      try {
         if (TextUtils.isEmpty(var0)) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("FileUtils", "file_name is null , give up read ");
            return null;
         } else {
            String var1 = recoverMethod_e_e96ea081(var0);
            return RecoverTypeaf2b07d5.recoverMethod_a_aad3b203(var1) ? null : new JSONObject(var1.trim());
         }
      } catch (Throwable var2) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("FileUtils", "can't build " + var0 + " into JsonObject, give up read :" + var2);
         return null;
      }
   }

   public static JSONArray recoverMethod_b_f96f9a51(String var0) {
      try {
         if (TextUtils.isEmpty(var0)) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("FileUtils", "file_name is null , give up read ");
            return null;
         } else {
            String var1 = recoverMethod_e_e96ea081(var0);
            return RecoverTypeaf2b07d5.recoverMethod_a_aad3b203(var1) ? null : new JSONArray(var1.trim());
         }
      } catch (Throwable var2) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("FileUtils", "can't build " + var0 + " into JSONArray, give up read :" + var2);
         return null;
      }
   }

   public static File[] recoverMethod_a_a68c0411(String var0, FileFilter... var1) {
      try {
         File var2 = new File(var0);
         return recoverMethod_a_51736ac4(var2, var1);
      } catch (Throwable var3) {
         return null;
      }
   }

   public static File[] recoverMethod_a_51736ac4(File var0, FileFilter... var1) {
      if (var0 != null && var0.exists() && var0.isDirectory()) {
         if (var1 != null && var1.length != 0 && (var1.length != 1 || var1[0] != null)) {
            if (var1.length == 1) {
               return var0.listFiles(var1[0]);
            }

            LinkedList var2 = new LinkedList();
            var2.add(var0);

            for (FileFilter var6 : var1) {
               LinkedList var7 = new LinkedList();

               for (Object recoveryElement_147 : var2) {
                  File var9 = (File)recoveryElement_147;
                  File[] var10 = var6 != null ? var9.listFiles(var6) : var9.listFiles();
                  if (var10 != null && var10.length > 0) {
                     Collections.addAll(var7, var10);
                  }
               }

               if (var7.isEmpty()) {
                  return null;
               }

               var2 = var7;
            }

            return (File[])var2.toArray(new File[0]);
         } else {
            return var0.listFiles();
         }
      } else {
         return null;
      }
   }

   public static File recoverMethod_c_3b83896e(String var0) {
      File var1 = RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d().getFilesDir();
      if (var1 != null) {
         return new File(var1, var0);
      }

      RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("FileUtils", "can't get file :" + var0);
      return null;
   }

   public static File recoverMethod_a_51027336(File[] var0) {
      if (var0 != null && var0.length != 0) {
         File var1 = null;

         for (File var5 : var0) {
            long var6 = var1 != null ? var1.lastModified() : 0L;
            if (var5.lastModified() > var6) {
               var1 = var5;
            }
         }

         return var1;
      } else {
         return null;
      }
   }

   public static void recoverMethod_d_aad3b1ff(String var0) {
      recoverMethod_b_4ad1f430(recoverMethod_c_3b83896e(var0));
   }

   public static boolean recoverMethod_b_4ad1f430(File var0) {
      if (var0 != null && var0.exists()) {
         try {
            RecoverTypeaf2af8c1.recoverMethod_b_aad3b203(var0.getAbsolutePath());
         } catch (Throwable var6) {
         }

         if (var0.isDirectory() && !recoverMethod_d_4ad1f430(var0)) {
            File[] var1 = var0.listFiles();
            if (var1 != null) {
               for (File var5 : var1) {
                  if (!recoverMethod_b_4ad1f430(var5)) {
                     return false;
                  }
               }
            }
         }

         return var0.delete();
      } else {
         return false;
      }
   }

   private static boolean recoverMethod_d_4ad1f430(File var0) {
      if (var0 != null && var0.exists()) {
         try {
            if (VERSION.SDK_INT >= 26) {
               return Files.isSymbolicLink(var0.toPath());
            }

            String var1 = var0.getCanonicalPath();
            String var2 = var0.getAbsolutePath();
            return !var1.equals(var2);
         } catch (Throwable var3) {
            return false;
         }
      } else {
         return false;
      }
   }

   private static ReentrantLock recoverMethod_h_eb2a038d(String var0) {
      ReentrantLock var1 = (ReentrantLock)recoverField_b.get(var0);
      if (var1 == null) {
         ReentrantLock var2 = new ReentrantLock();
         ReentrantLock var3 = (ReentrantLock)recoverField_b.putIfAbsent(var0, var2);
         var1 = var3 != null ? var3 : var2;
      }

      return var1;
   }

   private static void recoverMethod_i_aad3b1ff(String var0) {
      ReentrantLock var1 = (ReentrantLock)recoverField_b.get(var0);
      if (var1 != null) {
         if (var1.isHeldByCurrentThread()) {
            while (var1.getHoldCount() > 0) {
               var1.unlock();
            }
         }

         if (!var1.hasQueuedThreads() && var1.getHoldCount() == 0) {
            recoverField_b.remove(var0, var1);
         }
      }
   }

   public static String recoverMethod_e_e96ea081(String var0) {
      return recoverMethod_c_af215df4(recoverMethod_c_3b83896e(var0));
   }

   public static String recoverMethod_c_af215df4(File var0) {
      if (recoverMethod_e_4ad1f430(var0)) {
         return null;
      }

      if (var0.length() > 10485760L) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("FileUtils", "File too large to read: " + var0.getAbsolutePath() + ", size: " + var0.length());
         return null;
      }

      try {
         String var1 = RecoverTypeaf2af8c1.recoverMethod_a_e96ea081(var0.getAbsolutePath());
         if (!RecoverTypeaf2b07d5.recoverMethod_a_aad3b203(var1)) {
            return var1;
         }
      } catch (Throwable var172) {
      }

      ReentrantLock var173 = recoverMethod_h_eb2a038d(var0.getAbsolutePath());
      FileChannel var2 = null;

      try {
         if (!var173.tryLock(10L, TimeUnit.SECONDS)) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("FileUtils", "read2String Failed to acquire file lock within timeout");
            return null;
         }

         var2 = new FileInputStream(var0).getChannel();
         long var3 = var2.size();
         boolean var5 = false;
         byte[] var6 = new byte[recoverField_a.length];
         long var7 = 0L;
         if (var3 >= recoverField_a.length) {
            ByteBuffer var9 = ByteBuffer.allocate(recoverField_a.length);
            var2.read(var9);
            ((Buffer)var9).flip();
            var9.get(var6);
            var5 = Arrays.equals(var6, recoverField_a);
            if (var5) {
               var7 = recoverField_a.length;
            } else {
               var7 = 0L;
            }
         }

         long var174 = var3 - var7;
         if (var174 <= 0L) {
            return var5 ? "" : new String(var6, StandardCharsets.UTF_8);
         }

         ByteBuffer var11 = ByteBuffer.allocateDirect((int)var174);
         var2.position(var7);
         var2.read(var11);
         ((Buffer)var11).flip();
         byte[] var12 = new byte[var11.remaining()];
         var11.get(var12);
         String var13 = new String(var12, StandardCharsets.UTF_8);
         return var5 ? recoverMethod_j_e96ea081(var13) : new String(var6, StandardCharsets.UTF_8) + var13;
      } catch (OutOfMemoryError var169) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("FileUtils", "Out of memory reading file: " + var0.getAbsolutePath());
         return null;
      } catch (Throwable var170) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("FileUtils", "read fail: " + var170.getMessage());
         return null;
      } finally {
         try {
            if (var2 != null) {
               var2.close();
            }
         } catch (Throwable var167) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("FileUtils", "read fail: " + var167.getMessage());
         } finally {
            recoverMethod_i_aad3b1ff(var0.getAbsolutePath());
         }
      }
   }

   public static boolean recoverMethod_a_e925fa0d(String var0, String var1) {
      return recoverMethod_b_d9caeafa(recoverMethod_c_3b83896e(var0), var1);
   }

   public static boolean recoverMethod_b_d9caeafa(File var0, String var1) {
      if (TextUtils.isEmpty(var1)) {
         return recoverMethod_b_4ad1f430(var0);
      }

      recoverMethod_a_4ad1f42c(var0);

      try {
         boolean var2 = RecoverTypeaf2af8c1.recoverMethod_a_d9caeafa(var0, var1);
         if (var2) {
            return true;
         }
      } catch (Throwable var15) {
      }

      ReentrantLock var20 = recoverMethod_h_eb2a038d(var0.getAbsolutePath());
      File var3 = null;

      boolean var5;
      try {
         if (!var20.tryLock(10L, TimeUnit.SECONDS)) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("FileUtils", "save Failed to acquire file lock within timeout");
            return false;
         }

         var3 = new File(var0.getParent(), var0.getName() + ".tmp");
         BufferedOutputStream var4 = new BufferedOutputStream(new FileOutputStream(var3), 8192);

         try {
            var4.write(recoverField_a);
            recoverMethod_a_fc20f892(var4, var1);
            var5 = var3.renameTo(var0) || recoverMethod_a_7c4240a7(var3, var0);
         } catch (Throwable var16) {
            try {
               var4.close();
            } catch (Throwable var14) {
               var16.addSuppressed(var14);
            }

            throw var16;
         }

         var4.close();
      } catch (InterruptedException var17) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("FileUtils", "save Thread interrupted while waiting for file lock");
         return false;
      } catch (Throwable var18) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("FileUtils", "save fail: " + var18.getMessage());
         return false;
      } finally {
         recoverMethod_i_aad3b1ff(var0.getAbsolutePath());
         if (var3 != null && var3.exists()) {
            var3.delete();
         }
      }

      return var5;
   }

   private static void recoverMethod_a_fc20f892(BufferedOutputStream var0, String var1) throws java.io.IOException {
      if (var1.length() <= 65536) {
         String var2 = recoverMethod_j_e96ea081(var1);
         var0.write(var2.getBytes(StandardCharsets.UTF_8));
      } else {
         for (int var6 = 0; var6 < var1.length(); var6 += 65536) {
            int var3 = Math.min(var1.length(), var6 + 65536);
            String var4 = var1.substring(var6, var3);
            String var5 = recoverMethod_j_e96ea081(var4);
            var0.write(var5.getBytes(StandardCharsets.UTF_8));
         }
      }

      var0.flush();
   }

   private static boolean recoverMethod_a_7c4240a7(File var0, File var1) {
      try {
         FileChannel var2 = new FileInputStream(var0).getChannel();

         boolean var4;
         try {
            FileChannel var3 = new FileOutputStream(var1).getChannel();

            try {
               var2.transferTo(0L, var2.size(), var3);
               var4 = true;
            } catch (Throwable var8) {
               if (var3 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (var3 != null) {
               var3.close();
            }
         } catch (Throwable var9) {
            if (var2 != null) {
               try {
                  var2.close();
               } catch (Throwable var6) {
                  var9.addSuppressed(var6);
               }
            }

            throw var9;
         }

         if (var2 != null) {
            var2.close();
         }

         return var4;
      } catch (IOException var10) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("FileUtils", "Copy failed: " + var10.toString());
         return false;
      }
   }

   public static boolean recoverMethod_b_e925fa0d(String var0, String var1) {
      return recoverMethod_c_d9caeafa(recoverMethod_c_3b83896e(var0), var1);
   }

   public static boolean recoverMethod_c_d9caeafa(File var0, String var1) {
      if (TextUtils.isEmpty(var1)) {
         return false;
      }

      recoverMethod_a_4ad1f42c(var0);

      try {
         boolean var2 = RecoverTypeaf2af8c1.recoverMethod_a_e925fa0d(var0.getAbsolutePath(), var1);
         if (var2) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("FileUtils", "append success using native method: " + var0.getAbsolutePath());
            return true;
         }
      } catch (Throwable var14) {
      }

      String var18 = var0.getAbsolutePath();
      ReentrantLock var3 = recoverMethod_h_eb2a038d(var18);

      boolean var19;
      try {
         if (!var3.tryLock(10L, TimeUnit.SECONDS)) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("FileUtils", "append Failed to acquire file lock within timeout");
            return false;
         }

         RandomAccessFile var4 = new RandomAccessFile(var0, "rw");

         label126: {
            try {
               boolean var5 = recoverMethod_a_cc388e29(var4);
               if (var5) {
                  var19 = recoverMethod_a_ecd658b3(var4, var1);
                  break label126;
               }

               var19 = recoverMethod_d_d9caeafa(var0, var1);
            } catch (Throwable var15) {
               try {
                  var4.close();
               } catch (Throwable var13) {
                  var15.addSuppressed(var13);
               }

               throw var15;
            }

            var4.close();
            return var19;
         }

         var4.close();
      } catch (Throwable var16) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("FileUtils", "append fail: " + var16.getMessage());
         return false;
      } finally {
         recoverMethod_i_aad3b1ff(var18);
      }

      return var19;
   }

   private static boolean recoverMethod_a_ecd658b3(RandomAccessFile var0, String var1) {
      try {
         var0.seek(var0.length());

         for (int var2 = 0; var2 < var1.length(); var2 += 65536) {
            int var3 = Math.min(var1.length(), var2 + 65536);
            String var4 = var1.substring(var2, var3);
            String var5 = recoverMethod_j_e96ea081(var4);
            var0.write(var5.getBytes(StandardCharsets.UTF_8));
         }

         return true;
      } catch (Exception var6) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("FileUtils", "Append encrypted failed: " + var6.getMessage());
         return false;
      }
   }

   private static boolean recoverMethod_d_d9caeafa(File var0, String var1) {
      File var2 = new File(var0.getParent(), var0.getName() + ".tmp");

      try {
         BufferedOutputStream var3 = new BufferedOutputStream(new FileOutputStream(var2));

         try {
            var3.write(recoverField_a);
            if (var0.exists() && var0.length() > 0L) {
               FileInputStream var4 = new FileInputStream(var0);

               try {
                  byte[] var5 = new byte[Math.min(65536, (int)var0.length())];

                  int var6;
                  while ((var6 = var4.read(var5)) != -1) {
                     String var7 = new String(var5, 0, var6, StandardCharsets.UTF_8);
                     String var8 = recoverMethod_j_e96ea081(var7);
                     var3.write(var8.getBytes(StandardCharsets.UTF_8));
                  }
               } catch (Throwable var18) {
                  try {
                     var4.close();
                  } catch (Throwable var17) {
                     var18.addSuppressed(var17);
                  }

                  throw var18;
               }

               var4.close();
            }

            String var22 = recoverMethod_j_e96ea081(var1);
            var3.write(var22.getBytes(StandardCharsets.UTF_8));
            var3.flush();
         } catch (Throwable var19) {
            try {
               var3.close();
            } catch (Throwable var16) {
               var19.addSuppressed(var16);
            }

            throw var19;
         }

         var3.close();
         return var2.renameTo(var0) || recoverMethod_a_7c4240a7(var2, var0);
      } catch (Exception var20) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("FileUtils", "Rebuild file failed: " + var20.getMessage());
         return false;
      } finally {
         if (var2.exists()) {
            var2.delete();
         }
      }
   }

   private static boolean recoverMethod_e_4ad1f430(File var0) {
      return var0 == null || var0.isDirectory() || !var0.exists();
   }

   private static boolean recoverMethod_a_cc388e29(RandomAccessFile var0) throws java.io.IOException {
      if (var0.length() < recoverField_a.length) {
         return false;
      }

      byte[] var1 = new byte[recoverField_a.length];
      var0.seek(0L);
      var0.readFully(var1);
      return Arrays.equals(var1, recoverField_a);
   }

   private static String recoverMethod_j_e96ea081(String var0) {
      if (TextUtils.isEmpty(var0)) {
         return var0;
      }

      char[] var1 = var0.toCharArray();

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = (char)(var1[var2] ^ 2000);
      }

      return new String(var1);
   }

   public static void recoverMethod_c_e925fa09(String var0, String var1) {
      try {
         File var2 = recoverMethod_c_3b83896e(var0);
         if (var2 == null || !var2.exists()) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("FileUtils", "Source file does not exist: " + var0);
            return;
         }

         File var3 = recoverMethod_c_3b83896e(var1);
         if (var3 == null) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("FileUtils", "Cannot create temp file: " + var1);
            return;
         }

         recoverMethod_a_4ad1f42c(var3);

         try {
            FileInputStream var4 = new FileInputStream(var2);

            try {
               FileOutputStream var5 = new FileOutputStream(var3);

               try {
                  byte[] var6 = new byte[Math.min(65536, (int)var2.length())];

                  int var7;
                  while ((var7 = var4.read(var6)) != -1) {
                     var5.write(var6, 0, var7);
                  }

                  var5.flush();
               } catch (Throwable var10) {
                  try {
                     var5.close();
                  } catch (Throwable var9) {
                     var10.addSuppressed(var9);
                  }

                  throw var10;
               }

               var5.close();
            } catch (Throwable var11) {
               try {
                  var4.close();
               } catch (Throwable var8) {
                  var11.addSuppressed(var8);
               }

               throw var11;
            }

            var4.close();
         } catch (IOException var12) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("FileUtils", "Copy file failed: " + var12.getMessage());
            if (var3.exists()) {
               var3.delete();
            }
         }
      } catch (Throwable var13) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("FileUtils", "copyFileToTemp error: " + var13.getMessage());
      }
   }

   public static boolean recoverMethod_f_aad3b203(String var0) {
      if (TextUtils.isEmpty(var0)) {
         return false;
      }

      File var1 = new File(var0);
      return var1.exists() && var1.length() > 0L && var1.canRead();
   }

   public static String recoverMethod_g_e96ea081(String var0) {
      if (!recoverMethod_f_aad3b203(var0)) {
         return null;
      }

      try {
         FileInputStream var1 = new FileInputStream(var0);

         String var5;
         try {
            byte[] var2 = new byte[8192];
            ByteArrayOutputStream var4 = new ByteArrayOutputStream();

            int var3;
            while ((var3 = var1.read(var2)) != -1) {
               var4.write(var2, 0, var3);
            }

            var5 = RecoverTypeaf2b07d5.recoverMethod_c_79834524(var4.toByteArray());
         } catch (Throwable var7) {
            try {
               var1.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }

            throw var7;
         }

         var1.close();
         return var5;
      } catch (IOException var8) {
         return null;
      }
   }

   public static boolean recoverMethod_a_de3c0df7(File var0, byte[] var1) {
      if (var0 != null && var1 != null) {
         File var2 = new File(var0.getParent(), var0.getName() + ".tmp");

         try {
            FileOutputStream var3 = new FileOutputStream(var2);

            try {
               var3.write(var1);
               var3.flush();
               var3.getFD().sync();
            } catch (Throwable var12) {
               try {
                  var3.close();
               } catch (Throwable var11) {
                  var12.addSuppressed(var11);
               }

               throw var12;
            }

            var3.close();
            return var2.renameTo(var0);
         } catch (Exception var13) {
            return false;
         } finally {
            if (var2.exists()) {
               var2.delete();
            }
         }
      } else {
         return false;
      }
   }

   public static class Member90883e46 implements FileFilter {
      private final boolean recoverField_c;
      private final boolean recoverField_d;
      private final String recoverField_e;
      private final int recoverField_f;
      public static final RecoverTypeaf2b07c9.Member90883e46 recoverField_a = new RecoverTypeaf2b07c9.Member90883e46(true, false);
      public static final RecoverTypeaf2b07c9.Member90883e46 recoverField_b = new RecoverTypeaf2b07c9.Member90883e46(false, true);

      public static RecoverTypeaf2b07c9.Member90883e46 recoverMethod_a_de38896a(String var0) {
         return new RecoverTypeaf2b07c9.Member90883e46(false, true, var0, 3);
      }

      public Member90883e46(boolean var1, boolean var2, String var3, int var4) {
         this.recoverField_e = var3;
         this.recoverField_f = var4;
         this.recoverField_c = var1;
         this.recoverField_d = var2;
      }

      public Member90883e46(boolean var1, boolean var2) {
         this.recoverField_c = var1;
         this.recoverField_d = var2;
         this.recoverField_e = null;
         this.recoverField_f = 0;
      }

      @Override
      public boolean accept(File var1) {
         if (this.recoverField_c && !var1.isFile()) {
            return false;
         }

         if (this.recoverField_d && !var1.isDirectory()) {
            return false;
         }

         if (TextUtils.isEmpty(this.recoverField_e)) {
            return true;
         }

         switch (this.recoverField_f) {
            case 1:
               return var1.getName().startsWith(this.recoverField_e);
            case 2:
               return var1.getName().endsWith(this.recoverField_e);
            case 3:
               return var1.getName().equals(this.recoverField_e);
            case 4:
               return var1.getName().contains(this.recoverField_e);
            default:
               return false;
         }
      }
   }
}
