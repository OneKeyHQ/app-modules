package cn.jiguang.api.utils;

import android.content.Context;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.api.JCoreManager;

public class JCollectionAuth {
   @Deprecated
   public static void setAuth(Context var0, boolean var1) {
      RecoverTypeaf2a9ae9.recoverMethod_e_e925fa09("JCollectionAuth", "action:setAuth: " + var1);
      JCoreManager.onEvent(var0, null, 96, null, null, var1);
   }

   public static void enableAutoWakeup(Context var0, boolean var1) {
      RecoverTypeaf2a9ae9.recoverMethod_e_e925fa09("JCollectAuth", "action: enableAutoWakeup: " + var1);
      JCoreManager.onEvent(var0, null, 109, null, null, var1);
   }

   @Deprecated
   public static void enableDynamicLoad(Context var0, boolean var1) {
      JCoreManager.onEvent(var0, null, 115, null, null, var1);
   }

   public static void enableAppTerminate(Context var0, boolean var1) {
      JCoreManager.onEvent(var0, null, 119, null, null, var1);
   }
}
