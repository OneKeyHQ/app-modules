package cn.jiguang.as;

import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;

public class RecoverTypeaf2ab170 implements Serializable, Comparable {
   private byte[] recoverField_c;
   private long recoverField_d;
   private int recoverField_e;
   private static final byte[] recoverField_f = new byte[]{0};
   private static final byte[] recoverField_g = new byte[]{1, 42};
   public static final RecoverTypeaf2ab170 recoverField_a;
   public static final RecoverTypeaf2ab170 recoverField_b;
   private static final DecimalFormat recoverField_h = new DecimalFormat();
   private static final byte[] recoverField_i = new byte[256];
   private static final RecoverTypeaf2ab170 recoverField_j;

   private RecoverTypeaf2ab170() {
   }

   private final void recoverMethod_a_255f295(int var1, int var2) {
      if (var1 < 7) {
         int var3 = 8 * (7 - var1);
         this.recoverField_d &= ~(255L << var3);
         this.recoverField_d |= (long)var2 << var3;
      }
   }

   private final int recoverMethod_a_134621(int var1) {
      if (var1 == 0 && this.recoverMethod_d_9b68() == 0) {
         return 0;
      }

      if (var1 < 0 || var1 >= this.recoverMethod_d_9b68()) {
         throw new IllegalArgumentException("label out of range");
      }

      if (var1 < 7) {
         int var4 = 8 * (7 - var1);
         return (int)(this.recoverField_d >>> var4) & 0xFF;
      }

      int var2 = this.recoverMethod_a_134621(6);

      for (int var3 = 6; var3 < var1; var3++) {
         var2 += this.recoverField_c[var2] + 1;
      }

      return var2;
   }

   private final void recoverMethod_b_13462e(int var1) {
      this.recoverField_d &= -256L;
      this.recoverField_d |= var1;
   }

   private final int recoverMethod_d_9b68() {
      return (int)(this.recoverField_d & 255L);
   }

   private static final void recoverMethod_b_19f24a55(RecoverTypeaf2ab170 var0, RecoverTypeaf2ab170 var1) {
      if (var0.recoverMethod_a_134621(0) == 0) {
         var1.recoverField_c = var0.recoverField_c;
         var1.recoverField_d = var0.recoverField_d;
      } else {
         int var2 = var0.recoverMethod_a_134621(0);
         int var3 = var0.recoverField_c.length - var2;
         int var4 = var0.recoverMethod_b_9b68();
         var1.recoverField_c = new byte[var3];
         System.arraycopy(var0.recoverField_c, var2, var1.recoverField_c, 0, var3);

         for (int var5 = 0; var5 < var4 && var5 < 7; var5++) {
            var1.recoverMethod_a_255f295(var5, var0.recoverMethod_a_134621(var5) - var2);
         }

         var1.recoverMethod_b_13462e(var4);
      }
   }

   private final void recoverMethod_a_e306985c(byte[] var1, int var2, int var3) throws java.io.IOException {
      int var4 = this.recoverField_c == null ? 0 : this.recoverField_c.length - this.recoverMethod_a_134621(0);
      int var5 = 0;
      int var6 = 0;
      int var7 = var2;

      while (var6 < var3) {
         int var8 = var1[var7];
         if (var8 > 63) {
            throw new IllegalStateException("invalid label");
         }

         var7 += ++var8;
         var5 += var8;
         var6++;
      }

      var6 = var4 + var5;
      if (var6 > 255) {
         throw new IOException();
      }

      var7 = this.recoverMethod_d_9b68();
      int var15 = var7 + var3;
      if (var15 > 128) {
         throw new IllegalStateException("too many labels");
      }

      byte[] var9 = new byte[var6];
      if (var4 != 0) {
         System.arraycopy(this.recoverField_c, this.recoverMethod_a_134621(0), var9, 0, var4);
      }

      System.arraycopy(var1, var2, var9, var4, var5);
      this.recoverField_c = var9;
      int var10 = 0;
      int var11 = var4;

      while (var10 < var3) {
         this.recoverMethod_a_255f295(var7 + var10, var11);
         var11 += var9[var11] + 1;
         var10++;
      }

      this.recoverMethod_b_13462e(var15);
   }

   private static IOException recoverMethod_a_84af806d(String var0, String var1) {
      return new IOException("'" + var0 + "': " + var1);
   }

   private final void recoverMethod_a_29f759e6(String var1, byte[] var2, int var3, int var4) throws java.io.IOException {
      try {
         this.recoverMethod_a_e306985c(var2, var3, var4);
      } catch (Exception var6) {
         throw recoverMethod_a_84af806d(var1, "Name too long");
      }
   }

   private final void recoverMethod_b_e306985c(byte[] var1, int var2, int var3) {
      try {
         this.recoverMethod_a_e306985c(var1, var2, var3);
      } catch (Exception var5) {
      }
   }

   public RecoverTypeaf2ab170(String var1, RecoverTypeaf2ab170 var2) throws java.io.IOException {
      if (var1.equals("")) {
         throw recoverMethod_a_84af806d(var1, "empty name");
      }

      if (var1.equals("@")) {
         if (var2 == null) {
            recoverMethod_b_19f24a55(recoverField_b, this);
         } else {
            recoverMethod_b_19f24a55(var2, this);
         }
      } else if (var1.equals(".")) {
         recoverMethod_b_19f24a55(recoverField_a, this);
      } else {
         int var3 = -1;
         int var4 = 1;
         byte[] var5 = new byte[64];
         boolean var6 = false;
         int var7 = 0;
         int var8 = 0;
         boolean var9 = false;

         for (int var10 = 0; var10 < var1.length(); var10++) {
            byte var11 = (byte)var1.charAt(var10);
            if (var6) {
               if (var11 >= 48 && var11 <= 57 && var7 < 3) {
                  var7++;
                  var8 *= 10;
                  var8 += var11 - 48;
                  if (var8 > 255) {
                     throw recoverMethod_a_84af806d(var1, "bad escape");
                  }

                  if (var7 < 3) {
                     continue;
                  }

                  var11 = (byte)var8;
               } else if (var7 > 0 && var7 < 3) {
                  throw recoverMethod_a_84af806d(var1, "bad escape");
               }

               if (var4 > 63) {
                  throw recoverMethod_a_84af806d(var1, "label too long");
               }

               var3 = var4;
               var5[var4++] = var11;
               var6 = false;
            } else if (var11 == 92) {
               var6 = true;
               var7 = 0;
               var8 = 0;
            } else if (var11 == 46) {
               if (var3 == -1) {
                  throw recoverMethod_a_84af806d(var1, "invalid empty label");
               }

               var5[0] = (byte)(var4 - 1);
               this.recoverMethod_a_29f759e6(var1, var5, 0, 1);
               var3 = -1;
               var4 = 1;
            } else {
               if (var3 == -1) {
                  var3 = var10;
               }

               if (var4 > 63) {
                  throw recoverMethod_a_84af806d(var1, "label too long");
               }

               var5[var4++] = var11;
            }
         }

         if (var7 > 0 && var7 < 3) {
            throw recoverMethod_a_84af806d(var1, "bad escape");
         }

         if (var6) {
            throw recoverMethod_a_84af806d(var1, "bad escape");
         }

         if (var3 == -1) {
            this.recoverMethod_a_29f759e6(var1, recoverField_f, 0, 1);
            var9 = true;
         } else {
            var5[0] = (byte)(var4 - 1);
            this.recoverMethod_a_29f759e6(var1, var5, 0, 1);
         }

         if (var2 != null && !var9) {
            this.recoverMethod_a_29f759e6(var1, var2.recoverField_c, var2.recoverMethod_a_134621(0), var2.recoverMethod_d_9b68());
         }
      }
   }

   public static RecoverTypeaf2ab170 recoverMethod_a_fe97baf3(String var0, RecoverTypeaf2ab170 var1) throws java.io.IOException {
      if (var0.equals("@") && var1 != null) {
         return var1;
      } else {
         return var0.equals(".") ? recoverField_a : new RecoverTypeaf2ab170(var0, var1);
      }
   }

   public static RecoverTypeaf2ab170 recoverMethod_a_800ea4c0(String var0) throws java.io.IOException {
      return recoverMethod_a_fe97baf3(var0, null);
   }

   public RecoverTypeaf2ab170(RecoverTypeaf2ab16b var1) throws java.io.IOException {
      boolean var4 = false;
      byte[] var5 = new byte[64];
      boolean var6 = false;

      while (!var4) {
         int var2 = var1.recoverMethod_f_9b68();
         switch (var2 & 192) {
            case 0:
               if (this.recoverMethod_d_9b68() >= 128) {
                  throw new IOException("too many labels");
               }

               if (var2 == 0) {
                  this.recoverMethod_a_e306985c(recoverField_f, 0, 1);
                  var4 = true;
               } else {
                  var5[0] = (byte)var2;
                  var1.recoverMethod_a_e306985c(var5, 1, var2);
                  this.recoverMethod_a_e306985c(var5, 0, 1);
               }
               break;
            case 192:
               int var3 = var1.recoverMethod_f_9b68();
               var3 += (var2 & -193) << 8;
               if (var3 >= var1.recoverMethod_a_9b68() - 2) {
                  throw new IOException("bad compression");
               }

               if (!var6) {
                  var1.recoverMethod_d_9b75();
                  var6 = true;
               }

               var1.recoverMethod_b_13462e(var3);
               break;
            default:
               throw new IOException("bad label type");
         }
      }

      if (var6) {
         var1.recoverMethod_e_9b75();
      }
   }

   public RecoverTypeaf2ab170(RecoverTypeaf2ab170 var1, int var2) {
      int var3 = var1.recoverMethod_b_9b68();
      if (var2 > var3) {
         throw new IllegalArgumentException("attempted to remove too many labels");
      }

      this.recoverField_c = var1.recoverField_c;
      this.recoverMethod_b_13462e(var3 - var2);

      for (int var4 = 0; var4 < 7 && var4 < var3 - var2; var4++) {
         this.recoverMethod_a_255f295(var4, var1.recoverMethod_a_134621(var4 + var2));
      }
   }

   public static RecoverTypeaf2ab170 recoverMethod_a_e80dd116(RecoverTypeaf2ab170 var0, RecoverTypeaf2ab170 var1) throws java.io.IOException {
      if (var0.recoverMethod_a_9b79()) {
         return var0;
      }

      RecoverTypeaf2ab170 var2 = new RecoverTypeaf2ab170();
      recoverMethod_b_19f24a55(var0, var2);
      var2.recoverMethod_a_e306985c(var1.recoverField_c, var1.recoverMethod_a_134621(0), var1.recoverMethod_d_9b68());
      return var2;
   }

   public boolean recoverMethod_a_9b79() {
      int var1 = this.recoverMethod_b_9b68();
      return var1 == 0 ? false : this.recoverField_c[this.recoverMethod_a_134621(var1 - 1)] == 0;
   }

   public int recoverMethod_b_9b68() {
      return this.recoverMethod_d_9b68();
   }

   private String recoverMethod_a_f86447f9(byte[] var1, int var2) {
      StringBuffer var3 = new StringBuffer();
      byte var4 = var1[var2++];

      for (int var5 = var2; var5 < var2 + var4; var5++) {
         int var6 = var1[var5] & 255;
         if (var6 <= 32 || var6 >= 127) {
            var3.append('\\');
            var3.append(recoverField_h.format(var6));
         } else if (var6 != 34 && var6 != 40 && var6 != 41 && var6 != 46 && var6 != 59 && var6 != 92 && var6 != 64 && var6 != 36) {
            var3.append((char)var6);
         } else {
            var3.append('\\');
            var3.append((char)var6);
         }
      }

      return var3.toString();
   }

   public String recoverMethod_a_a9514c81(boolean var1) {
      int var2 = this.recoverMethod_b_9b68();
      if (var2 == 0) {
         return "@";
      }

      if (var2 == 1 && this.recoverField_c[this.recoverMethod_a_134621(0)] == 0) {
         return ".";
      }

      StringBuffer var3 = new StringBuffer();
      int var4 = 0;
      int var5 = this.recoverMethod_a_134621(0);

      while (var4 < var2) {
         byte var6 = this.recoverField_c[var5];
         if (var6 > 63) {
            throw new IllegalStateException("invalid label");
         }

         if (var6 == 0) {
            if (!var1) {
               var3.append('.');
            }
            break;
         }

         if (var4 > 0) {
            var3.append('.');
         }

         var3.append(this.recoverMethod_a_f86447f9(this.recoverField_c, var5));
         var5 += 1 + var6;
         var4++;
      }

      return var3.toString();
   }

   @Override
   public String toString() {
      return this.recoverMethod_a_a9514c81(false);
   }

   public void recoverMethod_a_e78bba17(RecoverTypeaf2ab16c var1, RecoverTypeaf2ab16a var2) {
      int var3 = this.recoverMethod_b_9b68();

      for (int var4 = 0; var4 < var3 - 1; var4++) {
         RecoverTypeaf2ab170 var5;
         if (var4 == 0) {
            var5 = this;
         } else {
            var5 = new RecoverTypeaf2ab170(this, var4);
         }

         int var6 = -1;
         if (var2 != null) {
            var6 = var2.recoverMethod_a_991dff6f(var5);
         }

         if (var6 >= 0) {
            var6 |= 49152;
            var1.recoverMethod_c_13462e(var6);
            return;
         }

         if (var2 != null) {
            var2.recoverMethod_a_6ce0fde3(var1.recoverMethod_a_9b68(), var5);
         }

         int var7 = this.recoverMethod_a_134621(var4);
         var1.recoverMethod_a_e306985c(this.recoverField_c, var7, this.recoverField_c[var7] + 1);
      }

      var1.recoverMethod_b_13462e(0);
   }

   public void recoverMethod_a_991c2e00(RecoverTypeaf2ab16c var1) {
      byte[] var2 = this.recoverMethod_c_12d408();
      var1.recoverMethod_a_25e06fc(var2);
   }

   public byte[] recoverMethod_c_12d408() {
      int var1 = this.recoverMethod_b_9b68();
      if (var1 == 0) {
         return new byte[0];
      }

      byte[] var2 = new byte[this.recoverField_c.length - this.recoverMethod_a_134621(0)];
      int var3 = 0;
      int var4 = this.recoverMethod_a_134621(0);
      int var5 = 0;

      while (var3 < var1) {
         byte var6 = this.recoverField_c[var4];
         if (var6 > 63) {
            throw new IllegalStateException("invalid label");
         }

         var2[var5++] = this.recoverField_c[var4++];

         for (int var7 = 0; var7 < var6; var7++) {
            var2[var5++] = recoverField_i[this.recoverField_c[var4++] & 0xFF];
         }

         var3++;
      }

      return var2;
   }

   public void recoverMethod_a_9ec3b9d(RecoverTypeaf2ab16c var1, RecoverTypeaf2ab16a var2, boolean var3) {
      if (var3) {
         this.recoverMethod_a_991c2e00(var1);
      } else {
         this.recoverMethod_a_e78bba17(var1, var2);
      }
   }

   private final boolean recoverMethod_b_49634b8b(byte[] var1, int var2) {
      int var3 = this.recoverMethod_b_9b68();
      int var4 = 0;
      int var5 = this.recoverMethod_a_134621(0);

      while (var4 < var3) {
         if (this.recoverField_c[var5] != var1[var2]) {
            return false;
         }

         byte var6 = this.recoverField_c[var5++];
         var2++;
         if (var6 > 63) {
            throw new IllegalStateException("invalid label");
         }

         for (int var7 = 0; var7 < var6; var7++) {
            if (recoverField_i[this.recoverField_c[var5++] & 0xFF] != recoverField_i[var1[var2++] & 0xFF]) {
               return false;
            }
         }

         var4++;
      }

      return true;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      }

      if (var1 != null && var1 instanceof RecoverTypeaf2ab170) {
         RecoverTypeaf2ab170 var2 = (RecoverTypeaf2ab170)var1;
         if (var2.recoverField_e == 0) {
            var2.hashCode();
         }

         if (this.recoverField_e == 0) {
            this.hashCode();
         }

         if (var2.recoverField_e != this.recoverField_e) {
            return false;
         } else {
            return var2.recoverMethod_b_9b68() != this.recoverMethod_b_9b68()
               ? false
               : this.recoverMethod_b_49634b8b(var2.recoverField_c, var2.recoverMethod_a_134621(0));
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      if (this.recoverField_e != 0) {
         return this.recoverField_e;
      }

      int var1 = 0;

      for (int var2 = this.recoverMethod_a_134621(0); var2 < this.recoverField_c.length; var2++) {
         var1 += (var1 << 3) + recoverField_i[this.recoverField_c[var2] & 0xFF];
      }

      this.recoverField_e = var1;
      return this.recoverField_e;
   }

   @Override
   public int compareTo(Object var1) {
      RecoverTypeaf2ab170 var2 = (RecoverTypeaf2ab170)var1;
      if (this == var2) {
         return 0;
      }

      int var3 = this.recoverMethod_b_9b68();
      int var4 = var2.recoverMethod_b_9b68();
      int var5 = var3 > var4 ? var4 : var3;

      for (int var6 = 1; var6 <= var5; var6++) {
         int var7 = this.recoverMethod_a_134621(var3 - var6);
         int var8 = var2.recoverMethod_a_134621(var4 - var6);
         byte var9 = this.recoverField_c[var7];
         byte var10 = var2.recoverField_c[var8];

         for (int var11 = 0; var11 < var9 && var11 < var10; var11++) {
            int var12 = recoverField_i[this.recoverField_c[var11 + var7 + 1] & 0xFF] - recoverField_i[var2.recoverField_c[var11 + var8 + 1] & 0xFF];
            if (var12 != 0) {
               return var12;
            }
         }

         if (var9 != var10) {
            return var9 - var10;
         }
      }

      return var3 - var4;
   }

   static {
      recoverField_h.setMinimumIntegerDigits(3);

      for (int var0 = 0; var0 < recoverField_i.length; var0++) {
         if (var0 >= 65 && var0 <= 90) {
            recoverField_i[var0] = (byte)(var0 - 65 + 97);
         } else {
            recoverField_i[var0] = (byte)var0;
         }
      }

      recoverField_a = new RecoverTypeaf2ab170();
      recoverField_a.recoverMethod_b_e306985c(recoverField_f, 0, 1);
      recoverField_b = new RecoverTypeaf2ab170();
      recoverField_b.recoverField_c = new byte[0];
      recoverField_j = new RecoverTypeaf2ab170();
      recoverField_j.recoverMethod_b_e306985c(recoverField_g, 0, 1);
   }
}
