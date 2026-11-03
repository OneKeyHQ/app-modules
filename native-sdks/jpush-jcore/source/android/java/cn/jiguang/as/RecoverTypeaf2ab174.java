package cn.jiguang.as;

import cn.jiguang.bk.RecoverTypeaf2b07d5;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class RecoverTypeaf2ab174 {
   private String[] recoverField_a = null;
   private RecoverTypeaf2ab170[] recoverField_b = null;
   private int recoverField_c = -1;
   private static RecoverTypeaf2ab174 recoverField_d;

   public RecoverTypeaf2ab174() {
      if (!this.recoverMethod_d_9b79()) {
         if (!this.recoverMethod_e_9b79()) {
            if (this.recoverField_a == null || this.recoverField_b == null) {
               String var1 = System.getProperty("java.vendor");
               if (var1.indexOf("Android") != -1) {
                  this.recoverMethod_g_9b75();
               } else {
                  this.recoverMethod_f_9b75();
               }
            }
         }
      }
   }

   private void recoverMethod_a_1b7460f0(String var1, List var2) {
      if (!var2.contains(var1)) {
         var2.add(var1);
      }
   }

   private void recoverMethod_b_1b7460f0(String var1, List var2) {
      RecoverTypeaf2ab170 var3;
      try {
         var3 = RecoverTypeaf2ab170.recoverMethod_a_fe97baf3(var1, RecoverTypeaf2ab170.recoverField_a);
      } catch (Exception var5) {
         return;
      }

      if (!var2.contains(var3)) {
         var2.add(var3);
      }
   }

   private int recoverMethod_b_aad3b1f2(String var1) {
      var1 = var1.substring(6);

      try {
         int var2 = Integer.parseInt(var1);
         if (var2 >= 0) {
            return var2;
         }
      } catch (NumberFormatException var3) {
      }

      return -1;
   }

   private void recoverMethod_a_b19c9f57(List var1, List var2) {
      if (this.recoverField_a == null && var1.size() > 0) {
         this.recoverField_a = (String[])var1.toArray(new String[0]);
      }

      if (this.recoverField_b == null && var2.size() > 0) {
         this.recoverField_b = (RecoverTypeaf2ab170[])var2.toArray(new RecoverTypeaf2ab170[0]);
      }
   }

   private void recoverMethod_a_13462e(int var1) {
      if (this.recoverField_c < 0 && var1 > 0) {
         this.recoverField_c = var1;
      }
   }

   private boolean recoverMethod_d_9b79() {
      ArrayList var2 = new ArrayList(0);
      ArrayList var3 = new ArrayList(0);
      String var1 = System.getProperty("dns.server");
      if (var1 != null) {
         StringTokenizer var4 = new StringTokenizer(var1, ",");

         while (var4.hasMoreTokens()) {
            this.recoverMethod_a_1b7460f0(var4.nextToken(), var2);
         }
      }

      var1 = System.getProperty("dns.search");
      if (var1 != null) {
         StringTokenizer var6 = new StringTokenizer(var1, ",");

         while (var6.hasMoreTokens()) {
            this.recoverMethod_b_1b7460f0(var6.nextToken(), var3);
         }
      }

      this.recoverMethod_a_b19c9f57(var2, var3);
      return this.recoverField_a != null && this.recoverField_b != null;
   }

   private boolean recoverMethod_e_9b79() {
      ArrayList var1 = new ArrayList(0);
      ArrayList var3 = new ArrayList(0);

      List var2;
      List var4;
      try {
         Class[] var5 = new Class[0];
         Object[] var6 = new Object[0];
         String var7 = "sun.net.dns.ResolverConfiguration";
         Class var8 = Class.forName(var7);
         Method var10 = var8.getDeclaredMethod("open", var5);
         Object var9 = var10.invoke(null, var6);
         Method var11 = var8.getMethod("nameservers", var5);
         var2 = (List)var11.invoke(var9, var6);
         Method var12 = var8.getMethod("searchlist", var5);
         var4 = (List)var12.invoke(var9, var6);
      } catch (Exception var13) {
         return false;
      }

      if (var2.size() == 0) {
         return false;
      }

      if (var2.size() > 0) {
         for (Object var16 : var2) {
            this.recoverMethod_a_1b7460f0((String)var16, var1);
         }
      }

      if (var4.size() > 0) {
         for (Object var17 : var4) {
            this.recoverMethod_b_1b7460f0((String)var17, var3);
         }
      }

      this.recoverMethod_a_b19c9f57(var1, var3);
      return true;
   }

   public void recoverMethod_a_aad3b1ff(String var1) {
      ArrayList var2 = new ArrayList();
      ArrayList var3 = new ArrayList();
      int var4 = -1;

      try {
         BufferedReader var5 = new BufferedReader(new InputStreamReader(new FileInputStream(var1)));

         String var6;
         try {
            while ((var6 = var5.readLine()) != null) {
               if (var6.startsWith("nameserver")) {
                  StringTokenizer var15 = new StringTokenizer(var6);
                  var15.nextToken();
                  this.recoverMethod_a_1b7460f0(var15.nextToken(), var2);
               } else if (var6.startsWith("domain")) {
                  StringTokenizer var14 = new StringTokenizer(var6);
                  var14.nextToken();
                  if (var14.hasMoreTokens() && var3.isEmpty()) {
                     this.recoverMethod_b_1b7460f0(var14.nextToken(), var3);
                  }
               } else if (var6.startsWith("search")) {
                  var3.clear();
                  StringTokenizer var13 = new StringTokenizer(var6);
                  var13.nextToken();

                  while (var13.hasMoreTokens()) {
                     this.recoverMethod_b_1b7460f0(var13.nextToken(), var3);
                  }
               } else if (var6.startsWith("options")) {
                  StringTokenizer var7 = new StringTokenizer(var6);
                  var7.nextToken();

                  while (var7.hasMoreTokens()) {
                     String var8 = var7.nextToken();
                     if (var8.startsWith("ndots:")) {
                        var4 = this.recoverMethod_b_aad3b1f2(var8);
                     }
                  }
               }
            }
         } catch (Throwable var10) {
            try {
               var5.close();
            } catch (Throwable var9) {
               var10.addSuppressed(var9);
            }

            throw var10;
         }

         var5.close();
      } catch (FileNotFoundException var11) {
         return;
      } catch (IOException var12) {
      }

      this.recoverMethod_a_b19c9f57(var2, var3);
      this.recoverMethod_a_13462e(var4);
   }

   private void recoverMethod_f_9b75() {
      this.recoverMethod_a_aad3b1ff("/etc/resolv.conf");
   }

   private void recoverMethod_g_9b75() {
      String var1 = "^\\d+(\\.\\d+){3}$";
      String var2 = "^[0-9a-f]+(:[0-9a-f]*)+:[0-9a-f]+$";
      ArrayList var3 = new ArrayList();
      ArrayList var4 = new ArrayList();
      String[] var5 = new String[]{"net.dns1", "net.dns2", "net.dns3", "net.dns4"};

      for (String var9 : var5) {
         String var10 = RecoverTypeaf2b07d5.recoverMethod_a_8ea82db7(var9, "");
         if (!var10.isEmpty() && (var10.matches("^\\d+(\\.\\d+){3}$") || var10.matches("^[0-9a-f]+(:[0-9a-f]*)+:[0-9a-f]+$")) && !var3.contains(var10)) {
            var3.add(var10);
         }
      }

      this.recoverMethod_a_b19c9f57(var3, var4);
   }

   public String[] recoverMethod_a_7e9e5c04() {
      return this.recoverField_a;
   }

   public static synchronized RecoverTypeaf2ab174 recoverMethod_b_c695e2b2() {
      return recoverField_d;
   }

   public static void recoverMethod_c_9b75() {
      RecoverTypeaf2ab174 var0 = new RecoverTypeaf2ab174();
      synchronized (RecoverTypeaf2ab174.class) {
         recoverField_d = var0;
      }
   }

   static {
      recoverMethod_c_9b75();
   }
}
