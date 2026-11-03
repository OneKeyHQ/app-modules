package cn.jiguang.ac;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.al.RecoverTypeaf2a9723;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.bk.RecoverTypeaf2b07cb;
import cn.jiguang.bk.RecoverTypeaf2b07d5;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.d.RecoverType5a68d11;
import cn.jiguang.i.RecoverType5a69fd6;
import cn.jiguang.i.RecoverType5a69fd7;
import cn.jiguang.l.RecoverType5a6ab19;
import cn.jiguang.m.RecoverType5a6aedb;
import cn.jiguang.net.HttpRequest;
import cn.jiguang.net.HttpResponse;
import cn.jiguang.net.HttpUtils;
import java.util.LinkedList;
import org.json.JSONObject;

public class RecoverTypeaf2a755c {
   public static String recoverField_a = "";
   private static final String recoverField_b = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{
         113,
         124,
         98,
         97,
         67,
         26,
         54,
         39,
         112,
         114,
         81,
         80,
         112,
         38,
         124,
         97,
         69,
         83,
         113,
         38,
         117,
         127,
         31,
         76,
         123,
         123,
         57,
         103,
         1,
         15,
         120,
         120,
         102,
         118,
         71,
         67,
         54,
         123,
         98,
         112,
         68,
         85,
         106
      }
   );
   private static final String recoverField_c = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{
         72,
         109,
         124,
         102,
         98,
         10,
         15,
         54,
         110,
         117,
         112,
         64,
         73,
         52,
         97,
         102,
         103,
         6,
         14,
         115,
         120,
         99,
         98,
         88,
         14,
         122,
         102,
         57,
         125,
         82,
         83,
         54,
         126,
         39,
         62,
         81,
         80,
         105,
         111,
         97,
         114,
         31,
         83,
         109,
         105,
         98,
         100,
         67
      }
   );
   private static LinkedList recoverField_d = new LinkedList();

   private RecoverTypeaf2a755c() {
   }

   public static RecoverTypeaf2a755c recoverMethod_a_c68e9bca() {
      return RecoverTypeaf2a755c.Member8e62931a.recoverField_a;
   }

   public static void recoverMethod_a_5ffc00fd(Context var0, String var1) {
      try {
         if (TextUtils.isEmpty(var1)) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocationHelper", "parseConfig empty");
            return;
         }

         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocationHelper", "parseConfig:" + var1);
         JSONObject var2 = new JSONObject(var1);
         int var3 = var2.optInt("code");
         if (var3 == 0) {
            RecoverTypeaf2a755b.recoverField_j = var2.optString("pkgList", "");
            RecoverTypeaf2a755b.recoverField_k = var2.optString("servList", "");
            String var4 = var2.optString("enable", "0,0,0");
            if (!TextUtils.isEmpty(var4)) {
               String[] var5 = var4.split(",");
               if (var5.length >= 3) {
                  RecoverTypeaf2a755b.recoverField_b = var5[0].equals("1");
                  RecoverTypeaf2a755b.recoverField_a = var5[1].equals("1");
                  RecoverTypeaf2a755b.recoverField_c = var5[2].equals("1");
               }
            }

            RecoverTypeaf2a755b.recoverField_l = var2.optInt("r_interval", 3600);
            RecoverType5a69fd6.recoverMethod_c_9f849567(var0, "JLocationv2", RecoverTypeaf2a755b.recoverField_l * 1000);
            String var9 = var2.optString("b_interval", "800,800,800");
            if (!TextUtils.isEmpty(var9)) {
               try {
                  String[] var6 = var9.split(",");
                  if (var6.length >= 3) {
                     RecoverTypeaf2a755b.recoverField_e = Integer.parseInt(var6[0]);
                     RecoverTypeaf2a755b.recoverField_d = Integer.parseInt(var6[1]);
                     RecoverTypeaf2a755b.recoverField_f = Integer.parseInt(var6[2]);
                     RecoverType5a69fd6.recoverMethod_b_9f849567(var0, "JLocationv2_g", RecoverTypeaf2a755b.recoverField_e * 1000);
                     RecoverType5a69fd6.recoverMethod_b_9f849567(var0, "JLocationv2_w", RecoverTypeaf2a755b.recoverField_d * 1000);
                     RecoverType5a69fd6.recoverMethod_b_9f849567(var0, "JLocationv2_c", RecoverTypeaf2a755b.recoverField_f * 1000);
                  }
               } catch (Throwable var7) {
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocationHelper", "parseConfig parse interval error:" + var7);
               }
            }

            RecoverTypeaf2a755b.recoverField_h = var2.optInt("wi_level", -90);
            RecoverTypeaf2a755b.recoverField_g = var2.optInt("wi_count", 20);
            RecoverTypeaf2a755b.recoverField_i = var2.optInt("wi_percent", 30);
            RecoverTypeaf2a755b.recoverField_m = var2.optInt("g_interval", 240);
            RecoverTypeaf2a755b.recoverField_n = var2.optInt("g_distance", 800);
         } else {
            RecoverType5a69fd6.recoverMethod_b_9f849567(var0, "JLocationv2_g", RecoverTypeaf2a755b.recoverField_e * 1000);
            RecoverType5a69fd6.recoverMethod_b_9f849567(var0, "JLocationv2_w", RecoverTypeaf2a755b.recoverField_d * 1000);
            RecoverType5a69fd6.recoverMethod_b_9f849567(var0, "JLocationv2_c", RecoverTypeaf2a755b.recoverField_f * 1000);
            RecoverType5a69fd6.recoverMethod_c_9f849567(var0, "JLocationHelper", RecoverTypeaf2a755b.recoverField_l * 1000);
         }
      } catch (Throwable var8) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocationHelper", "parseConfig error:" + var8);
         RecoverType5a69fd6.recoverMethod_b_9f849567(var0, "JLocationv2_g", RecoverTypeaf2a755b.recoverField_e * 1000);
         RecoverType5a69fd6.recoverMethod_b_9f849567(var0, "JLocationv2_w", RecoverTypeaf2a755b.recoverField_d * 1000);
         RecoverType5a69fd6.recoverMethod_b_9f849567(var0, "JLocationv2_c", RecoverTypeaf2a755b.recoverField_f * 1000);
         RecoverType5a69fd6.recoverMethod_c_9f849567(var0, "JLocationHelper", RecoverTypeaf2a755b.recoverField_l * 1000);
      }
   }

   public static void recoverMethod_a_53d4850d(LinkedList var0) {
      if (RecoverType5a69fd7.Member367e0ad4.recoverField_a && var0 != null && var0.size() > 0) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocationHelper", "setTestUrls use loc url :" + var0);
         recoverField_d.clear();
         recoverField_d.addAll(var0);
      }
   }

   public Integer recoverMethod_b_bd10275c() {
      return null;
   }

   public void recoverMethod_a_f0554df3(Context var1) {
      try {
         RecoverTypeaf2aa628.recoverMethod_e_a4fc3973(new RecoverTypeaf2a755c.Member8e629319(var1));
      } catch (Throwable var3) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JLocationHelper", "updateImmediately failed: " + var3.getMessage());
      }
   }

   public boolean recoverMethod_b_f0554df7(Context var1) {
      String var2 = this.recoverMethod_c_e501eb0d(var1);
      if (!TextUtils.isEmpty(var2)) {
         recoverMethod_a_5ffc00fd(var1, var2);
         RecoverType5a69fd6.recoverMethod_t_5ffc00fd(var1, var2);
         return true;
      } else {
         return false;
      }
   }

   public String recoverMethod_c_e501eb0d(Context var1) {
      try {
         boolean var2 = RecoverType5a68d11.recoverMethod_a_f0554df7(var1);
         if (!var2) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocationHelper", "request l config failed because is not validRegistered");
            return null;
         }

         String var3 = RecoverTypeaf2a9723.recoverMethod_a_e501eb0d(var1);
         if (TextUtils.isEmpty(var3)) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocationHelper", "request l config failed because can't get appKey");
            return null;
         }

         long var4 = RecoverType5a68d10.recoverMethod_f_f0554de7(var1);
         if (var4 == 0L) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocationHelper", "request l config failed because can't get uid");
            return null;
         }

         String var6 = RecoverType5a68d10.recoverMethod_j_e501eb0d(var1);
         if (TextUtils.isEmpty(var6)) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocationHelper", "request l config failed because need register first");
            return null;
         }

         RecoverType5a69fd6.recoverMethod_d_5ffc00fd(var1, "JLocationHelper");
         RecoverTypeaf2b07d5.recoverMethod_a_fa4794e4(var1, recoverField_d);

         for (Object recoveryElement_258 : recoverField_d) {
            String var8 = (String)recoveryElement_258;
            JSONObject var9 = new JSONObject();
            var9.put("appkey", var3);
            var9.put("uid", var4);
            var9.put("platform", "a");
            var9.put("user_enable", RecoverType5a69fd6.recoverMethod_i_5ffc0101(var1, "JLocation"));
            var9.put("rom", RecoverType5a6ab19.recoverMethod_a_e501eb0d(var1));
            var9.put("manufacturer", RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_x_e501eb0d(var1));
            var9.put("model", RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_u_e501eb0d(var1));
            int var10 = -1;

            try {
               var10 = Integer.valueOf(RecoverType5a6aedb.recoverMethod_a_487cedf5().recoverMethod_c_e501eb0d(var1));
            } catch (Throwable var20) {
            }

            var9.put("osversion", var10);
            var9.put("itime", System.currentTimeMillis());
            var9.put("apitype", 0);
            var9.put("coreversion", RecoverType5a681cd.recoverField_b);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocationHelper", "url:" + var8 + ", param json:" + var9.toString());
            String var11 = RecoverTypeaf2b07d5.recoverMethod_e_e96ea081(var9.toString());
            String var12 = RecoverTypeaf2b07d5.recoverMethod_e_e96ea081(var6);
            String var13 = RecoverTypeaf2b07d5.recoverMethod_e_e96ea081(var4 + var12 + var11);
            if (TextUtils.isEmpty(var13)) {
               return null;
            }

            String var14 = var4 + ":" + var13;
            var14 = Base64.encodeToString(var14.getBytes(), 10);
            if (TextUtils.isEmpty(var14)) {
               return null;
            }

            HttpRequest var15 = new HttpRequest(var8);
            var15.setRequestProperty("Content-Type", "application/json");
            var15.setRequestProperty("Accept", "application/json");
            var15.setRequestProperty("X-Http-Platform", "android");
            var15.setRequestProperty("X-Http-Appkey", var3);
            var15.setRequestProperty("Authorization", "Basic " + var14);
            var15.setRequestProperty("Charset", "UTF-8");
            String var16 = RecoverType5a68d10.recoverMethod_b_e96ea081(var9.toString());
            var15.setBody(var16);
            HttpResponse var17 = HttpUtils.httpPost(var1, var15);
            int var18 = var17.getResponseCode();
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocationHelper", "responseCode:" + var18);
            String var19 = var17.getResponseBody();
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocationHelper", "responseBody:" + var19);
            if (200 == var18 && !TextUtils.isEmpty(var19)) {
               var19 = RecoverType5a68d10.recoverMethod_c_e96ea081(var19);
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocationHelper", "request l config success,response body:" + var19);
               return var19;
            }
         }
      } catch (Throwable var21) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocationHelper", "request l config exception:" + var21.getMessage());
      }

      return null;
   }

   static {
      recoverField_d.clear();
      recoverField_d.add(recoverField_b);
      recoverField_d.add(recoverField_c);
   }

   private class Member8e629319 extends RecoverTypeaf2b0401 {
      private Context recoverField_b;

      public Member8e629319(Context var2) {
         this.recoverField_b = var2;
         this.recoverField_h = "JLocationHelper#RequestConfigAction";
      }

      @Override
      public void recoverMethod_a_9b75() {
         RecoverTypeaf2a755c.recoverMethod_a_c68e9bca().recoverMethod_b_f0554df7(this.recoverField_b);
      }
   }

   private static final class Member8e62931a {
      private static final RecoverTypeaf2a755c recoverField_a = new RecoverTypeaf2a755c();
   }
}
