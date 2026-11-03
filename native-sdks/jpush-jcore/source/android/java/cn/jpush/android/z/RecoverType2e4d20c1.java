package cn.jpush.android.z;

import cn.jpush.android.helper.Logger;
import cn.jpush.android.local.JPushResponse;
import java.nio.ByteBuffer;

public class RecoverType2e4d20c1 extends JPushResponse {
   public int recoverField_a;
   String recoverField_b;

   public RecoverType2e4d20c1(int var1, long var2, long var4, ByteBuffer var6) {
      super(var1, var2, var4, var6);
      this.parseBody();
   }

   public RecoverType2e4d20c1(JPushResponse var1) {
      this(var1.getCmd(), var1.getRid(), var1.rquestId, var1.getBody());
   }

   @Override
   protected void parseBody() {
      try {
         if (this.cmd == 10 || this.cmd == 38) {
            this.recoverField_a = this.body.getShort();
         }

         if (this.cmd != 38 && this.recoverField_a > 0) {
            Logger.recoverMethod_e_e925fa09("TagaliasResponse", "Response error - code:" + this.recoverField_a);
            return;
         }

         short var1 = this.body.getShort();
         byte[] var2 = new byte[var1];
         this.body.get(var2);
         this.recoverField_b = new String(var2, "UTF-8");
      } catch (Throwable var3) {
         Logger.recoverMethod_ww_e925fa09("TagaliasResponse", "parse failed:" + var3.getMessage());
      }
   }

   public String recoverMethod_a_73cf11cb() {
      return this.recoverField_b;
   }

   @Override
   public String toString() {
      return "[TagaliasResponse] - action:" + this.recoverField_b + " - " + super.toString();
   }
}
