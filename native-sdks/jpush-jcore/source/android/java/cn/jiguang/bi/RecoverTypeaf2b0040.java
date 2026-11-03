package cn.jiguang.bi;

import android.content.Context;
import android.os.Bundle;
import cn.jiguang.api.JCoreManager;

public class RecoverTypeaf2b0040 {
   public static Bundle recoverMethod_a_9c9f3728(Context var0, int var1, Bundle var2) {
      try {
         Bundle var3 = new Bundle();
         switch (var1) {
            case 4096:
               if (var2 != null) {
                  Object var4 = var2.get("arg1");
                  if (var4 != null) {
                     JCoreManager.onEvent(var0, null, 9, var4.toString(), null);
                  }
               }

               return var3;
            case 4098:
               JCoreManager.init(var0);
               return var3;
            case 36864:
               JCoreManager.onEvent(var0, null, 52, null, var2);
               return null;
            default:
               return null;
         }
      } catch (Throwable var5) {
         RecoverTypeaf2b0042.recoverMethod_e_e925fa09("JCoreInterface", "si e:" + var5);
         return null;
      }
   }
}
