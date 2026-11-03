package cn.jpush.android.b;

import android.content.Context;
import android.text.TextUtils;
import cn.jpush.android.d.RecoverType2e4cce2a;
import cn.jpush.android.helper.JCoreHelper;
import cn.jpush.android.helper.Logger;
import java.io.Serializable;
import org.json.JSONObject;

public class RecoverType2e4cc6a6 implements Serializable {
   private static final long serialVersionUID = 2310616383854860780L;
   public String recoverField_a;
   public long recoverField_b;
   public String recoverField_c;
   public boolean recoverField_d;
   public long recoverField_e;
   public double recoverField_f = 200.0;
   public double recoverField_g = 200.0;
   public String recoverField_h;
   public int recoverField_i;
   public String recoverField_j;
   public String recoverField_k;
   public int recoverField_l;
   public int recoverField_m;
   public int recoverField_n;
   public long recoverField_o;
   public String recoverField_p;
   public int recoverField_q;
   public String recoverField_r;
   public int recoverField_s;
   public RecoverType2e4cce2a recoverField_t;

   public static RecoverType2e4cc6a6 recoverMethod_a_ef7f2e56(JSONObject var0) {
      try {
         if (var0 != null) {
            RecoverType2e4cc6a6 var1 = new RecoverType2e4cc6a6();
            Logger.recoverMethod_d_e925fa09("Geofence", "geofence message:" + var0.toString());
            var1.recoverField_k = var0.optString("op");
            var1.recoverField_a = var0.optString("geofenceid");
            var1.recoverField_j = var0.optString("name");
            var1.recoverField_b = var0.optLong("radius");
            var1.recoverField_c = var0.optString("status");
            var1.recoverField_d = var0.optBoolean("repeat");
            var1.recoverField_l = var0.optInt("repeat_week_num");
            var1.recoverField_m = var0.optInt("repeat_day_num");
            var1.recoverField_n = var0.optInt("repeat_time");
            var1.recoverField_e = var0.optLong("expiration");
            var1.recoverField_i = var0.optInt("type", 1);
            JSONObject var2 = var0.optJSONObject("center");
            if (var2 != null) {
               var1.recoverField_f = var2.optDouble("lon", 200.0);
               var1.recoverField_g = var2.optDouble("lat", 200.0);
            }

            return var1;
         }

         Logger.recoverMethod_w_e925fa09("Geofence", "geofence json is null");
      } catch (Throwable var3) {
      }

      return null;
   }

   public void recoverMethod_b_be7fc169(JSONObject var1) {
      try {
         if (var1.has("name")) {
            this.recoverField_j = var1.optString("name");
         }

         long var2 = var1.optLong("radius", -1L);
         if (var2 > 0L) {
            this.recoverField_b = var2;
         }

         if (var1.has("status")) {
            this.recoverField_c = var1.optString("status");
         }

         if (var1.has("repeat")) {
            this.recoverField_d = var1.optBoolean("repeat");
            if (this.recoverField_d) {
               if (var1.has("repeat_week_num")) {
                  this.recoverField_l = var1.optInt("repeat_week_num");
               }

               if (var1.has("repeat_day_num")) {
                  this.recoverField_m = var1.optInt("repeat_day_num");
               }

               if (var1.has("repeat_time")) {
                  this.recoverField_n = var1.optInt("repeat_time");
               }
            }
         }

         if (var1.has("expiration")) {
            this.recoverField_e = var1.optLong("expiration");
         }

         JSONObject var4 = var1.optJSONObject("center");
         if (var4 != null) {
            double var5 = var4.optDouble("lon", 200.0);
            double var7 = var4.optDouble("lat", 200.0);
            if (var5 >= -180.0 && var5 <= 180.0 && var7 >= -90.0 && var7 <= 90.0) {
               this.recoverField_f = var5;
               this.recoverField_g = var7;
            } else {
               Logger.recoverMethod_w_e925fa09("Geofence", "update center failed because value invalid, [" + var7 + "," + var5 + "]");
            }
         }
      } catch (Throwable var9) {
      }
   }

   public void recoverMethod_a_708444be(RecoverType2e4cc6a6 var1) {
      this.recoverField_h = var1.recoverField_h;
      this.recoverField_o = var1.recoverField_o;
      this.recoverField_p = var1.recoverField_p;
      this.recoverField_r = var1.recoverField_r;
      this.recoverField_q = var1.recoverField_q;
      this.recoverField_s = var1.recoverField_s;
   }

   public static RecoverType2e4cc6a6 recoverMethod_a_8c09e354(Context var0, JSONObject var1) {
      try {
         if (var1 != null) {
            RecoverType2e4cc6a6 var2 = new RecoverType2e4cc6a6();
            Logger.recoverMethod_d_e925fa09("Geofence", "geofence message:" + var1.toString());
            var2.recoverField_k = var1.optString("op");
            var2.recoverField_a = var1.optString("geofenceid");
            var2.recoverField_j = var1.optString("name");
            var2.recoverField_b = var1.optLong("radius");
            var2.recoverField_c = var1.optString("status");
            var2.recoverField_d = var1.optBoolean("repeat");
            var2.recoverField_l = var1.optInt("repeat_week_num");
            var2.recoverField_m = var1.optInt("repeat_day_num");
            var2.recoverField_n = var1.optInt("repeat_time");
            var2.recoverField_e = var1.optLong("expiration");
            var2.recoverField_i = var1.optInt("type", 1);
            var2.recoverField_f = var1.optDouble("lon", 200.0);
            var2.recoverField_g = var1.optDouble("lat", 200.0);
            var2.recoverField_o = var1.optLong("lastTime");
            var2.recoverField_p = var1.optString("lastTimeWeek");
            var2.recoverField_q = var1.optInt("weekNum");
            var2.recoverField_r = var1.optString("lastTimeDay");
            var2.recoverField_s = var1.optInt("dayNum");
            var2.recoverField_h = var1.optString("lastGeoStatus");
            String var3 = var1.optString("entity");
            if (!TextUtils.isEmpty(var3)) {
               RecoverType2e4cce2a var4 = RecoverType2e4cce2a.recoverMethod_a_e5f4ac2a(var3, var0.getPackageName(), JCoreHelper.getAppKey(var0), 0L);
               var2.recoverField_t = var4;
            }

            return var2;
         }

         Logger.recoverMethod_w_e925fa09("Geofence", "geofence json is null");
      } catch (Throwable var5) {
      }

      return null;
   }

   public JSONObject recoverMethod_a_a7dbfe3() {
      try {
         JSONObject var1 = new JSONObject();
         var1.put("op", this.recoverField_k);
         var1.put("geofenceid", this.recoverField_a);
         var1.put("name", this.recoverField_j);
         var1.put("radius", this.recoverField_b);
         var1.put("status", this.recoverField_c);
         var1.put("repeat", this.recoverField_d);
         var1.put("repeat_week_num", this.recoverField_l);
         var1.put("repeat_day_num", this.recoverField_m);
         var1.put("repeat_time", this.recoverField_n);
         var1.put("expiration", this.recoverField_e);
         var1.put("type", this.recoverField_i);
         var1.put("lon", this.recoverField_f);
         var1.put("lat", this.recoverField_g);
         var1.put("lastTime", this.recoverField_o);
         var1.put("lastTimeWeek", this.recoverField_p);
         var1.put("weekNum", this.recoverField_q);
         var1.put("lastTimeDay", this.recoverField_r);
         var1.put("dayNum", this.recoverField_s);
         var1.put("lastGeoStatus", this.recoverField_h);
         if (this.recoverField_t != null) {
            var1.put("entity", this.recoverField_t.recoverField_i);
         }

         return var1;
      } catch (Throwable var2) {
         return null;
      }
   }
}
