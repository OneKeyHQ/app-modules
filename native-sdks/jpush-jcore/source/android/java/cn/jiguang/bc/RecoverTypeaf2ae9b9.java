package cn.jiguang.bc;

import android.content.Context;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.d.RecoverType5a68d11;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class RecoverTypeaf2ae9b9 {
   private String recoverField_a;
   private static String recoverField_b = UUID.randomUUID().toString();
   private String recoverField_c;
   private long recoverField_d;

   public RecoverTypeaf2ae9b9(Context var1, String var2) {
      this.recoverField_a = var2;
      this.recoverField_c = UUID.randomUUID().toString();
      this.recoverField_d = var1 == null ? System.currentTimeMillis() / 1000L : RecoverType5a68d11.recoverMethod_b_f0554de7(var1);
   }

   public abstract JSONObject recoverMethod_a_a7dbfe3();

   public JSONObject recoverMethod_b_a7dbfe3() {
      JSONObject var1 = this.recoverMethod_a_a7dbfe3();
      if (var1 != null) {
         JSONObject var2 = new JSONObject();

         try {
            var2.put("moniter_type", this.recoverField_a);
            var2.put("uuid", recoverField_b);
            var2.put("moniter_id", this.recoverField_c);
            var2.put("timestamp", this.recoverField_d);
            var2.put("extra", var1);
            return var2;
         } catch (JSONException var4) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("BaseMoniter", "build baseMoniter data error" + var4.getMessage());
         }
      }

      return null;
   }

   public String recoverMethod_c_73cf11cb() {
      return this.recoverField_c;
   }
}
