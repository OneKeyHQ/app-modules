package cn.jiguang.bk;

import java.util.Iterator;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

public class RecoverTypeaf2b07cd {
   public static String recoverMethod_a_13ea1257(JSONObject var0) {
      if (var0 == null) {
         return "null";
      }

      String var1;
      try {
         var1 = var0.toString(2);
      } catch (JSONException var3) {
         var1 = var0.toString();
      }

      return var1;
   }

   public static void recoverMethod_a_e0cf9bf5(JSONObject var0, JSONObject var1) {
      if (var1 != null && var1.length() != 0 && var0 != null) {
         Iterator var2 = var1.keys();

         while (var2.hasNext()) {
            String var3 = (String)var2.next();

            try {
               var0.put(var3, var1.get(var3));
            } catch (JSONException var5) {
            }
         }
      }
   }

   public static int recoverMethod_b_be7fc15c(JSONObject var0) {
      if (var0 != null && var0.length() != 0) {
         try {
            byte[] var1 = RecoverTypeaf2b07d5.recoverMethod_i_afa28ebe(var0.toString());
            return var1.length;
         } catch (Throwable var2) {
            return 0;
         }
      } else {
         return 0;
      }
   }

   public static JSONObject recoverMethod_a_d3ac7824(JSONObject var0, Set var1) {
      JSONObject var2 = new JSONObject();
      if (var1 != null && !var1.isEmpty()) {
         for (Object recoveryElement_55 : var1) {
            String var4 = (String)recoveryElement_55;
            try {
               var2.put(var4, var0.opt(var4));
            } catch (JSONException var6) {
            }
         }

         return var2;
      } else {
         return var2;
      }
   }

   public static boolean recoverMethod_c_be7fc16d(JSONObject var0) {
      return var0 == null || var0.length() == 0;
   }
}
