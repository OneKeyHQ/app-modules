package cn.jiguang.k;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.bk.RecoverTypeaf2b07d1;
import cn.jiguang.g.RecoverType5a69853;
import cn.jiguang.i.RecoverType5a69fd5;
import org.json.JSONException;
import org.json.JSONObject;

public class RecoverType5a6a759 extends RecoverType5a69fd5 {
   private Context recoverField_a;
   private JSONObject recoverField_b;

   private RecoverType5a6a759() {
   }

   public static RecoverType5a6a759 recoverMethod_a_487c0537() {
      return RecoverType5a6a759.Member369a39d6.recoverField_a;
   }

   @Override
   protected String recoverMethod_a_e501eb0d(Context var1) {
      this.recoverField_a = var1;
      return "JDeviceBattery";
   }

   @Override
   protected void recoverMethod_c_5ffc00fd(Context var1, String var2) {
      if (!RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_e_134632(1200)) {
         try {
            String var3 = var1.getPackageName() + ".permission.JPUSH_MESSAGE";
            Intent var4 = RecoverTypeaf2b07d1.recoverMethod_a_6169dcd2(var1, null, new IntentFilter("android.intent.action.BATTERY_CHANGED"), var3, null, false);
            if (var4 == null) {
               return;
            }

            int var5 = var4.getIntExtra("level", -1);
            int var6 = var4.getIntExtra("scale", -1);
            int var7 = var4.getIntExtra("status", -1);
            switch (var7) {
               case 1:
                  var7 = 0;
                  break;
               case 2:
                  var7 = 2;
                  break;
               case 3:
               case 4:
                  var7 = 1;
                  break;
               case 5:
                  var7 = 3;
            }

            int var8 = var4.getIntExtra("voltage", -1);
            int var9 = var4.getIntExtra("temperature", -1);
            if (this.recoverField_b == null) {
               this.recoverField_b = new JSONObject();
            }

            this.recoverField_b.put("level", var5);
            this.recoverField_b.put("scale", var6);
            this.recoverField_b.put("status", var7);
            this.recoverField_b.put("voltage", var8);
            this.recoverField_b.put("temperature", var9);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDeviceBattery", "collect success:" + this.recoverField_b);
            super.recoverMethod_c_5ffc00fd(var1, var2);
         } catch (JSONException var10) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDeviceBattery", "packageJson exception: " + var10.getMessage());
         }
      }
   }

   @Override
   protected void recoverMethod_d_5ffc00fd(Context var1, String var2) {
      if (!RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_e_134632(1200)) {
         if (this.recoverField_b == null) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDeviceBattery", "there are no data to report");
         } else {
            RecoverTypeaf2aa628.recoverMethod_a_d97c5eb5(var1, this.recoverField_b, "battery");
            super.recoverMethod_d_5ffc00fd(var1, var2);
            this.recoverField_b = null;
         }
      }
   }

   private static final class Member369a39d6 {
      private static final RecoverType5a6a759 recoverField_a = new RecoverType5a6a759();
   }
}
