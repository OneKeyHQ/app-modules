package cn.jiguang.as;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public class RecoverTypeaf2ab16b {
   private ByteBuffer recoverField_a;
   private int recoverField_b;
   private int recoverField_c;

   public RecoverTypeaf2ab16b(byte[] var1) {
      this.recoverField_a = ByteBuffer.wrap(var1);
      this.recoverField_b = -1;
      this.recoverField_c = -1;
   }

   public int recoverMethod_a_9b68() {
      return this.recoverField_a.position();
   }

   public int recoverMethod_b_9b68() {
      return this.recoverField_a.remaining();
   }

   private void recoverMethod_c_13462e(int var1) throws java.io.IOException {
      if (var1 > this.recoverMethod_b_9b68()) {
         throw new IOException("end of input");
      }
   }

   public void recoverMethod_a_13462e(int var1) {
      if (var1 > this.recoverField_a.capacity() - this.recoverField_a.position()) {
         throw new IllegalArgumentException("cannot set active region past end of input");
      }

      ((Buffer)this.recoverField_a).limit(this.recoverField_a.position() + var1);
   }

   public void recoverMethod_c_9b75() {
      ((Buffer)this.recoverField_a).limit(this.recoverField_a.capacity());
   }

   public void recoverMethod_b_13462e(int var1) {
      if (var1 >= this.recoverField_a.capacity()) {
         throw new IllegalArgumentException("cannot jump past end of input");
      }

      ((Buffer)this.recoverField_a).position(var1);
      ((Buffer)this.recoverField_a).limit(this.recoverField_a.capacity());
   }

   public void recoverMethod_d_9b75() {
      this.recoverField_b = this.recoverField_a.position();
      this.recoverField_c = this.recoverField_a.limit();
   }

   public void recoverMethod_e_9b75() {
      if (this.recoverField_b < 0) {
         throw new IllegalStateException("no previous state");
      }

      ((Buffer)this.recoverField_a).position(this.recoverField_b);
      ((Buffer)this.recoverField_a).limit(this.recoverField_c);
      this.recoverField_b = -1;
      this.recoverField_c = -1;
   }

   public int recoverMethod_f_9b68() throws java.io.IOException {
      this.recoverMethod_c_13462e(1);
      return this.recoverField_a.get() & 0xFF;
   }

   public int recoverMethod_g_9b68() throws java.io.IOException {
      this.recoverMethod_c_13462e(2);
      return this.recoverField_a.getShort() & 65535;
   }

   public long recoverMethod_h_9b69() throws java.io.IOException {
      this.recoverMethod_c_13462e(4);
      return this.recoverField_a.getInt() & 4294967295L;
   }

   public void recoverMethod_a_e306985c(byte[] var1, int var2, int var3) throws java.io.IOException {
      this.recoverMethod_c_13462e(var3);
      this.recoverField_a.get(var1, var2, var3);
   }
}
