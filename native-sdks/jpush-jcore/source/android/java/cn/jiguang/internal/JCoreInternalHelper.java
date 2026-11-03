package cn.jiguang.internal;

import android.content.Context;
import android.os.Bundle;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.bj.RecoverTypeaf2b0403;
import cn.jiguang.c.RecoverType5a68950;
import cn.jpush.android.service.JCommonService;
import java.util.concurrent.atomic.AtomicReference;

public class JCoreInternalHelper {
   private static final String TAG = "JCoreInternalHelper";
   private AtomicReference localJCoreHelperAction = new AtomicReference();
   private static JCoreHelperAction jCoreHelperAction = null;

   public static JCoreInternalHelper getInstance() {
      return JCoreInternalHelper.Member1d3bae45.recoverField_a;
   }

   private JCoreInternalHelper() {
   }

   public synchronized void initLoad(Context var1) {
      if (jCoreHelperAction == null) {
         if (var1 != null) {
            if (jCoreHelperAction == null) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreInternalHelper", "load action by local");
               jCoreHelperAction = new RecoverType5a68950();
            }
         }
      }
   }

   public Object onEvent(final Context var1, final String var2, final int var3, boolean var4, final String var5, final Bundle var6, final Object... var7) {
      if (!var4) {
         if (jCoreHelperAction != null) {
            return jCoreHelperAction.onEvent(var1, var2, var3, var5, var6, var7);
         }

         JCoreHelperAction var8 = (JCoreHelperAction)this.localJCoreHelperAction.get();
         if (var8 == null) {
            RecoverType5a68950 var9 = new RecoverType5a68950();
            var8 = var9;
            this.localJCoreHelperAction.set(var9);
         }

         return var8.onEvent(var1, var2, var3, var5, var6, var7);
      } else {
         RecoverTypeaf2b0403.recoverMethod_a_63b8eb77("DYNAMIC_LOAD_TASK", new RecoverTypeaf2b0401("JCoreInternalHelper#onEvent") {
            @Override
            public void recoverMethod_a_9b75() {
               try {
                  JCoreInternalHelper.this.initLoad(var1);
                  JCoreInternalHelper.jCoreHelperAction.onEvent(var1, var2, var3, var5, var6, var7);
               } catch (Throwable var2x) {
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCoreInternalHelper", "onEvent e:" + var2x);
               }
            }
         }, !RecoverType5a68950.recoverField_a.contains(var3));
         return null;
      }
   }

   public Bundle directHandle(final Context var1, final String var2, final String var3, final Bundle var4) {
      try {
         Class var5 = Class.forName(var2);
         if (JCommonService.class.isAssignableFrom(var5)) {
            getInstance().onEvent(var1, "JCore", 2, true, var3, var4);
            return new Bundle();
         }
      } catch (ClassNotFoundException var6) {
      }

      RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("DYNAMIC_LOAD_TASK", new RecoverTypeaf2b0401("JCoreInternalHelper#directHandle") {
         @Override
         public void recoverMethod_a_9b75() {
            try {
               if (JCoreInternalHelper.jCoreHelperAction != null) {
                  JCoreInternalHelper.jCoreHelperAction.directHandle(var1, var2, var3, var4);
                  return;
               }

               JCoreInternalHelper.this.initLoad(var1);
               JCoreInternalHelper.jCoreHelperAction.directHandle(var1, var2, var3, var4);
            } catch (Throwable var2x) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCoreInternalHelper", "directHandle e:" + var2x);
            }
         }
      });
      return new Bundle();
   }

   public Object commonMethod(final Context var1, final String var2, final Object var3) {
      if (jCoreHelperAction != null) {
         return jCoreHelperAction.onCommonMethod(var1, var2, var3);
      }

      RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("ONCE_TASK", new RecoverTypeaf2b0401("JCoreInternalHelper#commonMethod") {
         @Override
         public void recoverMethod_a_9b75() {
            try {
               JCoreInternalHelper.this.initLoad(var1);
               JCoreInternalHelper.jCoreHelperAction.onCommonMethod(var1, var2, var3);
            } catch (Throwable var2x) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCoreInternalHelper", "commonMethod e:" + var2x);
            }
         }
      });
      return null;
   }

   private static final class Member1d3bae45 {
      private static final JCoreInternalHelper recoverField_a = new JCoreInternalHelper();
   }
}
