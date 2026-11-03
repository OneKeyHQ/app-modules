package cn.jiguang.n;

import cn.jiguang.am.RecoverTypeaf2a9ae9;
import java.math.BigInteger;

public class RecoverType5a6b29a {
   private byte[] recoverField_a;
   private int recoverField_b;
   private int recoverField_c;
   private static final BigInteger recoverField_d = BigInteger.ONE.shiftLeft(64);

   public RecoverType5a6b29a(int var1) {
      this.recoverField_a = new byte[var1];
      this.recoverField_b = 0;
      this.recoverField_c = -1;
   }

   public RecoverType5a6b29a() {
      this(32);
   }

   private void recoverMethod_a_25666f4(long var1, int var3) {
      long var4 = 1L;
      var4 <<= var3;
      if (var1 < 0L || var1 > var4) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCommonPackager", var1 + " out of range for " + var3 + " bit value max:" + var4);
      }
   }

   private void recoverMethod_b_13462e(int var1) {
      if (this.recoverField_a.length - this.recoverField_b < var1) {
         int var2 = this.recoverField_a.length * 2;
         if (var2 < this.recoverField_b + var1) {
            var2 = this.recoverField_b + var1;
         }

         byte[] var3 = new byte[var2];
         System.arraycopy(this.recoverField_a, 0, var3, 0, this.recoverField_b);
         this.recoverField_a = var3;
      }
   }

   public void recoverMethod_a_13462e(int var1) {
      this.recoverMethod_a_25666f4(var1, 16);
      this.recoverMethod_b_13462e(2);
      this.recoverField_a[this.recoverField_b++] = (byte)(var1 >>> 8 & 0xFF);
      this.recoverField_a[this.recoverField_b++] = (byte)(var1 & 0xFF);
   }

   public void recoverMethod_a_1349ef(long var1) {
      long var3 = var1;
      this.recoverMethod_b_13462e(8);
      this.recoverField_a[this.recoverField_b++] = (byte)(var3 >>> 56 & 255L);
      this.recoverField_a[this.recoverField_b++] = (byte)(var3 >>> 48 & 255L);
      this.recoverField_a[this.recoverField_b++] = (byte)(var3 >>> 40 & 255L);
      this.recoverField_a[this.recoverField_b++] = (byte)(var3 >>> 32 & 255L);
      this.recoverField_a[this.recoverField_b++] = (byte)(var3 >>> 24 & 255L);
      this.recoverField_a[this.recoverField_b++] = (byte)(var3 >>> 16 & 255L);
      this.recoverField_a[this.recoverField_b++] = (byte)(var3 >>> 8 & 255L);
      this.recoverField_a[this.recoverField_b++] = (byte)(var3 & 255L);
   }

   public void recoverMethod_a_e306985c(byte[] var1, int var2, int var3) {
      this.recoverMethod_b_13462e(var3);
      System.arraycopy(var1, var2, this.recoverField_a, this.recoverField_b, var3);
      this.recoverField_b += var3;
   }

   public void recoverMethod_a_25e06fc(byte[] var1) {
      this.recoverMethod_a_13462e(var1.length);
      this.recoverMethod_a_e306985c(var1, 0, var1.length);
   }

   public byte[] recoverMethod_a_12d408() {
      byte[] var1 = new byte[this.recoverField_b];
      System.arraycopy(this.recoverField_a, 0, var1, 0, this.recoverField_b);
      return var1;
   }
}
