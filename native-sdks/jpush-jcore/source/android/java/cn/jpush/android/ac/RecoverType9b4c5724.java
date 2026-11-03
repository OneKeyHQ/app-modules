package cn.jpush.android.ac;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import cn.jiguang.internal.JConstants;
import cn.jpush.android.ad.RecoverType9b4c5ae5;
import cn.jpush.android.ad.RecoverType9b4c5ae8;
import cn.jpush.android.ad.RecoverType9b4c5aea;
import cn.jpush.android.ad.RecoverType9b4c5af0;
import cn.jpush.android.ad.RecoverType9b4c5af2;
import cn.jpush.android.cache.RecoverTypea2146d45;
import cn.jpush.android.helper.JCoreHelper;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.local.JPushConstants;
import cn.jpush.android.w.RecoverType2e4d157c;
import cn.jpush.android.w.RecoverType2e4d157d;
import cn.jpush.android.w.RecoverType2e4d157e;
import java.util.LinkedList;
import org.json.JSONObject;

public class RecoverType9b4c5724 {
   private static final String recoverField_a = new String(new byte[]{73, 55, 77, 85, 90, 88, 67, 80, 90, 87, 82, 70, 90, 65, 74, 83});
   private static final String recoverField_b = new String(new byte[]{67, 66, 68, 72, 74, 88, 77, 68, 68, 86, 88, 72, 56, 84, 72, 87});
   private static final String recoverField_c = RecoverType9b4c5ae8.recoverMethod_b_79834524(
      new byte[]{96, 98, 101, 64, 83, 35, 39, 57, 114, 85, 19, 124, 63, 35, 117, 5, 14, 115, 120, 99, 98, 88, 14, 122, 102, 57, 102, 89, 15, 122, 102, 122}
   );
   private static final String recoverField_d = RecoverType9b4c5ae8.recoverMethod_b_79834524(
      new byte[]{
         113,
         124,
         98,
         97,
         67,
         26,
         54,
         39,
         117,
         116,
         3,
         69,
         46,
         61,
         114,
         36,
         29,
         73,
         105,
         126,
         32,
         63,
         90,
         80,
         108,
         123,
         126,
         63,
         83,
         78,
         54,
         127,
         127,
         62,
         83,
         78,
         117
      }
   );
   private static LinkedList recoverField_e = new LinkedList();

   public static void recoverMethod_a_14f3cf61(Context var0, String var1, RecoverType9b4c5723 var2) {
      JCoreHelper.normalExecutor(var0, null, new RecoverType9b4c5724.Memberf99322e1(var0, var1, var2));
   }

   static {
      recoverField_e.clear();
      recoverField_e.add(recoverField_c);
      recoverField_e.add(recoverField_d);
   }

   private static class Memberf99322e1 extends RecoverType9b4c5aea {
      private Context recoverField_a;
      private String recoverField_b;
      private RecoverType9b4c5723 recoverField_c;

      public Memberf99322e1(Context var1, String var2, RecoverType9b4c5723 var3) {
         this.recoverField_a = var1;
         this.recoverField_e = "JPConfig";
         this.recoverField_c = var3;
         this.recoverField_b = var2;
      }

      @Override
      public void recoverMethod_a_9b75() {
         try {
            if (System.currentTimeMillis() - RecoverTypea2146d45.recoverMethod_t_f0554de7(this.recoverField_a) < 86400000L) {
               Logger.recoverMethod_d_e925fa09("JPConfig", "it not sdk config request business time");
               return;
            }

            byte var1 = RecoverType9b4c5727.recoverMethod_a_d8c3f71f().recoverMethod_e_f0554ddf(this.recoverField_a);
            JSONObject var2 = new JSONObject();
            var2.put("dev_key", this.recoverField_b);
            var2.put("p_name", this.recoverField_a.getPackageName());
            var2.put("rom_type", var1);
            String var3 = RecoverType9b4c5ae5.recoverMethod_d_e96ea081(var2.toString()).toUpperCase();
            String var4 = RecoverType9b4c5ae5.recoverMethod_d_e96ea081(this.recoverField_b + var3).toUpperCase();
            if (TextUtils.isEmpty(var4)) {
               Logger.recoverMethod_w_e925fa09("JPConfig", "authToken is null");
               return;
            }

            String var5 = this.recoverField_b + ":" + var4;
            var5 = Base64.encodeToString(var5.getBytes(), 10);
            if (TextUtils.isEmpty(var5)) {
               Logger.recoverMethod_w_e925fa09("JPConfig", "authorization is null");
               return;
            }

            String var6 = var2.toString();
            var6 = RecoverType9b4c5af0.recoverMethod_a_ed76890(var6.getBytes(), RecoverType9b4c5724.recoverField_a, RecoverType9b4c5724.recoverField_b);
            this.recoverMethod_b_9b75();
            RecoverType9b4c5af2.recoverMethod_a_fa4794e4(this.recoverField_a, RecoverType9b4c5724.recoverField_e);

            for (Object recoveryElement_126 : RecoverType9b4c5724.recoverField_e) {
               String var8 = (String)recoveryElement_126;
               RecoverType2e4d157c var9 = new RecoverType2e4d157c(var8);
               var9.recoverMethod_a_aad3b1ff("init_config");
               var9.recoverMethod_a_e925fa09("Content-Type", "application/json");
               var9.recoverMethod_a_e925fa09("Accept", "application/json");
               var9.recoverMethod_a_e925fa09("X-Http-Platform", "android");
               var9.recoverMethod_a_e925fa09("Authorization", "Basic " + var5);
               var9.recoverMethod_a_e925fa09("Charset", "UTF-8");
               var9.recoverMethod_a_5d527811(var6);
               Logger.recoverMethod_d_e925fa09("JPConfig", "url: " + var8 + ", request param: " + var2.toString());
               RecoverType2e4d157d var10 = RecoverType2e4d157e.recoverMethod_a_ad8da5c2(this.recoverField_a, var9);
               int var11 = var10.recoverMethod_b_9b68();
               Logger.recoverMethod_d_e925fa09("JPConfig", "responseCode:" + var11);
               if (200 == var11) {
                  String var12 = var10.recoverMethod_a_73cf11cb();
                  Logger.recoverMethod_d_e925fa09("JPConfig", "responseBody:" + var12);
                  if (!TextUtils.isEmpty(var12)) {
                     try {
                        var12 = new String(
                           RecoverType9b4c5af0.recoverMethod_a_78a45faa(var12, RecoverType9b4c5724.recoverField_a, RecoverType9b4c5724.recoverField_b)
                        );
                     } catch (Throwable var14) {
                        var12 = "";
                        Logger.recoverMethod_w_e925fa09("JPConfig", "decode response failed, " + var14.getMessage());
                     }

                     if (null != this.recoverField_c && !TextUtils.isEmpty(var12)) {
                        Logger.recoverMethod_d_e925fa09("JPConfig", "request response - p2:" + var12);
                        JSONObject var13 = new JSONObject(var12);
                        if (var13.optInt("code") == 2000) {
                           RecoverTypea2146d45.recoverMethod_d_1a54e731(this.recoverField_a, System.currentTimeMillis());
                           this.recoverField_c.recoverMethod_a_5ffc00fd(this.recoverField_a, var13.optJSONObject("data").toString());
                           return;
                        }
                     }
                  }
               }
            }

            if (null != this.recoverField_c) {
               this.recoverField_c.recoverMethod_a_9b75();
            }
         } catch (Throwable var15) {
            Logger.recoverMethod_w_e925fa09("JPConfig", "request JP config failed, " + var15.getMessage());
         }
      }

      private void recoverMethod_b_9b75() {
         Bundle var1 = JPushConstants.getBundle();
         if (JConstants.isTestEnv() && null != var1) {
            String var2 = var1.getString("test_key_config_url");
            if (!TextUtils.isEmpty(var2) && RecoverType9b4c5af2.recoverMethod_c_3e0e9f8f(var2) != null) {
               RecoverType9b4c5724.recoverField_e.clear();
               RecoverType9b4c5724.recoverField_e.addAll(RecoverType9b4c5af2.recoverMethod_c_3e0e9f8f(var2));
               Logger.recoverMethod_d_e925fa09("JPConfig", "test env and use the test urls " + RecoverType9b4c5724.recoverField_e);
            }
         }
      }
   }
}
