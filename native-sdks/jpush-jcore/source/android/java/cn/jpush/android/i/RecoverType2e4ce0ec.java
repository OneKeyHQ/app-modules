package cn.jpush.android.i;

import android.util.Log;
import cn.jpush.android.f.RecoverType2e4cd5ab;

public final class RecoverType2e4ce0ec {
   // $VF: Duplicated exception handlers to handle obfuscated exceptions
   public static void recoverMethod_a_84873242(String var0, String var1, boolean var2, int var3, String var4) {
      try {
         if (!RecoverType2e4cd5ab.recoverField_d && var3 < 5) {
            return;
         }
      } catch (Throwable var6) {
         return;
      }

      if (!var2 || var3 >= 6) {
         if (var3 >= 3) {
            try {
               switch (var3) {
                  case 2:
                     Log.v("JIGUANG-" + var0, "[" + var1 + "] " + var4);
                     break;
                  case 3:
                     Log.d("JIGUANG-" + var0, "[" + var1 + "] " + var4);
                     break;
                  case 4:
                     Log.i("JIGUANG-" + var0, "[" + var1 + "] " + var4);
                     break;
                  case 5:
                     Log.w("JIGUANG-" + var0, "[" + var1 + "] " + var4);
                     break;
                  case 6:
                     Log.e("JIGUANG-" + var0, "[" + var1 + "] " + var4);
                  default:
                     return;
               }
            } catch (Throwable var5) {
            }
         }
      }
   }
}
