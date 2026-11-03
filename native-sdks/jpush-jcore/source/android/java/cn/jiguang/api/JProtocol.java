package cn.jiguang.api;

import cn.jiguang.api.utils.ProtocolUtil;
import cn.jiguang.bi.RecoverTypeaf2b0041;
import cn.jiguang.bi.RecoverTypeaf2b0042;
import cn.jiguang.bi.RecoverTypeaf2b0044;
import java.io.ByteArrayOutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public abstract class JProtocol {
   private static final String TAG = "JProtocol";
   private static final int PACKET_SIZE = 7168;
   public static final int NO_SID = -1;
   public static final int NO_RESP_CODE = -1;
   public static final int NO_JUID = -1;
   public static final int DEFAULT_RID = 0;
   public static final int DEFAULT_SID = 0;
   public static final int DEFAULT_RESP_CODE = 0;
   private boolean isRequest;
   protected RecoverTypeaf2b0041 head;
   protected ByteBuffer body;

   public JProtocol(boolean var1, Object var2, ByteBuffer var3) {
      this.isRequest = var1;
      this.head = (RecoverTypeaf2b0041)var2;
      if (null != var3) {
         this.body = var3;
         this.parseBody();
      } else {
         RecoverTypeaf2b0042.recoverMethod_c_e925fa09("JProtocol", "No body to parse.");
      }
   }

   public JProtocol(boolean var1, ByteBuffer var2, byte[] var3) {
      this.isRequest = var1;

      try {
         this.head = new RecoverTypeaf2b0041(var1, var3);
      } catch (Exception var5) {
         RecoverTypeaf2b0042.recoverMethod_c_e925fa09("JProtocol", "create JHead failed:" + var5.getMessage());
      }

      if (null != var2) {
         this.body = var2;
         this.parseBody();
      } else {
         RecoverTypeaf2b0042.recoverMethod_c_e925fa09("JProtocol", "No body to parse.");
      }
   }

   public static byte[] parseHead(Object var0) {
      if (var0 == null) {
         RecoverTypeaf2b0042.recoverMethod_c_e925fa09("JProtocol", "object was null");
         return null;
      }

      if (var0 instanceof RecoverTypeaf2b0041) {
         return ((RecoverTypeaf2b0041)var0).recoverMethod_f_12d408();
      }

      RecoverTypeaf2b0042.recoverMethod_c_e925fa09("JProtocol", "unknow Object");
      return null;
   }

   public JProtocol(boolean var1, int var2, int var3, long var4) {
      this.isRequest = var1;
      this.head = new RecoverTypeaf2b0041(var1, var2, var3, var4);
      this.body = ByteBuffer.allocate(7168);
   }

   public JProtocol(boolean var1, int var2, int var3, long var4, int var6, long var7) {
      this.isRequest = var1;
      this.head = new RecoverTypeaf2b0041(var1, 0, var2, var3, var4, var6, var7);
      this.body = ByteBuffer.allocate(7168);
   }

   public int getCommand() {
      return this.head.recoverMethod_a_9b68();
   }

   public Long getRid() {
      return this.head.recoverMethod_b_10c66500();
   }

   public long getJuid() {
      return this.head.recoverMethod_c_9b69();
   }

   public int getSid() {
      return this.head.recoverMethod_d_9b68();
   }

   public int getVersion() {
      return this.head.recoverMethod_e_9b68();
   }

   public RecoverTypeaf2b0041 getHead() {
      return this.head;
   }

   public ByteBuffer getBody() {
      return this.body;
   }

   public abstract String getName();

   protected abstract void parseBody();

   protected abstract void writeBody();

   protected abstract boolean isNeedParseeErrorMsg();

   protected void writeBytes(byte[] var1) {
      this.body.put(var1);
   }

   protected void writeInt1(int var1) {
      this.body.put((byte)var1);
   }

   protected void writeInt2(int var1) {
      this.body.putShort((short)var1);
   }

   protected void writeInt4(int var1) {
      this.body.putInt(var1);
   }

   protected void writeLong8(long var1) {
      this.body.putLong(var1);
   }

   protected void writeTlv2(String var1) {
      this.body.put(ProtocolUtil.tlv2ToByteArray(var1));
   }

   public final byte[] writeBodyAndToBytes() {
      ((Buffer)this.body).clear();
      this.writeBody();
      ((Buffer)this.body).flip();
      return this.toBytes();
   }

   private final byte[] toBytes() {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();
      byte[] var2 = ProtocolUtil.getBytes(this.body);
      if (var2 == null) {
         RecoverTypeaf2b0042.recoverMethod_c_e925fa09("JProtocol", "toBytes bodyBytes  is  null");
         return null;
      }

      this.head.recoverMethod_a_13462e((this.isRequest ? 24 : 20) + var2.length);

      try {
         var1.write(this.head.recoverMethod_f_12d408());
         var1.write(var2);
      } catch (Exception var4) {
      }

      byte[] var3 = var1.toByteArray();
      RecoverTypeaf2b0042.recoverMethod_b_e925fa09("JProtocol", "Final - len:" + var3.length + ", bytes: " + RecoverTypeaf2b0044.recoverMethod_a_79834524(var3));
      return var3;
   }

   @Override
   public String toString() {
      return (this.isRequest ? "[Request]" : "[Response]") + " - " + this.head.toString();
   }
}
