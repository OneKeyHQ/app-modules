package cn.jiguang.ar;

import android.text.TextUtils;
import java.util.LinkedList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecoverTypeaf2aadb5 {
   public String recoverField_a;
   public int recoverField_b;
   public RecoverTypeaf2aadaf recoverField_c;
   public long recoverField_d;
   public long recoverField_e;
   public long recoverField_f;
   public int recoverField_g;
   public double recoverField_h;
   public double recoverField_i;
   public long recoverField_j;
   public int recoverField_k;

   public JSONObject recoverMethod_a_a7dbfe3() {
      JSONObject var1 = new JSONObject();

      try {
         if (!TextUtils.isEmpty(this.recoverField_a)) {
            var1.put("appkey", this.recoverField_a);
         }

         var1.put("type", this.recoverField_b);
         var1.put("addr", this.recoverField_c.toString());
         var1.put("rtime", this.recoverField_e);
         var1.put("interval", this.recoverField_f);
         var1.put("net", this.recoverField_g);
         var1.put("code", this.recoverField_k);
         if (this.recoverField_d != 0L) {
            var1.put("uid", this.recoverField_d);
         }

         if (recoverMethod_a_25399f9(this.recoverField_h, this.recoverField_i)) {
            var1.put("lat", this.recoverField_h);
            var1.put("lng", this.recoverField_i);
            var1.put("ltime", this.recoverField_j);
         }
      } catch (JSONException var3) {
      }

      return var1;
   }

   public static RecoverTypeaf2aadb5 recoverMethod_a_16122c85(JSONObject var0) {
      if (var0 != null && var0.length() != 0) {
         try {
            RecoverTypeaf2aadb5 var1 = new RecoverTypeaf2aadb5();
            var1.recoverField_a = var0.optString("appkey");
            var1.recoverField_b = var0.getInt("type");
            var1.recoverField_c = RecoverTypeaf2aadaf.recoverMethod_a_800e3061(var0.getString("addr"));
            var1.recoverField_e = var0.getLong("rtime");
            var1.recoverField_f = var0.getLong("interval");
            var1.recoverField_g = var0.getInt("net");
            var1.recoverField_k = var0.getInt("code");
            var1.recoverField_d = var0.optLong("uid");
            var1.recoverField_h = var0.optDouble("lat");
            var1.recoverField_i = var0.optDouble("lng");
            var1.recoverField_j = var0.optLong("ltime");
            return var1;
         } catch (JSONException var2) {
            return null;
         }
      } else {
         return null;
      }
   }

   public static LinkedList recoverMethod_a_3e0e9f8f(String var0) {
      LinkedList var1 = new LinkedList();
      if (!TextUtils.isEmpty(var0)) {
         try {
            JSONArray var2 = new JSONArray(var0);

            for (int var3 = 0; var3 < var2.length(); var3++) {
               JSONObject var4 = var2.getJSONObject(var3);
               var1.add(recoverMethod_a_16122c85(var4));
            }
         } catch (JSONException var5) {
         }
      }

      return var1;
   }

   private static boolean recoverMethod_a_25399f9(double var0, double var2) {
      return var0 > -90.0 && var0 < 90.0 && var2 > -180.0 && var2 < 180.0;
   }
}
