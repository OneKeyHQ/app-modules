package cn.jiguang.ae;

import android.content.Context;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.common.app.helper.RecoverType82ecf7ac;
import cn.jiguang.g.RecoverType5a69853;
import cn.jiguang.i.RecoverType5a69fd5;
import java.util.ArrayList;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecoverTypeaf2a7cdd extends RecoverType5a69fd5 {
   private Context recoverField_a;

   @Override
   protected String recoverMethod_a_e501eb0d(Context var1) {
      this.recoverField_a = var1;
      return "JDataCollectStatistic";
   }

   @Override
   protected boolean recoverMethod_a_5ffc0101(Context var1, String var2) {
      return true;
   }

   @Override
   protected boolean recoverMethod_b_5ffc0101(Context var1, String var2) {
      return super.recoverMethod_b_5ffc0101(var1, var2);
   }

   @Override
   protected void recoverMethod_c_5ffc00fd(Context var1, String var2) {
      super.recoverMethod_c_5ffc00fd(var1, var2);
   }

   @Override
   protected void recoverMethod_d_5ffc00fd(Context var1, String var2) {
      if (!RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_e_134632(2600)) {
         try {
            JSONArray var3 = RecoverTypeaf2a7cdc.recoverMethod_a_5d5a71dd(var1);
            if (var3 == null || var3.length() == 0) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDataCollectStatistic", "no collect statistic data to report");
               return;
            }

            ArrayList var4 = RecoverType82ecf7ac.recoverMethod_a_48eba5e9(var3);
            if (var4 == null || var4.isEmpty()) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDataCollectStatistic", "statistic data partition failed");
               return;
            }

            int var5 = 0;

            for (int var6 = var4.size(); var5 < var6; var5++) {
               JSONObject var7 = new JSONObject();
               JSONArray var8 = (JSONArray)var4.get(var5);
               var7.put("slice_index", var5 + 1);
               var7.put("slice_count", var6);
               var7.put("data", var8);
               var7.put("sequence", UUID.randomUUID().toString());
               RecoverTypeaf2aa628.recoverMethod_a_d97c5eb5(var1, var7, "collect_statistic");
            }

            RecoverTypeaf2a7cdc.recoverMethod_b_f0554df3(var1);
            super.recoverMethod_d_5ffc00fd(var1, var2);
         } catch (Throwable var9) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDataCollectStatistic", "collect data report failed: " + var9.getMessage());
         }
      }
   }
}
