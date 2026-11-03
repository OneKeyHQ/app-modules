package cn.jiguang.az;

import cn.jiguang.ba.RecoverTypeaf2ae23c;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class RecoverTypeaf2acbbb {
   public RecoverTypeaf2acbbb() {
   }

   List recoverMethod_a_55c69211(StackTraceElement[] var1) {
      ArrayList var2 = null;
      if (var1 != null && var1.length > 0) {
         var2 = new ArrayList();

         for (StackTraceElement var6 : var1) {
            if (var6 != null) {
               String var7 = var6.getClassName();
               RecoverTypeaf2ae23c var8 = new RecoverTypeaf2ae23c();
               boolean var9 = this.recoverMethod_a_aad3b203(var7);
               var8.recoverMethod_a_e8e24db8(var9);
               var8.recoverMethod_c_aad3b1ff(var7);
               var8.recoverMethod_b_aad3b1ff(var6.getMethodName());
               var8.recoverMethod_a_aad3b1ff(var6.getFileName());
               if (var6.getLineNumber() >= 0) {
                  var8.recoverMethod_a_9b5e18e2(var6.getLineNumber());
               }

               var8.recoverMethod_b_e8e24db8(var6.isNativeMethod());
               var2.add(var8);
            }
         }

         Collections.reverse(var2);
      }

      return var2;
   }

   boolean recoverMethod_a_aad3b203(String var1) {
      return var1 == null || var1.isEmpty() ? false : var1.contains("cn.jiguang") || var1.contains("cn.jpush");
   }
}
