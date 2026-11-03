package cn.jiguang.as;

public class RecoverTypeaf2ab16c {
   private byte[] recoverField_a;
   private int recoverField_b;
   private int recoverField_c;

   public RecoverTypeaf2ab16c(int var1) {
      this.recoverField_a = new byte[var1];
      this.recoverField_b = 0;
      this.recoverField_c = -1;
   }

   public RecoverTypeaf2ab16c() {
      this(32);
   }

   public int recoverMethod_a_9b68() {
      return this.recoverField_b;
   }

   private void recoverMethod_a_25666f4(long var1, int var3) {
      long var4 = 1L;
      var4 <<= var3;
      if (var1 < 0L || var1 > var4) {
         throw new IllegalArgumentException(var1 + " out of range for " + var3 + " bit value");
      }
   }

   private void recoverMethod_d_13462e(int var1) {
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
      if (var1 > this.recoverField_b) {
         throw new IllegalArgumentException("cannot jump past end of data");
      }

      this.recoverField_b = var1;
   }

   public void recoverMethod_b_13462e(int var1) {
      this.recoverMethod_a_25666f4(var1, 8);
      this.recoverMethod_d_13462e(1);
      this.recoverField_a[this.recoverField_b++] = (byte)(var1 & 0xFF);
   }

   public void recoverMethod_c_13462e(int var1) {
      this.recoverMethod_a_25666f4(var1, 16);
      this.recoverMethod_d_13462e(2);
      this.recoverField_a[this.recoverField_b++] = (byte)(var1 >>> 8 & 0xFF);
      this.recoverField_a[this.recoverField_b++] = (byte)(var1 & 0xFF);
   }

   public void recoverMethod_a_255f295(int var1, int var2) {
      this.recoverMethod_a_25666f4(var1, 16);
      if (var2 > this.recoverField_b - 2) {
         throw new IllegalArgumentException("cannot write past end of data");
      }

      this.recoverField_a[var2++] = (byte)(var1 >>> 8 & 0xFF);
      this.recoverField_a[var2++] = (byte)(var1 & 0xFF);
   }

   public void recoverMethod_a_1349ef(long var1) {
      this.recoverMethod_a_25666f4(var1, 32);
      this.recoverMethod_d_13462e(4);
      this.recoverField_a[this.recoverField_b++] = (byte)(var1 >>> 24 & 255L);
      this.recoverField_a[this.recoverField_b++] = (byte)(var1 >>> 16 & 255L);
      this.recoverField_a[this.recoverField_b++] = (byte)(var1 >>> 8 & 255L);
      this.recoverField_a[this.recoverField_b++] = (byte)(var1 & 255L);
   }

   public void recoverMethod_a_e306985c(byte[] var1, int var2, int var3) {
      this.recoverMethod_d_13462e(var3);
      System.arraycopy(var1, var2, this.recoverField_a, this.recoverField_b, var3);
      this.recoverField_b += var3;
   }

   public void recoverMethod_a_25e06fc(byte[] var1) {
      this.recoverMethod_a_e306985c(var1, 0, var1.length);
   }

   public byte[] recoverMethod_b_12d408() {
      byte[] var1 = new byte[this.recoverField_b];
      System.arraycopy(this.recoverField_a, 0, var1, 0, this.recoverField_b);
      return var1;
   }
}
