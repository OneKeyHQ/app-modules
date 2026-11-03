package cn.jiguang.b;

import android.content.Context;
import cn.jiguang.al.RecoverTypeaf2a9729;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ao.RecoverTypeaf2aa26b;
import cn.jiguang.api.ReportCallBack;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.bk.RecoverTypeaf2b07c4;
import cn.jiguang.bk.RecoverTypeaf2b07ce;
import cn.jiguang.d.RecoverType5a68d10;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecoverType5a6858f extends RecoverTypeaf2b0401 implements ReportCallBack {
   public RecoverType5a6858f() {
      this.recoverField_h = "ReportCrashLogDirect";
   }

   @Override
   public void recoverMethod_a_9b75() {
      try {
         Context var1 = RecoverTypeaf2b07c4.recoverMethod_a_6263c3eb(null);
         if (var1 == null) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ReportCrashLogDirect", "ReportDirect context is null");
            return;
         }

         JSONObject var2 = this.recoverMethod_a_16ce4761(var1);
         if (var2 != null) {
            RecoverTypeaf2aa26b.recoverMethod_a_6892eae2(var1, var2, this);
         }
      } catch (Throwable var3) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("ReportCrashLogDirect", "run report crash e:" + var3);
      }
   }

   @Override
   public void onFinish(int var1) {
      RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ReportCrashLogDirect", "ReportDirect finish : " + var1);
      if (var1 == 0) {
         RecoverType5a6858e.recoverMethod_d_f0554df3(RecoverTypeaf2b07c4.recoverMethod_a_6263c3eb(null));
      }
   }

   private JSONObject recoverMethod_a_16ce4761(Context var1) {
      JSONArray var2 = RecoverType5a6858e.recoverMethod_c_5d5a71dd(var1);
      if (var2 == null) {
         return null;
      }

      JSONObject var3 = new JSONObject();

      try {
         var3.put("crashlogs", var2);
         var3.put("network_type", RecoverTypeaf2b07ce.recoverMethod_c_e501eb0d(var1));
         RecoverType5a68d10.recoverMethod_a_1b76323(var1, var3, "crash_log");
         Object var4 = RecoverTypeaf2a9729.recoverMethod_a_c692b39d().recoverMethod_a_54cf32c4(68);
         JSONObject var5 = null;
         if (var4 instanceof JSONObject) {
            var5 = (JSONObject)var4;
         }

         if (var5 != null && var5.length() > 0) {
            var3.put("device_info", var5);
         }
      } catch (Exception var6) {
      }

      return var3;
   }
}
