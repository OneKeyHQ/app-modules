package cn.jiguang.ar;

import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import java.util.LinkedHashSet;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecoverTypeaf2aadb4 {
   LinkedHashSet recoverField_a;
   LinkedHashSet recoverField_b;
   LinkedHashSet recoverField_c;
   LinkedHashSet recoverField_d;
   LinkedHashSet recoverField_e;
   JSONObject recoverField_f;
   transient RecoverTypeaf2aadaf recoverField_g;
   boolean recoverField_h;
   int recoverField_i;

   RecoverTypeaf2aadb4(String var1) {
      try {
         if (!TextUtils.isEmpty(var1)) {
            JSONObject var2 = new JSONObject(var1);
            this.recoverField_a = this.recoverMethod_a_878541c9(var2, "ips");
            this.recoverField_b = this.recoverMethod_a_878541c9(var2, "ssl_ips");
            this.recoverField_c = this.recoverMethod_a_878541c9(var2, "http_report");
            this.recoverField_d = this.recoverMethod_a_878541c9(var2, "https_report");
            this.recoverField_e = this.recoverMethod_a_878541c9(var2, "sis_ips");
            this.recoverField_h = var2.optBoolean("data_report");
            this.recoverField_f = var2.optJSONObject("tcp_report");
            this.recoverField_i = var2.optInt("cert_ver", 0);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("sis", "get sis=" + var2.toString(2));
         }
      } catch (Throwable var3) {
      }
   }

   public boolean recoverMethod_a_9b79() {
      return (this.recoverField_a == null || this.recoverField_a.isEmpty()) && (this.recoverField_b == null || this.recoverField_b.isEmpty());
   }

   private LinkedHashSet recoverMethod_a_878541c9(JSONObject var1, String var2) {
      LinkedHashSet var3 = new LinkedHashSet();

      try {
         JSONArray var4 = var1.optJSONArray(var2);
         if (var4 != null) {
            for (int var5 = 0; var5 < var4.length(); var5++) {
               String var6 = var4.optString(var5, null);
               RecoverTypeaf2aadaf var7 = RecoverTypeaf2aadaf.recoverMethod_a_800e3061(var6);
               if (var7 != null && var7.recoverMethod_a_9b79()) {
                  var3.add(var7);
               }
            }
         }
      } catch (Throwable var8) {
      }

      return var3;
   }
}
