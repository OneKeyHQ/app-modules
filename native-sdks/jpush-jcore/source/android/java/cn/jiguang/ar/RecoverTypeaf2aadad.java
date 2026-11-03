package cn.jiguang.ar;

import android.content.Context;
import android.text.TextUtils;
import cn.jiguang.bk.RecoverTypeaf2b07c2;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

public class RecoverTypeaf2aadad {
   private static final Comparator recoverField_a = new Comparator() {
      public int recoverMethod_a_a83f9ac8(RecoverTypeaf2aadac var1, RecoverTypeaf2aadac var2) {
         if (var1.equals(var2)) {
            return 0;
         }

         if (var1.recoverField_b > var2.recoverField_b) {
            return -1;
         }

         if (var1.recoverField_b < var2.recoverField_b) {
            return 1;
         }

         if (var1.recoverField_e > var2.recoverField_e) {
            return -1;
         }

         if (var1.recoverField_e < var2.recoverField_e) {
            return 1;
         }

         if (var1.recoverField_d != 0L && var2.recoverField_d != 0L) {
            if (var1.recoverField_d < var2.recoverField_d) {
               return -1;
            }

            if (var1.recoverField_d > var2.recoverField_d) {
               return 1;
            }
         }

         if (var1.recoverField_c != 0L && var2.recoverField_c != 0L) {
            if (var1.recoverField_c > var2.recoverField_c + 180000L) {
               return -1;
            }

            if (var1.recoverField_c < var2.recoverField_c - 180000L) {
               return 1;
            }
         }

         return 0;
      }

      // $VF: synthetic method
      @Override
      public int compare(Object var1, Object var2) {
         return this.recoverMethod_a_a83f9ac8((RecoverTypeaf2aadac)var1, (RecoverTypeaf2aadac)var2);
      }
   };

   public static void recoverMethod_a_51aad880(Context var0, RecoverTypeaf2aadaf var1, int var2, long var3) {
      if (var1 != null) {
         RecoverType5a690d1 var5 = RecoverType5a690d1.recoverMethod_f_5ef90ef5(var1.toString());
         RecoverType5a690d1 var6 = RecoverType5a690d1.recoverMethod_g_5ef90ef5(var1.toString());
         String var7 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var0, var6);
         if (TextUtils.isEmpty(var7)) {
            var7 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var0, var5);
         }

         RecoverTypeaf2aadac var8 = RecoverTypeaf2aadac.recoverMethod_a_800e3004(var7);
         if (var8 == null) {
            var8 = new RecoverTypeaf2aadac(var1);
         }

         var8.recoverField_d = var3;
         var8.recoverField_b = var2;
         RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, var6.recoverMethod_a_4e9722a3(var8.recoverMethod_a_73cf11cb()));
      }
   }

   public static LinkedHashSet recoverMethod_a_56f73f67(Context var0, LinkedHashSet var1, long var2) {
      int var4 = var1 != null ? var1.size() : 0;
      if (var4 == 0) {
         return new LinkedHashSet();
      }

      LinkedList var5 = new LinkedList();
      boolean var6 = true;

      for (Object recoveryElement_97 : var1) {
         RecoverTypeaf2aadaf var8 = (RecoverTypeaf2aadaf)recoveryElement_97;
         if (var8.recoverMethod_a_9b79()) {
            RecoverType5a690d1 var9 = RecoverType5a690d1.recoverMethod_f_5ef90ef5(var8.toString());
            RecoverType5a690d1 var10 = RecoverType5a690d1.recoverMethod_g_5ef90ef5(var8.toString());
            String var11 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var0, var10);
            if (TextUtils.isEmpty(var11)) {
               var11 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var0, var9);
            }

            RecoverTypeaf2aadac var12 = RecoverTypeaf2aadac.recoverMethod_a_800e3004(var11);
            if (var12 == null) {
               var12 = new RecoverTypeaf2aadac(var8);
            }

            if (var6) {
               var12.recoverField_e = 1;
               var6 = false;
            }

            if (var2 > 0L) {
               var12.recoverField_c = var2;
               RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, var10.recoverMethod_a_4e9722a3(var12.recoverMethod_a_73cf11cb()));
            }

            var5.add(var12);
         }
      }

      LinkedList var13 = recoverMethod_a_899a862f(var5, recoverField_a);
      return recoverMethod_a_518edbe1(var0, var13);
   }

   public static LinkedHashSet recoverMethod_a_518edbe1(Context var0, LinkedList var1) {
      boolean var2 = RecoverTypeaf2b07c2.recoverMethod_d_f0554df7(var0);
      LinkedHashSet var3 = new LinkedHashSet();
      ArrayList var4 = new ArrayList();
      ArrayList var5 = new ArrayList();

      for (Object recoveryElement_135 : var1) {
         LinkedList var7 = (LinkedList)recoveryElement_135;
         for (Object recoveryElement_136 : var7) {
            RecoverTypeaf2aadac var9 = (RecoverTypeaf2aadac)recoveryElement_136;
            RecoverTypeaf2aadaf var10 = var9.recoverField_a;
            if (var10.recoverField_a.contains(":")) {
               var4.add(var10);
            } else {
               var5.add(var10);
            }
         }
      }

      var3.clear();
      if (var2) {
         var3.addAll(var4);
         var3.addAll(var5);
      } else {
         var3.addAll(var5);
         var3.addAll(var4);
      }

      return var3;
   }

   private static LinkedList recoverMethod_a_899a862f(Collection var0, Comparator var1) {
      int var2 = var0 != null ? var0.size() : 0;
      if (var2 == 0) {
         return new LinkedList();
      }

      LinkedList var3 = new LinkedList();
      if (var2 == 1) {
         LinkedList var12 = new LinkedList(var0);
         var3.add(var12);
         return var3;
      }

      SecureRandom var4 = new SecureRandom();

      for (Object var6 : var0) {
         boolean var7 = false;

         for (int var8 = 0; var8 < var3.size(); var8++) {
            LinkedList var9 = (LinkedList)var3.get(var8);
            int var10 = var1.compare(var6, var9.get(0));
            if (var10 == 0) {
               int var14 = var4.nextInt(var9.size() + 1);
               var9.add(var14, var6);
               var7 = true;
               break;
            }

            if (var10 < 0) {
               LinkedList var11 = new LinkedList();
               var11.add(var6);
               var3.add(var8, var11);
               var7 = true;
               break;
            }
         }

         if (!var7) {
            LinkedList var13 = new LinkedList();
            var13.add(var6);
            var3.add(var13);
         }
      }

      return var3;
   }
}
