package cn.jiguang.bi;

import cn.jiguang.api.utils.ProtocolUtil;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public class RecoverTypeaf2b0041 {
   int recoverField_a;
   int recoverField_b;
   int recoverField_c;
   Long recoverField_d;
   int recoverField_e;
   long recoverField_f;
   private boolean recoverField_g = false;

   public RecoverTypeaf2b0041(boolean var1, int var2, int var3, int var4, long var5, int var7, long var8) {
      this.recoverField_g = var1;
      this.recoverField_a = var2;
      this.recoverField_b = var3;
      this.recoverField_c = var4;
      this.recoverField_d = var5;
      this.recoverField_e = var7;
      this.recoverField_f = var8;
   }

   public RecoverTypeaf2b0041(boolean var1, int var2, int var3, long var4) {
      this(var1, 0, var2, var3, var4, 0, 0L);
   }

   public RecoverTypeaf2b0041(boolean var1, byte[] var2) {
      this.recoverField_g = var1;
      ByteBuffer var3 = ByteBuffer.wrap(var2);
      this.recoverField_a = var3.getShort();
      this.recoverField_a &= 32767;
      this.recoverField_b = var3.get();
      this.recoverField_c = var3.get();
      this.recoverField_d = var3.getLong();
      this.recoverField_d = this.recoverField_d & 65535L;
      if (var1) {
         this.recoverField_e = var3.getInt();
      }

      this.recoverField_f = var3.getLong();
   }

   public int recoverMethod_a_9b68() {
      return this.recoverField_c;
   }

   public Long recoverMethod_b_10c66500() {
      return this.recoverField_d;
   }

   public long recoverMethod_c_9b69() {
      return this.recoverField_f;
   }

   public int recoverMethod_d_9b68() {
      return this.recoverField_e;
   }

   public int recoverMethod_e_9b68() {
      return this.recoverField_b;
   }

   public void recoverMethod_a_13462e(int var1) {
      this.recoverField_a = var1;
   }

   public void recoverMethod_b_13462e(int var1) {
      this.recoverField_e = var1;
   }

   public void recoverMethod_a_1349ef(long var1) {
      this.recoverField_f = var1;
   }

   public byte[] recoverMethod_f_12d408() {
      if (this.recoverField_a == 0) {
         throw new IllegalStateException("The head is not initialized yet.");
      }

      ByteBuffer var1 = ByteBuffer.allocate(24);
      var1.putShort((short)this.recoverField_a);
      var1.put((byte)this.recoverField_b);
      var1.put((byte)this.recoverField_c);
      var1.putLong(this.recoverField_d);
      if (this.recoverField_g) {
         var1.putInt(this.recoverField_e);
      }

      var1.putLong(this.recoverField_f);
      ((Buffer)var1).flip();
      return ProtocolUtil.getBytesConsumed(var1);
   }

   @Override
   public String toString() {
      return "[JHead] - len:"
         + this.recoverField_a
         + ", version:"
         + this.recoverField_b
         + ", command:"
         + this.recoverField_c
         + ", rid:"
         + this.recoverField_d
         + (this.recoverField_g ? ", sid:" + this.recoverField_e : "")
         + ", juid:"
         + this.recoverField_f;
   }
}
