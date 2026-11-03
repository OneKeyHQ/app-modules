package cn.jiguang.at;

import android.content.Context;
import android.text.TextUtils;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.bk.RecoverTypeaf2b07d4;
import cn.jiguang.bk.RecoverTypeaf2b07d5;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;

public class RecoverTypeaf2ab52d {
   public static boolean recoverMethod_a_f0554df7(Context var0) {
      String var1 = recoverMethod_c_e501eb0d(var0);
      String var2 = RecoverType5a68d10.recoverMethod_e_e501eb0d(var0);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("InitHelper", "appkey=" + var2 + " last=" + var1);
      if (!RecoverTypeaf2b07d5.recoverMethod_a_aad3b203(var1) && !"null".equals(var1) && var1.equalsIgnoreCase(var2)) {
         return false;
      }

      RecoverType5a690d2.recoverMethod_a_abcb8d40(
         var0,
         RecoverType5a690d1.recoverMethod_ah_48794abf()
            .recoverMethod_a_4e9722a3(RecoverTypeaf2b07d4.recoverMethod_a_8ea82db7(var2, RecoverType5a681cd.recoverField_i))
      );
      RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("InitHelper", "We found the appKey is changed or register appkey is empty. Will re-register.");
      RecoverTypeaf2aa628.recoverMethod_a_f0554df3(var0);
      return true;
   }

   private static String recoverMethod_c_e501eb0d(Context var0) {
      String var1 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_ah_48794abf());
      if (!TextUtils.isEmpty(var1)) {
         return RecoverTypeaf2b07d4.recoverMethod_b_8ea82db7(var1, RecoverType5a681cd.recoverField_i);
      }

      String var2 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_ai_48794abf());
      RecoverType5a690d2.recoverMethod_a_abcb8d40(
         var0,
         RecoverType5a690d1.recoverMethod_ah_48794abf()
            .recoverMethod_a_4e9722a3(RecoverTypeaf2b07d4.recoverMethod_a_8ea82db7(var2, RecoverType5a681cd.recoverField_i))
      );
      RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_ai_48794abf().recoverMethod_a_4e9722a3(""));
      return var2;
   }

   public static void recoverMethod_b_f0554df3(Context var0) {
      String var1 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_aa_48794abf());
      String var2 = "5.2.0";
      if ((TextUtils.isEmpty(var1) || var1.startsWith("1.")) && var2.startsWith("2.")) {
         RecoverTypeaf2aa628.recoverMethod_b_f0554df3(var0);
      }

      if (TextUtils.isEmpty(var1) || !var2.equals(var1)) {
         RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_aa_48794abf().recoverMethod_a_4e9722a3(var2));
      }
   }
}
