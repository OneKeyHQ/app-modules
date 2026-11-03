package cn.jiguang.ag;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.NetworkRequest.Builder;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Base64;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.al.RecoverTypeaf2a9723;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.bh.RecoverTypeaf2afc7e;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.bk.RecoverTypeaf2b07c2;
import cn.jiguang.bk.RecoverTypeaf2b07c4;
import cn.jiguang.bk.RecoverTypeaf2b07cb;
import cn.jiguang.bk.RecoverTypeaf2b07d0;
import cn.jiguang.bk.RecoverTypeaf2b07d4;
import cn.jiguang.bk.RecoverTypeaf2b07d5;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.g.RecoverType5a69853;
import cn.jiguang.g.RecoverType5a69855;
import cn.jiguang.i.RecoverType5a69fd7;
import cn.jiguang.net.HttpRequest;
import cn.jiguang.net.HttpResponse;
import cn.jiguang.net.HttpUtils;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecoverTypeaf2a845e {
   public static String recoverField_a = "";
   private static final String recoverField_b = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{88, 99, 23, 33, 91, 70, 122, 67, 18, 113, 127, 85, 75, 114, 108, 120}
   );
   private static final String recoverField_c = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{65, 8, 65, 72, 109, 123, 93, 64, 118, 122, 88, 68, 82, 67, 76, 43}
   );
   private static final String recoverField_d = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{88, 84, 109, 120, 101, 43, 31, 15, 122, 109, 37, 116, 7, 21, 125, 61, 56, 123, 64, 85, 106, 96, 56, 114, 94, 15, 110, 97, 57}
   );
   private static final String recoverField_e = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{
         121, 68, 84, 105, 123, 44, 62, 31, 67, 124, 59, 115, 38, 5, 68, 44, 37, 127, 97, 70, 22, 55, 98, 102, 100, 67, 72, 55, 107, 120, 62, 71, 73, 54
      }
   );
   private static final String recoverField_f = recoverField_d;
   private String recoverField_g = recoverMethod_i_73cf11cb() + RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{82, 120, 56, 117, 117, 85});
   private String recoverField_h = recoverMethod_i_73cf11cb() + RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{66, 125, 56, 100, 32, 65});
   private Context recoverField_i;
   private long recoverField_j = 0L;
   private long recoverField_k = 86400000L;
   private static LinkedList recoverField_l = new LinkedList();

   private RecoverTypeaf2a845e() {
   }

   public static RecoverTypeaf2a845e recoverMethod_a_c6906d08() {
      return RecoverTypeaf2a845e.Member8e9ae99c.recoverField_a;
   }

   public void recoverMethod_a_f0554df3(Context var1) {
      this.recoverField_i = var1;
   }

   public void recoverMethod_b_f0554df3(Context var1) {
      if (this.recoverField_i == null) {
         this.recoverField_i = var1;
      }

      RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(999);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JUaidManager", "getUaidAsyn");
      RecoverTypeaf2aa628.recoverMethod_e_a4fc3973(new RecoverTypeaf2a845e.Member8e9ae99b());
   }

   public void recoverMethod_b_9b75() {
      try {
         boolean var1 = RecoverTypeaf2b07d0.recoverMethod_b_5ffc0101(this.recoverField_i, "android.permission.CHANGE_NETWORK_STATE");
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(var1 ? 1045 : 1046);
         ConnectivityManager var2 = this.recoverMethod_c_79f2a562();
         if (var2 == null) {
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1003);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JUaidManager", "no connectivityManager hadChangePermission= " + var1);
            this.recoverMethod_a_1385ff(var1);
            return;
         }

         if (VERSION.SDK_INT >= 23) {
            Network var3 = var2.getActiveNetwork();
            if (var3 == null) {
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1004);
               this.recoverMethod_a_eb6cd22c(var2, var1);
               return;
            }

            NetworkCapabilities var4 = var2.getNetworkCapabilities(var3);
            if (var4 == null) {
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1005);
               this.recoverMethod_a_eb6cd22c(var2, var1);
               return;
            }

            boolean var5 = var4.hasTransport(0);

            try {
               boolean var6 = var4.hasTransport(1);
               StringBuilder var7 = new StringBuilder("n");
               if (var6) {
                  var7.append(1);
                  RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1043);
               }

               if (var5) {
                  var7.append(0);
                  RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1042);
               }

               if (var4.hasTransport(3)) {
                  var7.append(3);
               }

               if (var4.hasTransport(2)) {
                  var7.append(2);
               }

               if (var4.hasTransport(4)) {
                  var7.append(4);
               }

               if (var4.hasTransport(5)) {
                  var7.append(5);
               }

               if (var4.hasTransport(6)) {
                  var7.append(6);
               }

               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(var7.toString());
            } catch (Throwable var8) {
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1044);
            }

            boolean var10 = var4.hasCapability(12);
            boolean var11 = var4.hasCapability(16);
            if (!var10) {
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1006);
               this.recoverMethod_a_eb6cd22c(var2, var1);
               return;
            }

            if (!var11) {
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1007);
               this.recoverMethod_a_eb6cd22c(var2, var1);
               return;
            }

            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1048);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JUaidManager", "new network, hadChangePermission= " + var1 + " hasTransportCell=" + var5);
            this.recoverMethod_a_1385ff(var1 && !var5);
         } else {
            this.recoverMethod_a_eb6cd22c(var2, var1);
         }
      } catch (Throwable var9) {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1044);
      }
   }

   private void recoverMethod_a_eb6cd22c(ConnectivityManager var1, boolean var2) {
      try {
         NetworkInfo var3 = var1.getActiveNetworkInfo();
         if (var3 == null) {
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1009);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JUaidManager", "old network no activeNetworkInfo, hadChangePermission= " + var2);
            this.recoverMethod_a_1385ff(var2);
            return;
         }

         int var4 = var3.getType();
         boolean var5 = this.recoverMethod_a_134632(var4);

         try {
            boolean var6 = 1 == var4;
            if (var5) {
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1040);
            }

            if (var6) {
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1041);
            }

            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811("o" + var4);
         } catch (Throwable var7) {
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1044);
         }

         if (!var3.isConnected()) {
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1010);
            return;
         }

         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1048);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
            "JUaidManager", "old network no activeNetworkInfo, hadChangePermission= " + var2 + " mobileNetworkType=" + var5
         );
         this.recoverMethod_a_1385ff(var2 && !var5);
      } catch (Throwable var8) {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1044);
      }
   }

   private boolean recoverMethod_a_134632(int var1) {
      return var1 == 0 || var1 == 4 || var1 == 5 || var1 == 2 || var1 == 6 || var1 == 3;
   }

   public ConnectivityManager recoverMethod_c_79f2a562() {
      Context var1 = RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d();
      if (var1 == null) {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1001);
         return null;
      } else if (!RecoverTypeaf2b07d0.recoverMethod_a_5ffc0101(var1, "android.permission.ACCESS_NETWORK_STATE")) {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1002);
         return null;
      } else {
         return (ConnectivityManager)var1.getSystemService("connectivity");
      }
   }

   private void recoverMethod_a_1385ff(boolean var1) {
      try {
         JSONObject var2 = this.recoverMethod_f_a7dbfe3();
         if (null == var2) {
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1022);
            return;
         }

         JSONObject var3 = var2.optJSONObject("data");
         if (var3 != null) {
            boolean var4 = var3.optBoolean("token_switch");
            long var5 = var3.optLong("next_time") * 1000L;
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JUaidManager", "mSwitch=" + var4 + " internalTime=" + var5);
            this.recoverMethod_a_1349ef(var5);
            if (var4) {
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1053);
               this.recoverMethod_a_11791e8b(var3, var1);
            } else {
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1021);
            }
         } else {
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1023);
         }
      } catch (Throwable var7) {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1024);
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JUaidManager", "startReq throwable:" + var7);
      }
   }

   private void recoverMethod_a_11791e8b(JSONObject var1, boolean var2) {
      try {
         boolean var3 = RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_d_134632(1037);
         boolean var4 = var3 && var2;
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JUaidManager", "first enable=" + var2 + " server enable=" + var3 + " final enable=" + var4);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JUaidManager", "startReqTokenFromOperator startReqTokenFromOperator= " + var1);
         if (var1 == null) {
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1023);
            return;
         }

         JSONArray var5 = var1.optJSONArray("list");
         if (var5 == null) {
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1027);
            return;
         }

         for (int var6 = 0; var6 < var5.length(); var6++) {
            JSONObject var7 = var5.getJSONObject(var6);
            String var8 = var7.optString("channelId");
            JSONObject var9 = var7.optJSONObject("headers");
            String var10 = var7.optString("method");
            String var11 = var7.optString("requestBody");
            String var12 = var7.optString("url");
            String var13 = var10.toUpperCase();
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1055);
            if (var4) {
               this.recoverMethod_a_a7b5f5e9(var13, var12, var9, var11, var8);
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1051);
            } else {
               this.recoverMethod_b_a7b5f5e9(var13, var12, var9, var11, var8);
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1052);
            }
         }
      } catch (Throwable var14) {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1029);
      }
   }

   private void recoverMethod_a_a7b5f5e9(final String var1, final String var2, final JSONObject var3, final String var4, final String var5) {
      try {
         if (VERSION.SDK_INT >= 21) {
            final ConnectivityManager var6 = this.recoverMethod_c_79f2a562();
            if (var6 == null) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JUaidManager", "No connectivity manager available");
               this.recoverMethod_b_a7b5f5e9(var1, var2, var3, var4, var5);
               return;
            }

            Builder var7 = new Builder();
            var7.addCapability(12);
            var7.addTransportType(0);
            NetworkCallback var8 = new NetworkCallback() {
               @Override
               public void onAvailable(Network var1x) {
                  try {
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JUaidManager", "Network available: " + var1x);
                     RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1049);
                     HttpURLConnection var2x = (HttpURLConnection)var1x.openConnection(new URL(var2));
                     RecoverTypeaf2a845e.this.recoverMethod_a_46fbfd60(var2x, var1, var2, var3, var4, var5);
                  } catch (Throwable var6x) {
                     RecoverTypeaf2a845e.this.recoverMethod_e_9b75();
                  } finally {
                     var6.unregisterNetworkCallback(this);
                  }
               }

               @Override
               public void onUnavailable() {
                  try {
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JUaidManager", "Network unavailable");
                     RecoverTypeaf2a845e.this.recoverMethod_b_a7b5f5e9(var1, var2, var3, var4, var5);
                  } catch (Throwable var5x) {
                     RecoverTypeaf2a845e.this.recoverMethod_e_9b75();
                  } finally {
                     var6.unregisterNetworkCallback(this);
                  }
               }
            };
            if (VERSION.SDK_INT >= 26) {
               var6.requestNetwork(var7.build(), var8, 15000);
            } else {
               var6.requestNetwork(var7.build(), var8);
            }
         } else {
            this.recoverMethod_b_a7b5f5e9(var1, var2, var3, var4, var5);
         }
      } catch (Throwable var9) {
         this.recoverMethod_e_9b75();
      }
   }

   private void recoverMethod_b_a7b5f5e9(String var1, String var2, JSONObject var3, String var4, String var5) {
      this.recoverMethod_a_46fbfd60(HttpUtils.getHttpURLConnectionWithProxy(var2), var1, var2, var3, var4, var5);
   }

   private void recoverMethod_a_46fbfd60(HttpURLConnection var1, String var2, String var3, JSONObject var4, String var5, String var6) {
      HttpResponse var7 = this.recoverMethod_a_5708886(var1, var2, var3, var4, var5);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JUaidManager", "performRequest httpResponse=" + var7);
      this.recoverMethod_a_f66d2775(var7, var6);
   }

   private void recoverMethod_e_9b75() {
      RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1029);
   }

   private HttpResponse recoverMethod_a_5708886(HttpURLConnection var1, String var2, String var3, JSONObject var4, String var5) {
      switch (var2) {
         case "GET":
            return HttpUtils.httpResponse(this.recoverField_i, var1, new HttpRequest(var3), false, false);
         case "POST":
         case "PUT":
            HttpRequest var8 = new HttpRequest(var3);
            this.recoverMethod_a_11d2f09(var8, var4);
            var8.setBody(var5);
            return HttpUtils.httpResponse(this.recoverField_i, var1, var8, "POST".equalsIgnoreCase(var2), "PUT".equalsIgnoreCase(var2));
         default:
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1038);
            return null;
      }
   }

   private void recoverMethod_a_11d2f09(HttpRequest var1, JSONObject var2) {
      try {
         Iterator var3 = var2.keys();

         while (var3.hasNext()) {
            String var4 = (String)var3.next();
            String var5 = var2.getString(var4);
            var1.setRequestProperty(var4, var5);
         }
      } catch (Throwable var6) {
      }
   }

   private void recoverMethod_a_f66d2775(HttpResponse var1, String var2) {
      try {
         if (var1 != null) {
            int var3 = var1.getResponseCode();
            String var4 = var1.getResponseBody();
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JUaidManager", "token code:" + var3);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JUaidManager", "tokenResponse:" + var4);
            if (200 == var3) {
               if (!TextUtils.isEmpty(var4)) {
                  JSONObject var5 = this.recoverMethod_h_a7dbfe3();
                  if (var5 == null) {
                     RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1015);
                     return;
                  }

                  var5.put("channelId", var2);
                  var5.put("core_sdk_ver", RecoverType5a681cd.recoverField_b);
                  var5.put("respBody", var4);
                  var5.put("respHeader", this.recoverMethod_a_5f44a05e(var1.getResponseHeaders()));
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JUaidManager", "start to reportToken paramJson=" + var5);
                  JSONObject var6 = this.recoverMethod_a_b2992603(this.recoverField_g, var5, false);
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JUaidManager", "reportToken response=" + var6);
                  if (var6 != null) {
                     int var7 = var6.optInt("code");
                     if (var7 == 2000) {
                        RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JUaidManager", "reportToken success");
                        RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1000);
                     } else {
                        RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JUaidManager", "reportToken fail");
                        RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1033);
                     }
                  } else {
                     RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1032);
                  }
               } else {
                  RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1031);
               }
            } else {
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1030);
            }
         } else {
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1028);
         }
      } catch (Throwable var8) {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1034);
      }
   }

   private JSONObject recoverMethod_a_5f44a05e(Map var1) {
      JSONObject var2 = new JSONObject();

      try {
         for (Object recoveryElement_456 : var1.entrySet()) {
            Entry var4 = (Entry)recoveryElement_456;
            String var5 = (String)var4.getKey();
            Object var6 = var4.getValue();
            if (TextUtils.isEmpty(var5)) {
               if (var6 instanceof String) {
                  var5 = (String)var6;
               } else {
                  var5 = "空key";
               }
            }

            var2.put(var5, var6);
         }
      } catch (Throwable var7) {
      }

      return var2;
   }

   private JSONObject recoverMethod_f_a7dbfe3() {
      try {
         JSONObject var1 = this.recoverMethod_h_a7dbfe3();
         if (var1 == null) {
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1035);
            return null;
         } else {
            var1.put("core_sdk_ver", RecoverType5a681cd.recoverField_b);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JUaidManager", "start to reqUrlInternal paramJson=" + var1);
            return this.recoverMethod_a_b2992603(this.recoverField_h, var1, true);
         }
      } catch (Throwable var2) {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1017);
         return null;
      }
   }

   private void recoverMethod_a_1349ef(long var1) {
      this.recoverMethod_a_2566ab5(System.currentTimeMillis(), var1);
   }

   private void recoverMethod_a_2566ab5(long var1, long var3) {
      this.recoverField_j = var1;
      if (var3 <= 0L) {
         var3 = 86400000L;
      }

      SharedPreferences var5 = this.recoverField_i.getSharedPreferences("cn.jiguang.common", 0);
      if (null != var5) {
         var5.edit().putLong("uaid_last_time", this.recoverField_j).putLong("uaid_internal_time", var3).apply();
      }

      this.recoverField_k = var3;
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JUaidManager", "save reqTime requestTime=" + this.recoverField_j + " next_time=" + this.recoverField_k);
   }

   private boolean recoverMethod_g_9b79() {
      if (0L == this.recoverField_j) {
         SharedPreferences var1 = this.recoverField_i.getSharedPreferences("cn.jiguang.common", 0);
         if (null != var1) {
            this.recoverField_j = var1.getLong("uaid_last_time", 0L);
            this.recoverField_k = var1.getLong("uaid_internal_time", 86400000L);
         }
      }

      if (this.recoverField_k == 86400000L) {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1056);
      } else {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811("stime" + this.recoverField_k);
      }

      long var5 = System.currentTimeMillis();
      long var3 = Math.abs(var5 - this.recoverField_j);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
         "JUaidManager", "mLastRequestTime=" + this.recoverField_j + " next_time=" + this.recoverField_k + " currentTimeMillis=" + var5 + " diffTime=" + var3
      );
      return var3 >= this.recoverField_k;
   }

   private JSONObject recoverMethod_h_a7dbfe3() {
      JSONObject var1 = new JSONObject();

      try {
         String var2 = RecoverTypeaf2a9723.recoverMethod_a_e501eb0d(this.recoverField_i);
         if (TextUtils.isEmpty(var2)) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JUaidManager", "appKey is null, no need req");
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1013);
            return null;
         }

         long var3 = RecoverType5a68d10.recoverMethod_f_f0554de7(this.recoverField_i);
         if (0L == var3) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JUaidManager", "uid is 0,no need req");
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1014);
            return null;
         }

         var1.put("appkey", var2);
         var1.put("uid", var3);
         var1.put("os_ver", RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_z_e501eb0d(this.recoverField_i));
         var1.put("platform", "a");
      } catch (Throwable var5) {
      }

      return var1;
   }

   private static String recoverMethod_i_73cf11cb() {
      String var0 = recoverField_f;
      if (RecoverType5a69fd7.Member367e0ad4.recoverField_a && !TextUtils.isEmpty(recoverField_a)) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JUaidManager", "use folder data config url :" + recoverField_a);
         var0 = recoverField_a;
      }

      return var0;
   }

   private JSONObject recoverMethod_a_b2992603(String var1, JSONObject var2, boolean var3) {
      try {
         String var4 = var2.optString("uid");
         String var5 = RecoverTypeaf2b07d5.recoverMethod_e_e96ea081(RecoverType5a68d10.recoverMethod_j_e501eb0d(this.recoverField_i)).toUpperCase();
         String var6 = RecoverTypeaf2b07d5.recoverMethod_e_e96ea081(var4 + var5).toUpperCase();
         if (TextUtils.isEmpty(var6)) {
            return null;
         }

         String var7 = var4 + ":" + var6;
         var7 = Base64.encodeToString(var7.getBytes(), 10);
         if (TextUtils.isEmpty(var7)) {
            return null;
         }

         boolean var8 = false;
         String var9 = var1;
         RecoverTypeaf2b07d5.recoverMethod_a_fa4794e4(this.recoverField_i, recoverField_l);

         for (int var10 = 0; var10 < recoverField_l.size(); var10++) {
            if (RecoverTypeaf2b07c2.recoverMethod_d_f0554df7(this.recoverField_i) && !var8) {
               var1 = var1.replace(recoverField_d, recoverField_e);
               var8 = true;
            } else {
               var1 = var9;
               if (!var8 && var10 == recoverField_l.size() - 1) {
                  var1 = var1.replace(recoverField_d, recoverField_e);
               }
            }

            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JUaidManager", "url:" + var1 + ", param json:" + var2.toString());
            HttpRequest var11 = new HttpRequest(var1);
            var11.setRequestProperty("Content-Type", "application/json");
            var11.setRequestProperty("Accept", "application/json");
            var11.setRequestProperty("X-Http-Platform", "android");
            var11.setRequestProperty("X-Http-Appkey", var2.optString("appkey"));
            var11.setRequestProperty("Authorization", "Basic " + var7);
            var11.setRequestProperty("Charset", "UTF-8");
            String var12 = this.recoverMethod_b_e96ea081(var2.toString());
            var11.setBody(var12);
            HttpResponse var13 = HttpUtils.httpPost(this.recoverField_i, var11);
            int var14 = var13.getResponseCode();
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JUaidManager", "responseCode:" + var14);
            String var15 = var13.getResponseBody();
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JUaidManager", "response:" + var15);
            if (200 == var14) {
               if (!TextUtils.isEmpty(var15)) {
                  String var16 = this.recoverMethod_a_e96ea081(var15);
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JUaidManager", "request success,response body:" + var16);
                  JSONObject var17 = new JSONObject(var16);
                  if (2000 == var17.optInt("code")) {
                     return var17;
                  }

                  RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(var3 ? 1020 : 1033);
               } else {
                  RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(var3 ? 1019 : 1032);
               }
            } else {
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(var3 ? 1016 : 1018);
            }
         }
      } catch (Throwable var18) {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(var3 ? 1036 : 1037);
      }

      return null;
   }

   private String recoverMethod_a_e96ea081(String var1) {
      if (TextUtils.isEmpty(var1)) {
         return "";
      }

      String var2 = "";

      try {
         byte[] var3 = Base64.decode(var1, 2);
         var3 = RecoverTypeaf2b07d4.recoverMethod_a_f31cd299(var3, recoverField_c, recoverField_b, false);
         return new String(var3);
      } catch (Throwable var4) {
         return "";
      }
   }

   private String recoverMethod_b_e96ea081(String var1) {
      String var3 = "";

      try {
         byte[] var2 = var1.getBytes("UTF-8");
         var2 = RecoverTypeaf2b07d4.recoverMethod_a_f31cd299(var2, recoverField_c, recoverField_b, true);
         var3 = Base64.encodeToString(var2, 2);
      } catch (Throwable var5) {
      }

      return var3;
   }

   public Integer recoverMethod_d_bd10275c() {
      return null;
   }

   public void recoverMethod_c_f0554df3(Context var1) {
      if (var1 == null) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JUaidManager", "updateImmediately failed: context is null");
      } else {
         boolean var2 = this.recoverMethod_g_9b79();
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JUaidManager", "updateImmediately: businessCondition=" + var2);
         if (!var2) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JUaidManager", "Reset request time and continue update");
            this.recoverMethod_a_2566ab5(0L, this.recoverField_k);
         }

         this.recoverMethod_b_f0554df3(var1);
      }
   }

   static {
      recoverField_l.clear();
      recoverField_l.add(recoverField_d);
      recoverField_l.add(recoverField_e);
   }

   private class Member8e9ae99b extends RecoverTypeaf2b0401 {
      private Member8e9ae99b() {
      }

      @Override
      public void recoverMethod_a_9b75() {
         try {
            boolean var1 = RecoverType5a69855.recoverMethod_a_487a33bb().recoverMethod_a_134632(2201);
            boolean var2 = RecoverType5a69855.recoverMethod_a_487a33bb().recoverMethod_b_134632(2201);
            if (var1) {
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1025);
            }

            if (var2) {
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1026);
            }

            if (!var1 && var2) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
                  "JUaidManager", "user disable to get uaid, containEnableDatas=" + var1 + " containDisableDatas=" + var2
               );
            } else {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
                  "JUaidManager", "user  enable or default can to get uaid, containEnableDatas=" + var1 + " containDisableDatas=" + var2
               );
               boolean var3 = RecoverTypeaf2a845e.this.recoverMethod_g_9b79();
               if (var3) {
                  RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1047);
                  RecoverTypeaf2a845e.this.recoverMethod_b_9b75();
               } else {
                  RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1012);
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JUaidManager", "the next request time is not reached");
               }
            }
         } catch (Throwable var4) {
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_5d527811(1039);
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JUaidManager", "Throwable e=" + var4.getMessage());
         }
      }
   }

   private static final class Member8e9ae99c {
      private static final RecoverTypeaf2a845e recoverField_a = new RecoverTypeaf2a845e();
   }
}
