package cn.jpush.android.b;

import android.content.Context;
import android.text.TextUtils;
import cn.jpush.android.ad.RecoverType9b4c5af2;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.n.RecoverType2e4cf3b2;
import cn.jpush.android.n.RecoverType2e4cf3b4;
import cn.jpush.android.n.RecoverType2e4cf3b5;
import cn.jpush.android.n.RecoverType2e4cf3b6;
import org.json.JSONObject;

public class RecoverType2e4cc6a8 {
   private RecoverType2e4cc6a7 recoverField_a;
   private RecoverType2e4cf3b6 recoverField_b;

   private RecoverType2e4cc6a8() {
      this.recoverMethod_f_9b75();
   }

   public static RecoverType2e4cc6a8 recoverMethod_a_cd2f5408() {
      return RecoverType2e4cc6a8.Memberce35c165.recoverField_a;
   }

   private void recoverMethod_f_9b75() {
      Context var1 = RecoverType9b4c5af2.recoverMethod_a_88bf2b6d();
      RecoverType2e4cf3b5.recoverMethod_a_cd34c89b().recoverMethod_a_f0554df3(var1);
      this.recoverField_a = new RecoverType2e4cc6a5(var1);
      this.recoverField_b = new RecoverType2e4cf3b6(var1);
   }

   public void recoverMethod_b_9b75() {
      this.recoverField_a.recoverMethod_b_9b75();
   }

   public void recoverMethod_c_9b75() {
      this.recoverField_a.recoverMethod_a_9b75();
   }

   public void recoverMethod_a_708444be(RecoverType2e4cc6a6 var1) {
      Logger.recoverMethod_dd_e925fa09("GeofenceManager", "recv geofence...");
      RecoverType2e4cf3b5.recoverMethod_a_cd34c89b().recoverMethod_b_708444be(var1);
   }

   public void recoverMethod_a_be7fc169(JSONObject var1) {
      try {
         JSONObject var2 = var1.getJSONObject("content");
         if (var2 != null && var2.length() > 0) {
            String var3 = var2.optString("op");
            String var4 = var2.optString("geofenceid");
            if (TextUtils.isEmpty(var3) || TextUtils.isEmpty(var4)) {
               Logger.recoverMethod_w_e925fa09("GeofenceManager", "json error: required key not found");
               return;
            }

            switch (var3) {
               case "add":
                  RecoverType2e4cc6a6 var7 = RecoverType2e4cc6a6.recoverMethod_a_ef7f2e56(var2);
                  RecoverType2e4cf3b5.recoverMethod_a_cd34c89b().recoverMethod_b_708444be(var7);
                  break;
               case "upd":
                  RecoverType2e4cf3b5.recoverMethod_a_cd34c89b().recoverMethod_a_cdf45195(var4, var2, true);
                  break;
               case "del":
                  RecoverType2e4cf3b5.recoverMethod_a_cd34c89b().recoverMethod_a_aad3b1ff(var4);
                  break;
               default:
                  Logger.recoverMethod_w_e925fa09("GeofenceManager", "operation " + var3 + " not supported");
            }
         }
      } catch (Throwable var8) {
         Logger.recoverMethod_w_e925fa09("GeofenceManager", "json error:" + var8);
      }
   }

   public void recoverMethod_a_1349ef(long var1) {
      this.recoverField_a.recoverMethod_a_1349ef(var1);
   }

   public void recoverMethod_a_13462e(int var1) {
      RecoverType2e4cf3b5.recoverMethod_a_cd34c89b().recoverMethod_a_13462e(var1);
   }

   public void recoverMethod_a_aad3b1ff(String var1) {
      RecoverType2e4cf3b5.recoverMethod_a_cd34c89b().recoverMethod_a_aad3b1ff(var1);
   }

   public void recoverMethod_d_9b75() {
      this.recoverField_b.recoverMethod_a_9b75();
   }

   public void recoverMethod_e_9b75() {
      this.recoverField_b.recoverMethod_b_9b75();
   }

   public void recoverMethod_a_84fe6c32(RecoverType2e4cf3b2 var1) {
      this.recoverField_b.recoverMethod_a_84fe6c32(var1);
   }

   public void recoverMethod_a_cb5e72f(long var1, int var3, RecoverType2e4cf3b4 var4) {
      this.recoverField_b.recoverMethod_a_cb5e72f(var1, var3, var4);
   }

   private static final class Memberce35c165 {
      private static final RecoverType2e4cc6a8 recoverField_a = new RecoverType2e4cc6a8();
   }
}
