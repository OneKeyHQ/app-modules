package cn.jiguang.j;

import android.content.Context;
import android.os.Build.VERSION;
import android.text.TextUtils;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.al.RecoverTypeaf2a9723;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.at.RecoverTypeaf2ab52c;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.bk.RecoverTypeaf2b07c9;
import cn.jiguang.bk.RecoverTypeaf2b07cb;
import cn.jiguang.bk.RecoverTypeaf2b07d4;
import cn.jiguang.bk.RecoverTypeaf2b07d5;
import cn.jiguang.common.app.helper.RecoverType82ecf7ac;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import cn.jiguang.g.RecoverType5a69853;
import cn.jiguang.h.RecoverType5a69c14;
import cn.jiguang.i.RecoverType5a69fd6;
import cn.jiguang.i.RecoverType5a69fd7;
import cn.jiguang.internal.JConstants;
import cn.jiguang.l.RecoverType5a6ab19;
import cn.jiguang.m.RecoverType5a6aedb;
import cn.jiguang.net.HttpRequest;
import cn.jiguang.net.HttpResponse;
import cn.jiguang.net.HttpUtils;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecoverType5a6a398 {
   private static final String recoverField_b = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{107, 125, 122, 116, 111, 70, 112, 100, 115, 78, 93, 68, 44}
   );
   public static String recoverField_a = "";
   private Context recoverField_c;
   private int recoverField_d = 0;
   private String recoverField_e = "";
   private String recoverField_f = "";
   private static final Object recoverField_g = new Object();
   private static final String recoverField_h = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{
         88,
         84,
         109,
         120,
         101,
         43,
         31,
         15,
         122,
         109,
         37,
         116,
         7,
         21,
         125,
         61,
         56,
         123,
         64,
         85,
         106,
         96,
         56,
         114,
         94,
         15,
         110,
         97,
         57,
         126,
         64,
         24,
         115,
         108,
         99
      }
   );
   private static final String recoverField_i = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{
         121,
         68,
         84,
         105,
         123,
         44,
         62,
         31,
         67,
         124,
         59,
         115,
         38,
         5,
         68,
         44,
         37,
         127,
         97,
         70,
         22,
         55,
         98,
         102,
         100,
         67,
         72,
         55,
         107,
         120,
         62,
         71,
         73,
         54,
         103,
         102,
         41,
         90,
         68,
         108
      }
   );
   private static LinkedList recoverField_j = new LinkedList();

   private RecoverType5a6a398() {
   }

   public Integer recoverMethod_a_bd10275c() {
      return null;
   }

   public void recoverMethod_a_f0554df3(Context var1) {
      if (var1 == null) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDataConfigManager", "updateImmediately failed: context is null");
      } else {
         boolean var2 = RecoverType5a69fd6.recoverMethod_a_5ffc0101(var1, "JDataConfigManager");
         if (!var2) {
            RecoverType5a69fd6.recoverMethod_a_9f849567(var1, "JDataConfigManager", 0L);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDataConfigManager", "Reset business time for immediate update");
         }

         this.recoverMethod_a_b13261ab(var1, null);
      }
   }

   public static RecoverType5a6a398 recoverMethod_b_487b90d8() {
      return RecoverType5a6a398.Member368c2256.recoverField_a;
   }

   public void recoverMethod_a_b13261ab(final Context var1, final RecoverType5a6a397 var2) {
      this.recoverField_c = var1;
      RecoverTypeaf2aa628.recoverMethod_b_a4fc3973(new RecoverTypeaf2b0401() {
         @Override
         public void recoverMethod_a_9b75() {
            try {
               RecoverType5a6a398.this.recoverMethod_b_f0554df3(var1);
            } catch (Throwable var2x) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDataConfigManager", "request config error:" + var2x);
            }

            if (var2 != null) {
               var2.recoverMethod_a_9b75();
            }
         }
      });
   }

   public void recoverMethod_a_1a54e370(Context var1, int var2) {
      try {
         this.recoverField_c = var1;
         FutureTask var3 = new FutureTask(new RecoverType5a6a398.Member368c2255(var1));
         RecoverTypeaf2aa628.recoverMethod_b_a4fc3973(var3);
         var3.get(var2, TimeUnit.SECONDS);
      } catch (Throwable var4) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDataConfigManager", "requestConfigSync timeout");
      }
   }

   public synchronized void recoverMethod_b_f0554df3(Context var1) {
      if (this.recoverField_d >= 3) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDataConfigManager", "try more than 3 times,won't try again");
         RecoverType5a69fd6.recoverMethod_d_5ffc00fd(var1, "JDataConfigManager");
         this.recoverField_d = 0;
      } else {
         boolean var2 = RecoverType5a69fd6.recoverMethod_a_5ffc0101(var1, "JDataConfigManager");
         if (var2) {
            this.recoverMethod_c_f0554df3(var1);
            String var3 = this.recoverMethod_a_e96ea081(this.recoverField_e);
            if (!TextUtils.isEmpty(var3)) {
               if (this.recoverMethod_a_5ffc00f0(var1, var3) == 2000) {
                  RecoverTypeaf2b07c9.recoverMethod_a_e925fa0d("bac.catch", var3);
               }

               RecoverType5a69fd6.recoverMethod_d_5ffc00fd(var1, "JDataConfigManager");
               this.recoverField_d = 0;
            } else {
               this.recoverField_d++;
            }
         } else {
            this.recoverMethod_c_f0554df3(var1);
         }
      }
   }

   public void recoverMethod_c_f0554df3(Context var1) {
      synchronized (recoverField_g) {
         if (TextUtils.isEmpty(this.recoverField_e)) {
            String var3 = RecoverTypeaf2b07c9.recoverMethod_e_e96ea081("bac.catch");
            if (!TextUtils.isEmpty(var3)) {
               this.recoverMethod_a_5ffc00f0(var1, var3);
            }
         }
      }
   }

   private int recoverMethod_a_5ffc00f0(Context var1, String var2) {
      synchronized (recoverField_g) {
         if (!TextUtils.isEmpty(var2)) {
            int var10000;
            try {
               byte[] var4 = RecoverTypeaf2b07d4.recoverMethod_a_78a45faa(
                  var2, RecoverType5a69fd7.Member367e0ad4.recoverField_h, RecoverType5a69fd7.Member367e0ad4.recoverField_i
               );
               String var5 = new String(var4, "UTF-8");
               recoverMethod_a_e925fa09("JDataConfigManager", "serverConfig:" + var5);
               JSONObject var6 = new JSONObject(var5);
               int var7 = var6.optInt("code");
               String var8 = var6.optString("message");
               if (var7 == 2000) {
                  JSONObject var9 = var6.optJSONObject("data");
                  if (var9 != null) {
                     RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_c_9b75();
                     int var10 = var9.optInt("status");
                     this.recoverField_e = var9.optString("sign");
                     if (var10 == 0) {
                        JSONArray var11 = var9.optJSONArray("rt");
                        if (var11 != null) {
                           for (int var12 = 0; var12 < var11.length(); var12++) {
                              String var13 = var11.optString(var12);
                              if (!TextUtils.isEmpty(var13)) {
                                 String[] var14 = var13.split("-");
                                 String[] var15 = new String[]{var14[0], var14[1]};
                                 RecoverType5a69853.recoverMethod_a_487a337d().recoverField_b.add(var15);
                              }
                           }
                        }

                        JSONArray var20 = var9.optJSONArray("pl");
                        if (var20 != null) {
                           for (int var23 = 0; var23 < var20.length(); var23++) {
                              JSONObject var24 = var20.optJSONObject(var23);
                              if (var24 != null) {
                                 RecoverType5a6a396 var25 = new RecoverType5a6a396();
                                 var25.recoverMethod_a_1385ff(true);
                                 var25.recoverMethod_a_13462e(var24.optInt("ci"));
                                 var25.recoverMethod_b_13462e(var24.optInt("sc"));
                                 var25.recoverMethod_c_13462e(var24.optInt("rp"));
                                 var25.recoverMethod_e_13462e(var24.optInt("bc"));
                                 var25.recoverMethod_d_13462e(var24.optInt("sr"));
                                 var25.recoverMethod_f_13462e(var24.optInt("br"));
                                 var25.recoverMethod_g_13462e(var24.optInt("bg"));
                                 var25.recoverMethod_h_13462e(var24.optInt("md"));
                                 RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_a_18e5f6ce(var25);
                              }
                           }
                        }
                     } else {
                        RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDataConfigManager", "parse data config , config status is no");
                     }

                     JSONObject var19 = var9.optJSONObject("in_app");
                     if (null != var19) {
                        String var21 = var19.optString(recoverField_b);
                        RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDataConfigManager", "recv inapp file hash: " + var21);
                        if (!TextUtils.isEmpty(var21) && !"null".equals(var21) && JConstants.SDK_VERSION_INT > 284) {
                           RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_a_4abe089d(var1, 20002, 0, var21);
                        }
                     }

                     JSONObject var22 = var9.optJSONObject("extra");
                     this.recoverMethod_a_3c4029ab(var1, var22);
                  }
               } else {
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDataConfigManager", "[refreshConfig] data config code:" + var7 + " msg:" + var8);
               }

               var10000 = var7;
            } catch (Throwable var17) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDataConfigManager", "[refreshConfig] refreshConfig failed, errMsg: " + var17.getMessage());
               return 0;
            }

            return var10000;
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDataConfigManager", "[refreshConfig] data config is null");
            return 0;
         }
      }
   }

   private void recoverMethod_a_3c4029ab(Context var1, JSONObject var2) {
      if (null == var2) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDataConfigManager", "it can't process extra list, because no extra data");
      } else {
         JSONObject var3 = var2.optJSONObject("al");
         if (null != var3) {
            RecoverType82ecf7ac.recoverField_c = var3.optInt("priority", RecoverType82ecf7ac.recoverField_b);
         }

         this.recoverMethod_b_3c4029ab(var1, var2);
      }
   }

   private void recoverMethod_b_3c4029ab(Context var1, JSONObject var2) {
      if (null == var2) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDataConfigManager", "it can't process probe list, because no extra data");
      } else {
         JSONObject var3 = var2.optJSONObject("pb");
         if (var3 != null && !TextUtils.isEmpty(var3.optString("pb_sign"))) {
            this.recoverField_f = var3.optString("pb_sign");
            String var4 = var3.optString("list");
            var4 = !TextUtils.isEmpty(var4) && !TextUtils.equals("null", var4) ? var4 : "";
            if (!TextUtils.isEmpty(var4)) {
               RecoverType5a69c14.recoverMethod_a_487aa7dc().recoverMethod_e_5ffc00fd(var1, var4);
            }

            RecoverType5a69c14.Member366febd1 var5 = RecoverType5a69c14.recoverMethod_a_487aa7dc().recoverMethod_b_146fd67f();
            if (var5 != null && TextUtils.equals(this.recoverField_f, var5.recoverMethod_a_73cf11cb())) {
               var4 = RecoverType5a69c14.recoverMethod_a_487aa7dc().recoverMethod_b_e501eb0d(var1);
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDataConfigManager", "probe list not change, use cache list data");
            }

            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDataConfigManager", "probe list : " + var4);
            RecoverType5a69c14.Member366febd1 var6 = new RecoverType5a69c14.Member366febd1()
               .recoverMethod_b_8a0f6535(this.recoverField_f)
               .recoverMethod_a_8a0f6535(var4)
               .recoverMethod_a_49f21135(var3.optInt("ap", 1) != 0);
            RecoverType5a69c14.recoverMethod_a_487aa7dc().recoverMethod_a_a65619b3(var6);
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDataConfigManager", "it can't process probe list, because no pb data");
         }
      }
   }

   public static void recoverMethod_a_53d4850d(LinkedList var0) {
      if (RecoverType5a69fd7.Member367e0ad4.recoverField_a && var0 != null && var0.size() > 0) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDataConfigManager", "setTestUrls use data url :" + var0);
         recoverField_j.clear();
         recoverField_j.addAll(var0);
      }
   }

   private String recoverMethod_a_e96ea081(String var1) {
      try {
         String var2 = RecoverTypeaf2a9723.recoverMethod_a_e501eb0d(this.recoverField_c);
         if (TextUtils.isEmpty(var2)) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDataConfigManager", "request data config failed because can't get appKey");
            return null;
         }

         RecoverTypeaf2b07d5.recoverMethod_a_fa4794e4(this.recoverField_c, recoverField_j);

         for (Object recoveryElement_371 : recoverField_j) {
            String var4 = (String)recoveryElement_371;
            JSONObject var5 = new JSONObject();
            var5.put("ak", var2);
            var5.put("p", "a");
            var5.put("av", "4");
            var5.put("sign", var1);
            String var6 = RecoverType5a68d10.recoverMethod_m_e501eb0d(this.recoverField_c);
            var6 = TextUtils.isEmpty(var6) ? "" : var6;
            var5.put("file_version", var6);
            var5.put("manufacturer", RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_x_e501eb0d(this.recoverField_c));
            var5.put("rom", RecoverType5a6ab19.recoverMethod_a_e501eb0d(this.recoverField_c));
            var5.put("model", RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_u_e501eb0d(this.recoverField_c));
            String var7 = Integer.toString(VERSION.SDK_INT);
            var5.put("osversion", var7);
            if (!TextUtils.isEmpty(var7)
               && !var7.equalsIgnoreCase(
                  (String)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_c, RecoverType5a690d1.recoverMethod_aK_48794abf())
               )) {
               RecoverType5a6aedb.recoverMethod_a_487cedf5().recoverMethod_a_5ffc00fd(this.recoverField_c, var7);
            }

            String var8 = RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_z_e501eb0d(this.recoverField_c);
            if (!TextUtils.isEmpty(var8)) {
               var5.put("android_ver", var8);
            }

            String var9 = RecoverType5a681cd.recoverField_b;
            if (!TextUtils.isEmpty(var9)) {
               var5.put("jcore_ver", var9);
            }

            String var10 = RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_b_47921032(0);
            if (!TextUtils.isEmpty(var10)) {
               var5.put("jpush_ver", var10);
            }

            var5.put("pb_sign", this.recoverField_f);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDataConfigManager", "url:" + var4 + ", param json:" + var5.toString());
            HttpRequest var11 = new HttpRequest(var4);
            var11.setRequestProperty("Content-Type", "text/plain");
            var11.setRequestProperty("Accept", "application/json");
            var11.setRequestProperty("Charset", "UTF-8");
            String var12 = RecoverTypeaf2b07d4.recoverMethod_a_ed76890(
               var5.toString().getBytes(), RecoverType5a69fd7.Member367e0ad4.recoverField_h, RecoverType5a69fd7.Member367e0ad4.recoverField_i
            );
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDataConfigManager", "encryptStr:" + var12 + ", paramJson: " + var5);
            var11.setBody(var12);
            HttpResponse var13 = HttpUtils.httpPost(this.recoverField_c, var11);
            int var14 = var13.getResponseCode();
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDataConfigManager", "responseCode:" + var14);
            String var15 = var13.getResponseBody();
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDataConfigManager", "request data config code:" + var14 + ", body:" + var15);
            if (200 == var14 && !TextUtils.isEmpty(var15)) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDataConfigManager", "request data config success");
               return var15;
            }
         }
      } catch (Throwable var16) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDataConfigManager", "request data config exception:" + var16.getMessage());
      }

      return null;
   }

   public static void recoverMethod_a_e925fa09(String var0, String var1) {
      int var2 = 4000 - var0.length();

      while (var1.length() > var2) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(var0, var1.substring(0, var2));
         var1 = var1.substring(var2);
      }

      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(var0, var1);
   }

   static {
      recoverField_j.clear();
      recoverField_j.add(recoverField_h);
      recoverField_j.add(recoverField_i);
   }

   private class Member368c2255 implements Callable {
      private Context recoverField_b;

      public Member368c2255(Context var2) {
         this.recoverField_b = var2;
      }

      public Integer recoverMethod_a_bd10275c() {
         RecoverType5a6a398.this.recoverMethod_b_f0554df3(this.recoverField_b);
         return 0;
      }

      // $VF: synthetic method
      @Override
      public Object call() {
         return this.recoverMethod_a_bd10275c();
      }
   }

   private static final class Member368c2256 {
      private static final RecoverType5a6a398 recoverField_a = new RecoverType5a6a398();
   }
}
