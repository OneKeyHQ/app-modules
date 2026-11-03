package cn.jpush.android.n;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import cn.jpush.android.ad.RecoverType9b4c5ae5;
import cn.jpush.android.helper.JCoreHelper;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.local.JPushConstants;

public class RecoverType2e4cf3b3 {
   private static long recoverField_a = 0L;

   @SuppressLint("MissingPermission")
   public static Location recoverMethod_a_3a51be92(Context var0, LocationManager var1, boolean var2) {
      boolean var3 = !var2 || System.currentTimeMillis() - recoverField_a < 30000L;
      if (var1 != null && recoverMethod_a_c5f37608(var0, var1) && var3) {
         recoverField_a = System.currentTimeMillis();
         Location var4 = var1.getLastKnownLocation("gps");
         Location var5 = var1.getLastKnownLocation("network");
         Location var6 = var1.getLastKnownLocation("passive");
         Logger.recoverMethod_v_e925fa09("GeoLocHelper", "gpsLocation:" + var4);
         Logger.recoverMethod_v_e925fa09("GeoLocHelper", "netLocation:" + var5);
         Logger.recoverMethod_v_e925fa09("GeoLocHelper", "passLocation:" + var6);
         long var7 = var4 == null ? 0L : var4.getTime();
         long var9 = var5 == null ? 0L : var5.getTime();
         long var11 = var6 == null ? 0L : var6.getTime();
         return var7 > var9 ? (var7 > var11 ? var4 : var6) : (var9 > var11 ? var5 : var6);
      } else {
         return null;
      }
   }

   public static boolean recoverMethod_a_c5f37608(Context var0, LocationManager var1) {
      if (!RecoverType9b4c5ae5.recoverMethod_a_5ffc0101(var0, "android.permission.ACCESS_FINE_LOCATION")) {
         Logger.recoverMethod_w_e925fa09("GeoLocHelper", "Require the permissionandroid.permission.ACCESS_FINE_LOCATION");
         Logger.recoverMethod_dd_e925fa09("GeoLocHelper", "No location permission!");
         return false;
      }

      if (!JPushConstants.canGetLbsInBackGround(var0)) {
         Logger.recoverMethod_ww_e925fa09("GeoLocHelper", "can not get lbs in background");
         return false;
      }

      boolean var2 = JCoreHelper.geoEnable(var0);
      if (!var2) {
         Logger.recoverMethod_ww_e925fa09("GeoLocHelper", "lbs is disable by user");
         return false;
      }

      try {
         return null == var1 ? false : var1.isProviderEnabled("gps") || var1.isProviderEnabled("network") || var1.isProviderEnabled("passive");
      } catch (SecurityException var4) {
         Logger.recoverMethod_ww_e925fa09("GeoLocHelper", "No suitable permission is present when get GPS_PROVIDER!");
         return false;
      } catch (IllegalArgumentException var5) {
         Logger.recoverMethod_ww_e925fa09("GeoLocHelper", "The provider [gps] is illegal argument!");
         return false;
      } catch (Throwable var6) {
         Logger.recoverMethod_ww_e925fa09("GeoLocHelper", "Check GPS enable failed:" + var6);
         return false;
      }
   }
}
