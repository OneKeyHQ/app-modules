package cn.jiguang.k;

import android.content.Context;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.bk.RecoverTypeaf2b07d5;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.g.RecoverType5a69853;
import cn.jiguang.i.RecoverType5a69fd5;
import cn.jiguang.i.RecoverType5a69fd6;
import cn.jiguang.o.RecoverType5a6b65b;
import org.json.JSONException;
import org.json.JSONObject;

public class RecoverType5a6a75a extends RecoverType5a69fd5 {
   private Context recoverField_a;
   private JSONObject recoverField_b;

   private RecoverType5a6a75a() {
   }

   public static RecoverType5a6a75a recoverMethod_a_487c0556() {
      return RecoverType5a6a75a.Member369a3d97.recoverField_a;
   }

   @Override
   protected String recoverMethod_a_e501eb0d(Context var1) {
      this.recoverField_a = var1;
      return "JDeviceIds";
   }

   @Override
   protected void recoverMethod_c_5ffc00fd(Context var1, String var2) {
      if (!RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_e_134632(1900)) {
         try {
            JSONObject var3 = RecoverType5a6b65b.recoverMethod_a_16ce4761(var1);
            if (var3 == null) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDeviceIds", "ids collect failed");
               return;
            }

            if (!this.recoverMethod_a_be7fc16d(var3)) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDeviceIds", "ids not changed, need not report");
               return;
            }

            String var4 = var3.toString();

            try {
               var4 = RecoverType5a68d10.recoverMethod_b_e96ea081(var4);
            } catch (Exception var6) {
               var4 = "";
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDeviceIds", "ids encrypted failed, err: " + var6.getMessage());
            }

            if (!TextUtils.isEmpty(var4)) {
               if (this.recoverField_b == null) {
                  this.recoverField_b = new JSONObject();
               }

               this.recoverField_b.put("data", var4);
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDeviceIds", "collect success:" + this.recoverField_b + ", origin ids: " + var3.toString());
               super.recoverMethod_c_5ffc00fd(var1, var2);
            }
         } catch (JSONException var7) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDeviceIds", "packageJson exception: " + var7.getMessage());
         }
      }
   }

   @Override
   protected void recoverMethod_d_5ffc00fd(Context var1, String var2) {
      if (!RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_e_134632(1900)) {
         if (this.recoverField_b == null) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDeviceIds", "there are no data to report");
         } else {
            RecoverTypeaf2aa628.recoverMethod_a_d97c5eb5(var1, this.recoverField_b, "sdk_joa");
            super.recoverMethod_d_5ffc00fd(var1, var2);
            this.recoverMethod_b_9b75();
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDeviceIds", var2 + "report success, reportData: " + this.recoverField_b);
            this.recoverField_b = null;
         }
      }
   }

   private boolean recoverMethod_a_be7fc16d(JSONObject var1) {
      String var2 = RecoverType5a69fd6.recoverMethod_i_e501eb0d(this.recoverField_a);
      if (TextUtils.isEmpty(var2)) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDeviceIds", "ids cache is empty");
      } else {
         try {
            String var3 = RecoverTypeaf2b07d5.recoverMethod_j_e96ea081(var1.toString());
            return !var2.equals(var3);
         } catch (Exception var4) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDeviceIds", "[checkIdsChanged] toHash error: " + var4.getMessage());
         }
      }

      return true;
   }

   private void recoverMethod_b_9b75() {
      try {
         Object var1 = this.recoverField_b.get("data");
         if (var1 != null) {
            String var2 = RecoverType5a68d10.recoverMethod_c_e96ea081(var1.toString());
            String var3 = RecoverTypeaf2b07d5.recoverMethod_j_e96ea081(var2);
            RecoverType5a69fd6.recoverMethod_s_5ffc00fd(this.recoverField_a, var3);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDeviceIds", "device ids refresh cache success, hash-ids: " + var3);
         }
      } catch (Exception var4) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDeviceIds", "ids encrypted failed, err: " + var4.getMessage());
      }
   }

   private static final class Member369a3d97 {
      private static final RecoverType5a6a75a recoverField_a = new RecoverType5a6a75a();
   }
}
