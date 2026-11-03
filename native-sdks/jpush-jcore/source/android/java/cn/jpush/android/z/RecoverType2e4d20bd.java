package cn.jpush.android.z;

import cn.jpush.android.helper.Logger;
import cn.jpush.android.local.JPushResponse;
import java.nio.ByteBuffer;

public class RecoverType2e4d20bd extends JPushResponse {
   private int recoverField_a;

   public RecoverType2e4d20bd(int var1, long var2, long var4, ByteBuffer var6) {
      super(var1, var2, var4, var6);
      this.parseBody();
   }

   public RecoverType2e4d20bd(JPushResponse var1) {
      this(var1.getCmd(), var1.getRid(), var1.rquestId, var1.getBody());
   }

   @Override
   protected void parseBody() {
      try {
         this.recoverField_a = this.body.getShort();
      } catch (Throwable var2) {
         Logger.recoverMethod_ww_e925fa09("CommonResponse", "parse code failed :" + var2.getMessage());
      }
   }

   public int recoverMethod_a_9b68() {
      return this.recoverField_a;
   }

   @Override
   public String toString() {
      return "[CommonResponse] - " + this.recoverField_a;
   }
}
