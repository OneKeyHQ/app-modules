package cn.jiguang.ar;

import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;

public class RecoverTypeaf2aadaf implements Serializable {
   public String recoverField_a;
   public int recoverField_b;
   public transient InetAddress recoverField_c;

   public RecoverTypeaf2aadaf(String var1, int var2) {
      this.recoverField_a = var1;
      this.recoverField_b = var2;
   }

   public RecoverTypeaf2aadaf(InetAddress var1, int var2) {
      this.recoverField_a = var1.getHostAddress();
      this.recoverField_b = var2;
      this.recoverField_c = var1;
   }

   public boolean recoverMethod_a_9b79() {
      return !TextUtils.isEmpty(this.recoverField_a) && this.recoverField_b > 0 && this.recoverField_b <= 65535;
   }

   public static boolean recoverMethod_a_afa300e8(String var0, int var1) {
      return !TextUtils.isEmpty(var0) && var1 > 0 && var1 <= 65535;
   }

   public static RecoverTypeaf2aadaf recoverMethod_a_800e3061(String var0) {
      if (TextUtils.isEmpty(var0)) {
         return null;
      }

      int var1 = var0.lastIndexOf(":");
      if (var1 < 0) {
         return null;
      }

      try {
         String var2 = var0.substring(var1 + 1);
         int var3 = Integer.decode(var2);
         String var4;
         if (var1 == 0) {
            var4 = "";
         } else {
            int var5 = var0.startsWith("[") ? 1 : 0;
            int var6 = var1;
            if (var0.charAt(var1 - 1) == ']') {
               var6--;
            }

            var4 = var0.substring(var5, var6);
         }

         return new RecoverTypeaf2aadaf(var4, var3);
      } catch (Exception var7) {
         return null;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         RecoverTypeaf2aadaf var2 = (RecoverTypeaf2aadaf)var1;
         return this.recoverField_b == var2.recoverField_b
            && (this.recoverField_a != null ? this.recoverField_a.equals(var2.recoverField_a) : var2.recoverField_a == null);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.recoverField_a != null ? this.recoverField_a.hashCode() : 0;
      return 31 * var1 + this.recoverField_b;
   }

   @Override
   public String toString() {
      return !TextUtils.isEmpty(this.recoverField_a) && this.recoverField_a.contains(":")
         ? "[" + this.recoverField_a + "]:" + this.recoverField_b
         : this.recoverField_a + ":" + this.recoverField_b;
   }

   public static boolean recoverMethod_b_aad3b203(String var0) {
      try {
         long var1 = System.currentTimeMillis();
         long var3 = Long.valueOf(var0);
         long var5 = Math.abs(var1 - var3);
         if (var5 > 259200000L) {
            return true;
         }
      } catch (Throwable var7) {
      }

      return false;
   }

   public static LinkedHashSet recoverMethod_c_6066ccd5(String var0) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("IpPort", " string2Set=" + var0);
      LinkedHashSet var1 = new LinkedHashSet();
      if (!TextUtils.isEmpty(var0)) {
         String[] var2 = var0.split("-sis-");
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("IpPort", " string2Set split=" + Arrays.toString(var2));
         if (var2 != null) {
            if (var2.length >= 2) {
               if (recoverMethod_b_aad3b203(var2[1])) {
                  return var1;
               }

               recoverMethod_a_6b3c1007(var2[0], var1);
            } else if (var2.length >= 1) {
               recoverMethod_a_6b3c1007(var2[0], var1);
            }
         }
      }

      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("IpPort", " string2Set list=" + var1);
      return var1;
   }

   private static void recoverMethod_a_6b3c1007(String var0, LinkedHashSet var1) {
      if (!TextUtils.isEmpty(var0)) {
         String[] var2 = var0.split(",");

         for (String var6 : var2) {
            RecoverTypeaf2aadaf var7 = recoverMethod_a_800e3061(var6);
            if (var7 != null && var7.recoverMethod_a_9b79()) {
               var1.add(var7);
            }
         }
      }
   }

   public static String recoverMethod_a_13bfc96f(LinkedHashSet var0) {
      if (var0 != null && !var0.isEmpty()) {
         StringBuilder var1 = new StringBuilder();

         for (Object recoveryElement_148 : var0) {
            RecoverTypeaf2aadaf var3 = (RecoverTypeaf2aadaf)recoveryElement_148;
            var1.append(var3.toString()).append(",");
         }

         var1.deleteCharAt(var1.length() - 1);
         return var1.toString();
      } else {
         return null;
      }
   }

   public static LinkedHashSet recoverMethod_a_7ec0a6ed(LinkedHashMap var0) {
      LinkedHashSet var1 = new LinkedHashSet();

      for (Object recoveryElement_162 : var0.entrySet()) {
         Entry var3 = (Entry)recoveryElement_162;
         RecoverTypeaf2aadaf var4 = new RecoverTypeaf2aadaf((String)var3.getKey(), (Integer)var3.getValue());
         if (var4.recoverMethod_a_9b79()) {
            var1.add(var4);
         }
      }

      return var1;
   }
}
