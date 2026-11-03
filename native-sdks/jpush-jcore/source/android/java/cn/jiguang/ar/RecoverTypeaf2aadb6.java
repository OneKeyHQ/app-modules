package cn.jiguang.ar;

import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecoverTypeaf2aadb6 {
   public int recoverField_a;
   public String recoverField_b;
   public String recoverField_c;
   public long recoverField_d;
   public String recoverField_e;
   public double recoverField_f;
   public double recoverField_g;
   public long recoverField_h;
   private int recoverField_i = 0;
   private int recoverField_j = 0;
   private int recoverField_k = 0;

   public RecoverTypeaf2aadb6(int var1, String var2, String var3, long var4, String var6, double var7, double var9, long var11) {
      this.recoverField_a = var1;
      this.recoverField_b = var2;
      this.recoverField_c = var3;
      this.recoverField_d = var4;
      this.recoverField_e = var6;
      this.recoverField_f = var7;
      this.recoverField_g = var9;
      this.recoverField_h = var11;
   }

   public RecoverTypeaf2aadb6 recoverMethod_a_7ad58769(int var1) {
      this.recoverField_k = var1;
      return this;
   }

   public JSONObject recoverMethod_a_6b240298(Set var1) {
      JSONObject var2 = new JSONObject();

      try {
         var2.put("type", this.recoverField_a);
         var2.put("appkey", this.recoverField_b);
         var2.put("sdkver", this.recoverField_c);
         var2.put("platform", 0);
         if (this.recoverField_d != 0L) {
            var2.put("uid", this.recoverField_d);
         }

         if (this.recoverField_e != null) {
            var2.put("opera", this.recoverField_e);
         }

         if (recoverMethod_a_25399f9(this.recoverField_f, this.recoverField_g)) {
            var2.put("lat", this.recoverField_f);
            var2.put("lng", this.recoverField_g);
            var2.put("time", this.recoverField_h);
         }

         if (var1 != null && !var1.isEmpty()) {
            JSONArray var3 = new JSONArray();

            for (Object recoveryElement_62 : var1) {
               String var5 = (String)recoveryElement_62;
               var3.put(var5);
            }

            var2.put("fail_ips", var3);
         }

         if (this.recoverField_i != 0) {
            var2.put("ips_flag", this.recoverField_i);
         }

         if (this.recoverField_j != 0) {
            var2.put("report_flag", this.recoverField_j);
         }

         if (this.recoverField_k >= 0) {
            var2.put("cert_ver", this.recoverField_k == 0 ? 1 : this.recoverField_k);
         }
      } catch (JSONException var6) {
      }

      return var2;
   }

   private static boolean recoverMethod_a_25399f9(double var0, double var2) {
      return var0 > -90.0 && var0 < 90.0 && var2 > -180.0 && var2 < 180.0;
   }
}
