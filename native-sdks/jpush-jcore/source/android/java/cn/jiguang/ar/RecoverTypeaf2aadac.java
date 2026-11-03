package cn.jiguang.ar;

import android.text.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class RecoverTypeaf2aadac {
   final RecoverTypeaf2aadaf recoverField_a;
   int recoverField_b;
   long recoverField_c;
   long recoverField_d;
   int recoverField_e;

   public RecoverTypeaf2aadac(RecoverTypeaf2aadaf var1) {
      this.recoverField_a = var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof RecoverTypeaf2aadac)) {
         return false;
      } else {
         RecoverTypeaf2aadac var2 = (RecoverTypeaf2aadac)var1;
         if (this.recoverField_b != var2.recoverField_b) {
            return false;
         } else if (this.recoverField_c != var2.recoverField_c) {
            return false;
         } else if (this.recoverField_d != var2.recoverField_d) {
            return false;
         } else if (this.recoverField_e != var2.recoverField_e) {
            return false;
         } else {
            return this.recoverField_a != null ? this.recoverField_a.equals(var2.recoverField_a) : var2.recoverField_a == null;
         }
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.recoverField_a != null ? this.recoverField_a.hashCode() : 0;
      var1 = 31 * var1 + this.recoverField_b;
      var1 = 31 * var1 + (int)(this.recoverField_c ^ this.recoverField_c >>> 32);
      var1 = 31 * var1 + (int)(this.recoverField_d ^ this.recoverField_d >>> 32);
      return 31 * var1 + this.recoverField_e;
   }

   public static RecoverTypeaf2aadac recoverMethod_a_800e3004(String var0) {
      if (TextUtils.isEmpty(var0)) {
         return null;
      }

      try {
         JSONObject var1 = new JSONObject(var0);
         String var2 = var1.getString("ip");
         int var3 = var1.getInt("port");
         RecoverTypeaf2aadaf var4 = new RecoverTypeaf2aadaf(var2, var3);
         RecoverTypeaf2aadac var5 = new RecoverTypeaf2aadac(var4);
         var5.recoverField_b = var1.optInt("status");
         var5.recoverField_c = var1.optLong("fetch_time");
         var5.recoverField_d = var1.optLong("cost");
         var5.recoverField_e = var1.optInt("prefer");
         return var5;
      } catch (JSONException var6) {
         return null;
      }
   }

   public String recoverMethod_a_73cf11cb() {
      try {
         JSONObject var1 = new JSONObject();
         var1.put("ip", this.recoverField_a.recoverField_a);
         var1.put("port", this.recoverField_a.recoverField_b);
         var1.put("status", this.recoverField_b);
         var1.put("fetch_time", this.recoverField_c);
         var1.put("cost", this.recoverField_d);
         var1.put("prefer", this.recoverField_e);
         return var1.toString();
      } catch (JSONException var2) {
         return null;
      }
   }

   @Override
   public String toString() {
      return "IpInfo{ipPort="
         + this.recoverField_a
         + ", status="
         + this.recoverField_b
         + ", fetchTime="
         + this.recoverField_c
         + ", cost="
         + this.recoverField_d
         + ", prefer="
         + this.recoverField_e
         + '}';
   }
}
