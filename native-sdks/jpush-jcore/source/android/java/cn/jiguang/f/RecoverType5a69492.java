package cn.jiguang.f;

import android.content.Context;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.at.RecoverTypeaf2ab52c;
import org.json.JSONObject;

public class RecoverType5a69492 {
   private static long recoverField_a = 0L;

   public static void recoverMethod_a_8f0208c4(Context var0, int var1, String var2, JSONObject var3) {
      recoverMethod_b_8f0208c4(var0, var1, var2, var3);
   }

   public static void recoverMethod_b_8f0208c4(Context var0, int var1, String var2, JSONObject var3) {
      if (null == var0) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JAnalytics", "reportPushAnylysis NULL context");
      } else {
         try {
            if (RecoverType5a681cd.recoverMethod_a_9b79()) {
               RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_a_4abe089d(var0, 2008, var1, "");
            } else {
               RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_a_4abe089d(var0, 2009, var1, "");
            }
         } catch (Throwable var5) {
         }
      }
   }

   public static void recoverMethod_a_1a54e370(Context var0, int var1) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JAnalytics", "sendStartAPP:" + var1);
      long var2 = System.currentTimeMillis();
      if (var2 - recoverField_a < 2000L) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JAnalytics", "sendStartAPP return:" + var1);
      } else {
         recoverField_a = var2;
         JSONObject var4 = new JSONObject();

         try {
            var4.put("type", var1);
         } catch (Throwable var6) {
         }

         recoverMethod_a_8f0208c4(var0, 2021, "Start the APP", var4);
      }
   }
}
