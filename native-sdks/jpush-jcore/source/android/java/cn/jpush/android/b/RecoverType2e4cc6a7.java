package cn.jpush.android.b;

import android.content.Context;
import android.location.Location;
import cn.jiguang.api.JCoreManager;
import cn.jiguang.api.ReportCallBack;
import cn.jpush.android.d.RecoverType2e4cce28;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.n.RecoverType2e4cf3b5;
import org.json.JSONObject;

public abstract class RecoverType2e4cc6a7 {
   protected Context recoverField_a;

   public RecoverType2e4cc6a7(Context var1) {
      this.recoverField_a = var1.getApplicationContext();
      RecoverType2e4cf3b5.recoverMethod_a_cd34c89b().recoverMethod_a_4307a9b2(new RecoverType2e4cf3b5.Membercededf32() {
         @Override
         public void recoverMethod_a_1385ff(boolean var1) {
            if (var1) {
               RecoverType2e4cc6a7.this.recoverMethod_a_9b75();
            } else {
               RecoverType2e4cc6a7.this.recoverMethod_b_9b75();
            }
         }

         @Override
         public void recoverMethod_a_708444be(RecoverType2e4cc6a6 var1) {
            RecoverType2e4cc6a7.this.recoverMethod_a_708444be(var1);
         }

         @Override
         public void recoverMethod_a_3317e147(RecoverType2e4cc6a6 var1, RecoverType2e4cc6a6 var2) {
            RecoverType2e4cc6a7.this.recoverMethod_a_3317e147(var1, var2);
         }

         @Override
         public void recoverMethod_b_708444be(RecoverType2e4cc6a6 var1) {
            RecoverType2e4cc6a7.this.recoverMethod_b_708444be(var1);
         }
      });
   }

   public void recoverMethod_b_9b75() {
   }

   protected void recoverMethod_a_9b75() {
   }

   protected abstract void recoverMethod_a_708444be(RecoverType2e4cc6a6 var1);

   protected abstract void recoverMethod_a_3317e147(RecoverType2e4cc6a6 var1, RecoverType2e4cc6a6 var2);

   protected abstract void recoverMethod_b_708444be(RecoverType2e4cc6a6 var1);

   public void recoverMethod_a_1349ef(long var1) {
   }

   protected void recoverMethod_a_47fde754(RecoverType2e4cc6a6 var1, Location var2) {
      try {
         if (var1 != null) {
            if (var1.recoverField_i == 2) {
               this.recoverMethod_a_c12b3896(this.recoverField_a, var1, var2);
            } else if (var1.recoverField_t != null) {
               RecoverType2e4cce28.recoverMethod_a_ae3b7eb8(this.recoverField_a, var1.recoverField_t);
            } else {
               Logger.recoverMethod_w_e925fa09("GeofenceAction", "there is no push entity, won't show notification");
            }
         }
      } catch (Throwable var4) {
         Logger.recoverMethod_ww_e925fa09("GeofenceAction", "process geofence error:" + var4);
      }
   }

   private void recoverMethod_a_c12b3896(Context var1, RecoverType2e4cc6a6 var2, Location var3) {
      try {
         Logger.recoverMethod_d_e925fa09("GeofenceAction", "geofence report id=" + var2.recoverField_a);
         JSONObject var4 = new JSONObject();
         var4.put("geofence_id", var2.recoverField_a);
         if (var3 != null) {
            JSONObject var5 = new JSONObject();
            var5.put("lat", var3.getLatitude() + "");
            var5.put("lng", var3.getLongitude() + "");
            var4.put("gps", var5);
         }

         Object var7 = JCoreManager.onEvent(null, null, 26, null, null, var4, "geo_fence");
         if (var7 instanceof JSONObject) {
            JCoreManager.onEvent(var1, "JPUSH", 15, null, null, var4, new ReportCallBack() {
               @Override
               public void onFinish(int var1) {
                  Logger.recoverMethod_d_e925fa09("GeofenceAction", "report with callback:" + var1);
               }
            });
         }
      } catch (Throwable var6) {
         Logger.recoverMethod_w_e925fa09("GeofenceAction", "report geofence error:" + var6);
      }
   }
}
