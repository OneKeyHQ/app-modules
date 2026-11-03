package cn.jiguang.am;

import android.util.Log;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.internal.JConstants;

public class RecoverTypeaf2a9ae6 {
   private static boolean recoverMethod_a_134632(int var0) {
      return var0 >= 2;
   }

   public static void recoverMethod_a_473e1ef3(String var0, String var1, boolean var2, int var3, String var4, Throwable var5) {
      try {
         if (RecoverType5a681cd.recoverField_g) {
            switch (var3) {
               case 2:
                  RecoverTypeaf2a9ae8.recoverMethod_a_2d6f8978("T", "JIGUANG-" + var0, var1, var4, null);
                  break;
               case 3:
                  RecoverTypeaf2a9ae8.recoverMethod_a_2d6f8978("D", "JIGUANG-" + var0, var1, var4, null);
                  break;
               case 4:
                  RecoverTypeaf2a9ae8.recoverMethod_a_2d6f8978("I", "JIGUANG-" + var0, var1, var4, null);
                  break;
               case 5:
                  RecoverTypeaf2a9ae8.recoverMethod_a_2d6f8978("W", "JIGUANG-" + var0, var1, var4, null);
                  break;
               case 6:
                  RecoverTypeaf2a9ae8.recoverMethod_a_2d6f8978("E", "JIGUANG-" + var0, var1, var4, null);
            }
         }

         boolean var6 = false;

         try {
            var6 = JConstants.CMD_TO_PRINT_ALL_LOG;
         } catch (Throwable var8) {
         }

         if (!var6) {
            if (!JConstants.DEBUG_MODE && var3 < 5) {
               return;
            }

            if (var2 && var3 < 6) {
               return;
            }

            if (!recoverMethod_a_134632(var3)) {
               return;
            }
         }

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
         }
      } catch (Throwable var9) {
      }
   }
}
