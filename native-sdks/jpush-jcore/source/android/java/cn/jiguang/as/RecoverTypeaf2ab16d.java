package cn.jiguang.as;

import java.security.SecureRandom;

public class RecoverTypeaf2ab16d implements Cloneable {
   private int recoverField_a;
   private int recoverField_b = 256;
   private int[] recoverField_c;
   private static SecureRandom recoverField_d = new SecureRandom();

   private void recoverMethod_d_9b75() {
      this.recoverField_c = new int[4];
      this.recoverField_b = 256;
      this.recoverField_a = -1;
   }

   public RecoverTypeaf2ab16d(int var1) {
      this.recoverMethod_d_9b75();
      this.recoverMethod_a_13462e(var1);
   }

   public RecoverTypeaf2ab16d() {
      this.recoverMethod_d_9b75();
   }

   RecoverTypeaf2ab16d(RecoverTypeaf2ab16b var1) throws java.io.IOException {
      this(var1.recoverMethod_g_9b68());
      this.recoverField_b = var1.recoverMethod_g_9b68();

      for (int var2 = 0; var2 < this.recoverField_c.length; var2++) {
         this.recoverField_c[var2] = var1.recoverMethod_g_9b68();
      }
   }

   void recoverMethod_a_991c2e00(RecoverTypeaf2ab16c var1) {
      var1.recoverMethod_c_13462e(this.recoverMethod_a_9b68());
      var1.recoverMethod_c_13462e(this.recoverField_b);

      for (int var2 = 0; var2 < this.recoverField_c.length; var2++) {
         var1.recoverMethod_c_13462e(this.recoverField_c[var2]);
      }
   }

   public int recoverMethod_a_9b68() {
      if (this.recoverField_a >= 0) {
         return this.recoverField_a;
      }

      synchronized (this) {
         if (this.recoverField_a < 0) {
            this.recoverField_a = recoverField_d.nextInt(65535);
         }

         return this.recoverField_a;
      }
   }

   public void recoverMethod_a_13462e(int var1) {
      if (var1 >= 0 && var1 <= 65535) {
         this.recoverField_a = var1;
      } else {
         throw new IllegalArgumentException("DNS message ID " + var1 + " is out of range");
      }
   }

   public int recoverMethod_b_9b68() {
      return this.recoverField_b & 15;
   }

   void recoverMethod_b_13462e(int var1) {
      if (this.recoverField_c[var1] == 65535) {
         throw new IllegalStateException("DNS section count cannot be incremented");
      }

      this.recoverField_c[var1]++;
   }

   public int recoverMethod_c_134621(int var1) {
      return this.recoverField_c[var1];
   }

   int recoverMethod_c_9b68() {
      return this.recoverField_b;
   }

   String recoverMethod_d_47921032(int var1) {
      StringBuffer var2 = new StringBuffer();
      var2.append(";; ->>HEADER<<- ");
      var2.append(", id: " + this.recoverMethod_a_9b68());
      var2.append("\n");
      var2.append("; ");

      for (int var3 = 0; var3 < 4; var3++) {
         var2.append(RecoverTypeaf2ab177.recoverMethod_a_47921032(var3) + ": " + this.recoverMethod_c_134621(var3) + " ");
      }

      return var2.toString();
   }

   @Override
   public String toString() {
      return this.recoverMethod_d_47921032(this.recoverMethod_b_9b68());
   }

   @Override
   public Object clone() {
      RecoverTypeaf2ab16d var1 = new RecoverTypeaf2ab16d();
      var1.recoverField_a = this.recoverField_a;
      var1.recoverField_b = this.recoverField_b;
      System.arraycopy(this.recoverField_c, 0, var1.recoverField_c, 0, this.recoverField_c.length);
      return var1;
   }
}
