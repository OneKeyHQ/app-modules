package cn.jpush.android.a;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public abstract class RecoverType2e4cc2e4 {
   private static SharedPreferences recoverField_a = null;

   public static void recoverMethod_a_f0554df3(Context var0) {
      if (recoverField_a == null) {
         recoverField_a = var0.getSharedPreferences("asus_push_sdk_preferences", 0);
      }
   }

   protected static void recoverMethod_a_e925fa09(String var0, String var1) {
      if (recoverField_a != null) {
         Editor var2;
         (var2 = recoverField_a.edit()).putString(var0, var1);
         var2.apply();
      }
   }

   protected static String recoverMethod_b_8ea82db7(String var0, String var1) {
      return recoverField_a != null ? recoverField_a.getString(var0, var1) : var1;
   }

   protected static void recoverMethod_a_afa340b5(String var0, boolean var1) {
      if (recoverField_a != null) {
         Editor var2;
         (var2 = recoverField_a.edit()).putBoolean(var0, false);
         var2.apply();
      }
   }

   protected static Boolean recoverMethod_b_19636ef2(String var0, boolean var1) {
      return recoverField_a != null ? recoverField_a.getBoolean(var0, true) : true;
   }
}
