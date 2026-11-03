package cn.jiguang.m;

import android.text.TextUtils;
import cn.jiguang.bk.RecoverTypeaf2b07d5;
import cn.jiguang.n.RecoverType5a6b29a;

public class RecoverType5a6aeda {
   private static long recoverField_a = 1L;

   public static byte[] recoverMethod_a_25254a04(long var0, String var2) {
      RecoverType5a6b29a var3 = new RecoverType5a6b29a(20480);
      var3.recoverMethod_a_13462e(0);
      var3.recoverMethod_a_1349ef(var0);
      if (var2 != null && !TextUtils.isEmpty(var2)) {
         var3.recoverMethod_a_25e06fc(RecoverTypeaf2b07d5.recoverMethod_i_afa28ebe(var2));
      }

      return var3.recoverMethod_a_12d408();
   }

   public static long recoverMethod_a_9b69() {
      recoverField_a++;
      if (recoverField_a >= 2147483647L) {
         recoverField_a = 1L;
      }

      return recoverField_a;
   }
}
