package cn.jiguang.aa;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION;
import android.text.TextUtils;
import cn.jiguang.ab.RecoverTypeaf2a719a;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bh.RecoverTypeaf2afc7e;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.bk.RecoverTypeaf2b07ca;
import cn.jiguang.bk.RecoverTypeaf2b07ce;
import cn.jiguang.bk.RecoverTypeaf2b07d0;
import cn.jiguang.bk.RecoverTypeaf2b07d5;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.d.RecoverType5a68d11;
import cn.jiguang.g.RecoverType5a69853;
import cn.jiguang.i.RecoverType5a69fd6;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RecoverTypeaf2a6ddb {
   private static List recoverField_a = new ArrayList();

   public static List recoverMethod_a_5616a063(Context var0, int var1, boolean var2) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocationWifi", "getWifiInfoList local memory wifiInfoList=" + recoverField_a);
      boolean var3 = RecoverTypeaf2b07ca.recoverMethod_a_52a38c94("getWifiInfoList", 900000L, Integer.MAX_VALUE);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocationWifi", "getWifiInfoList is freq=" + var3);
      if (var3) {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_e_5d527811(2037);
      }

      if (var2 || !var3) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocationWifi", "getWifiInfoList no freq,start use api");
         recoverMethod_a_c8efa4e6(var0, var2);
      }

      if (recoverField_a != null && recoverField_a.size() > 0) {
         if (var1 > recoverField_a.size()) {
            var1 = recoverField_a.size();
         }

         List var4 = recoverField_a.subList(0, var1);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocationWifi", "getWifiInfoList return data=" + var4);
         return var4;
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocationWifi", "getWifiInfoList  default data=" + recoverField_a);
         return recoverField_a;
      }
   }

   public static List recoverMethod_a_c8efa4e6(Context var0, boolean var1) {
      if (!RecoverType5a68d10.recoverMethod_o_f0554df7(var0)) {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_e_5d527811(2038);
         return null;
      }

      WifiManager var2 = (WifiManager)var0.getApplicationContext().getSystemService("wifi");
      if (var2 != null && var2.isWifiEnabled()) {
         recoverField_a.clear();
         RecoverTypeaf2a719a var3 = new RecoverTypeaf2a719a();
         var3.recoverField_a = RecoverType5a68d11.recoverMethod_b_f0554de7(var0);
         if (RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_a_134632(1608)) {
            String var4 = RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_a_5e799b7f(var0, var1);
            if (!TextUtils.isEmpty(var4) && !"<unknown ssid>".equals(var4)) {
               var3.recoverField_b = RecoverTypeaf2b07d5.recoverMethod_b_e96ea081(var4);
            } else {
               var3.recoverField_b = RecoverTypeaf2b07d5.recoverMethod_b_e96ea081(recoverMethod_a_e501eb0d(var0));
            }
         }

         RecoverType5a69fd6.recoverMethod_p_5ffc00fd(var0, var3.recoverField_b);
         String var11 = RecoverTypeaf2b07ce.recoverMethod_c_e501eb0d(var0);
         if (!TextUtils.isEmpty(var11) && "WIFI".equalsIgnoreCase(var11)) {
            var3.recoverField_c = "connect";
         }

         var3.recoverField_d = RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_l_f0554de6(var0);
         if (RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_a_134632(1601)) {
            var3.recoverField_e = RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_b_5e799b7f(var0, var1);
         }

         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocationWifi", "connectingWifi:" + var3.toString());
         recoverField_a.add(var3);
         List var5;
         if (VERSION.SDK_INT < 23) {
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_e_5d527811(2056);
            var5 = var2.getScanResults();
         } else {
            boolean var6 = RecoverTypeaf2b07d0.recoverMethod_a_5ffc0101(var0, "android.permission.ACCESS_COARSE_LOCATION");
            boolean var7 = RecoverTypeaf2b07d0.recoverMethod_a_5ffc0101(var0, "android.permission.ACCESS_FINE_LOCATION");
            if (!var6) {
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_e_5d527811(2041);
            }

            if (!var7) {
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_e_5d527811(2040);
            }

            if (!var6 && !var7) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocationWifi", "scan wifi list failed because has no Manifest.permission.LOCATION");
               return recoverField_a;
            }

            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_e_5d527811(2056);
            var5 = var2.getScanResults();
         }

         if (var5 != null && var5.size() != 0) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocationWifi", "scan wifi list success:" + var5);
            ArrayList var12 = new ArrayList(var5);

            for (Object recoveryElement_117 : var12) {
               ScanResult var8 = (ScanResult)recoveryElement_117;
               if (!TextUtils.isEmpty(var3.recoverField_b)
                  && var3.recoverField_b.equals(RecoverTypeaf2b07d5.recoverMethod_b_e96ea081(var8.SSID))
                  && !TextUtils.isEmpty(var3.recoverField_e)
                  && var3.recoverField_e.equals(var8.BSSID)) {
                  var5.remove(var8);
               } else if (var8.level < -200) {
                  var5.remove(var8);
               } else {
                  for (Object recoveryElement_126 : var12) {
                     ScanResult var10 = (ScanResult)recoveryElement_126;
                     if (var10 != var8 && var8.SSID.equals(var10.SSID) && var8.BSSID.equals(var10.BSSID)) {
                        var5.remove(var8);
                     }
                  }
               }
            }

            var12.clear();
            Collections.sort(var5, new Comparator() {
               public int recoverMethod_a_9b57fb88(ScanResult var1, ScanResult var2x) {
                  return var2x.level - var1.level;
               }

               // $VF: synthetic method
               @Override
               public int compare(Object var1, Object var2) {
                  return this.recoverMethod_a_9b57fb88((ScanResult)var1, (ScanResult)var2);
               }
            });

            for (int var14 = 0; var14 < var5.size(); var14++) {
               ScanResult var15 = (ScanResult)var5.get(var14);
               String var16 = RecoverTypeaf2b07d5.recoverMethod_b_e96ea081(var15.SSID);
               RecoverTypeaf2a719a var17 = new RecoverTypeaf2a719a();
               var17.recoverField_a = RecoverType5a68d11.recoverMethod_b_f0554de7(var0);
               var17.recoverField_b = var16;
               var17.recoverField_c = null;
               if (var14 == 0) {
                  var17.recoverField_c = "strongest";
               }

               var17.recoverField_d = var15.level;
               var17.recoverField_e = var15.BSSID;
               recoverField_a.add(var17);
            }

            return recoverField_a;
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocationWifi", "scan wifi list failed");
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_e_5d527811(2046);
            return recoverField_a;
         }
      } else {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_e_5d527811(2039);
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocationWifi", "get wifiManager failed");
         return null;
      }
   }

   private static String recoverMethod_a_e501eb0d(Context var0) {
      String var1 = "";

      try {
         if (VERSION.SDK_INT < 29) {
            return var1;
         }

         if (!RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_a_134632(1608)) {
            return var1;
         }

         WifiManager var2 = (WifiManager)var0.getApplicationContext().getSystemService("wifi");
         if (null == var2) {
            return var1;
         }

         var1 = RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_a_5e799b7f(var0, false);
         int var3 = RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_m_f0554de6(var0);

         for (WifiConfiguration var6 : var2.getConfiguredNetworks()) {
            if (var6.networkId == var3) {
               var1 = var6.SSID;
            }
         }
      } catch (Throwable var7) {
      }

      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocationWifi", "ssid is " + var1);
      return var1.replace("\"", "");
   }
}
