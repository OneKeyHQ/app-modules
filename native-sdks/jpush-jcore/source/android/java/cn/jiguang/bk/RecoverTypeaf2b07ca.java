package cn.jiguang.bk;

import android.util.Pair;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import java.util.HashMap;

public class RecoverTypeaf2b07ca {
   private static HashMap recoverField_a = new HashMap();

   public static boolean recoverMethod_a_5d527815(Object var0) {
      return recoverMethod_a_4cfd00d7(var0, 0L);
   }

   public static boolean recoverMethod_a_4cfd00d7(Object var0, long var1) {
      return recoverMethod_a_52a38c94(var0, var1, 0);
   }

   public static boolean recoverMethod_a_52a38c94(Object var0, long var1, int var3) {
      try {
         int var5 = 0;
         var1 = var1 <= 0L ? 3600000L : var1;
         var3 = var3 <= 0 ? Integer.MAX_VALUE : var3;
         long var6 = System.currentTimeMillis();
         if (recoverField_a.containsKey(var0)) {
            Pair var4 = (Pair)recoverField_a.get(var0);
            if (null != var4) {
               long var8 = (Long)var4.first;
               var5 = (Integer)var4.second;
               long var10 = Math.abs(var6 - var8);
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
                  "ApiFrequency",
                  "check api frequency, functionName: "
                     + var0
                     + "; callApiInternalTimeLimit: "
                     + var1
                     + ", diffInternalTime: "
                     + var10
                     + "; callApiTimesLimit： "
                     + var3
                     + ", currentApiCallTimes: "
                     + var5
               );
               if (var10 <= var1 || var5 >= var3) {
                  return true;
               }
            }
         }

         Pair var15 = new Pair(var6, ++var5);
         recoverField_a.put(var0, var15);
      } catch (Throwable var12) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ApiFrequency", "isFrequency failed: " + var12.getMessage());
      }

      return false;
   }
}
