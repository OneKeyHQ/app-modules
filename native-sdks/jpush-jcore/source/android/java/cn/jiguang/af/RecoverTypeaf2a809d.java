package cn.jiguang.af;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ao.RecoverTypeaf2aa26b;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.d.RecoverType5a68d11;
import cn.jiguang.i.RecoverType5a69fd5;
import cn.jiguang.i.RecoverType5a69fd6;
import cn.jiguang.internal.JConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecoverTypeaf2a809d extends RecoverType5a69fd5 {
   private Context recoverField_a;
   private Bundle recoverField_b;
   private String recoverField_c = "";
   private int recoverField_d = 0;
   private int recoverField_e = 0;
   private int recoverField_f = 0;

   @Override
   protected String recoverMethod_a_e501eb0d(Context var1) {
      this.recoverField_a = var1;
      return "JType";
   }

   @Override
   protected void recoverMethod_a_5dc9c75(String var1, Bundle var2) {
      this.recoverField_b = var2;
   }

   @Override
   protected boolean recoverMethod_e_9b79() {
      if (this.recoverField_b == null) {
         return false;
      }

      this.recoverField_c = this.recoverField_b.getString("name");
      this.recoverField_d = this.recoverField_b.getInt("custom", 0);
      this.recoverField_e = this.recoverField_b.getInt("dynamic", 0);
      this.recoverField_f = this.recoverField_b.getInt("sdk_v", 0);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
         "JType",
         "parseBundle type:"
            + this.recoverField_c
            + ",custom:"
            + this.recoverField_d
            + ",dynamic:"
            + this.recoverField_e
            + ",sdkVersion:"
            + this.recoverField_f
      );
      boolean var1 = recoverMethod_a_d0b5208a(this.recoverField_a, this.recoverField_c, this.recoverField_d, this.recoverField_e, this.recoverField_f);
      if (!var1) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JType", "type [" + this.recoverField_c + "] data not change");
      } else {
         String var2 = this.recoverField_d + "," + this.recoverField_e;
         RecoverType5a69fd6.recoverMethod_a_9f8491a6(this.recoverField_a, this.recoverField_c, this.recoverField_f);
         RecoverType5a69fd6.recoverMethod_a_9d23c587(this.recoverField_a, this.recoverField_c, var2);
      }

      return var1;
   }

   @Override
   protected void recoverMethod_c_5ffc00fd(Context var1, String var2) {
   }

   @Override
   protected void recoverMethod_d_5ffc00fd(Context var1, String var2) {
      JSONObject var3 = this.recoverMethod_a_db5b7a0d(this.recoverField_c, this.recoverField_d, this.recoverField_e);
      if (var3 == null) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JType", "there are no data to report");
      } else {
         RecoverTypeaf2aa26b.recoverMethod_a_127ac70f(var1, var3);
         RecoverType5a69fd6.recoverMethod_f_5ffc00fd(var1, "JType" + this.recoverField_c);
      }
   }

   private static boolean recoverMethod_a_d0b5208a(Context var0, String var1, int var2, int var3, int var4) {
      if (!TextUtils.isEmpty(var1) && var2 >= 0 && var3 >= 0) {
         int var5 = RecoverType5a69fd6.recoverMethod_n_5ffc00f0(var0, var1);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JType", "[isTypeReportEnable],lastversion:" + var5 + ",curversion:" + var4 + ",type:" + var1);
         if (var5 != var4) {
            return true;
         }

         long var6 = RecoverType5a69fd6.recoverMethod_g_5ffc00f1(var0, "JType" + var1);
         long var8 = System.currentTimeMillis();
         if (Math.abs(var8 - var6) > 15724800000L) {
            return true;
         }

         String var10 = RecoverType5a69fd6.recoverMethod_m_a4ea6d43(var0, var1);
         String var11 = var2 + "," + var3;
         return !var10.equals(var11);
      } else {
         return false;
      }
   }

   private JSONObject recoverMethod_a_db5b7a0d(String var1, int var2, int var3) {
      try {
         JSONObject var4 = new JSONObject();
         JSONArray var5 = new JSONArray();
         JSONObject var6 = new JSONObject();
         var5.put(var2);
         var5.put(var3);
         if ("core".equals(var1)) {
            var5.put(JConstants.SDK_VERSION_INT);
            var5.put(this.recoverField_f);
         } else if ("push".equals(var1)) {
            var5.put(RecoverType5a68d10.recoverMethod_c_9b68());
            var5.put(this.recoverField_f);
         } else if ("JAD".equals(var1)) {
         }

         var6.put(var1, var5);
         var4.put("type", "sdk_type");
         var4.put("itime", RecoverType5a68d11.recoverMethod_b_f0554de7(this.recoverField_a));
         var4.put("sdk", var6);
         return var4;
      } catch (JSONException var7) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JType", "package json exception: " + var7.getMessage());
         return null;
      }
   }

   public static int recoverMethod_a_9b68() {
      return 1;
   }

   public static int recoverMethod_b_9b68() {
      if (RecoverType5a681cd.recoverMethod_b_9b79()) {
         return 1;
      } else {
         return RecoverType5a681cd.recoverField_c != JConstants.SDK_VERSION_INT ? 2 : 0;
      }
   }

   public static class Member8e8cd21a {
      public static int recoverMethod_a_4868d301(int var0, int var1, int var2) {
         return var2 & 0xFF | (var1 & 0xFF) << 2 | (var0 & 15) << 6;
      }
   }
}
