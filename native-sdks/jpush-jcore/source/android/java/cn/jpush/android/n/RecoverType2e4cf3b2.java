package cn.jpush.android.n;

import cn.jpush.android.helper.Logger;
import cn.jpush.android.local.JPushResponse;
import java.nio.ByteBuffer;

public class RecoverType2e4cf3b2 extends JPushResponse {
   private int recoverField_a = 0;

   public RecoverType2e4cf3b2(int var1, long var2, long var4, ByteBuffer var6) {
      super(var1, var2, var4, var6);
      this.parseBody();
   }

   public RecoverType2e4cf3b2(JPushResponse var1) {
      this(var1.getCmd(), var1.getRid(), var1.rquestId, var1.getBody());
   }

   @Override
   protected void parseBody() {
      try {
         if (null != this.body) {
            this.recoverField_a = this.body.get();
            Logger.recoverMethod_ww_e925fa09("GeoControl", "control type:" + this.recoverField_a);
         }
      } catch (Throwable var2) {
         Logger.recoverMethod_ww_e925fa09("GeoControl", "parse geoContorl failed");
      }
   }

   public int recoverMethod_a_9b68() {
      return this.recoverField_a;
   }

   @Override
   public String toString() {
      return "[GeoControl] - type:" + this.recoverField_a + " - " + super.toString();
   }
}
