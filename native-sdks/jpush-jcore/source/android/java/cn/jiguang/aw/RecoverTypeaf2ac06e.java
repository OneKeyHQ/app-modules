package cn.jiguang.aw;

import java.io.Closeable;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class RecoverTypeaf2ac06e implements Closeable {
   protected ByteBuffer recoverField_a;
   protected SocketChannel recoverField_b;
   protected int recoverField_c;
   protected Selector recoverField_d;
   protected volatile boolean recoverField_e;
   protected int recoverField_f = 20;
   public int recoverField_g = 0;
   private static AtomicInteger recoverField_j = new AtomicInteger(0);
   public String recoverField_h;
   public int recoverField_i;

   public RecoverTypeaf2ac06e() {
      this.recoverField_a = ByteBuffer.allocate(49152);
      this.recoverField_e = false;
      this.recoverField_g = recoverField_j.incrementAndGet();
   }

   public int recoverMethod_a_afa300d7(String var1, int var2) {
      if (this.recoverField_a == null) {
         this.recoverField_a = ByteBuffer.allocate(49152);
      }

      ((Buffer)this.recoverField_a).clear();
      this.recoverField_c = 0;
      this.recoverField_e = true;
      this.recoverField_h = var1;
      this.recoverField_i = var2;
      return 0;
   }

   public ByteBuffer recoverMethod_a_d68a3c36() throws cn.jiguang.at.RecoverTypeaf2ab52f {
      return this.recoverMethod_a_c72d506f(0);
   }

   public abstract ByteBuffer recoverMethod_a_c72d506f(int var1) throws cn.jiguang.at.RecoverTypeaf2ab52f;

   public abstract int recoverMethod_a_25e06ef(byte[] var1);

   @Override
   public void close() {
      this.recoverField_e = false;
      if (this.recoverField_a != null) {
         ((Buffer)this.recoverField_a).clear();
      }

      this.recoverField_c = 0;
   }

   public boolean recoverMethod_b_9b79() {
      return this.recoverField_e && this.recoverField_b != null && this.recoverField_b.isConnected();
   }

   protected int recoverMethod_c_9b68() {
      if (this.recoverField_c < this.recoverField_f) {
         return 0;
      }

      int var1 = this.recoverField_a.position();
      ((Buffer)this.recoverField_a).position(0);
      int var2 = this.recoverField_a.getShort();
      var2 &= 32767;
      ((Buffer)this.recoverField_a).position(var1);
      return var2;
   }

   protected ByteBuffer recoverMethod_b_c72d506f(int var1) {
      if (this.recoverField_c >= var1) {
         this.recoverField_c -= var1;
         byte[] var2 = new byte[var1];
         ((Buffer)this.recoverField_a).flip();
         this.recoverField_a.get(var2, 0, var1);
         ByteBuffer var3 = ByteBuffer.wrap(var2);
         this.recoverField_a.compact();
         return var3;
      } else {
         return null;
      }
   }
}
