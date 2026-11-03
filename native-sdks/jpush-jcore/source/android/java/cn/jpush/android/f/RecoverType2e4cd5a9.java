package cn.jpush.android.f;

import cn.jpush.android.e.RecoverType2e4cd1e8;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class RecoverType2e4cd5a9 {
   private static Map recoverField_a = new ConcurrentHashMap();

   public static void recoverMethod_a_fb7a1441(RecoverType2e4cd5aa var0, RecoverType2e4cd1e8 var1) {
      if (var1 != null) {
         recoverField_a.put(var0, var1);
      }
   }

   public static RecoverType2e4cd1e8 recoverMethod_a_66272223(RecoverType2e4cd5aa var0) {
      return (RecoverType2e4cd1e8)recoverField_a.get(var0);
   }

   public static void recoverMethod_b_7757a73a(RecoverType2e4cd5aa var0) {
      recoverField_a.remove(var0);
   }
}
