package cn.jiguang.as;

import android.text.TextUtils;
import android.util.Pair;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.ar.RecoverTypeaf2aadaf;
import cn.jiguang.au.RecoverTypeaf2ab8ed;
import cn.jiguang.bb.RecoverTypeaf2ae5fa;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class RecoverTypeaf2ab175 {
   private long recoverField_a = 86400000L;
   private long recoverField_b = 1800000L;
   private final Map recoverField_c = new HashMap();

   private RecoverTypeaf2ab175() {
   }

   public static RecoverTypeaf2ab175 recoverMethod_a_c695e2d1() {
      return RecoverTypeaf2ab175.Member8f442cf3.recoverField_a;
   }

   public LinkedHashSet recoverMethod_a_d8320d7b(String var1, long var2) {
      if (TextUtils.isEmpty(var1)) {
         return null;
      }

      LinkedHashSet var4 = null;
      Pair var5 = this.recoverMethod_a_ae156749(var1, this.recoverField_a, this.recoverField_b);
      if (var5 != null) {
         if (var5.first != null && ((LinkedHashSet)var5.first).size() > 0) {
            var4 = (LinkedHashSet)var5.first;
         }

         if (!(Boolean)var5.second) {
            return var4;
         }
      }

      FutureTask var6 = new FutureTask(new RecoverTypeaf2ab175.Member8f442cf2(var1, this));
      RecoverTypeaf2aa628.recoverMethod_e_a4fc3973(var6);
      if (var2 == 0L) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SRVLoader", "use cache=" + var4);
         return var4;
      }

      LinkedHashSet var7 = null;

      try {
         var7 = (LinkedHashSet)var6.get(var2, TimeUnit.MILLISECONDS);
      } catch (Throwable var9) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("SRVLoader", "run futureTask e:" + var9);
      }

      if (var7 != null && var7.size() > 0) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SRVLoader", "use resolved result=" + var7);
         return var7;
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SRVLoader", "use cache=" + var4);
         return var4;
      }
   }

   private static byte[] recoverMethod_b_afa28ebe(String var0) throws java.io.IOException {
      RecoverTypeaf2ab170 var1 = RecoverTypeaf2ab170.recoverMethod_a_800ea4c0(var0);
      RecoverTypeaf2ab170 var2 = RecoverTypeaf2ab170.recoverMethod_a_e80dd116(var1, RecoverTypeaf2ab170.recoverField_a);
      RecoverTypeaf2ab172 var3 = RecoverTypeaf2ab172.recoverMethod_a_e84e09db(var2, 33, 1);
      RecoverTypeaf2ab16e var4 = RecoverTypeaf2ab16e.recoverMethod_a_e45404bd(var3);
      return var4.recoverMethod_c_255806f(65535);
   }

   public static LinkedHashSet recoverMethod_a_6066ccd5(String var0) {
      if (TextUtils.isEmpty(var0)) {
         return null;
      }

      byte[] var1;
      try {
         var1 = recoverMethod_b_afa28ebe(var0);
      } catch (IOException var20) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("SRVLoader", "can't srv, create query:" + var20);
         return null;
      }

      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SRVLoader", "srv host:" + var0);
      String var2 = RecoverTypeaf2ae5fa.recoverMethod_a_c69c3eec().recoverMethod_a_e96ea081(var0);
      LinkedHashSet var3 = new LinkedHashSet();

      try {
         String[] var4 = RecoverTypeaf2ab174.recoverMethod_b_c695e2b2().recoverMethod_a_7e9e5c04();
         if (var4 == null || var4.length == 0) {
            return var3;
         }

         LinkedHashSet var5 = new LinkedHashSet();
         RecoverTypeaf2ab8ed var6 = RecoverTypeaf2ab8ed.recoverMethod_a_c696ca59();

         for (String var10 : var4) {
            InetAddress var11 = var6.recoverMethod_a_4a85a8b7(null, var10, 3000L, false);
            if (var11 != null) {
               var5.add(var11);
            }
         }

         Iterator var23 = var5.iterator();

         label91:
         while (true) {
            RecoverTypeaf2ab16e var25;
            while (true) {
               if (!var23.hasNext()) {
                  break label91;
               }

               InetAddress var24 = (InetAddress)var23.next();

               try {
                  byte[] var26 = RecoverTypeaf2ab178.recoverMethod_a_fbb032ef(null, new InetSocketAddress(var24, 53), var1, System.currentTimeMillis() + 1000L);
                  var25 = new RecoverTypeaf2ab16e(var26);
                  break;
               } catch (IOException var21) {
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("SRVLoader", "tcp send to  err:" + var21);
               }
            }

            RecoverTypeaf2ab172 var27 = var25.recoverMethod_a_c695e274();
            if (var27 == null) {
               break;
            }

            RecoverTypeaf2ab171[] var28 = var25.recoverMethod_b_fdcaeb63(1);

            for (RecoverTypeaf2ab171 var15 : var28) {
               if (var15.recoverMethod_d_9b68() == var27.recoverMethod_g_9b68()
                  && var15.recoverMethod_c_9b68() == var27.recoverMethod_e_9b68()
                  && var15.recoverMethod_b_c695e236().equals(var27.recoverMethod_d_c695e236())) {
                  Iterator var16 = var15.recoverMethod_a_b7246de2();

                  while (var16.hasNext()) {
                     RecoverTypeaf2ab176 var17 = (RecoverTypeaf2ab176)var16.next();
                     if (var17.recoverMethod_j_9b68() > 0) {
                        String var18 = var17.recoverMethod_k_c695e236().toString();
                        if (!TextUtils.isEmpty(var18)) {
                           if (var18.endsWith(".")) {
                              var18 = var18.substring(0, var18.length() - 1);
                           }

                           RecoverTypeaf2aadaf var19 = new RecoverTypeaf2aadaf(var18, var17.recoverMethod_j_9b68());
                           if (var19.recoverMethod_a_9b79()) {
                              var3.add(var19);
                           }
                        }
                     }
                  }
               }
            }
         }

         RecoverTypeaf2ae5fa.recoverMethod_a_c69c3eec().recoverMethod_a_afa300e4(var2, 0);
      } catch (Throwable var22) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("SRVLoader", "Get default ports error with Exception:" + var22);
         RecoverTypeaf2ae5fa.recoverMethod_a_c69c3eec().recoverMethod_a_afa300e4(var2, 2);
      }

      return var3;
   }

   private void recoverMethod_a_d4f8e87b(String var1, Pair var2) {
      if (var2.first != null && ((LinkedHashSet)var2.first).size() > 0 && var2.second != null) {
         this.recoverField_c.put(var1, var2);
         StringBuilder var3 = new StringBuilder();

         for (Object recoveryElement_185 : (LinkedHashSet)var2.first) {
            RecoverTypeaf2aadaf var5 = (RecoverTypeaf2aadaf)recoveryElement_185;
            var3.append(var5.toString()).append(",");
         }

         var3.deleteCharAt(var3.length() - 1);
         RecoverType5a690d2.recoverMethod_a_abcb8d40(
            null,
            RecoverType5a690d1.recoverMethod_d_5ef90ef5(var1).recoverMethod_a_4e9722a3(var3.toString()),
            RecoverType5a690d1.recoverMethod_e_5ef90ef5(var1).recoverMethod_a_4e9722a3((Long)var2.second)
         );
      }
   }

   private Pair recoverMethod_a_ae156749(String var1, long var2, long var4) {
      Pair var6 = (Pair)this.recoverField_c.get(var1);
      boolean var7 = var6 == null || var6.first == null || ((LinkedHashSet)var6.first).size() == 0;
      long var8;
      if (var7) {
         var8 = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(null, RecoverType5a690d1.recoverMethod_e_5ef90ef5(var1));
      } else {
         var8 = (Long)var6.second;
      }

      long var10 = System.currentTimeMillis();
      if (var10 > var8 + var2) {
         return null;
      }

      LinkedHashSet var12 = new LinkedHashSet();
      if (var7) {
         String var13 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(null, RecoverType5a690d1.recoverMethod_d_5ef90ef5(var1));
         if (!TextUtils.isEmpty(var13)) {
            String[] var14 = var13.split(",");

            for (String var18 : var14) {
               RecoverTypeaf2aadaf var19 = RecoverTypeaf2aadaf.recoverMethod_a_800e3061(var18);
               if (var19 != null && var19.recoverMethod_a_9b79()) {
                  var12.add(var19);
               }
            }

            this.recoverField_c.put(var1, new Pair(var12, System.currentTimeMillis()));
         }
      } else {
         var12 = (LinkedHashSet)var6.first;
      }

      return !var12.isEmpty() ? new Pair(var12, var10 > var8 + var4) : null;
   }

   static class Member8f442cf2 implements Callable {
      private String recoverField_a;
      private RecoverTypeaf2ab175 recoverField_b;

      Member8f442cf2(String var1, RecoverTypeaf2ab175 var2) {
         this.recoverField_a = var1;
         this.recoverField_b = var2;
      }

      public LinkedHashSet recoverMethod_a_d5ec344b() {
         LinkedHashSet var1 = RecoverTypeaf2ab175.recoverMethod_a_6066ccd5(this.recoverField_a);
         if (var1 != null && var1.size() > 0) {
            this.recoverField_b.recoverMethod_a_d4f8e87b(this.recoverField_a, new Pair(var1, System.currentTimeMillis()));
         }

         return var1;
      }

      // $VF: synthetic method
      @Override
      public Object call() {
         return this.recoverMethod_a_d5ec344b();
      }
   }

   private static final class Member8f442cf3 {
      private static final RecoverTypeaf2ab175 recoverField_a = new RecoverTypeaf2ab175();
   }
}
