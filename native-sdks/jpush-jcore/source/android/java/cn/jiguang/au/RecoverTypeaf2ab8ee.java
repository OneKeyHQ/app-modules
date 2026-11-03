package cn.jiguang.au;

import android.content.Context;
import android.text.TextUtils;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.bk.RecoverTypeaf2b07ce;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import java.util.HashMap;
import java.util.Map;

public class RecoverTypeaf2ab8ee {
   private static int recoverField_a = 0;
   private final Map recoverField_b = new HashMap();

   private RecoverTypeaf2ab8ee() {
   }

   public static RecoverTypeaf2ab8ee recoverMethod_a_c696ca78() {
      return RecoverTypeaf2ab8ee.Member8f603a2b.recoverField_a;
   }

   public static void recoverMethod_a_13462e(int var0) {
      if (var0 <= 3 && var0 >= 0) {
         recoverField_a = var0;
      }
   }

   public void recoverMethod_a_1a54e370(Context var1, int var2) {
      String var3 = RecoverTypeaf2b07ce.recoverMethod_a_e501eb0d(var1);
      String var4 = "";
      boolean var5 = "wifi".equals(var3);
      if (var5) {
         var4 = RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_b_5e799b7f(var1, false);
      }

      boolean var6 = !TextUtils.isEmpty(var4);
      if (!var5 || var6) {
         this.recoverField_b.put(var3 + var4, var2);
      }

      if (var6) {
         RecoverType5a690d2.recoverMethod_a_abcb8d40(var1, RecoverType5a690d1.recoverMethod_h_5ef90ef5(var4).recoverMethod_a_4e9722a3(var2));
      }
   }

   public int recoverMethod_b_134621(int var1) {
      if (recoverField_a != 2 && recoverField_a != 1) {
         switch (var1) {
            case 0:
            case 3:
            default:
               return recoverField_a;
            case 1:
               return 1;
            case 2:
               return 2;
         }
      } else {
         return recoverField_a;
      }
   }

   private static final class Member8f603a2b {
      private static final RecoverTypeaf2ab8ee recoverField_a = new RecoverTypeaf2ab8ee();
   }
}
