package cn.jpush.android.ups;

import android.content.Context;
import cn.jiguang.api.JCoreManager;
import cn.jpush.android.helper.JCoreHelper;
import cn.jpush.android.helper.Logger;

public class JPushUPSManager {
   private static final String TAG = "JPushUPSManager";

   public static void registerToken(Context var0, String var1, String var2, String var3, UPSRegisterCallBack var4) {
      try {
         int var5 = UPSPushHelper.getNextRid();
         UPSPushHelper.createMapping(var0, var5, var4);
         JCoreHelper.registerSdkType(var0, var1);
      } catch (Throwable var6) {
         Logger.recoverMethod_e_e925fa09("JPushUPSManager", "[registerToken] failed:" + var6.getMessage());
         if (var4 != null) {
            var4.onResult(new TokenResult("", 1000));
         }
      }
   }

   public static void unRegisterToken(Context var0, UPSUnRegisterCallBack var1) {
      try {
         int var2 = UPSPushHelper.getNextRid();
         UPSPushHelper.createMapping(var0, var2, var1);
         JCoreManager.unRegister(var0);
      } catch (Throwable var3) {
         Logger.recoverMethod_e_e925fa09("JPushUPSManager", "[unRegisterToken] failed:" + var3.getMessage());
         if (var1 != null) {
            var1.onResult(new TokenResult("", 1000));
         }
      }
   }

   public static void turnOnPush(Context var0, UPSTurnCallBack var1) {
      UPSPushHelper.callResult(var0, "ups.turnOn", null, var1);
   }

   public static void turnOffPush(Context var0, UPSTurnCallBack var1) {
      UPSPushHelper.callResult(var0, "ups.turnOff", null, var1);
   }
}
