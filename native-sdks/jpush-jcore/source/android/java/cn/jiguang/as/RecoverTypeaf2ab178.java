package cn.jiguang.as;

import java.io.EOFException;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public final class RecoverTypeaf2ab178 {
   protected long recoverField_a;
   protected SelectionKey recoverField_b;

   public RecoverTypeaf2ab178(long var1) throws java.io.IOException {
      this.recoverField_a = var1;
      SocketChannel var3 = SocketChannel.open();

      try {
         Selector var4 = Selector.open();

         try {
            var3.configureBlocking(false);
            this.recoverField_b = var3.register(var4, 1);
         } catch (Throwable var9) {
            if (var4 != null) {
               try {
                  var4.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (var4 != null) {
            var4.close();
         }
      } catch (Throwable var10) {
         if (var3 != null) {
            try {
               var3.close();
            } catch (Throwable var7) {
               var10.addSuppressed(var7);
            }
         }

         throw var10;
      }

      if (var3 != null) {
         var3.close();
      }
   }

   void recoverMethod_a_2bdec1a2(SocketAddress var1) throws java.io.IOException {
      SocketChannel var2 = (SocketChannel)this.recoverField_b.channel();
      var2.socket().bind(var1);
   }

   void recoverMethod_b_2bdec1a2(SocketAddress var1) throws java.io.IOException {
      SocketChannel var2 = (SocketChannel)this.recoverField_b.channel();
      if (!var2.connect(var1)) {
         this.recoverField_b.interestOps(8);

         try {
            while (!var2.finishConnect()) {
               if (!this.recoverField_b.isConnectable()) {
                  recoverMethod_a_baf8dea(this.recoverField_b, this.recoverField_a);
               }
            }
         } finally {
            if (this.recoverField_b.isValid()) {
               this.recoverField_b.interestOps(0);
            }
         }
      }
   }

   void recoverMethod_a_25e06fc(byte[] var1) throws java.io.EOFException, java.io.IOException, java.net.SocketTimeoutException {
      SocketChannel var2 = (SocketChannel)this.recoverField_b.channel();
      byte[] var3 = new byte[]{(byte)(var1.length >>> 8), (byte)(var1.length & 0xFF)};
      ByteBuffer[] var4 = new ByteBuffer[]{ByteBuffer.wrap(var3), ByteBuffer.wrap(var1)};
      int var5 = 0;
      this.recoverField_b.interestOps(4);

      try {
         while (var5 < var1.length + 2) {
            if (this.recoverField_b.isWritable()) {
               long var6 = var2.write(var4);
               if (var6 < 0L) {
                  throw new EOFException();
               }

               var5 += (int)var6;
               if (var5 < var1.length + 2 && System.currentTimeMillis() > this.recoverField_a) {
                  throw new SocketTimeoutException();
               }
            } else {
               recoverMethod_a_baf8dea(this.recoverField_b, this.recoverField_a);
            }
         }
      } finally {
         if (this.recoverField_b.isValid()) {
            this.recoverField_b.interestOps(0);
         }
      }
   }

   private byte[] recoverMethod_a_255806f(int var1) throws java.io.EOFException, java.io.IOException, java.net.SocketTimeoutException {
      SocketChannel var2 = (SocketChannel)this.recoverField_b.channel();
      int var3 = 0;
      byte[] var4 = new byte[var1];
      ByteBuffer var5 = ByteBuffer.wrap(var4);
      this.recoverField_b.interestOps(1);

      try {
         while (var3 < var1) {
            if (this.recoverField_b.isReadable()) {
               long var6 = var2.read(var5);
               if (var6 < 0L) {
                  throw new EOFException();
               }

               var3 += (int)var6;
               if (var3 < var1 && System.currentTimeMillis() > this.recoverField_a) {
                  throw new SocketTimeoutException();
               }
            } else {
               recoverMethod_a_baf8dea(this.recoverField_b, this.recoverField_a);
            }
         }
      } finally {
         if (this.recoverField_b.isValid()) {
            this.recoverField_b.interestOps(0);
         }
      }

      return var4;
   }

   byte[] recoverMethod_a_12d408() throws java.io.EOFException , java.io.IOException {
      byte[] var1 = this.recoverMethod_a_255806f(2);
      int var2 = ((var1[0] & 255) << 8) + (var1[1] & 255);
      byte[] var3 = this.recoverMethod_a_255806f(var2);
      SocketChannel var4 = (SocketChannel)this.recoverField_b.channel();
      return var3;
   }

   protected static void recoverMethod_a_baf8dea(SelectionKey var0, long var1) throws java.io.IOException, java.net.SocketTimeoutException {
      long var3 = var1 - System.currentTimeMillis();
      int var5 = 0;
      if (var3 > 0L) {
         var5 = var0.selector().select(var3);
      } else if (var3 == 0L) {
         var5 = var0.selector().selectNow();
      }

      if (var5 == 0) {
         throw new SocketTimeoutException();
      }
   }

   public static byte[] recoverMethod_a_fbb032ef(SocketAddress var0, SocketAddress var1, byte[] var2, long var3) throws java.io.EOFException, java.io.IOException {
      RecoverTypeaf2ab178 var5 = new RecoverTypeaf2ab178(var3);

      try {
         if (var0 != null) {
            var5.recoverMethod_a_2bdec1a2(var0);
         }

         var5.recoverMethod_b_2bdec1a2(var1);
         var5.recoverMethod_a_25e06fc(var2);
         return var5.recoverMethod_a_12d408();
      } finally {
         var5.recoverMethod_b_9b75();
      }
   }

   void recoverMethod_b_9b75() throws java.io.IOException {
      this.recoverField_b.selector().close();
      this.recoverField_b.channel().close();
   }
}
