package cn.jpush.android.helper;

import android.content.Context;
import android.text.TextUtils;
import cn.jiguang.api.JCoreManager;
import cn.jpush.android.d.RecoverType2e4cce2a;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecoverType8b455831 {
   public static void recoverMethod_a_3c4029ab(Context var0, JSONObject var1) {
      if (null == var0) {
         throw new IllegalArgumentException("NULL context");
      }

      if (null != var1 && var1.length() > 0) {
         Logger.recoverMethod_d_e925fa09("JPushReportHelper", "action:reportOperation - content:" + var1.toString());
         JCoreManager.onEvent(var0, "JPUSH", 14, null, null, var1);
      }
   }

   public static void recoverMethod_a_36ca9ac3(String var0, long var1, Context var3) {
      recoverMethod_a_d7d77f79(var0, var1, null, var3);
   }

   public static void recoverMethod_a_510e8a7c(Context var0, String var1, long var2, int var4) {
      recoverMethod_a_d7d77f79(var1, RecoverType8b455833.recoverMethod_a_255f64a(var4, var2), null, var0);
   }

   public static void recoverMethod_a_d7d77f79(String var0, long var1, String var3, Context var4) {
      if (null == var4) {
         Logger.recoverMethod_d_e925fa09("JPushReportHelper", "context did not init, return");
      } else {
         StringBuffer var5 = new StringBuffer();
         var5.append("action:reportActionResult - messageId: " + var0 + ", code: " + var1 + "-" + RecoverType8b455833.recoverMethod_a_2f33e691(var1));
         if (!TextUtils.isEmpty(var3)) {
            var5.append("， report content: " + var3);
         }

         String var6 = RecoverType2e4cce2a.recoverMethod_c_e96ea081(var0);
         if (!TextUtils.isEmpty(var6)) {
            var5.append("， report jData:" + var6);
         }

         Logger.recoverMethod_d_e925fa09("JPushReportHelper", var5.toString());
         JSONObject var7 = new JSONObject();

         try {
            var7.put("msg_id", var0);
            var7.put("result", var1);
            if (!TextUtils.isEmpty(var3)) {
               var7.put("data", var3);
            }

            if (!TextUtils.isEmpty(var6)) {
               var7.put("_j_data_", var6);
            }

            var7 = JCoreHelper.fillBase(var4, var7, "msg_status");
            if (null == var7) {
               Logger.recoverMethod_ww_e925fa09("JPushReportHelper", "report msg json is null, code: " + var1);
               return;
            }

            var7.put("type", "msg_status");
            recoverMethod_a_3c4029ab(var4, var7);
         } catch (Throwable var9) {
         }
      }
   }

   public static void recoverMethod_a_2ef42797(String var0, String var1, byte var2, long var3, Context var5) {
      recoverMethod_a_3e8d9d2b(var0, var1, var2, var3, var5, null, null);
   }

   public static void recoverMethod_a_3e8d9d2b(String var0, String var1, byte var2, long var3, Context var5, String var6, String var7) {
      if (null == var5) {
         Logger.recoverMethod_d_e925fa09("JPushReportHelper", "context did not init, return");
      } else {
         StringBuffer var8 = new StringBuffer();
         var8.append("action:reportThirdSDKMsgActionResult - messageId: " + var0 + ", code: " + var3);
         if (!TextUtils.isEmpty(var7)) {
            var8.append("， report content: " + var7);
         }

         String var9 = RecoverType2e4cce2a.recoverMethod_c_e96ea081(var0);
         if (TextUtils.isEmpty(var9) && !TextUtils.isEmpty(var6)) {
            try {
               JSONObject var10 = new JSONObject(var6);
               var9 = var10.optString("_j_data_", "");
               Logger.recoverMethod_d_e925fa09("JPushReportHelper", "get jdata from third push msg:" + var9);
            } catch (Throwable var13) {
               Logger.recoverMethod_d_e925fa09("JPushReportHelper", "get jdata from third push msg error:" + var13.getMessage());
            }
         }

         if (!TextUtils.isEmpty(var9)) {
            var8.append("， report jData:" + var9);
         }

         Logger.recoverMethod_d_e925fa09("JPushReportHelper", var8.toString());
         JSONObject var14 = new JSONObject();

         try {
            var14.put("msg_id", var0);
            var14.put("tmsg_id", var1);
            if (!TextUtils.isEmpty(var7)) {
               var14.put("data", var7);
            }

            var14.put("result", var3);
            var14.put("sdk_type", var2);
            if (!TextUtils.isEmpty(var9)) {
               var14.put("_j_data_", var9);
            }

            var14 = JCoreHelper.fillBase(var5, var14, "third_msg_status");
            if (null == var14) {
               Logger.recoverMethod_ww_e925fa09("JPushReportHelper", "report third sdk msg json is null, code: " + var3);
               return;
            }

            var14.put("type", "third_msg_status");
            recoverMethod_a_3c4029ab(var5, var14);
         } catch (Throwable var12) {
         }
      }
   }

   public static void recoverMethod_a_4ef42769(Context var0, String var1, byte var2, String var3) {
      if (TextUtils.isEmpty(var1)) {
         Logger.recoverMethod_ee_e925fa09("JPushReportHelper", "The msgId is not valid - " + var1);
      } else {
         recoverMethod_a_3e8d9d2b(var1, "", var2, 1000L, var0, var3, null);
      }
   }

   public static boolean recoverMethod_a_f08e8936(Context var0, Map var1) {
      try {
         JSONObject var2 = new JSONObject();
         JCoreHelper.fillBase(var0, var2, "api_record");
         JSONArray var3 = new JSONArray();

         for (Object recoveryElement_146 : var1.entrySet()) {
            Entry var5 = (Entry)recoveryElement_146;
            Object var6 = var5.getValue();
            if (var6 instanceof Integer && (Integer)var6 != 0) {
               JSONObject var7 = new JSONObject();
               var7.put("method", var5.getKey());
               var7.put("count", var6);
               var3.put(var7);
            }
         }

         var2.put("record_list", var3);
         recoverMethod_a_3c4029ab(var0, var2);
         return true;
      } catch (Throwable var8) {
         return false;
      }
   }
}
