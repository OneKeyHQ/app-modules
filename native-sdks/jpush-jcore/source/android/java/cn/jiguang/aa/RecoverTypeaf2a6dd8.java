package cn.jiguang.aa;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import cn.jiguang.ab.RecoverTypeaf2a7199;
import cn.jiguang.ab.RecoverTypeaf2a719a;
import cn.jiguang.ac.RecoverTypeaf2a755a;
import cn.jiguang.al.RecoverTypeaf2a9729;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ao.RecoverTypeaf2aa26b;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.bh.RecoverTypeaf2afc7e;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.bk.RecoverTypeaf2b07c9;
import cn.jiguang.bk.RecoverTypeaf2b07ce;
import cn.jiguang.bk.RecoverTypeaf2b07d0;
import cn.jiguang.bk.RecoverTypeaf2b07d6;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.g.RecoverType5a69853;
import cn.jiguang.i.RecoverType5a69fd5;
import cn.jiguang.i.RecoverType5a69fd6;
import cn.jiguang.k.RecoverType5a6a75b;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecoverTypeaf2a6dd8 extends RecoverType5a69fd5 {
   private Context recoverField_a;
   private boolean recoverField_b = false;
   private boolean recoverField_c = false;
   private boolean recoverField_d = false;
   private boolean recoverField_e = false;
   private boolean recoverField_f = false;
   private boolean recoverField_g = false;
   private boolean recoverField_h = false;
   private boolean recoverField_i = false;

   private RecoverTypeaf2a6dd8() {
   }

   public static RecoverTypeaf2a6dd8 recoverMethod_a_c68db2ce() {
      return RecoverTypeaf2a6dd8.Member8e465c97.recoverField_a;
   }

   @Override
   protected String recoverMethod_a_e501eb0d(Context var1) {
      this.recoverField_a = var1;
      return "JLocation";
   }

   @Override
   protected boolean recoverMethod_e_f0554df7(Context var1) {
      return true;
   }

   public void recoverMethod_a_1385ff(boolean var1) {
      this.recoverField_i = var1;
   }

   public boolean recoverMethod_b_9b79() {
      return this.recoverField_b;
   }

   @Override
   protected void recoverMethod_a_27ad051f(String var1, JSONObject var2) {
      int var3 = var2.optInt("cmd");
      JSONObject var4 = var2.optJSONObject("content");
      if (var3 == 5) {
         int var5 = var4.optInt("en_config", 0);
         this.recoverField_b = (var5 & 1) != 0;
         this.recoverField_c = (var5 & 2) != 0;
         this.recoverField_d = (var5 & 4) != 0;
         this.recoverField_h = this.recoverField_b || this.recoverField_c || this.recoverField_d;
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
            "JLocation", "gpsEnable=" + this.recoverField_b + " ,cellEnable=" + this.recoverField_c + " ,wifiEnable=" + this.recoverField_d
         );
         if (this.recoverField_h) {
            long var6 = var4.optLong("frequency", 0L) * 1000L;
            RecoverType5a69fd6.recoverMethod_c_9f849567(this.recoverField_a, "JLocation", var6);
         }
      } else if (var3 == 45) {
         long var8 = var4.optLong("interval", 0L) * 1000L;
         RecoverType5a69fd6.recoverMethod_b_9f849567(this.recoverField_a, "JLocation", var8);
      }
   }

   @Override
   protected boolean recoverMethod_d_9b79() {
      return this.recoverField_i ? this.recoverField_h : true;
   }

   @Override
   protected boolean recoverMethod_c_9b79() {
      return true;
   }

   @Override
   protected boolean recoverMethod_a_5ffc0101(Context var1, String var2) {
      return RecoverType5a69fd6.recoverMethod_a_5ffc0101(var1, var2);
   }

   @Override
   protected void recoverMethod_c_5ffc00fd(Context var1, String var2) {
      RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_c_5d527811(2083);
      List var3 = this.recoverMethod_b_bb3fa0d7(var1, Integer.MAX_VALUE);
      List var4 = this.recoverMethod_g_f8c27ff4(var1);
      RecoverTypeaf2a7199 var5 = this.recoverMethod_h_5eda1dab(var1);
      JSONObject var6 = this.recoverMethod_a_46601d7f(var3, var4, var5);
      JSONObject var7 = RecoverTypeaf2b07c9.recoverMethod_a_fd5e2d6d("rl.catch");
      JSONObject var8 = this.recoverMethod_a_ba478063(var6, var7);
      if (var8 != null) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocation", "save json:" + var8);
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_c_5d527811(2060);
         RecoverTypeaf2b07c9.recoverMethod_a_27ad0523("rl.catch", var8);
      }

      Bundle var9 = this.recoverMethod_b_3df933f7(var1);
      RecoverTypeaf2a9729.recoverMethod_a_c692b39d().recoverMethod_a_5dc9c75("deviceinfo", var9);
      super.recoverMethod_c_5ffc00fd(var1, var2);
   }

   private List recoverMethod_g_f8c27ff4(Context var1) {
      List var2 = null;
      if (!this.recoverField_c) {
         this.recoverField_f = RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_d_134632(1501);
      }

      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocation", "configCellEnable:" + this.recoverField_f + " cmdCellEnable=" + this.recoverField_c);
      if (this.recoverField_c || this.recoverField_f) {
         try {
            boolean var3 = RecoverTypeaf2b07d0.recoverMethod_a_5ffc0101(var1, "android.permission.ACCESS_COARSE_LOCATION");
            boolean var4 = RecoverTypeaf2b07d0.recoverMethod_a_5ffc0101(var1, "android.permission.ACCESS_FINE_LOCATION");
            boolean var5 = RecoverType5a68d10.recoverMethod_o_f0554df7(var1);
            if (!var3) {
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_d_5d527811(2041);
            }

            if (!var4) {
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_d_5d527811(2040);
            }

            if (!var3 && !var4) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
                  "JLocation", "collect cell failed because has no android.permission.ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION"
               );
            } else if (var5) {
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_d_5d527811(2044);
               FutureTask var6 = new FutureTask(new RecoverTypeaf2a6dd8.Member8e465c95());
               RecoverTypeaf2aa628.recoverMethod_e_a4fc3973(var6);
               var2 = (List)var6.get(20000L, TimeUnit.MILLISECONDS);
               short var7 = 2042;
               if (null != var2) {
                  var7 = 2035;
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocation", "collect cell success:" + var2.toString());
               }

               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_d_5d527811(Integer.valueOf(var7));
            } else {
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_d_5d527811(2038);
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocation", "collect cell failed because not enable to collect cell in background");
            }
         } catch (Throwable var8) {
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_d_5d527811(2043);
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocation", "collect cell throwable:" + var8.getMessage());
         }
      }

      return var2;
   }

   public List recoverMethod_b_bb3fa0d7(Context var1, int var2) {
      List var3 = null;

      try {
         if (!this.recoverField_d) {
            this.recoverField_g = RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_d_134632(1505);
         }

         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocation", "configWIfiEnable:" + this.recoverField_g + " cmdWifiEnable=" + this.recoverField_d);
         if (this.recoverField_d || this.recoverField_g) {
            if (RecoverTypeaf2b07d0.recoverMethod_b_5ffc0101(var1, "android.permission.ACCESS_WIFI_STATE")) {
               if (RecoverTypeaf2b07c1.recoverMethod_h_f0554df7(var1)) {
                  RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_e_5d527811(2044);
                  var3 = RecoverTypeaf2a6ddb.recoverMethod_a_5616a063(var1, var2, this.recoverField_d);
                  if (null != var3 && var3.size() > 0) {
                     RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_e_5d527811(2035);
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocation", "collect wifi success:" + var3);
                  } else {
                     RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_e_5d527811(2042);
                  }
               } else {
                  RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_e_5d527811(2034);
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocation", "collect wifi failed because not enable to collect wifi list");
               }
            } else {
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_e_5d527811(2033);
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocation", "collect wifi failed because has no android.permission.ACCESS_WIFI_STATE");
            }
         }
      } catch (Exception var5) {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_e_5d527811(2043);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocation", "collect wifi failed :" + var5.getMessage());
      }

      return var3;
   }

   public ArrayList recoverMethod_c_badbaf4a(Context var1, int var2) {
      List var3 = this.recoverMethod_b_bb3fa0d7(var1, Integer.MAX_VALUE);
      ArrayList var4 = null;
      if (var3 != null) {
         var4 = new ArrayList();

         for (Object recoveryElement_220 : var3) {
            RecoverTypeaf2a719a var6 = (RecoverTypeaf2a719a)recoveryElement_220;
            String var7 = var6.recoverField_b;
            if (!TextUtils.isEmpty(var7) && !var4.contains(var7)) {
               var4.add(var7);
               if (var4.size() == var2) {
                  break;
               }
            }
         }
      }

      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocation", "getWifiNameArray:" + var4);
      return var4;
   }

   private RecoverTypeaf2a7199 recoverMethod_h_5eda1dab(Context var1) {
      RecoverTypeaf2a7199 var2 = null;
      if (!this.recoverField_b) {
         this.recoverField_e = RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_d_134632(1502);
      }

      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocation", "configGpsEnable:" + this.recoverField_e + " cmdGpsEnable=" + this.recoverField_b);
      if (this.recoverField_b || this.recoverField_e) {
         try {
            boolean var3 = RecoverTypeaf2b07d0.recoverMethod_a_5ffc0101(var1, "android.permission.ACCESS_COARSE_LOCATION");
            boolean var4 = RecoverTypeaf2b07d0.recoverMethod_a_5ffc0101(var1, "android.permission.ACCESS_FINE_LOCATION");
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocation", "coares:" + var3);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocation", "fine:" + var4);
            boolean var5 = RecoverType5a68d10.recoverMethod_o_f0554df7(var1);
            if (!var3) {
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_b_5d527811(2041);
            }

            if (!var4) {
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_b_5d527811(2040);
            }

            if (!var3 && !var4) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
                  "JLocation", "collect gps failed because has no android.permission.ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION"
               );
            } else if (var5) {
               if (RecoverTypeaf2a6dda.recoverMethod_a_5ed9a98a(var1).recoverMethod_e_9b79()) {
                  RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_b_5d527811(2044);
                  var2 = RecoverTypeaf2a6dda.recoverMethod_a_5ed9a98a(var1).recoverMethod_d_c68e272d();
                  if (var2 == null) {
                     FutureTask var6 = new FutureTask(new RecoverTypeaf2a6dd8.Member8e465c96());
                     RecoverTypeaf2aa628.recoverMethod_e_a4fc3973(var6);
                     var2 = (RecoverTypeaf2a7199)var6.get(31000L, TimeUnit.MILLISECONDS);
                     if (var2 != null) {
                        RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocation", "collect gps success:" + var2.toString());
                        RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_b_5d527811(2035);
                     } else {
                        RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocation", "collect gps failed");
                        RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_b_5d527811(2042);
                     }
                  } else {
                     RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_b_5d527811(2049);
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocation", "load  last gps success:" + var2.toString());
                  }

                  if (var2 != null) {
                     RecoverType5a69fd6.recoverMethod_o_5ffc00fd(var1, var2.recoverField_a + "," + var2.recoverField_d + "," + var2.recoverField_c);
                  }
               } else {
                  RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_b_5d527811(2048);
               }
            } else {
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_b_5d527811(2038);
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocation", "collect gps failed because not enable to collect gps in background");
            }
         } catch (Throwable var7) {
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_b_5d527811(2043);
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocation", "collect gps throwable:" + var7.getMessage());
         }
      }

      return var2;
   }

   public int recoverMethod_b_1385f2(boolean var1) {
      int var2 = RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_b_134621(1500);
      var2 = Math.max(var2, 0);
      return var1 ? var2 + 30000 : var2;
   }

   @Override
   protected void recoverMethod_d_5ffc00fd(Context var1, String var2) {
      RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_c_5d527811(2084);
      this.recoverField_h = false;
      this.recoverField_d = false;
      this.recoverField_b = false;
      this.recoverField_c = false;
      this.recoverField_i = false;
      JSONObject var3 = RecoverTypeaf2b07c9.recoverMethod_a_fd5e2d6d("rl.catch");
      if (var3 != null && !"{}".equals(var3.toString())) {
         JSONArray var4 = var3.optJSONArray("content");

         try {
            if (var4 == null || var4.length() == 0 || var4.length() == 1 && "{}".equals(var4.getJSONObject(0).toString())) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocation", "there are no content data to report");
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_c_5d527811(2063);
               return;
            }

            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_c_5d527811(2064);
            RecoverTypeaf2aa26b.recoverMethod_a_127ac70f(var1, var4);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocation", "report success.clean cache json=" + var4);
            RecoverTypeaf2b07c9.recoverMethod_d_aad3b1ff("rl.catch");
         } catch (Throwable var6) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocation", "report failed, " + var6.getMessage());
         }

         super.recoverMethod_d_5ffc00fd(var1, var2);
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocation", "there are no data to report");
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_c_5d527811(2062);
      }
   }

   public Bundle recoverMethod_b_3df933f7(Context var1) {
      String var2 = RecoverType5a69fd6.recoverMethod_e_e501eb0d(var1);
      Bundle var3;
      if (TextUtils.isEmpty(var2)) {
         var3 = this.recoverMethod_a_77a5f0f3(0L, 200.0, 200.0);
      } else {
         String[] var4 = var2.split(",");
         var3 = this.recoverMethod_a_77a5f0f3(Long.valueOf(var4[0]), Double.valueOf(var4[1]), Double.valueOf(var4[2]));
      }

      String var7 = RecoverType5a69fd6.recoverMethod_f_e501eb0d(var1);
      if (!TextUtils.isEmpty(var7)) {
         var3.putString("wss", var7);
      }

      String var5 = RecoverTypeaf2b07d6.recoverMethod_a_5e799b7f(var1, this.recoverField_h);
      String var6 = RecoverTypeaf2b07ce.recoverMethod_a_e501eb0d(var1);
      var3.putString("operator", var5);
      var3.putString("networkType", var6);
      return var3;
   }

   private JSONObject recoverMethod_a_ba478063(JSONObject var1, JSONObject var2) {
      try {
         if (var1 == null || "{}".equals(var1.toString())) {
            return var2;
         }

         JSONArray var3 = new JSONArray();

         try {
            if (var2 == null) {
               var2 = new JSONObject();
            } else {
               var3 = var2.getJSONArray("content");
            }
         } catch (Throwable var9) {
         }

         for (int var4 = 0; var4 < var3.length(); var4++) {
            JSONObject var5 = var3.optJSONObject(var4);
            if (this.recoverField_d || this.recoverField_g) {
               JSONArray var6 = var5.optJSONArray("wifi");
               JSONArray var7 = var1.optJSONArray("wifi");
               if (var6 != null && var6.equals(var7)) {
                  var1.remove("wifi");
               }
            }

            if (this.recoverField_c || this.recoverField_f) {
               JSONArray var13 = var5.optJSONArray("cell");
               JSONArray var15 = var1.optJSONArray("cell");
               if (var13 != null && var13.equals(var15)) {
                  var1.remove("cell");
               }
            }

            if (this.recoverField_b || this.recoverField_e) {
               JSONArray var14 = var5.optJSONArray("gps");
               JSONArray var16 = var1.optJSONArray("gps");
               if (var14 != null && var14.equals(var16)) {
                  var1.remove("gps");
               }
            }
         }

         if (var1.length() != 0) {
            try {
               if (RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_d_134632(1504)) {
                  var1.put("network_type", RecoverTypeaf2b07ce.recoverMethod_c_e501eb0d(this.recoverField_a));
               }

               if (RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_d_134632(1503)) {
                  var1.put("local_dns", RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_e_73cf11cb());
               }

               String var11 = RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_a_a9514c81(this.recoverField_h);
               String var12 = RecoverType5a6a75b.recoverMethod_a_487c0575().recoverMethod_b_e501eb0d(this.recoverField_a);
               if (!TextUtils.isEmpty(var12)) {
                  var1.put("ipv6", var12);
               }

               if (!TextUtils.isEmpty(var11)) {
                  var1.put("ip2", var11);
               }

               RecoverType5a68d10.recoverMethod_a_1b76323(this.recoverField_a, var1, "loc_info");
               var3.put(var1);
            } catch (JSONException var8) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocation", "package json exception:" + var8.getMessage());
            }
         }

         var2.put("content", var3);
      } catch (Throwable var10) {
      }

      return var2;
   }

   private JSONObject recoverMethod_a_46601d7f(List var1, List var2, RecoverTypeaf2a7199 var3) {
      JSONObject var4 = new JSONObject();
      if ((this.recoverField_d || this.recoverField_g) && var1 != null && !var1.isEmpty()) {
         JSONArray var5 = new JSONArray();

         for (int var6 = 0; var6 < var1.size(); var6++) {
            try {
               JSONObject var7 = new JSONObject();
               RecoverTypeaf2a719a var8 = (RecoverTypeaf2a719a)var1.get(var6);
               var7.put("itime", var8.recoverField_a);
               if (var8.recoverField_c != null) {
                  var7.put("tag", var8.recoverField_c);
               }

               var7.put("ssid", var8.recoverField_b);
               var7.put("mac_address", var8.recoverField_e);
               var7.put("signal_strength", var8.recoverField_d);
               var7.put("age", 0);
               var5.put(var7);
               var4.put("wifi", var5);
            } catch (Throwable var10) {
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_e_5d527811(2057);
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocation", "package wifi json exception:" + var10.getMessage());
            }
         }

         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_e_5d527811(2058);
      } else {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_e_5d527811(2059);
      }

      if ((this.recoverField_c || this.recoverField_f) && var2 != null) {
         try {
            JSONArray var12 = new JSONArray();

            for (Object recoveryElement_475 : var2) {
               RecoverTypeaf2a755a var16 = (RecoverTypeaf2a755a)recoveryElement_475;
               if (var16 != null) {
                  JSONObject var17 = new JSONObject();
                  var17.put("itime", var16.recoverField_a);
                  var17.put("radio_type", var16.recoverField_i);
                  var17.put("generation", var16.recoverField_h);
                  var17.put("carrier", var16.recoverField_g);
                  var17.put("mobile_country_code", var16.recoverField_d);
                  var17.put("mobile_network_code", var16.recoverField_e);
                  var17.put("signal_strength", var16.recoverField_j);
                  var17.put("cell_id", var16.recoverField_b);
                  var17.put("location_area_code", var16.recoverField_c);
                  var12.put(var17);
               }
            }

            var4.put("cell", var12);
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_d_5d527811(2058);
         } catch (Throwable var11) {
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_d_5d527811(2057);
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocation", "package cell json exception:" + var11.getMessage());
         }
      } else {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_d_5d527811(2059);
      }

      if ((this.recoverField_b || this.recoverField_e) && var3 != null) {
         try {
            JSONArray var13 = new JSONArray();
            JSONObject var15 = new JSONObject();
            var15.put("itime", var3.recoverField_a);
            var15.put("tag", var3.recoverField_b);
            var15.put("lat", var3.recoverField_c);
            var15.put("lng", var3.recoverField_d);
            var15.put("alt", var3.recoverField_e);
            var15.put("bear", var3.recoverField_f);
            var15.put("acc", var3.recoverField_g);
            var13.put(var15);
            var4.put("gps", var13);
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_b_5d527811(2058);
         } catch (Throwable var9) {
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_b_5d527811(2057);
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocation", "package gps json exception:" + var9.getMessage());
         }
      } else {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_b_5d527811(2059);
      }

      return var4;
   }

   private Bundle recoverMethod_a_77a5f0f3(long var1, double var3, double var5) {
      Bundle var7 = new Bundle();
      var7.putLong("time", var1);
      var7.putDouble("lot", var3);
      var7.putDouble("lat", var5);
      return var7;
   }

   // $VF: synthetic method
   @Override
   public Object recoverMethod_f_f23f0d9f(Context var1) {
      return this.recoverMethod_b_3df933f7(var1);
   }

   class Member8e465c95 implements Callable {
      public List recoverMethod_a_a9863e32() {
         try {
            RecoverTypeaf2a6dd9 var1 = new RecoverTypeaf2a6dd9(RecoverTypeaf2a6dd8.this.recoverField_a, RecoverTypeaf2a6dd8.this.recoverField_c);
            var1.recoverMethod_c_9b75();

            for (int var2 = 0; var2 < 20; var2++) {
               boolean var3 = var1.recoverMethod_b_9b79();
               if (var3) {
                  return var1.recoverMethod_a_a9863e32();
               }

               Thread.sleep(2000L);
            }
         } catch (Throwable var4) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocation", "JLocationCellInfo call failed:" + var4.getMessage());
         }

         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_d_5d527811(2045);
         return null;
      }

      // $VF: synthetic method
      @Override
      public Object call() {
         return this.recoverMethod_a_a9863e32();
      }
   }

   class Member8e465c96 implements Callable {
      public RecoverTypeaf2a7199 recoverMethod_a_c68e272d() {
         try {
            RecoverTypeaf2a6dda var1 = RecoverTypeaf2a6dda.recoverMethod_a_5ed9a98a(RecoverTypeaf2a6dd8.this.recoverField_a);
            var1.recoverMethod_c_9b75();

            for (int var2 = 0; var2 < 30; var2++) {
               boolean var3 = var1.recoverMethod_b_9b79();
               if (var3) {
                  return var1.recoverMethod_a_c68e272d();
               }

               Thread.sleep(1000L);
            }
         } catch (Throwable var4) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocation", "JLocationGpsInfo call failed:" + var4.getMessage());
         }

         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_b_5d527811(2045);
         return null;
      }

      // $VF: synthetic method
      @Override
      public Object call() {
         return this.recoverMethod_a_c68e272d();
      }
   }

   private static final class Member8e465c97 {
      private static final RecoverTypeaf2a6dd8 recoverField_a = new RecoverTypeaf2a6dd8();
   }
}
