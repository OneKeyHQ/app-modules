package cn.jiguang.commons.b;

import android.os.Bundle;
import cn.jiguang.api.JCoreManager;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONException;
import org.json.JSONObject;

public class RecoverType529188e8 {
   public static void recoverMethod_u_aad3b1ff(String var0) {
      JCoreManager.onEvent(null, null, 133, null, null, var0);
   }

   public static Object recoverMethod_h_bb4ac56f(String var0, String var1, Map var2, byte[] var3) {
      Bundle var4 = new Bundle();
      var4.putString("m", var0);
      var4.putString("u", var1);
      var4.putString("h", var2 != null ? convertMapToString(var2).toString() : null);
      var4.putByteArray("b", var3);
      return JCoreManager.onEvent(null, null, 134, null, var4);
   }

   public static void recoverMethod_c_aad3b1ff(String var0) {
      try {
         JCoreManager.onEvent(null, null, 135, null, null, var0);
      } catch (Exception var2) {
      }
   }

   public static void recoverMethod_r_13462e(int var0) {
      try {
         JCoreManager.onEvent(null, null, 136, null, null, var0);
      } catch (Exception var2) {
      }
   }

   public static String convertMapToString(Map var0) {
      if (var0 != null && var0.size() > 0) {
         JSONObject var1 = new JSONObject();

         try {
            for (Object recoveryElement_43 : var0.entrySet()) {
               Entry var3 = (Entry)recoveryElement_43;
               var1.put((String)var3.getKey(), var3.getValue());
            }

            return var1.toString();
         } catch (JSONException var4) {
            return null;
         }
      } else {
         return null;
      }
   }
}
