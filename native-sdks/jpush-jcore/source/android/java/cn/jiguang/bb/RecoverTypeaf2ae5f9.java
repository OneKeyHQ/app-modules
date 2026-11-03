package cn.jiguang.bb;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.ar.RecoverTypeaf2aadab;
import cn.jiguang.at.RecoverTypeaf2ab531;
import cn.jiguang.at.RecoverTypeaf2ab532;
import cn.jiguang.bc.RecoverTypeaf2ae9b9;
import cn.jiguang.be.RecoverTypeaf2af143;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.bj.RecoverTypeaf2b0403;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.bk.RecoverTypeaf2b07c4;
import cn.jiguang.bk.RecoverTypeaf2b07c9;
import cn.jiguang.bk.RecoverTypeaf2b07d7;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import cn.jiguang.net.HttpRequest;
import cn.jiguang.net.HttpResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecoverTypeaf2ae5f9 {
   public static long recoverField_a = 314572800L;
   public static long recoverField_b = 7000L;
   public static long recoverField_c = 104857600L;
   public static long recoverField_d = 3000L;
   private JSONObject recoverField_f;
   private long recoverField_g;
   private long recoverField_h;
   private long recoverField_i;
   private long recoverField_j;
   private long recoverField_k;
   private long recoverField_l;
   private Context recoverField_m;
   private Boolean recoverField_n;
   private long recoverField_o;
   private long recoverField_p = 0L;
   private long recoverField_q = 3000L;
   public static String recoverField_e = "sync_net_monitor";

   private RecoverTypeaf2ae5f9() {
      this.recoverField_m = RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d();
      this.recoverMethod_a_1385ff(false);
   }

   public static RecoverTypeaf2ae5f9 recoverMethod_a_c69c3ecd() {
      return RecoverTypeaf2ae5f9.Member90095076.recoverField_a;
   }

   public void recoverMethod_a_1385ff(boolean var1) {
      if ((Boolean)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_m, RecoverType5a690d1.recoverMethod_n_48794abf())) {
         if (var1 || this.recoverField_n == null) {
            this.recoverField_n = true;
            long var2 = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_m, RecoverType5a690d1.recoverMethod_o_48794abf());
            if (var2 > 0L) {
               recoverField_b = var2;
            }

            long var4 = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_m, RecoverType5a690d1.recoverMethod_p_48794abf());
            if (var4 > 0L) {
               recoverField_a = var4;
            }

            long var6 = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_m, RecoverType5a690d1.recoverMethod_q_48794abf());
            if (var6 > 0L) {
               recoverField_d = var6;
            }

            long var8 = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_m, RecoverType5a690d1.recoverMethod_r_48794abf());
            if (var8 > 0L) {
               recoverField_c = var8;
            }

            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetDataMonitorManager", "use count=" + recoverField_b + " net threshold=" + recoverField_a);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetDataMonitorManager", "use conn count=" + recoverField_d + " net conn threshold=" + recoverField_c);
            this.recoverField_i = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_m, RecoverType5a690d1.recoverMethod_t_48794abf());
            this.recoverField_g = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_m, RecoverType5a690d1.recoverMethod_s_48794abf());
            this.recoverField_h = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_m, RecoverType5a690d1.recoverMethod_u_48794abf());
            this.recoverField_l = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_m, RecoverType5a690d1.recoverMethod_w_48794abf());
            this.recoverField_j = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_m, RecoverType5a690d1.recoverMethod_v_48794abf());
            this.recoverField_k = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_m, RecoverType5a690d1.recoverMethod_x_48794abf());
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
               "NetDataMonitorManager",
               "init first mAllCount=" + this.recoverField_i + " mAllReqLength=" + this.recoverField_g + " mAllResLength=" + this.recoverField_h
            );
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
               "NetDataMonitorManager",
               "init first conn mAllCount=" + this.recoverField_l + " mAllReqLength=" + this.recoverField_j + " mAllResLength=" + this.recoverField_k
            );
            this.recoverMethod_h_9b75();
            this.recoverMethod_c_9b75();
         }
      }
   }

   public void recoverMethod_b_9b75() {
      long var1 = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_m, RecoverType5a690d1.recoverMethod_e_48794abf());
      if (0L == var1) {
         var1 = System.currentTimeMillis();
         RecoverType5a690d2.recoverMethod_a_abcb8d40(this.recoverField_m, RecoverType5a690d1.recoverMethod_e_48794abf().recoverMethod_a_4e9722a3(var1));
      }

      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetDataMonitorManager", "lastReportTime " + var1 + " BuildConfig.INTERNAL_USE=" + false);
      long var3 = System.currentTimeMillis();
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetDataMonitorManager", " currentTimestamp " + var3);
      if (var3 - var1 < 86400000L) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetDataMonitorManager", "it not business time, interval: 86400000");
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetDataMonitorManager", "need to  report");
         this.recoverMethod_d_9b75();
      }
   }

   private void recoverMethod_d_9b75() {
      RecoverTypeaf2b0403.recoverMethod_a_97dca6fd(
         "FUTURE_TASK",
         new RecoverTypeaf2b0401() {
            @Override
            public void recoverMethod_a_9b75() {
               try {
                  synchronized (RecoverTypeaf2ae5f9.class) {
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetDataMonitorManager", "real start ");
                     JSONObject var2 = RecoverTypeaf2ae5f9.this.recoverMethod_f_a7dbfe3();
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetDataMonitorManager", "start report lastJson: " + var2);
                     if (var2 != null && var2.has("data")) {
                        RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetDataMonitorManager", "start report net data monitor");
                        RecoverTypeaf2aa628.recoverMethod_a_d97c5eb5(RecoverTypeaf2ae5f9.this.recoverField_m, var2, "sdk_moniter");
                        RecoverType5a690d2.recoverMethod_a_abcb8d40(
                           RecoverTypeaf2ae5f9.this.recoverField_m,
                           RecoverType5a690d1.recoverMethod_e_48794abf().recoverMethod_a_4e9722a3(System.currentTimeMillis())
                        );
                     }
                  }
               } catch (Throwable var5) {
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("NetDataMonitorManager", "save data error:" + var5.getMessage());
               }
            }
         }
      );
   }

   private String recoverMethod_c_e96ea081(String var1) {
      if (var1.contains("wi/cjc4sa")) {
         return "1";
      } else if (var1.contains("wi/d8n3hj")) {
         return "2";
      } else if (var1.contains("v1/status")) {
         return "3";
      } else if (var1.contains("tsis")) {
         return "4";
      } else if (var1.contains("ali-stats")) {
         return "6";
      } else if (var1.contains("gd-stats")) {
         return "7";
      } else if (var1.contains("stats")) {
         return "5";
      } else if (var1.contains("v1/version-check")) {
         return "8";
      } else if (var1.contains("wi/plp8j0k")) {
         return "9";
      } else if (var1.contains("wi/spoml")) {
         return "10";
      } else if (var1.contains("qiniup")) {
         return "11";
      } else if (var1.contains("1/getEvent")) {
         return "12";
      } else if (var1.contains("1/setUserInfo")) {
         return "13";
      } else if (var1.contains("1/setChannel")) {
         return "14";
      } else if (var1.contains("1/updateUserProperties")) {
         return "15";
      } else if (var1.contains("1/switcUserProperties")) {
         return "16";
      } else if (var1.contains("v1/user")) {
         return "17";
      } else if (var1.contains("wi/qj18op")) {
         return "18";
      } else if (var1.contains("wi/op8jdu")) {
         return "19";
      } else if (var1.contains("v1/reqtime")) {
         return "20";
      } else if (var1.contains("appgwc/status")) {
         return "21";
      } else if (var1.contains("appawake/status")) {
         return "22";
      } else if (var1.contains("jx/6ae71c")) {
         return "23";
      } else if (var1.contains("wi/swelbv")) {
         return "24";
      } else if (var1.contains("wi/ysdiox")) {
         return "25";
      } else {
         return var1.startsWith("http") ? "1000" : var1;
      }
   }

   public void recoverMethod_a_79e5e33f(Bundle var1) {
      try {
         String var2 = var1.getString("url");
         long var3 = var1.getLong("req");
         long var5 = var1.getLong("res");
         String var7 = var1.getString("type");
         RecoverTypeaf2af143 var8 = new RecoverTypeaf2af143(this.recoverField_m);
         var8.recoverMethod_a_aad3b1ff(this.recoverMethod_c_e96ea081(var2));
         var8.recoverMethod_b_aad3b1ff(var7);
         var8.recoverMethod_b_1349ef(var3);
         var8.recoverMethod_a_1349ef(var5);
         var8.recoverMethod_c_1349ef(1L);
         this.recoverMethod_a_b2b3f693(var8);
      } catch (Throwable var9) {
      }
   }

   public void recoverMethod_a_ad38039f(HttpRequest var1, boolean var2) {
      try {
         long var4 = -1L;
         String var3;
         if (var2) {
            var3 = var1.getUrl();
            var4 = var1.getParas().length;
         } else {
            String var6 = var1.getUrl();
            String[] var7 = var6.split(" \\?");
            var3 = var7[0];
            if (var7.length > 1) {
               var4 = var7[1].getBytes().length;
            }
         }

         this.recoverMethod_b_3b99bddb(this.recoverMethod_c_e96ea081(var3), var1.getType(), var4);
      } catch (Throwable var8) {
      }
   }

   public void recoverMethod_a_3b99bddb(String var1, String var2, long var3) {
      RecoverTypeaf2af143 var5 = new RecoverTypeaf2af143(this.recoverField_m);
      var5.recoverMethod_a_aad3b1ff(var1);
      var5.recoverMethod_b_aad3b1ff(var2);
      var5.recoverMethod_a_1349ef(var3);
      var5.recoverMethod_c_1349ef(1L);
      this.recoverMethod_a_b2b3f693(var5);
   }

   public void recoverMethod_b_3b99bddb(String var1, String var2, long var3) {
      RecoverTypeaf2af143 var5 = new RecoverTypeaf2af143(this.recoverField_m);
      var5.recoverMethod_a_aad3b1ff(var1);
      var5.recoverMethod_b_aad3b1ff(var2);
      var5.recoverMethod_b_1349ef(var3);
      var5.recoverMethod_c_1349ef(1L);
      this.recoverMethod_a_b2b3f693(var5);
   }

   public void recoverMethod_a_6b0b6281(HttpRequest var1, boolean var2, HttpResponse var3) {
      long var5 = 0L;
      String var4;
      if (var2) {
         var4 = var1.getUrl();
      } else {
         String var7 = var1.getUrl();
         String[] var8 = var7.split(" \\?");
         var4 = var8[0];
      }

      if (!TextUtils.isEmpty(var3.getResponseBody())) {
         var5 = var3.getResponseBody().getBytes().length;
      }

      this.recoverMethod_c_3b99bddb(this.recoverMethod_c_e96ea081(var4), var1.getType(), var5);
   }

   public void recoverMethod_c_3b99bddb(String var1, String var2, long var3) {
      RecoverTypeaf2af143 var5 = new RecoverTypeaf2af143(this.recoverField_m);
      var5.recoverMethod_a_aad3b1ff(var1);
      var5.recoverMethod_b_aad3b1ff(var2);
      var5.recoverMethod_a_1349ef(var3);
      this.recoverMethod_a_b2b3f693(var5);
   }

   private void recoverMethod_a_b2b3f693(RecoverTypeaf2ae9b9 var1) {
      try {
         Bundle var2 = new Bundle();
         JSONObject var3 = var1.recoverMethod_b_a7dbfe3();
         if (RecoverTypeaf2b07c1.recoverMethod_b_f0554df7(this.recoverField_m)) {
            var2.putString("save_data", var3.toString());
            RecoverType5a681cd.recoverMethod_a_d19840b7(this.recoverField_m, "a12", var2);
         } else {
            recoverMethod_a_c69c3ecd().recoverMethod_a_aad3b1ff(var3.toString());
         }
      } catch (Throwable var4) {
      }
   }

   public synchronized void recoverMethod_a_aad3b1ff(final String var1) {
      if ((Boolean)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_m, RecoverType5a690d1.recoverMethod_n_48794abf())) {
         RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("FUTURE_TASK", new RecoverTypeaf2b0401() {
            @Override
            public void recoverMethod_a_9b75() {
               try {
                  synchronized (RecoverTypeaf2ae5f9.class) {
                     JSONObject var2 = new JSONObject(var1);
                     String var3 = var2.optString("moniter_type");
                     JSONObject var4 = var2.optJSONObject("extra");
                     String var5 = var4.optString("type");
                     String var6 = var4.optString("url");
                     long var7 = var4.optLong("count");
                     long var9 = var4.optLong("req");
                     long var11 = var4.optLong("res");
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetDataMonitorManager", "save new data:" + var2);
                     RecoverTypeaf2ae5f9.this.recoverMethod_a_c60deb9(var9, var11, var7, var5);
                     JSONObject var13 = RecoverTypeaf2ae5f9.this.recoverMethod_e_a7dbfe3();
                     JSONArray var14;
                     if (var13 != null && var13.has("data")) {
                        var14 = var13.optJSONArray("data");
                     } else {
                        var13 = new JSONObject();
                        var13.put("type", "sdk_moniter");
                        var14 = new JSONArray();
                     }

                     boolean var15 = false;

                     for (int var16 = 0; var16 < var14.length(); var16++) {
                        JSONObject var17 = var14.optJSONObject(var16);
                        if (var17 != null) {
                           String var18 = var17.optString("moniter_type");
                           if (TextUtils.equals(var3, var18)) {
                              JSONObject var19 = var17.optJSONObject("extra");
                              if (null != var19) {
                                 String var20 = var19.optString("type");
                                 String var21 = var19.optString("url");
                                 long var22 = var19.optLong("req");
                                 long var24 = var19.optLong("res");
                                 long var26 = var19.optLong("count");
                                 if (TextUtils.equals(var5, var20) && TextUtils.equals(var21, var6)) {
                                    var19.put("req", var22 + var9);
                                    var19.put("res", var24 + var11);
                                    var19.put("count", var26 + var7);
                                    var15 = true;
                                    break;
                                 }
                              }
                           }
                        }
                     }

                     if (!var15) {
                        var2.put("uuid", "");
                        var2.put("moniter_id", "");
                        var14.put(var2);
                     }

                     var13.put("data", var14);
                     RecoverTypeaf2ae5f9.this.recoverField_f = var13;
                     RecoverTypeaf2ae5f9.this.recoverMethod_a_3c4029ab(RecoverTypeaf2ae5f9.this.recoverField_m, RecoverTypeaf2ae5f9.this.recoverField_f);
                  }
               } catch (Throwable var30) {
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("NetDataMonitorManager", "save data failed, " + var30.getMessage());
               }
            }
         });
      }
   }

   private JSONObject recoverMethod_e_a7dbfe3() {
      try {
         if (this.recoverField_f == null) {
            this.recoverField_f = new JSONObject(this.recoverMethod_a_5e799b7f(this.recoverField_m, false));
         }

         return this.recoverField_f;
      } catch (Throwable var2) {
         return null;
      }
   }

   private JSONObject recoverMethod_f_a7dbfe3() {
      try {
         return new JSONObject(this.recoverMethod_a_5e799b7f(this.recoverField_m, true));
      } catch (Throwable var2) {
         return null;
      }
   }

   private void recoverMethod_a_3c4029ab(Context var1, JSONObject var2) {
      try {
         File var3 = new File(var1.getFilesDir() + "/nes");
         if (!var3.exists()) {
            var3.mkdirs();
         }

         Calendar var4 = Calendar.getInstance();
         SimpleDateFormat var5 = new SimpleDateFormat("yyyyMMdd");
         String var6 = var5.format(var4.getTime());
         File var7 = new File(var3, var6 + ".l");
         if (!var7.exists()) {
            var7.createNewFile();
         }

         String var8 = var2 != null ? var2.toString() : "";
         RecoverTypeaf2b07c9.recoverMethod_b_d9caeafa(var7, var8);
         File[] var9 = var3.listFiles();
         if (var9 != null && var9.length > 15) {
            for (File var13 : var9) {
               String var14 = var13.getName();
               long var15 = this.recoverMethod_a_e925f9fd(var6, var14);
               if (var15 > 15L) {
                  RecoverTypeaf2b07c9.recoverMethod_b_4ad1f430(var13);
               }
            }
         }
      } catch (IOException var17) {
      }
   }

   private String recoverMethod_a_5e799b7f(Context var1, boolean var2) {
      try {
         Calendar var3 = Calendar.getInstance();
         if (var2) {
            var3.add(5, -1);
         }

         SimpleDateFormat var4 = new SimpleDateFormat("yyyyMMdd");
         String var5 = var4.format(var3.getTime());
         File var6 = new File(var1.getFilesDir() + "/nes");
         File var7 = new File(var6, var5 + ".l");
         return RecoverTypeaf2b07c9.recoverMethod_c_af215df4(var7);
      } catch (Throwable var8) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("NetDataMonitorManager", "read Throwable " + var8);
         return "";
      }
   }

   private long recoverMethod_a_e925f9fd(String var1, String var2) {
      try {
         SimpleDateFormat var3 = new SimpleDateFormat("yyyyMMdd");
         Calendar var4 = Calendar.getInstance();
         var4.setTime(var3.parse(var1));
         Calendar var5 = Calendar.getInstance();
         var5.setTime(var3.parse(var2));
         long var6 = var4.getTimeInMillis() - var5.getTimeInMillis();
         return var6 / 86400000L;
      } catch (Exception var8) {
         return Long.MAX_VALUE;
      }
   }

   private void recoverMethod_a_c60deb9(long var1, long var3, long var5, String var7) {
      this.recoverMethod_h_9b75();
      long var8 = System.currentTimeMillis();
      boolean var10 = var8 - this.recoverField_p >= this.recoverField_q;
      if (!TextUtils.isEmpty(var7) && var7.contains("conn")) {
         this.recoverField_l += var5;
         this.recoverField_j += var1;
         this.recoverField_k += var3;
         RecoverType5a690d2.recoverMethod_a_abcb8d40(
            this.recoverField_m, RecoverType5a690d1.recoverMethod_w_48794abf().recoverMethod_a_4e9722a3(this.recoverField_l)
         );
         RecoverType5a690d2.recoverMethod_a_abcb8d40(
            this.recoverField_m, RecoverType5a690d1.recoverMethod_x_48794abf().recoverMethod_a_4e9722a3(this.recoverField_k)
         );
         RecoverType5a690d2.recoverMethod_a_abcb8d40(
            this.recoverField_m, RecoverType5a690d1.recoverMethod_v_48794abf().recoverMethod_a_4e9722a3(this.recoverField_j)
         );
         if (!var10) {
            return;
         }

         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("NetDataMonitorManager", "current conn data mAllReqLength, " + this.recoverField_j);
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("NetDataMonitorManager", "current conn data mAllResLength, " + this.recoverField_k);
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("NetDataMonitorManager", "current conn mAllCount, " + this.recoverField_l);
      } else {
         this.recoverField_i += var5;
         this.recoverField_h += var3;
         this.recoverField_g += var1;
         RecoverType5a690d2.recoverMethod_a_abcb8d40(
            this.recoverField_m, RecoverType5a690d1.recoverMethod_t_48794abf().recoverMethod_a_4e9722a3(this.recoverField_i)
         );
         RecoverType5a690d2.recoverMethod_a_abcb8d40(
            this.recoverField_m, RecoverType5a690d1.recoverMethod_u_48794abf().recoverMethod_a_4e9722a3(this.recoverField_h)
         );
         RecoverType5a690d2.recoverMethod_a_abcb8d40(
            this.recoverField_m, RecoverType5a690d1.recoverMethod_s_48794abf().recoverMethod_a_4e9722a3(this.recoverField_g)
         );
         if (!var10) {
            return;
         }

         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("NetDataMonitorManager", "current data mAllReqLength, " + this.recoverField_g);
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("NetDataMonitorManager", "current data mAllResLength, " + this.recoverField_h);
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("NetDataMonitorManager", "current  mAllCount, " + this.recoverField_i);
      }

      this.recoverField_p = System.currentTimeMillis();
      this.recoverMethod_g_9b75();
   }

   private void recoverMethod_g_9b75() {
      if (this.recoverField_i > recoverField_b || this.recoverField_h + this.recoverField_g > recoverField_a) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09(
            "NetDataMonitorManager", "check business count is more than COUNT= " + (this.recoverField_i > recoverField_b)
         );
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09(
            "NetDataMonitorManager",
            "check business data length is more than NET_LENGTH_THRESHOLD= " + (this.recoverField_h + this.recoverField_g > recoverField_a)
         );
         this.recoverMethod_c_9b75();
         RecoverTypeaf2ab532.recoverMethod_a_c69656b4().recoverMethod_a_f0554df3(this.recoverField_m);
      }

      if (this.recoverField_l > recoverField_d || this.recoverField_k + this.recoverField_j > recoverField_c) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09(
            "NetDataMonitorManager", "check conn count is more than CONN_COUNT= " + (this.recoverField_l > recoverField_d)
         );
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09(
            "NetDataMonitorManager",
            "check conn data length is more than NET_CONN_LENGTH_THRESHOLD= " + (this.recoverField_k + this.recoverField_j > recoverField_c)
         );
         this.recoverMethod_c_9b75();
         RecoverTypeaf2ab531.recoverMethod_a_c6965695().recoverMethod_a_d19840b7(this.recoverField_m, "tcp_a23", null);
      }
   }

   private synchronized void recoverMethod_h_9b75() {
      long var1 = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_m, RecoverType5a690d1.recoverMethod_y_48794abf());
      if (this.recoverField_o > 0L) {
         var1 = this.recoverField_o;
      }

      long var3 = RecoverTypeaf2b07d7.recoverMethod_a_9b69();
      if (var1 == 0L || var1 != var3) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("NetDataMonitorManager", "checkCurrentDayTime saveCurrentDayTime " + var1);
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("NetDataMonitorManager", "checkCurrentDayTime currentDayTime " + var3);
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("NetDataMonitorManager", "recover all data");
         this.recoverField_i = 0L;
         this.recoverField_g = 0L;
         this.recoverField_h = 0L;
         this.recoverField_l = 0L;
         this.recoverField_j = 0L;
         this.recoverField_k = 0L;
         RecoverType5a690d2.recoverMethod_a_abcb8d40(this.recoverField_m, RecoverType5a690d1.recoverMethod_y_48794abf().recoverMethod_a_4e9722a3(var3));
         this.recoverField_o = var3;
         RecoverType5a690d2.recoverMethod_a_abcb8d40(
            this.recoverField_m, RecoverType5a690d1.recoverMethod_t_48794abf().recoverMethod_a_4e9722a3(this.recoverField_i)
         );
         RecoverType5a690d2.recoverMethod_a_abcb8d40(
            this.recoverField_m, RecoverType5a690d1.recoverMethod_u_48794abf().recoverMethod_a_4e9722a3(this.recoverField_h)
         );
         RecoverType5a690d2.recoverMethod_a_abcb8d40(
            this.recoverField_m, RecoverType5a690d1.recoverMethod_s_48794abf().recoverMethod_a_4e9722a3(this.recoverField_g)
         );
         RecoverType5a690d2.recoverMethod_a_abcb8d40(
            this.recoverField_m, RecoverType5a690d1.recoverMethod_w_48794abf().recoverMethod_a_4e9722a3(this.recoverField_l)
         );
         RecoverType5a690d2.recoverMethod_a_abcb8d40(
            this.recoverField_m, RecoverType5a690d1.recoverMethod_x_48794abf().recoverMethod_a_4e9722a3(this.recoverField_k)
         );
         RecoverType5a690d2.recoverMethod_a_abcb8d40(
            this.recoverField_m, RecoverType5a690d1.recoverMethod_v_48794abf().recoverMethod_a_4e9722a3(this.recoverField_j)
         );
         this.recoverField_f = null;
         this.recoverField_f = this.recoverMethod_e_a7dbfe3();
      }
   }

   public boolean recoverMethod_a_f0554df7(Context var1) {
      if (!(Boolean)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_m, RecoverType5a690d1.recoverMethod_n_48794abf())) {
         return true;
      } else {
         this.recoverMethod_h_9b75();
         if (this.recoverField_i > recoverField_b) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("NetDataMonitorManager", "business count is more than count: " + recoverField_b + ",so sdk limit");
            return false;
         } else if (this.recoverField_h + this.recoverField_g > recoverField_a) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09(
               "NetDataMonitorManager", "business data length is more than data threshold: " + recoverField_a + ",so sdk limit"
            );
            return false;
         } else {
            return true;
         }
      }
   }

   public boolean recoverMethod_b_f0554df7(Context var1) {
      if (!(Boolean)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_m, RecoverType5a690d1.recoverMethod_n_48794abf())) {
         return true;
      } else {
         this.recoverMethod_h_9b75();
         if (this.recoverField_l > recoverField_d) {
            String var3 = "conn count is more than count: " + recoverField_d + ",so sdk limit";
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("NetDataMonitorManager", var3);
            RecoverTypeaf2aadab.Member8f35f3a8.recoverMethod_a_3f52113a(var1, 1409, var3);
            return false;
         } else if (this.recoverField_k + this.recoverField_j > recoverField_c) {
            String var2 = "conn data length is more than data threshold: " + recoverField_c + ",so sdk limit";
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("NetDataMonitorManager", var2);
            RecoverTypeaf2aadab.Member8f35f3a8.recoverMethod_a_3f52113a(var1, 1410, var2);
            return false;
         } else {
            return true;
         }
      }
   }

   public void recoverMethod_c_9b75() {
      try {
         boolean var1 = this.recoverField_m.getPackageName().equals(RecoverTypeaf2b07c1.recoverMethod_a_e501eb0d(this.recoverField_m));
         if (var1) {
            return;
         }

         JSONObject var2 = new JSONObject();
         var2.put("count", this.recoverField_i);
         var2.put("res_l", this.recoverField_h);
         var2.put("req_l", this.recoverField_g);
         var2.put("count_th", recoverField_b);
         var2.put("l_th", recoverField_a);
         var2.put("conn_count", this.recoverField_l);
         var2.put("conn_res_l", this.recoverField_k);
         var2.put("conn_req_l", this.recoverField_j);
         var2.put("conn_count_th", recoverField_d);
         var2.put("conn_length_th", recoverField_c);
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("NetDataMonitorManager", "sendMessageToMain json=" + var2);
         RecoverTypeaf2b07c1.recoverMethod_b_9d23c587(this.recoverField_m, recoverField_e, var2.toString());
      } catch (Throwable var3) {
      }
   }

   public void recoverMethod_b_aad3b1ff(String var1) {
      try {
         JSONObject var2 = new JSONObject(var1);
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("NetDataMonitorManager", "syncMessage json=" + var2);
         long var3 = var2.optLong("count");
         if (var3 > 0L) {
            this.recoverField_i = var3;
         }

         long var5 = var2.optLong("res_l");
         if (var5 > 0L) {
            this.recoverField_h = var5;
         }

         long var7 = var2.optLong("req_l");
         if (var3 > 0L) {
            this.recoverField_g = var7;
         }

         long var9 = var2.optLong("count_th");
         if (var9 > 0L) {
            recoverField_b = var9;
         }

         long var11 = var2.optLong("l_th");
         if (var11 > 0L) {
            recoverField_a = var11;
         }

         long var13 = var2.optLong("conn_count");
         if (var13 > 0L) {
            this.recoverField_l = var13;
         }

         long var15 = var2.optLong("conn_res_l");
         if (var15 > 0L) {
            this.recoverField_k = var15;
         }

         long var17 = var2.optLong("conn_req_l");
         if (var17 > 0L) {
            this.recoverField_j = var17;
         }

         long var19 = var2.optLong("conn_count_th");
         if (var19 > 0L) {
            recoverField_d = var19;
         }

         long var21 = var2.optLong("conn_length_th");
         if (var21 > 0L) {
            recoverField_c = var21;
         }
      } catch (Throwable var23) {
      }
   }

   private static final class Member90095076 {
      private static final RecoverTypeaf2ae5f9 recoverField_a = new RecoverTypeaf2ae5f9();
   }
}
