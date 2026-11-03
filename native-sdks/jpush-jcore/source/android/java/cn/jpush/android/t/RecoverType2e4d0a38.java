package cn.jpush.android.t;

import cn.jpush.android.helper.Logger;
import org.json.JSONObject;

public class RecoverType2e4d0a38 {
   public String recoverField_a;
   public String recoverField_b;
   public long recoverField_c;

   public static RecoverType2e4d0a38 recoverMethod_a_574ffbd2(String var0, String var1, long var2) {
      RecoverType2e4d0a38 var4 = new RecoverType2e4d0a38();
      var4.recoverField_a = var0;
      var4.recoverField_b = var1;
      var4.recoverField_c = var2;
      return var4;
   }

   public static RecoverType2e4d0a38 recoverMethod_a_ef875d04(JSONObject var0) {
      if (null != var0) {
         RecoverType2e4d0a38 var1 = new RecoverType2e4d0a38();
         var1.recoverField_a = var0.optString("tpl_id");
         var1.recoverField_b = var0.optString("tpl_file_name");
         var1.recoverField_c = var0.optLong("tpl_recent_use_time");
         return var1;
      } else {
         return null;
      }
   }

   public JSONObject recoverMethod_a_a7dbfe3() {
      try {
         JSONObject var1 = new JSONObject();
         var1.put("tpl_id", this.recoverField_a);
         var1.put("tpl_file_name", this.recoverField_b);
         var1.put("tpl_recent_use_time", this.recoverField_c);
         return var1;
      } catch (Throwable var2) {
         Logger.recoverMethod_w_e925fa09("InAppTemplate", "notify inapp toJSON failed. " + var2.getMessage());
         return null;
      }
   }
}
