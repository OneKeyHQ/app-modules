package cn.jiguang.bk;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import cn.jiguang.ar.RecoverTypeaf2aadaf;
import cn.jiguang.i.RecoverType5a69fd7;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class RecoverTypeaf2b07d7 {
   private static final Object recoverField_a = new Object();
   private static ConcurrentHashMap recoverField_b = new ConcurrentHashMap();

   public static boolean recoverMethod_a_2566ab9(long var0, long var2) {
      try {
         Calendar var4 = Calendar.getInstance();
         Calendar var5 = Calendar.getInstance();
         var4.setTimeInMillis(var0);
         var5.setTimeInMillis(var2);
         return var4.get(0) == var5.get(0) && var4.get(1) == var5.get(1) && var4.get(6) == var5.get(6);
      } catch (Throwable var6) {
         return true;
      }
   }

   public static byte[] recoverMethod_a_4962d961(byte[] var0) throws java.io.IOException {
      if (var0 != null && var0.length != 0) {
         ByteArrayOutputStream var1 = new ByteArrayOutputStream();
         GZIPOutputStream var2 = null;

         try {
            var2 = new GZIPOutputStream(var1);
            var2.write(var0);
            var2.close();
            return var1.toByteArray();
         } finally {
            recoverMethod_a_d17885d6(var1);
            recoverMethod_a_d17885d6(var2);
         }
      } else {
         return var0;
      }
   }

   public static byte[] recoverMethod_b_4962d961(byte[] var0) throws java.io.IOException {
      if (var0 != null && var0.length != 0) {
         ByteArrayOutputStream var1 = new ByteArrayOutputStream();
         ByteArrayInputStream var2 = new ByteArrayInputStream(var0);
         GZIPInputStream var3 = null;

         try {
            var3 = new GZIPInputStream(var2);
            byte[] var4 = new byte[256];

            int var5;
            while ((var5 = var3.read(var4)) >= 0) {
               var1.write(var4, 0, var5);
            }

            return var1.toByteArray();
         } finally {
            recoverMethod_a_d17885d6(var1);
            recoverMethod_a_d17885d6(var2);
            recoverMethod_a_d17885d6(var3);
         }
      } else {
         return var0;
      }
   }

   public static LinkedList recoverMethod_a_f17ad488(Collection var0) {
      LinkedList var1 = new LinkedList();
      int var2 = var0 != null ? var0.size() : 0;
      if (var2 == 0) {
         return var1;
      }

      if (var2 == 1) {
         var1.addAll(var0);
         return var1;
      }

      int var3 = 0;
      SecureRandom var4 = new SecureRandom();

      for (Object var6 : var0) {
         var1.add(var4.nextInt(++var3), var6);
      }

      return var1;
   }

   public static List recoverMethod_a_3038db21(List var0) {
      ArrayList var1 = new ArrayList();
      ArrayList var2 = new ArrayList();

      for (int var3 = 0; var3 < var0.size() - 1; var3++) {
         RecoverTypeaf2aadaf var4 = (RecoverTypeaf2aadaf)var0.get(var3);
         if (var4.recoverField_a.contains("ipv6")) {
            var1.add(var4);
         } else {
            var2.add(var4);
         }
      }

      var0.clear();
      var0.addAll(var1);
      var0.addAll(var2);
      return var0;
   }

   public static LinkedList recoverMethod_a_bdfbcc03(Context var0, LinkedList var1) {
      boolean var2 = RecoverTypeaf2b07c2.recoverMethod_d_f0554df7(var0);
      if (!var2) {
         return var1;
      }

      LinkedList var3 = new LinkedList();
      LinkedList var4 = new LinkedList();

      for (Object recoveryElement_144 : var1) {
         InetAddress var6 = (InetAddress)recoveryElement_144;
         if (var6 instanceof Inet6Address) {
            var3.add(var6);
         } else if (var6 instanceof Inet4Address) {
            var4.add(var6);
         }
      }

      var1.clear();
      var1.addAll(var3);
      var1.addAll(var4);
      return var1;
   }

   public static LinkedHashSet recoverMethod_a_647dad69(Context var0, LinkedHashSet var1) {
      boolean var2 = RecoverTypeaf2b07c2.recoverMethod_d_f0554df7(var0);
      if (var2) {
         ArrayList var3 = new ArrayList();
         ArrayList var4 = new ArrayList();

         for (Object recoveryElement_164 : var1) {
            String var6 = (String)recoveryElement_164;
            if (var6.contains("ipv6")) {
               var3.add(var6);
            } else {
               var4.add(var6);
            }
         }

         var1.clear();
         var1.addAll(var3);
         var1.addAll(var4);
      }

      return var1;
   }

   public static boolean recoverMethod_b_2566ab9(long var0, long var2) {
      if (var2 <= 0L) {
         throw new AssertionError();
      } else {
         long var4 = SystemClock.elapsedRealtime();
         if (var0 <= 0L) {
            return true;
         } else {
            return var4 <= var0 ? true : var4 > var0 + var2;
         }
      }
   }

   public static void recoverMethod_a_d17885d6(Closeable var0) {
      if (var0 != null) {
         try {
            var0.close();
         } catch (Throwable var2) {
         }
      }
   }

   public static byte[] recoverMethod_a_b328ee5f(InputStream var0) throws java.io.IOException {
      if (var0 == null) {
         throw new IOException("InputStream is null");
      }

      ByteArrayOutputStream var1 = null;

      try {
         var1 = new ByteArrayOutputStream();
         byte[] var2 = new byte[1024];

         int var3;
         while ((var3 = var0.read(var2)) != -1) {
            var1.write(var2, 0, var3);
         }

         return var1.toByteArray();
      } finally {
         recoverMethod_a_d17885d6(var1);
      }
   }

   public static short recoverMethod_c_25e06f9(byte[] var0) throws java.lang.Exception {
      if (var0 != null && var0.length != 0) {
         if (var0.length == 1) {
            return var0[0];
         }

         short var1 = (short)((var0[0] & 255) << 8);
         short var2 = (short)(var0[1] & 0xFF);
         return (short)(var1 | var2);
      } else {
         throw new Exception("byte could not be empty");
      }
   }

   public static String recoverMethod_a_e96ea081(String var0) {
      return new String(Base64.decode(var0.getBytes(), 2));
   }

   public static String recoverMethod_b_e96ea081(String var0) {
      return Base64.encodeToString(var0.getBytes(), 2);
   }

   public static String recoverMethod_a_e7a3948b(String var0, boolean var1) {
      if (TextUtils.isEmpty(var0)) {
         return "";
      }

      String var2 = "";

      try {
         byte[] var3 = Base64.decode(var0, 2);
         if (var1) {
            var3 = recoverMethod_b_4962d961(var3);
         }

         var3 = RecoverTypeaf2b07d4.recoverMethod_a_f31cd299(
            var3, RecoverType5a69fd7.Member367e0ad4.recoverField_j, RecoverType5a69fd7.Member367e0ad4.recoverField_j, false
         );
         return new String(var3);
      } catch (Throwable var4) {
         return "";
      }
   }

   public static String recoverMethod_b_e7a3948b(String var0, boolean var1) {
      String var3 = "";

      try {
         byte[] var2 = var0.getBytes("UTF-8");
         if (var1) {
            var2 = recoverMethod_a_4962d961(var2);
         }

         var2 = RecoverTypeaf2b07d4.recoverMethod_a_f31cd299(
            var2, RecoverType5a69fd7.Member367e0ad4.recoverField_j, RecoverType5a69fd7.Member367e0ad4.recoverField_j, true
         );
         return Base64.encodeToString(var2, 2);
      } catch (Throwable var5) {
         return "";
      }
   }

   public static long recoverMethod_a_9b69() {
      long var0 = System.currentTimeMillis();
      Calendar var2 = Calendar.getInstance();
      var2.setTimeInMillis(var0);
      var2.set(11, 0);
      var2.set(12, 0);
      var2.set(13, 0);
      var2.set(14, 0);
      return var2.getTimeInMillis();
   }

   public static void recoverMethod_a_e925fa09(String var0, String var1) throws java.io.FileNotFoundException, java.io.IOException {
      FileOutputStream var2 = new FileOutputStream(var1);

      try {
         ZipOutputStream var3 = new ZipOutputStream(var2);

         try {
            File var4 = new File(var0);
            recoverMethod_a_26fed699(var3, var4, var4.getName());
            var3.flush();
         } catch (Throwable var8) {
            try {
               var3.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }

            throw var8;
         }

         var3.close();
      } catch (Throwable var9) {
         try {
            var2.close();
         } catch (Throwable var6) {
            var9.addSuppressed(var6);
         }

         throw var9;
      }

      var2.close();
   }

   private static void recoverMethod_a_26fed699(ZipOutputStream var0, File var1, String var2) throws java.io.FileNotFoundException, java.io.IOException {
      if (var1.isDirectory()) {
         for (File var6 : var1.listFiles()) {
            recoverMethod_a_26fed699(var0, var6, var2 + "/" + var6.getName());
         }
      } else {
         FileInputStream var9 = new FileInputStream(var1);

         try {
            ZipEntry var10 = new ZipEntry(var2);
            var0.putNextEntry(var10);
            byte[] var11 = new byte[1024];

            int var12;
            while ((var12 = var9.read(var11)) >= 0) {
               var0.write(var11, 0, var12);
            }
         } catch (Throwable var8) {
            try {
               var9.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }

            throw var8;
         }

         var9.close();
         var0.closeEntry();
      }
   }
}
