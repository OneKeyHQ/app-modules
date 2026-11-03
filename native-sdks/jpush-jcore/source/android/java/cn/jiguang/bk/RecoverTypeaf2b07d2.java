package cn.jiguang.bk;

import android.app.Application;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class RecoverTypeaf2b07d2 {
   private static void recoverMethod_a_5d527811(Object var0) throws java.lang.Exception {
      if (var0 == null) {
         throw new Exception("owner can not be null");
      }
   }

   private static void recoverMethod_a_979d1294(Class[] var0, Object[] var1) throws java.lang.Exception {
      int var2 = var1 != null ? var1.length : 0;
      int var3 = var0 != null ? var0.length : 0;
      if (var2 != var3) {
         throw new Exception("argClasses' size is not equal to args' size");
      }
   }

   public static Object recoverMethod_a_1564af74(Object var0, String var1, Object[] var2, Class[] var3) throws java.lang.IllegalAccessException, java.lang.NoSuchMethodException , java.lang.reflect.InvocationTargetException, java.lang.Exception {
      recoverMethod_a_5d527811(var0);
      recoverMethod_a_979d1294(var3, var2);
      Method var4 = var0.getClass().getMethod(var1, var3);
      return var4.invoke(var0, var2);
   }

   public static Object recoverMethod_a_92542ccb(Class var0, String var1, Object[] var2, Class[] var3) throws java.lang.IllegalAccessException, java.lang.NoSuchMethodException , java.lang.reflect.InvocationTargetException, java.lang.Exception {
      recoverMethod_a_5d527811(var0);
      recoverMethod_a_979d1294(var3, var2);
      Method var4 = var0.getMethod(var1, var3);
      return var4.invoke(var0, var2);
   }

   public static Application recoverMethod_a_c4d1044() {
      try {
         Class var0 = Class.forName("android.app.ActivityThread");
         Object var1 = recoverMethod_c_810c345d();
         Object var2 = var0.getMethod("getApplication").invoke(var1);
         return var2 == null ? null : (Application)var2;
      } catch (Throwable var3) {
         return null;
      }
   }

   private static Object recoverMethod_c_810c345d() {
      Object var0 = recoverMethod_d_810c345d();
      return var0 != null ? var0 : recoverMethod_e_810c345d();
   }

   private static Object recoverMethod_d_810c345d() {
      try {
         Class var0 = Class.forName("android.app.ActivityThread");
         Field var1 = var0.getDeclaredField("sCurrentActivityThread");
         var1.setAccessible(true);
         return var1.get(null);
      } catch (Throwable var2) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ReflectUtils", "getActivityThreadInActivityThreadStaticField: " + var2.getMessage());
         return null;
      }
   }

   private static Object recoverMethod_e_810c345d() {
      try {
         Class var0 = Class.forName("android.app.ActivityThread");
         return var0.getMethod("currentActivityThread").invoke(null);
      } catch (Throwable var1) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ReflectUtils", "getActivityThreadInActivityThreadStaticMethod: " + var1.getMessage());
         return null;
      }
   }

   public static String recoverMethod_b_73cf11cb() {
      String var0 = null;

      try {
         Method var1 = Class.forName("android.app.ActivityThread", false, Application.class.getClassLoader()).getDeclaredMethod("currentProcessName");
         var1.setAccessible(true);
         Object var2 = var1.invoke(null);
         if (var2 instanceof String) {
            var0 = (String)var2;
         }
      } catch (Throwable var3) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ReflectUtils", "getCurrentProcessNameByActivityThread: " + var3.getMessage());
      }

      return var0;
   }
}
