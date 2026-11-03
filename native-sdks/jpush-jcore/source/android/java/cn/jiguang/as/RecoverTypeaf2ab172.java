package cn.jiguang.as;

import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Arrays;

public abstract class RecoverTypeaf2ab172 implements Serializable, Cloneable, Comparable {
   protected RecoverTypeaf2ab170 recoverField_a;
   protected int recoverField_b;
   protected int recoverField_c;
   protected long recoverField_d;
   private static final DecimalFormat recoverField_e = new DecimalFormat();

   protected RecoverTypeaf2ab172() {
   }

   private static final RecoverTypeaf2ab172 recoverMethod_a_72d7376b(RecoverTypeaf2ab170 var0, int var1, int var2, long var3, boolean var5) {
      RecoverTypeaf2ab172 var7 = null;
      var7 = new RecoverTypeaf2ab176();
      var7.recoverField_a = var0;
      var7.recoverField_b = var1;
      var7.recoverField_c = var2;
      var7.recoverField_d = var3;
      return var7;
   }

   abstract void recoverMethod_a_991bb9a1(RecoverTypeaf2ab16b var1) throws IOException;

   private static RecoverTypeaf2ab172 recoverMethod_a_9b86c23a(RecoverTypeaf2ab170 var0, int var1, int var2, long var3, int var5, RecoverTypeaf2ab16b var6) throws java.io.IOException {
      RecoverTypeaf2ab172 var7 = recoverMethod_a_72d7376b(var0, var1, var2, var3, var6 != null);
      if (var6 != null) {
         if (var6.recoverMethod_b_9b68() < var5) {
            throw new IOException("truncated record");
         }

         var6.recoverMethod_a_13462e(var5);
         var7.recoverMethod_a_991bb9a1(var6);
         if (var6.recoverMethod_b_9b68() > 0) {
            throw new IOException("invalid record length");
         }

         var6.recoverMethod_c_9b75();
      }

      return var7;
   }

   public static RecoverTypeaf2ab172 recoverMethod_a_5dcd2267(RecoverTypeaf2ab170 var0, int var1, int var2, long var3) {
      if (!var0.recoverMethod_a_9b79()) {
         throw new RecoverTypeaf2ab173(var0);
      } else {
         return recoverMethod_a_72d7376b(var0, var1, var2, var3, false);
      }
   }

   public static RecoverTypeaf2ab172 recoverMethod_a_e84e09db(RecoverTypeaf2ab170 var0, int var1, int var2) {
      return recoverMethod_a_5dcd2267(var0, var1, var2, 0L);
   }

   static RecoverTypeaf2ab172 recoverMethod_a_bcad7a81(RecoverTypeaf2ab16b var0, int var1) throws IOException {
      RecoverTypeaf2ab170 var7 = new RecoverTypeaf2ab170(var0);
      int var2 = var0.recoverMethod_g_9b68();
      int var3 = var0.recoverMethod_g_9b68();
      if (var1 == 0) {
         return recoverMethod_a_e84e09db(var7, var2, var3);
      }

      long var4 = var0.recoverMethod_h_9b69();
      int var6 = var0.recoverMethod_g_9b68();
      return recoverMethod_a_9b86c23a(var7, var2, var3, var4, var6, var0);
   }

   void recoverMethod_a_ec7c6974(RecoverTypeaf2ab16c var1, int var2, RecoverTypeaf2ab16a var3) {
      this.recoverField_a.recoverMethod_a_e78bba17(var1, var3);
      var1.recoverMethod_c_13462e(this.recoverField_b);
      var1.recoverMethod_c_13462e(this.recoverField_c);
   }

   private void recoverMethod_a_8a6a44d4(RecoverTypeaf2ab16c var1, boolean var2) {
      this.recoverField_a.recoverMethod_a_991c2e00(var1);
      var1.recoverMethod_c_13462e(this.recoverField_b);
      var1.recoverMethod_c_13462e(this.recoverField_c);
      if (var2) {
         var1.recoverMethod_a_1349ef(0L);
      } else {
         var1.recoverMethod_a_1349ef(this.recoverField_d);
      }

      int var3 = var1.recoverMethod_a_9b68();
      var1.recoverMethod_c_13462e(0);
      this.recoverMethod_a_9ec3b9d(var1, null, true);
      int var4 = var1.recoverMethod_a_9b68() - var3 - 2;
      var1.recoverMethod_a_255f295(var4, var3);
   }

   private byte[] recoverMethod_a_25d3abe(boolean var1) {
      RecoverTypeaf2ab16c var2 = new RecoverTypeaf2ab16c();
      this.recoverMethod_a_8a6a44d4(var2, var1);
      return var2.recoverMethod_b_12d408();
   }

   public byte[] recoverMethod_a_12d408() {
      RecoverTypeaf2ab16c var1 = new RecoverTypeaf2ab16c();
      this.recoverMethod_a_9ec3b9d(var1, null, true);
      return var1.recoverMethod_b_12d408();
   }

   abstract String recoverMethod_b_73cf11cb();

   public String recoverMethod_c_73cf11cb() {
      return this.recoverMethod_b_73cf11cb();
   }

   @Override
   public String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append(this.recoverField_a);
      if (var1.length() < 8) {
         var1.append("\t");
      }

      if (var1.length() < 16) {
         var1.append("\t");
      }

      var1.append("\t");
      String var2 = this.recoverMethod_b_73cf11cb();
      if (!var2.equals("")) {
         var1.append("\t");
         var1.append(var2);
      }

      return var1.toString();
   }

   public RecoverTypeaf2ab170 recoverMethod_d_c695e236() {
      return this.recoverField_a;
   }

   public int recoverMethod_e_9b68() {
      return this.recoverField_b;
   }

   public int recoverMethod_f_9b68() {
      return this.recoverField_b;
   }

   public int recoverMethod_g_9b68() {
      return this.recoverField_c;
   }

   public long recoverMethod_h_9b69() {
      return this.recoverField_d;
   }

   abstract void recoverMethod_a_9ec3b9d(RecoverTypeaf2ab16c var1, RecoverTypeaf2ab16a var2, boolean var3);

   public boolean recoverMethod_a_991ee83e(RecoverTypeaf2ab172 var1) {
      return this.recoverMethod_f_9b68() == var1.recoverMethod_f_9b68()
         && this.recoverField_c == var1.recoverField_c
         && this.recoverField_a.equals(var1.recoverField_a);
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof RecoverTypeaf2ab172) {
         RecoverTypeaf2ab172 var2 = (RecoverTypeaf2ab172)var1;
         if (this.recoverField_b == var2.recoverField_b && this.recoverField_c == var2.recoverField_c && this.recoverField_a.equals(var2.recoverField_a)) {
            byte[] var3 = this.recoverMethod_a_12d408();
            byte[] var4 = var2.recoverMethod_a_12d408();
            return Arrays.equals(var3, var4);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      byte[] var1 = this.recoverMethod_a_25d3abe(true);
      int var2 = 0;

      for (int var3 = 0; var3 < var1.length; var3++) {
         var2 += (var2 << 3) + (var1[var3] & 255);
      }

      return var2;
   }

   RecoverTypeaf2ab172 recoverMethod_i_c695e274() {
      try {
         return (RecoverTypeaf2ab172)this.clone();
      } catch (CloneNotSupportedException var2) {
         throw new IllegalStateException();
      }
   }

   void recoverMethod_a_1349ef(long var1) {
      this.recoverField_d = var1;
   }

   @Override
   public int compareTo(Object var1) {
      RecoverTypeaf2ab172 var2 = (RecoverTypeaf2ab172)var1;
      if (this == var2) {
         return 0;
      }

      int var3 = this.recoverField_a.compareTo(var2.recoverField_a);
      if (var3 != 0) {
         return var3;
      }

      var3 = this.recoverField_c - var2.recoverField_c;
      if (var3 != 0) {
         return var3;
      }

      var3 = this.recoverField_b - var2.recoverField_b;
      if (var3 != 0) {
         return var3;
      }

      byte[] var4 = this.recoverMethod_a_12d408();
      byte[] var5 = var2.recoverMethod_a_12d408();

      for (int var6 = 0; var6 < var4.length && var6 < var5.length; var6++) {
         var3 = (var4[var6] & 255) - (var5[var6] & 255);
         if (var3 != 0) {
            return var3;
         }
      }

      return var4.length - var5.length;
   }

   static {
      recoverField_e.setMinimumIntegerDigits(3);
   }
}
