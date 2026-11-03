package cn.jiguang.ak;

import android.text.TextUtils;
import cn.jiguang.ai.RecoverTypeaf2a8be2;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import java.util.ArrayList;
import java.util.List;

public class RecoverTypeaf2a9365 {
   public static List recoverMethod_a_48092677(String var0, String var1, String var2, int var3) {
      RecoverTypeaf2a8be2 var4 = new RecoverTypeaf2a8be2();
      var4.recoverField_a = var1;
      var4.recoverField_b = var2;
      List var5 = null;
      if (var0 != null && !TextUtils.isEmpty(var0)) {
         var5 = RecoverTypeaf2a9364.recoverMethod_a_c0060268(var0);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakePackageHelper", "cache cmd wakeTargets:" + var5);
      }

      if (var5 != null && !var5.isEmpty()) {
         for (int var6 = var5.size() - 1; var6 >= 0; var6--) {
            if (((RecoverTypeaf2a8be2)var5.get(var6)).recoverField_a.equals(var1) && ((RecoverTypeaf2a8be2)var5.get(var6)).recoverField_b.equals(var2)) {
               var5.remove(var6);
               break;
            }
         }
      }

      if (var5 == null) {
         var5 = new ArrayList();
      }

      if (var3 == 1) {
         var5.add(var4);
      }

      return var5;
   }
}
