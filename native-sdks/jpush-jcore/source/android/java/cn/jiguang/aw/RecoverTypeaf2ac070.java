package cn.jiguang.aw;

import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.at.RecoverTypeaf2ab52f;
import cn.jiguang.bk.RecoverTypeaf2b07d7;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class RecoverTypeaf2ac070 extends RecoverTypeaf2ac06e {
   private final int recoverField_j;
   private ByteBuffer recoverField_k;

   public RecoverTypeaf2ac070(int var1, int var2) {
      this.recoverField_j = var1;
      this.recoverField_k = ByteBuffer.allocate(8192);
      this.recoverField_f = var2;
   }

   @Override
   public synchronized int recoverMethod_a_afa300d7(String var1, int var2) {
      super.recoverMethod_a_afa300d7(var1, var2);

      try {
         this.recoverField_b = SocketChannel.open();
         this.recoverField_d = Selector.open();
         this.recoverField_b.configureBlocking(false);
         InetSocketAddress var3 = new InetSocketAddress(var1, var2);
         this.recoverField_b.connect(var3);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NioSocketClient", "tcp connecting...");
         long var4 = System.currentTimeMillis();

         while (!this.recoverField_b.finishConnect()) {
            if (!this.recoverField_e) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NioSocketClient", "has close channel when connect...");
               return -991;
            }

            Thread.sleep(10L);
            if (System.currentTimeMillis() - var4 > 3000L) {
               this.close();
               return -994;
            }
         }

         if (!this.recoverField_e) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NioSocketClient", "has close channel when connected...");
            return -991;
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NioSocketClient", "tcp connected [" + var1 + ":" + var2 + "]");
            this.recoverField_b.register(this.recoverField_d, 1);
            return 0;
         }
      } catch (Throwable var6) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("NioSocketClient", "tcp connect has failed:" + var6);
         this.close();
         return var6 instanceof SocketTimeoutException ? -994 : -1000;
      }
   }

   @Override
   public ByteBuffer recoverMethod_a_c72d506f(int var1) throws cn.jiguang.at.RecoverTypeaf2ab52f {
      int var2 = 1048576;
      int var3 = 0;

      try {
         if (!this.recoverMethod_b_9b79()) {
            throw new RecoverTypeaf2ab52f(-991, "recv error,the connect is invalid");
         }

         int var4 = this.recoverMethod_c_9b68();
         if (var4 > 0) {
            ByteBuffer var5 = this.recoverMethod_b_c72d506f(var4);
            if (var5 != null) {
               return var5;
            }
         }

         while (this.recoverMethod_b_9b79() && this.recoverField_c < var2) {
            int var13;
            if (var1 > 0) {
               var13 = this.recoverField_d.select(var1);
            } else {
               var13 = this.recoverField_d.select();
            }

            if (0 == var13) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NioSocketClient", "readSelect:" + var13 + ",time out:" + var1);
               if (var1 > 0) {
                  throw new RecoverTypeaf2ab52f(-994, "recv time out");
               }
            } else {
               Set var6 = this.recoverField_d.selectedKeys();

               for (Iterator var7 = var6.iterator(); var7.hasNext(); var7.remove()) {
                  SelectionKey var8 = (SelectionKey)var7.next();
                  SocketChannel var9 = (SocketChannel)var8.channel();
                  if (var8.isReadable()) {
                     int var10 = var9.read(this.recoverField_k);
                     if (var10 < 0) {
                        throw new RecoverTypeaf2ab52f(-996, "read len < 0:" + var10);
                     }

                     ((Buffer)this.recoverField_k).flip();
                     var3 = this.recoverField_k.limit();
                     if (this.recoverField_a.remaining() < var3) {
                        throw new RecoverTypeaf2ab52f(
                           -996, "the total buf remaining less than readLen,remaining:" + this.recoverField_a.remaining() + ",readLen:" + var3
                        );
                     }

                     this.recoverField_a.put(this.recoverField_k);
                     this.recoverField_c += var3;
                     this.recoverField_k.compact();
                     if (this.recoverField_c < this.recoverField_f) {
                        RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
                           "NioSocketClient", "totalbuf can not parse head:" + this.recoverField_c + ",peerNetData len:" + var3 + ",read:" + var10
                        );
                     } else {
                        var2 = this.recoverMethod_c_9b68();
                     }
                  } else if (var8.isWritable()) {
                  }
               }
            }
         }
      } catch (Throwable var11) {
         if (var11 instanceof SocketTimeoutException) {
            throw new RecoverTypeaf2ab52f(-994, var11.getMessage());
         }

         if (var11 instanceof RecoverTypeaf2ab52f) {
            throw (RecoverTypeaf2ab52f)var11;
         }

         throw new RecoverTypeaf2ab52f(-997, var11.getMessage());
      }

      if (var2 == 1048576) {
         throw new RecoverTypeaf2ab52f(-997, "recv empty data or tcp has close");
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NioSocketClient", "read len:" + var3 + ",recvTotalLen:" + this.recoverField_c + ",shouldLen:" + var2);
         ByteBuffer var12 = this.recoverMethod_b_c72d506f(var2);
         if (var12 != null) {
            return var12;
         } else {
            throw new RecoverTypeaf2ab52f(-1001, "parse error");
         }
      }
   }

   @Override
   public synchronized int recoverMethod_a_25e06ef(byte[] var1) {
      if (var1 == null) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("NioSocketClient", "sendData failed, send data was null");
         return 103;
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NioSocketClient", "send data length:" + var1.length);
         if (var1.length >= this.recoverField_j) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("NioSocketClient", "sendData failed, data length must less than " + this.recoverField_j);
            return 6026;
         } else {
            return this.recoverMethod_b_25e0700(var1) ? 0 : 103;
         }
      }
   }

   private boolean recoverMethod_b_25e0700(byte[] var1) {
      try {
         if (!this.recoverMethod_b_9b79()) {
            RecoverTypeaf2a9ae9.recoverMethod_k_e925fa09("NioSocketClient", "send error -tcp connect was invalid");
            return false;
         }

         if (var1 != null && var1.length > 0) {
            int var2 = this.recoverField_b.write(ByteBuffer.wrap(var1));
            if (var2 > 0) {
               RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("NioSocketClient", "isWritable has send len:" + var2);
            } else if (var2 < 0) {
               RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("NioSocketClient", "isWritable error:" + var2);
               return false;
            }

            return true;
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NioSocketClient", "send error - invalide buffer");
            return false;
         }
      } catch (Exception var3) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("NioSocketClient", "send data error:" + var3);
         this.close();
         return false;
      }
   }

   @Override
   public void close() {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NioSocketClient", "close this connect");
      super.close();
      if (this.recoverField_d != null) {
         try {
            this.recoverField_d.close();
         } catch (IOException var2) {
         }
      }

      RecoverTypeaf2b07d7.recoverMethod_a_d17885d6(this.recoverField_b);
      this.recoverField_b = null;
   }
}
