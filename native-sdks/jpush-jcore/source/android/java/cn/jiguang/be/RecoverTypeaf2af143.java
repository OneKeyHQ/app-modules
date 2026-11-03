package cn.jiguang.be;

import android.content.Context;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bc.RecoverTypeaf2ae9b9;
import org.json.JSONObject;

public class RecoverTypeaf2af143 extends RecoverTypeaf2ae9b9 {
   private String recoverField_a;
   private long recoverField_b;
   private long recoverField_c;
   private long recoverField_d;
   private String recoverField_e;

   public RecoverTypeaf2af143(Context var1) {
      super(var1, "NET_DATA");
   }

   @Override
   public JSONObject recoverMethod_a_a7dbfe3() {
      JSONObject var1 = new JSONObject();

      try {
         var1.put("url", this.recoverField_e);
         var1.put("res", this.recoverField_c);
         var1.put("req", this.recoverField_b);
         var1.put("type", this.recoverField_a);
         var1.put("count", this.recoverField_d);
      } catch (Throwable var3) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("NetDataLengthMonitor", "build getExtraJson data failed, " + var3.getMessage());
      }

      return var1;
   }

   public void recoverMethod_a_aad3b1ff(String var1) {
      this.recoverField_e = var1;
   }

   public void recoverMethod_a_1349ef(long var1) {
      this.recoverField_c = var1;
   }

   public void recoverMethod_b_1349ef(long var1) {
      this.recoverField_b = var1;
   }

   public void recoverMethod_b_aad3b1ff(String var1) {
      this.recoverField_a = var1;
   }

   public void recoverMethod_c_1349ef(long var1) {
      this.recoverField_d = var1;
   }
}
