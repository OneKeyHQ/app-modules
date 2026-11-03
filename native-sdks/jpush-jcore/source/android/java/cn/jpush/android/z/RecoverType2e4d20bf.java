package cn.jpush.android.z;

import cn.jpush.android.helper.Logger;
import cn.jpush.android.local.JPushResponse;
import java.nio.ByteBuffer;

public class RecoverType2e4d20bf extends JPushResponse {
   int recoverField_a;
   long recoverField_b;
   String recoverField_c;

   public RecoverType2e4d20bf(int var1, long var2, long var4, ByteBuffer var6) {
      super(var1, var2, var4, var6);
      this.parseBody();
   }

   public RecoverType2e4d20bf(JPushResponse var1) {
      this(var1.getCmd(), var1.getRid(), var1.rquestId, var1.getBody());
   }

   @Override
   protected void parseBody() {
      try {
         ByteBuffer var1 = ByteBuffer.wrap(this.body.array());
         this.recoverField_a = var1.get();
         this.recoverField_b = var1.getLong();
         short var2 = var1.getShort();
         byte[] var3 = new byte[var2];
         var1.get(var3);
         this.recoverField_c = new String(var3, "UTF-8");
      } catch (Throwable var4) {
         Logger.recoverMethod_ww_e925fa09("MessagePush", "parse msg content failed, e: " + var4.getMessage());
      }
   }

   public int recoverMethod_a_9b68() {
      return this.recoverField_a;
   }

   public long recoverMethod_b_9b69() {
      return this.recoverField_b;
   }

   public String recoverMethod_c_73cf11cb() {
      return this.recoverField_c;
   }

   @Override
   public String toString() {
      return "[MessagePush] - msgType:"
         + this.recoverField_a
         + ", msgId:"
         + this.recoverField_b
         + ", msgContent:"
         + this.recoverField_c
         + " - "
         + super.toString();
   }
}
