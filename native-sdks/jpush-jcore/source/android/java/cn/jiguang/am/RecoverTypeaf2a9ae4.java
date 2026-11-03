package cn.jiguang.am;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.e.RecoverType5a690d3;
import org.json.JSONObject;

public class RecoverTypeaf2a9ae4 {
   public static void recoverMethod_a_69c77401(Context var0, Bundle var1) {
      if (var1 != null) {
         boolean var2 = var1.getBoolean("enable", false);
         boolean var3 = var1.getBoolean("upload", false);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("LogExternalManage", "enableSDKLocalLog: enable=" + var2 + ", upload=" + var3);
         if (var2) {
            recoverMethod_a_1a552341(var0, var3);
         } else {
            recoverMethod_b_f0554df3(var0);
         }
      }
   }

   private static void recoverMethod_a_1a552341(Context var0, boolean var1) {
      try {
         JSONObject var2 = new JSONObject();
         JSONObject var3 = new JSONObject();
         var3.put("log_enable", 1);
         var3.put("log_print", 1);
         var3.put("local_upload_enable", var1 ? 1 : 0);
         var3.put("expire", 7);
         var2.put("content", var3);
         RecoverTypeaf2a9ae7.recoverMethod_a_f0554df3(var0);
         RecoverType5a68d10.recoverMethod_a_3c4029ab(var0, var2);
         RecoverType5a690d3.recoverMethod_a_1a552341(var0, true);
         RecoverType5a690d3.recoverMethod_b_1a552341(var0, var1);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
            "LogExternalManage",
            "enableSDKLocalLog: " + var2.toString() + ", isUpload=" + var1 + ", spLogEnable: " + RecoverType5a690d3.recoverMethod_a_f0554df7(var0)
         );
      } catch (Throwable var4) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("LogExternalManage", "enableSDKLocalLog failed: " + var4.getMessage());
      }
   }

   private static void recoverMethod_b_f0554df3(Context var0) {
      try {
         boolean var1 = RecoverType5a690d3.recoverMethod_a_f0554df7(var0);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("LogExternalManage", "isExternalLogEnabled: " + var1);
         if (var1) {
            RecoverTypeaf2a9ae7.recoverMethod_b_f0554df3(var0);
            RecoverType5a690d3.recoverMethod_a_1a552341(var0, false);
            if (RecoverTypeaf2a9ae7.recoverMethod_c_f0554df7(var0)) {
               JSONObject var2 = new JSONObject();
               JSONObject var3 = new JSONObject();
               var3.put("log_enable", 0);
               var3.put("log_print", 0);
               var3.put("local_upload_enable", 0);
               var2.put("content", var3);
               RecoverType5a68d10.recoverMethod_a_3c4029ab(var0, var2);
            }
         }
      } catch (Throwable var4) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("LogExternalManage", "disableSDKLocalLog failed: " + var4.getMessage());
      }
   }

   public static String recoverMethod_a_e501eb0d(Context var0) {
      boolean var1 = RecoverType5a690d3.recoverMethod_a_f0554df7(var0);
      if (!var1) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("LogExternalManage", "readLogs: log is not enabled");
         return "";
      } else {
         long var2 = RecoverType5a690d3.recoverMethod_b_f0554de7(var0);
         String var4 = RecoverTypeaf2a9ae5.recoverMethod_a_e45c358f(var0, var2);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("LogExternalManage", "readLogs: logs len: " + (TextUtils.isEmpty(var4) ? 0 : var4.length()));
         return var4;
      }
   }
}
