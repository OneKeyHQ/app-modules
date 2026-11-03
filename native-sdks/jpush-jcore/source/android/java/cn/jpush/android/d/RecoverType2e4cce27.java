package cn.jpush.android.d;

import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;

public class RecoverType2e4cce27 implements Serializable {
   private static final long serialVersionUID = -942487107643335186L;
   public String recoverField_a;
   public String recoverField_b;
   public int recoverField_c = -1;

   public RecoverType2e4cce27(RecoverType2e4cce2a var1) {
      this.recoverField_a = var1.recoverField_d;
      this.recoverField_b = var1.recoverField_h;
      this.recoverField_c = var1.recoverField_a;
   }

   public RecoverType2e4cce27() {
   }

   public RecoverType2e4cce27(String var1, String var2, int var3) {
      this.recoverField_a = var1;
      this.recoverField_b = var2;
      this.recoverField_c = var3;
   }

   public static RecoverType2e4cce27 recoverMethod_a_ef8016f5(JSONObject var0) {
      String var1 = var0.optString("msg_id");
      String var2 = var0.optString("override_msg_id");
      int var3 = var0.optInt("msg_type", -1);
      return new RecoverType2e4cce27(var1, var2, var3);
   }

   public JSONObject recoverMethod_a_a7dbfe3() {
      JSONObject var1 = new JSONObject();

      try {
         var1.put("msg_id", this.recoverField_a);
         var1.put("override_msg_id", this.recoverField_b);
         var1.put("msg_type", this.recoverField_c);
      } catch (JSONException var3) {
      }

      return var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof RecoverType2e4cce27)) {
         return false;
      } else {
         RecoverType2e4cce27 var2 = (RecoverType2e4cce27)var1;
         if (!recoverMethod_a_e925fa0d(this.recoverField_a, var2.recoverField_a)) {
            return false;
         } else {
            return this.recoverField_c != -1 && this.recoverField_c != var2.recoverField_c
               ? false
               : recoverMethod_a_e925fa0d(this.recoverField_b, var2.recoverField_b);
         }
      }
   }

   private static boolean recoverMethod_a_e925fa0d(String var0, String var1) {
      return var0 == null && var1 == null || var0 != null && var0.equals(var1);
   }

   @Override
   public String toString() {
      return "EntityKey{msg_id='" + this.recoverField_a + '\'' + ", override_msg_id='" + this.recoverField_b + '\'' + ", msgType=" + this.recoverField_c + '}';
   }
}
