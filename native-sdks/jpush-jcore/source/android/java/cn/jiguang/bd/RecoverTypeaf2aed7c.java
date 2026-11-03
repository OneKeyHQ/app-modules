package cn.jiguang.bd;

import android.content.Context;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bc.RecoverTypeaf2ae9b9;
import org.json.JSONObject;

public class RecoverTypeaf2aed7c extends RecoverTypeaf2ae9b9 {
   private String recoverField_a;

   public RecoverTypeaf2aed7c(Context var1, String var2) {
      super(var1, var2);
   }

   public void recoverMethod_a_aad3b1ff(String var1) {
      this.recoverField_a = var1;
   }

   @Override
   public JSONObject recoverMethod_a_a7dbfe3() {
      JSONObject var1 = new JSONObject();

      try {
         var1.put("sdk_type", this.recoverField_a);
         var1.put("count", 1);
      } catch (Throwable var3) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ExceptMonitor", "build getExtraJson data failed, " + var3.getMessage());
      }

      return var1;
   }

   @Override
   public JSONObject recoverMethod_b_a7dbfe3() {
      return super.recoverMethod_b_a7dbfe3();
   }

   @Override
   public String recoverMethod_c_73cf11cb() {
      return super.recoverMethod_c_73cf11cb();
   }
}
