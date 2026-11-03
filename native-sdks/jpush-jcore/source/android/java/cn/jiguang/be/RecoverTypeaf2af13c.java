package cn.jiguang.be;

import android.content.Context;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import org.json.JSONObject;

public class RecoverTypeaf2af13c extends RecoverTypeaf2af145 {
   private int recoverField_a;
   private String recoverField_b = "";
   private String recoverField_c = "";
   private int recoverField_d;

   public void recoverMethod_b_aad3b1ff(String var1) {
      this.recoverField_b = var1;
   }

   public void recoverMethod_c_aad3b1ff(String var1) {
      this.recoverField_c = var1;
   }

   public void recoverMethod_b_13462e(int var1) {
      this.recoverField_d = var1;
   }

   public RecoverTypeaf2af13c(Context var1, String var2) {
      super(var1, var2);
      this.recoverField_a = RecoverTypeaf2aa628.recoverField_b.get() ? 0 : 1;
   }

   @Override
   JSONObject recoverMethod_d_a7dbfe3() throws org.json.JSONException {
      JSONObject var1 = new JSONObject();
      var1.put("is_foreground", this.recoverField_a);
      var1.put("tcp_id", this.recoverField_b);
      var1.put("host", this.recoverField_c);
      var1.put("port", this.recoverField_d);
      return var1;
   }
}
