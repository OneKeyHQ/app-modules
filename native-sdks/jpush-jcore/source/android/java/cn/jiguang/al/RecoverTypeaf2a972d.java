package cn.jiguang.al;

import android.content.Context;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.ay.RecoverTypeaf2ac7f0;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.bk.RecoverTypeaf2b07c9;
import cn.jiguang.bk.RecoverTypeaf2b07d5;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import cn.jiguang.i.RecoverType5a69fd6;
import org.json.JSONException;
import org.json.JSONObject;

public class RecoverTypeaf2a972d {
   private static long recoverField_f = 86400000L;
   private static Integer recoverField_g = null;
   public static String recoverField_a = "reg_finger_helper";
   public static String recoverField_b = "dId";
   public static String recoverField_c = "ud";
   public static String recoverField_d = "j_p_d";
   public static String recoverField_e = "rd";

   public static int recoverMethod_a_f0554de6(Context var0) {
      if (null == recoverField_g) {
         recoverField_g = (Integer)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_M_48794abf());
      }

      return recoverField_g;
   }

   public static void recoverMethod_a_148762fb(Context var0, String var1, long var2, String var4, String var5) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("RegFingerHelper", "sendDataMain:" + var2 + ",isNewFingerprint:" + recoverField_g);
      if (0 != recoverMethod_a_f0554de6(var0)) {
         JSONObject var6 = new JSONObject();

         try {
            var6.put(recoverField_b, var1);
            var6.put(recoverField_c, var2);
            var6.put(recoverField_d, var4);
            var6.put(recoverField_e, var5);
         } catch (JSONException var8) {
            RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("RegFingerHelper", "sendDataMain Fail: " + var8);
         }

         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("RegFingerHelper", "sendDataMain:" + var2 + ",jsonObject:" + var6);
         RecoverTypeaf2b07c1.recoverMethod_b_9d23c587(var0, recoverField_a, var6.toString());
      }
   }

   public static void recoverMethod_a_5ffc00fd(Context var0, String var1) {
      try {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("RegFingerHelper", "saveDataMain:" + var1);
         JSONObject var2 = new JSONObject(var1);
         String var3 = var2.optString(recoverField_b);
         long var4 = var2.optLong(recoverField_c);
         String var6 = var2.optString(recoverField_d);
         String var7 = var2.optString(recoverField_e);
         RecoverTypeaf2aa628.recoverMethod_a_5ffc00fd(var0, var3);
         RecoverTypeaf2aa628.recoverMethod_a_3b017c5(var0, var4, var6, var7);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("RegFingerHelper", "saveDataMain end:" + var1);
      } catch (JSONException var8) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("RegFingerHelper", "saveDataMain Fail: " + var8);
      }
   }

   public static void recoverMethod_b_f0554df3(Context var0) {
      RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_M_48794abf().recoverMethod_a_4e9722a3(0));
      recoverField_g = 0;
   }

   public static void recoverMethod_c_f0554df3(Context var0) {
      try {
         boolean var1 = RecoverTypeaf2a972a.recoverMethod_a_9b79();
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("RegFingerHelper", "regFinger is:" + var1);
         if (var1) {
            String var2 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_L_48794abf());
            String var3 = RecoverTypeaf2b07d5.recoverMethod_j_e96ea081(
               RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_s_e501eb0d(var0)
                  + RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_t_e501eb0d(var0)
            );
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("RegFingerHelper", "regFinger oldRule:" + var2);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("RegFingerHelper", "regFinger newRule:" + var3);
            if (!TextUtils.isEmpty(var2)) {
               if (!TextUtils.isEmpty(var3) && !var2.equals(var3)) {
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("RegFingerHelper", "regFinger is re");
                  RecoverType5a690d2.recoverMethod_a_5ffc00fd(var0, "cn.jiguang.sdk.user.profile");
                  RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_M_48794abf().recoverMethod_a_4e9722a3(1));
                  recoverField_g = 1;
                  RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_L_48794abf().recoverMethod_a_4e9722a3(var3));
                  RecoverType5a69fd6.recoverMethod_a_f0554df3(var0);
                  RecoverTypeaf2b07c1.recoverMethod_b_9b75();
                  RecoverTypeaf2b07c9.recoverMethod_d_aad3b1ff("bal.catch");
                  RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_b_48794abf().recoverMethod_a_4e9722a3(null));
                  RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_aj_48794abf().recoverMethod_a_4e9722a3(null));
                  RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_ak_48794abf().recoverMethod_a_4e9722a3(null));
                  RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_az_48794abf().recoverMethod_a_4e9722a3(null));
                  RecoverTypeaf2ac7f0.recoverMethod_b_f0554df3(var0);
               }
            } else if (!TextUtils.isEmpty(var3)) {
               RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_L_48794abf().recoverMethod_a_4e9722a3(var3));
            }
         }
      } catch (Throwable var4) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("RegFingerHelper", "reg Fail: " + var4);
      }
   }
}
