package cn.jiguang.bk;

import android.content.Context;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.l.RecoverType5a6ab18;
import org.json.JSONException;
import org.json.JSONObject;

public class RecoverTypeaf2b07c5 {
   private static String recoverField_a = "";
   private static String recoverField_b = "";

   public static JSONObject recoverMethod_a_16ce4761(Context var0) {
      if (var0 == null) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDeviceHelper", "when getDInfo, context can't be null");
         return null;
      }

      try {
         JSONObject var1 = new JSONObject();
         String var2 = RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_k_e501eb0d(var0);
         String var3 = RecoverType5a6ab18.recoverMethod_a_e501eb0d(var0);
         var1.put("resolution", TextUtils.isEmpty(var2) ? "" : var2);
         var1.put("screensize", TextUtils.isEmpty(var3) ? "" : var3);
         var1.put("os_version", RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_z_e501eb0d(var0));
         var1.put("model", RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_u_e501eb0d(var0));
         var1.put("brand", RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_v_e501eb0d(var0));
         var1.put("product", RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_p_e501eb0d(var0));
         var1.put("language", RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_w_e501eb0d(var0));
         var1.put("manufacturer", RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_x_e501eb0d(var0));
         var1.put("timezone", RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_y_e501eb0d(var0));
         return var1;
      } catch (JSONException var4) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDeviceHelper", "package json exception: " + var4.getMessage());
      } catch (Throwable var5) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDeviceHelper", "getDInfo exception: " + var5.getMessage());
      }

      return null;
   }
}
