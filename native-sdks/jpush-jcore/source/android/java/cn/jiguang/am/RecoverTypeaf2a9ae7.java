package cn.jiguang.am;

import android.content.Context;
import cn.jiguang.e.RecoverType5a690d3;

public class RecoverTypeaf2a9ae7 {
   public static synchronized void recoverMethod_a_f0554df3(Context var0) {
      RecoverType5a690d3.recoverMethod_a_1a54e370(var0, 1);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("LogRefManager", "Log reference count increased to: 1");
   }

   public static synchronized void recoverMethod_b_f0554df3(Context var0) {
      int var1 = RecoverType5a690d3.recoverMethod_c_f0554de6(var0);
      if (var1 > 0) {
         RecoverType5a690d3.recoverMethod_a_1a54e370(var0, --var1);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("LogRefManager", "Log reference count decreased to: " + var1);
      }
   }

   public static synchronized boolean recoverMethod_c_f0554df7(Context var0) {
      int var1 = RecoverType5a690d3.recoverMethod_c_f0554de6(var0);
      boolean var2 = var1 == 0;
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("LogRefManager", "Can clear log: " + var2);
      return var2;
   }
}
