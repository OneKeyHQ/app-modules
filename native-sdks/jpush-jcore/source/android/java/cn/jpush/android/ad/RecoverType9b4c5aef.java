package cn.jpush.android.ad;

import java.lang.reflect.Method;

public class RecoverType9b4c5aef {
   public static Object recoverMethod_a_222a2ac4(Object var0, String var1, Class[] var2, Object[] var3) throws java.lang.Exception, java.lang.NoSuchMethodException {
      int var4 = var3 != null ? var3.length : 0;
      int var5 = var2 != null ? var2.length : 0;
      if (var4 != var5) {
         throw new IllegalArgumentException("argClasses' size is not equal to args' size");
      }

      Method var6 = var0.getClass().getMethod(var1, var2);
      boolean var7 = var6.isAccessible();
      if (!var7) {
         var6.setAccessible(true);
      }

      Object var8 = null;
      Exception var9 = null;

      try {
         var8 = var6.invoke(var0, var3);
      } catch (Exception var11) {
         var9 = var11;
      }

      if (!var7) {
         var6.setAccessible(false);
      }

      if (var9 != null) {
         throw var9;
      } else {
         return var8;
      }
   }
}
