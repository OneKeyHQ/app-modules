package cn.jiguang.j;

import android.content.Context;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.g.RecoverType5a69853;
import cn.jiguang.i.RecoverType5a69fd6;
import java.util.ArrayList;
import java.util.List;

public class RecoverType5a6a399 {
   private static ArrayList recoverField_a = new ArrayList();
   private static ArrayList recoverField_b = new ArrayList();
   private static String recoverField_c = "JMethodControl";

   public static synchronized Object recoverMethod_a_8f0f26ca(Context var0, int var1, long var2, long var4, RecoverType5a6a399.Member368c2617 var6) {
      return recoverMethod_a_4a892cd6(var0, var1, var2, var4, true, false, true, true, false, false, true, var6);
   }

   public static synchronized Object recoverMethod_b_8f0f26ca(Context var0, int var1, long var2, long var4, RecoverType5a6a399.Member368c2617 var6) {
      return recoverMethod_a_4a892cd6(var0, var1, var2, var4, true, false, true, false, false, false, true, var6);
   }

   public static synchronized Object recoverMethod_a_b350334a(Context var0, int var1, RecoverType5a6a399.Member368c2617 var2) {
      return recoverMethod_a_4a892cd6(var0, var1, 0L, 0L, true, false, false, true, false, false, true, var2);
   }

   public static synchronized Object recoverMethod_a_344864ca(Context var0, int var1, boolean var2, boolean var3, RecoverType5a6a399.Member368c2617 var4) {
      return recoverMethod_a_4a892cd6(var0, var1, 0L, 0L, true, false, false, var2, false, false, var3, var4);
   }

   private static synchronized Object recoverMethod_a_4a892cd6(
      Context var0,
      int var1,
      long var2,
      long var4,
      boolean var6,
      boolean var7,
      boolean var8,
      boolean var9,
      boolean var10,
      boolean var11,
      boolean var12,
      RecoverType5a6a399.Member368c2617 var13
   ) {
      Object var14 = null;
      if (var7) {
         RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09(recoverField_c, "from cmd ,so get from api");
         return var13.recoverMethod_b_810c345d();
      }

      if (var13 == null) {
         return var14;
      }

      var14 = var13.recoverMethod_c_810c345d();
      RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09(recoverField_c, "local memory result=" + var14 + " ,configId= " + var1);
      if (recoverMethod_a_5d527815(var14)) {
         var14 = var13.recoverMethod_a_810c345d();
         RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09(recoverField_c, "local cache result=" + var14 + " ,configId= " + var1);
      }

      if (recoverMethod_a_5d527815(var14)) {
         var14 = RecoverType5a69fd6.recoverMethod_a_3dcfabbc(var0, var1, var12);
         RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09(recoverField_c, "sp cache result=" + var14 + " ,configId= " + var1);
      }

      if (var10 && !var11) {
         RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09(recoverField_c, "result out of expire time,current result=" + var14);
         var13.recoverMethod_a_5d527811(var14);
         if (var9 && !RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_d_134632(var1)) {
            RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09(recoverField_c, "not call use api. keep lastResult=" + var14 + " ,configId= " + var1);
         } else {
            var14 = var13.recoverMethod_b_810c345d();
            RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09(recoverField_c, "need use api, result=" + var14 + " ,configId= " + var1);
         }

         recoverMethod_a_92b5dd44(var0, var1, var14, var6, var12, var13);
         return var14;
      } else {
         if (var11) {
            RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09(recoverField_c, "is frequency, so use cache result=" + var14 + " ,configId= " + var1);
            return var14;
         }

         long var15 = System.currentTimeMillis();
         if (!recoverMethod_a_5d527815(var14)) {
            if (!var8) {
               RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09(recoverField_c, "no need update,so use cache result=" + var14 + " ,configId= " + var1);
               return var14;
            }

            long var17 = RecoverType5a69fd6.recoverMethod_b_1a54e364(var0, var1);
            if (Math.abs(var15 - var17) > var4) {
               RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09(recoverField_c, "result out of expire time,current result=" + var14);
               var13.recoverMethod_a_5d527811(var14);
               if (var9 && !RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_d_134632(var1)) {
                  RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09(recoverField_c, "not call use api. keep lastResult=" + var14 + " ,configId= " + var1);
               } else {
                  var14 = var13.recoverMethod_b_810c345d();
                  RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09(recoverField_c, "need use api, result=" + var14 + " ,configId= " + var1);
               }

               recoverMethod_a_92b5dd44(var0, var1, var14, var6, var12, var13);
            } else {
               var13.recoverMethod_b_5d527811(var14);
            }
         } else {
            long var21 = RecoverType5a69fd6.recoverMethod_a_1a54e364(var0, var1);
            boolean var19 = Math.abs(var15 - var21) >= var2 && !recoverField_b.contains(var1);
            if (var19 && (!var9 || RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_d_134632(var1))) {
               var14 = var13.recoverMethod_b_810c345d();
               if (recoverField_a.contains(var1)) {
                  recoverField_b.add(var1);
               }

               RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09(recoverField_c, "over interval time,  get api result=" + var14 + " ,configId= " + var1);
               recoverMethod_a_92b5dd44(var0, var1, var14, var6, var12, var13);
            } else {
               RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09(recoverField_c, "return default result=" + var14);
            }
         }

         return var14;
      }
   }

   private static void recoverMethod_a_92b5dd44(Context var0, int var1, Object var2, boolean var3, boolean var4, RecoverType5a6a399.Member368c2617 var5) {
      long var6 = System.currentTimeMillis();
      if (!recoverMethod_a_5d527815(var2)) {
         var5.recoverMethod_b_5d527811(var2);
         if (var3) {
            RecoverType5a69fd6.recoverMethod_a_aaf0c8da(var0, var1, (String)var2, var4);
            RecoverType5a69fd6.recoverMethod_b_30480154(var0, var1, var6);
         }
      }

      RecoverType5a69fd6.recoverMethod_a_30480154(var0, var1, var6);
   }

   private static boolean recoverMethod_a_5d527815(Object var0) {
      if (null == var0) {
         return true;
      } else if (var0 instanceof String) {
         return TextUtils.isEmpty((String)var0);
      } else {
         return var0 instanceof List ? ((List)var0).isEmpty() : false;
      }
   }

   static {
      recoverField_a.add(2004);
      recoverField_a.add(2005);
      recoverField_a.add(1012);
      recoverField_a.add(2000);
      recoverField_a.add(1014);
      recoverField_a.add(3334);
   }

   public abstract static class Member368c2616 implements RecoverType5a6a399.Member368c2617 {
      @Override
      public Object recoverMethod_a_810c345d() {
         return null;
      }

      @Override
      public void recoverMethod_a_5d527811(Object var1) {
      }
   }

   public interface Member368c2617 {
      Object recoverMethod_b_810c345d();

      Object recoverMethod_a_810c345d();

      Object recoverMethod_c_810c345d();

      void recoverMethod_b_5d527811(Object var1);

      void recoverMethod_a_5d527811(Object var1);
   }
}
