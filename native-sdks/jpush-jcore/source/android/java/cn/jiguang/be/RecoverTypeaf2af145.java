package cn.jiguang.be;

import android.content.Context;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bc.RecoverTypeaf2ae9b9;
import cn.jiguang.bk.RecoverTypeaf2b07ce;
import cn.jiguang.bk.RecoverTypeaf2b07d6;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class RecoverTypeaf2af145 extends RecoverTypeaf2ae9b9 {
   private String recoverField_a = "unkown";
   private String recoverField_b = "unkown";
   private int recoverField_c;
   private long recoverField_d;
   private int recoverField_e;
   private int recoverField_f;
   private long recoverField_g;
   private long recoverField_h;

   public RecoverTypeaf2af145(Context var1, String var2) {
      super(var1, var2);
      this.recoverField_a = RecoverTypeaf2b07ce.recoverMethod_a_e501eb0d(var1);
      String var3 = RecoverTypeaf2b07d6.recoverMethod_a_5e799b7f(var1, false);
      if (!TextUtils.isEmpty(var3)) {
         this.recoverField_a = var3;
      }
   }

   public void recoverMethod_c_13462e(int var1) {
      this.recoverField_e = var1;
   }

   public void recoverMethod_d_13462e(int var1) {
      this.recoverField_f = var1;
   }

   public void recoverMethod_e_9b75() {
      this.recoverField_g = System.currentTimeMillis();
   }

   public void recoverMethod_f_9b75() {
      this.recoverField_h = System.currentTimeMillis();
   }

   abstract JSONObject recoverMethod_d_a7dbfe3() throws org.json.JSONException;

   @Override
   public JSONObject recoverMethod_a_a7dbfe3() {
      try {
         this.recoverField_d = this.recoverField_h - this.recoverField_g;
         JSONObject var1 = this.recoverMethod_d_a7dbfe3();
         var1.put("network_type", this.recoverField_a);
         var1.put("operate_type", this.recoverField_b);
         var1.put("signal_strength", this.recoverField_c);
         var1.put("cost_time", this.recoverField_d);
         var1.put("error_code", this.recoverField_e);
         var1.put("status_code", this.recoverField_f);
         var1.put("status_code", this.recoverField_f);
         return var1;
      } catch (JSONException var2) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetMoniter", "build netmoniter data error" + var2.getMessage());
         return null;
      }
   }
}
