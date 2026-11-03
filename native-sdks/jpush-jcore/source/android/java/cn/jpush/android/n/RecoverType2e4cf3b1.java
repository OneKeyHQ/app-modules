package cn.jpush.android.n;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.helper.RecoverType8b45582f;
import cn.jpush.android.local.JPushConstants;

public class RecoverType2e4cf3b1 {
   public static void recoverMethod_a_510bbd7d(Context var0, String var1, double var2, double var4) {
      Logger.recoverMethod_dd_e925fa09("GeoCallbackHelper", "onGeofenceRegion,geo:" + var1 + ",lat:" + var2 + ",lng:" + var4);
      recoverMethod_a_e3548787(var0, "cn.jpush.android.intent.GEO_REGION", var1, var2, var4);
   }

   public static void recoverMethod_a_5ffc00fd(Context var0, String var1) {
      Logger.recoverMethod_dd_e925fa09("GeoCallbackHelper", "onGeofenceReceived,geos:" + var1);
      recoverMethod_a_e3548787(var0, "cn.jpush.android.intent.GEO_RECEIVED", var1, 0.0, 0.0);
   }

   public static void recoverMethod_a_e3548787(Context var0, String var1, String var2, double var3, double var5) {
      if (var0 != null) {
         try {
            String var7 = JPushConstants.getMessageReceiverClass(var0);
            if (!TextUtils.isEmpty(var7)) {
               Intent var8 = new Intent(var0, Class.forName(var7));
               var8.setAction(var1);
               var8.putExtra("geo", var2);
               var8.putExtra("geoLat", var3);
               var8.putExtra("geoLng", var5);
               RecoverType8b45582f.recoverMethod_a_c4d2ecb0(var0, var8);
            } else {
               Logger.recoverMethod_d_e925fa09("GeoCallbackHelper", "not found messageReceiver");
            }
         } catch (Throwable var9) {
            Logger.recoverMethod_ww_e925fa09("GeoCallbackHelper", "onResult error:" + var9);
         }
      }
   }
}
