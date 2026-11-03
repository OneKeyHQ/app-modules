package cn.jiguang.aw;

import cn.jiguang.al.RecoverTypeaf2a972c;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.at.RecoverTypeaf2ab52f;
import cn.jiguang.bh.RecoverTypeaf2afc7e;
import cn.jiguang.bk.RecoverTypeaf2b07d7;
import cn.jiguang.net.SSLTrustManager;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.Set;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;

public class RecoverTypeaf2ac06f extends RecoverTypeaf2ac06e {
   private SSLEngine recoverField_k;
   private ByteBuffer recoverField_l;
   private ByteBuffer recoverField_m;
   private ByteBuffer recoverField_n;
   private ByteBuffer recoverField_o;
   private final int recoverField_p;
   private boolean recoverField_q;
   protected SSLContext recoverField_j;

   public RecoverTypeaf2ac06f(int var1, int var2) {
      this.recoverField_p = var1;
      this.recoverField_n = ByteBuffer.allocate(8192);
      this.recoverField_f = var2;
   }

   @Override
   public synchronized int recoverMethod_a_afa300d7(String var1, int var2) {
      super.recoverMethod_a_afa300d7(var1, var2);

      try {
         boolean var3 = this.recoverMethod_d_9b79();
         if (!var3) {
            RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("NioSSLSocketClient", "#unexcepted - create SSL failed");
            return -992;
         }

         RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("NioSSLSocketClient", "tcp connecting...");
         this.recoverField_b = SocketChannel.open();
         this.recoverField_d = Selector.open();
         this.recoverField_b.configureBlocking(false);
         InetSocketAddress var4 = new InetSocketAddress(var1, var2);
         this.recoverField_b.connect(var4);
         long var5 = System.currentTimeMillis();

         while (!this.recoverField_b.finishConnect()) {
            if (!this.recoverField_e) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NioSSLSocketClient", "has close channel when connect...");
               return -991;
            }

            Thread.sleep(10L);
            if (System.currentTimeMillis() - var5 > 3000L) {
               this.close();
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_13462e(2);
               return -994;
            }
         }

         if (!this.recoverField_e) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NioSSLSocketClient", "has close channel when connected...");
            return -991;
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NioSSLSocketClient", "begin doHandShake");
            this.recoverField_q = this.recoverMethod_a_f687e3f7(this.recoverField_b);
            if (!this.recoverField_q) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NioSSLSocketClient", "handShake failed");
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_13462e(3);
               return -992;
            } else {
               ((Buffer)this.recoverField_n).clear();
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NioSSLSocketClient", "tcp connected [" + var1 + ":" + var2 + "]");
               return 0;
            }
         }
      } catch (Throwable var7) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("NioSSLSocketClient", "tcp connect has failed:" + var7);
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_13462e(var7 instanceof SocketTimeoutException ? 3 : 4);
         this.close();
         return var7 instanceof SocketTimeoutException ? -994 : -1000;
      }
   }

   @Override
   public boolean recoverMethod_b_9b79() {
      return super.recoverMethod_b_9b79() && this.recoverField_q;
   }

   @Override
   public ByteBuffer recoverMethod_a_c72d506f(int var1) throws cn.jiguang.at.RecoverTypeaf2ab52f {
      if (!this.recoverMethod_b_9b79()) {
         throw new RecoverTypeaf2ab52f(-991, "recv error,the connect is invalid");
      }

      int var2 = this.recoverMethod_c_9b68();
      if (var2 > 0) {
         ByteBuffer var3 = this.recoverMethod_b_c72d506f(var2);
         if (var3 != null) {
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_13462e(0);
            return var3;
         }
      }

      int var12 = 1048576;
      int var4 = 0;

      try {
         if (!this.recoverMethod_b_9b79()) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NioSSLSocketClient", "recv register error,the connect is invalid");
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_13462e(4);
            return null;
         }

         while (this.recoverMethod_b_9b79() && this.recoverField_c < var12) {
            int var5;
            if (var1 > 0) {
               var5 = this.recoverField_d.select(var1);
            } else {
               var5 = this.recoverField_d.select();
            }

            if (0 == var5) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NioSSLSocketClient", "readSelect:" + var5 + ", time out:" + var1);
               if (var1 > 0) {
                  RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_13462e(2);
                  throw new RecoverTypeaf2ab52f(-994, "recv time out");
               }
            } else {
               Set var6 = this.recoverField_d.selectedKeys();
               Iterator var7 = var6.iterator();

               while (var7.hasNext()) {
                  SelectionKey var8 = (SelectionKey)var7.next();
                  var7.remove();
                  SocketChannel var9 = (SocketChannel)var8.channel();
                  if (!this.recoverMethod_b_9b79()) {
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NioSSLSocketClient", "tcp connect has set false in select key");
                     RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_13462e(4);
                     return null;
                  }

                  if (var8.isReadable()) {
                     if (this.recoverField_k.getHandshakeStatus() == HandshakeStatus.NOT_HANDSHAKING) {
                        int var10 = this.recoverMethod_a_38a5141(var9, this.recoverField_k);
                        if (var10 != 0) {
                           var8.cancel();
                           return null;
                        }

                        ((Buffer)this.recoverField_o).flip();
                        var4 = this.recoverField_o.limit();
                        if (this.recoverField_a.remaining() < var4) {
                           RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_13462e(5);
                           throw new RecoverTypeaf2ab52f(
                              -996, "the total buf remaining less than readLen,remaining:" + this.recoverField_a.remaining() + ",readLen:" + var4
                           );
                        }

                        this.recoverField_a.put(this.recoverField_o);
                        this.recoverField_c += var4;
                        this.recoverField_o.compact();
                        if (this.recoverField_c < this.recoverField_f) {
                           RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_13462e(6);
                           RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
                              "NioSSLSocketClient", "totalbuf can not parse head:" + this.recoverField_c + ",peerNetData len:" + var4 + ",read:" + var10
                           );
                        } else {
                           var12 = this.recoverMethod_c_9b68();
                        }
                     }
                  } else if (var8.isWritable()) {
                  }
               }
            }
         }
      } catch (Throwable var11) {
         if (var11 instanceof SocketTimeoutException) {
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_13462e(2);
            throw new RecoverTypeaf2ab52f(-994, var11.getMessage());
         }

         if (var11 instanceof RecoverTypeaf2ab52f) {
            throw (RecoverTypeaf2ab52f)var11;
         }

         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_13462e(7);
         throw new RecoverTypeaf2ab52f(-997, var11.getMessage());
      }

      if (var12 == 1048576) {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_13462e(9);
         throw new RecoverTypeaf2ab52f(-997, "recv empty data or tcp has close");
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NioSSLSocketClient", "read len:" + var4 + ",recvTotalLen:" + this.recoverField_c + ",shouldLen:" + var12);
         ByteBuffer var13 = this.recoverMethod_b_c72d506f(var12);
         if (var13 != null) {
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_13462e(0);
            return var13;
         } else {
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_13462e(8);
            throw new RecoverTypeaf2ab52f(-1001, "parse error");
         }
      }
   }

   private int recoverMethod_a_38a5141(SocketChannel var1, SSLEngine var2) throws RecoverTypeaf2ab52f, SSLException, java.io.IOException {
      ((Buffer)this.recoverField_o).clear();
      int var3 = var1.read(this.recoverField_n);
      if (var3 < 0) {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_13462e(12);
         throw new RecoverTypeaf2ab52f(-996, "read len < 0: " + var3);
      }

      while (this.recoverField_n.hasRemaining()) {
         SSLEngineResult var4;
         try {
            ((Buffer)this.recoverField_n).flip();
            var4 = var2.unwrap(this.recoverField_n, this.recoverField_o);
            this.recoverField_n.compact();
         } catch (Throwable var6) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("NioSSLSocketClient", "recv data failed for unwrap net data, " + var6);
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_13462e(10);
            throw new SSLException("unwrap ssl net data failed: " + var6.getMessage());
         }

         switch (var4.getStatus()) {
            case OK:
               break;
            case BUFFER_OVERFLOW:
               this.recoverField_o = this.recoverMethod_b_dd4acd86(var2, this.recoverField_o);
               break;
            case BUFFER_UNDERFLOW:
               this.recoverField_n = this.recoverMethod_c_dd4acd86(var2, this.recoverField_n);
               return 0;
            case CLOSED:
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09(
                  "NioSSLSocketClient", "ssl connect need to close[" + this.recoverField_h + ":" + this.recoverField_i + "]"
               );
               return -1;
            default:
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_13462e(11);
               throw new IllegalStateException("Invalid SSL status: " + var4.getStatus());
         }
      }

      return 0;
   }

   @Override
   public synchronized int recoverMethod_a_25e06ef(byte[] var1) {
      if (var1 == null) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NioSSLSocketClient", "sendData failed, send data was null");
         return 103;
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NioSSLSocketClient", "send data length:" + var1.length);
         if (var1.length >= this.recoverField_p) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NioSSLSocketClient", "sendData failed, data length must less than " + this.recoverField_p);
            return 6026;
         } else {
            return this.recoverMethod_b_25e0700(var1) ? 0 : 103;
         }
      }
   }

   private boolean recoverMethod_b_25e0700(byte[] var1) {
      try {
         if (!this.recoverMethod_b_9b79()) {
            RecoverTypeaf2a9ae9.recoverMethod_k_e925fa09("NioSSLSocketClient", "send error - connect was invalid");
            return false;
         }

         if (var1 != null && var1.length > 0) {
            return this.recoverMethod_c_25e0700(var1);
         }

         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NioSSLSocketClient", "send error - invalide buffer");
         return false;
      } catch (Exception var3) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("NioSSLSocketClient", "send data error:" + var3.getMessage());
         this.close();
         return false;
      }
   }

   private boolean recoverMethod_c_25e0700(byte[] var1) throws SSLException, java.io.IOException, java.lang.Exception {
      ((Buffer)this.recoverField_l).clear();
      this.recoverField_l.put(var1);
      ((Buffer)this.recoverField_l).flip();

      while (this.recoverField_l.hasRemaining()) {
         ((Buffer)this.recoverField_m).clear();
         SSLEngineResult var2 = this.recoverField_k.wrap(this.recoverField_l, this.recoverField_m);
         switch (var2.getStatus()) {
            case OK:
               ((Buffer)this.recoverField_m).flip();
               int var3 = 0;

               while (this.recoverField_m.hasRemaining()) {
                  var3 += this.recoverField_b.write(this.recoverField_m);
               }

               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
                  "NioSSLSocketClient", "send data to server, writeLen: " + var3 + ", isRemaining: " + this.recoverField_l.hasRemaining()
               );
               break;
            case BUFFER_OVERFLOW:
               this.recoverField_m = this.recoverMethod_a_dd4acd86(this.recoverField_k, this.recoverField_m);
               break;
            case BUFFER_UNDERFLOW:
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_13462e(13);
               throw new SSLException("Buffer underflow occured after a wrap. I don't think we should ever get here.");
            case CLOSED:
               throw new Exception("connect close");
            default:
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_13462e(11);
               throw new IllegalStateException("Invalid SSL status: " + var2.getStatus());
         }
      }

      return true;
   }

   @Override
   public void close() {
      RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("NioSSLSocketClient", "close this connect...");
      super.close();
      if (this.recoverField_l != null) {
         ((Buffer)this.recoverField_l).clear();
      }

      if (this.recoverField_m != null) {
         ((Buffer)this.recoverField_m).clear();
      }

      if (this.recoverField_o != null) {
         ((Buffer)this.recoverField_o).clear();
      }

      if (this.recoverField_n != null) {
         ((Buffer)this.recoverField_n).clear();
      }

      if (this.recoverField_d != null) {
         try {
            this.recoverField_d.close();
         } catch (Exception var2) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NioSSLSocketClient", "selector close error：" + var2.getMessage());
         }
      }

      RecoverTypeaf2b07d7.recoverMethod_a_d17885d6(this.recoverField_b);
      this.recoverField_b = null;
      RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("NioSSLSocketClient", "tcp has closed");
   }

   private boolean recoverMethod_a_f687e3f7(SocketChannel var1) throws java.nio.channels.ClosedChannelException, SSLException, java.io.IOException {
      long var2 = System.currentTimeMillis();
      this.recoverField_k.beginHandshake();
      HandshakeStatus var4 = this.recoverField_k.getHandshakeStatus();

      while (this.recoverField_e) {
         if (System.currentTimeMillis() - var2 > 15000L) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NioSSLSocketClient", "ssl hand shake time out");
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_13462e(14);
            return false;
         }

         switch (var4) {
            case FINISHED:
            default:
               break;
            case NEED_TASK:
               var4 = this.recoverMethod_e_a4529584();
               break;
            case NEED_UNWRAP:
               int var5 = var1.read(this.recoverField_n);
               if (var5 > 0) {
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NioSSLSocketClient", "NEED_UNWRAP read:" + this.recoverField_n.toString());
               }

               ((Buffer)this.recoverField_n).flip();
               var4 = this.recoverMethod_f_a4529584();
               break;
            case NEED_WRAP:
               var4 = this.recoverMethod_g_a4529584();
               var1.write(this.recoverField_m);
               ((Buffer)this.recoverField_m).clear();
               break;
            case NOT_HANDSHAKING:
               var1.configureBlocking(false);
               var1.register(this.recoverField_d, 1);
               return true;
         }
      }

      return false;
   }

   private HandshakeStatus recoverMethod_e_a4529584() {
      Runnable var1;
      while (this.recoverField_e && (var1 = this.recoverField_k.getDelegatedTask()) != null) {
         RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("NioSSLSocketClient", "running delegated task...");
         var1.run();
      }

      HandshakeStatus var2 = this.recoverField_k.getHandshakeStatus();
      if (var2 == HandshakeStatus.NEED_TASK) {
         RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("NioSSLSocketClient", "handshake shouldn't need additional tasks");
      }

      return var2;
   }

   private HandshakeStatus recoverMethod_f_a4529584() throws SSLException {
      HandshakeStatus var1;
      SSLEngineResult var2;
      do {
         var2 = this.recoverField_k.unwrap(this.recoverField_n, this.recoverField_o);
         var1 = this.recoverMethod_e_a4529584();
      } while (var2.getStatus() != Status.BUFFER_UNDERFLOW && this.recoverField_e && var1 == HandshakeStatus.NEED_UNWRAP && this.recoverField_n.remaining() > 0);

      this.recoverField_n.compact();
      return var1;
   }

   private HandshakeStatus recoverMethod_g_a4529584() throws SSLException {
      ByteBuffer var2 = ByteBuffer.wrap("".getBytes());
      this.recoverField_k.wrap(var2, this.recoverField_m);
      HandshakeStatus var1 = this.recoverMethod_e_a4529584();
      ((Buffer)this.recoverField_m).flip();
      return var1;
   }

   protected boolean recoverMethod_d_9b79() {
      try {
         this.recoverField_j = SSLContext.getInstance("SSL");
         this.recoverField_j.init(null, new TrustManager[]{new SSLTrustManager(RecoverTypeaf2a972c.recoverMethod_a_73cf11cb())}, new SecureRandom());
         this.recoverField_k = this.recoverField_j.createSSLEngine();
         this.recoverField_k.setUseClientMode(true);
         SSLSession var1 = this.recoverField_k.getSession();
         int var2 = var1.getApplicationBufferSize();
         int var3 = var1.getPacketBufferSize();
         this.recoverField_o = ByteBuffer.allocate(var2 + 10);
         this.recoverField_n = ByteBuffer.allocate(var3);
         this.recoverField_l = ByteBuffer.allocate(var3);
         this.recoverField_m = ByteBuffer.allocate(var3);
         RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09(
            "NioSSLSocketClient", "application size:" + var1.getApplicationBufferSize() + ",package size:" + var1.getPacketBufferSize()
         );
         return true;
      } catch (Throwable var4) {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_13462e(1);
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09(
            "NioSSLSocketClient", "NioSSLSocketClient create ssl error, cur time is:" + System.currentTimeMillis() + ",error:" + var4
         );
         return false;
      }
   }

   private ByteBuffer recoverMethod_a_dd4acd86(SSLEngine var1, ByteBuffer var2) {
      return this.recoverMethod_a_c5a68148(var2, var1.getSession().getPacketBufferSize());
   }

   private ByteBuffer recoverMethod_b_dd4acd86(SSLEngine var1, ByteBuffer var2) {
      return this.recoverMethod_a_c5a68148(var2, var1.getSession().getApplicationBufferSize());
   }

   private ByteBuffer recoverMethod_a_c5a68148(ByteBuffer var1, int var2) {
      ByteBuffer var3;
      if (var2 > var1.capacity()) {
         var3 = ByteBuffer.allocate(var2);
      } else {
         var3 = ByteBuffer.allocate(var1.capacity() * 2);
      }

      return var3;
   }

   private ByteBuffer recoverMethod_c_dd4acd86(SSLEngine var1, ByteBuffer var2) {
      if (var1.getSession().getPacketBufferSize() < var2.limit()) {
         return var2;
      }

      ByteBuffer var3 = this.recoverMethod_a_dd4acd86(var1, var2);
      ((Buffer)var2).flip();
      var3.put(var2);
      return var3;
   }
}
