package cn.jiguang.au;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.bb.RecoverTypeaf2ae5fa;
import cn.jiguang.bk.RecoverTypeaf2b07d5;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class RecoverTypeaf2ab8ed {
   private long recoverField_a = 36000000L;
   private long recoverField_b = 900000L;
   private final Map recoverField_c = new HashMap();

   private RecoverTypeaf2ab8ed() {
   }

   public static RecoverTypeaf2ab8ed recoverMethod_a_c696ca59() {
      return RecoverTypeaf2ab8ed.Member8f60366b.recoverField_a;
   }

   public InetAddress recoverMethod_a_4a85a8b7(Context var1, String var2, long var3, boolean var5) {
      InetAddress[] var6 = this.recoverMethod_b_d5a42320(var1, var2, var3, var5);
      return var6 != null && var6.length > 0 ? var6[0] : null;
   }

   private InetAddress recoverMethod_a_a7a2e445(String var1) {
      InetAddress var2 = null;
      if (RecoverTypeaf2b07d5.recoverMethod_f_aad3b203(var1) || RecoverTypeaf2b07d5.recoverMethod_g_aad3b203(var1)) {
         try {
            var2 = InetAddress.getByName(var1);
         } catch (UnknownHostException var4) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("DNSLoader", "dns resolve failed:" + var4);
         }
      }

      return var2;
   }

   public InetAddress[] recoverMethod_b_d5a42320(Context var1, String var2, long var3, boolean var5) {
      if (TextUtils.isEmpty(var2)) {
         return null;
      }

      InetAddress var6 = this.recoverMethod_a_a7a2e445(var2);
      if (var6 != null) {
         return new InetAddress[]{var6};
      }

      InetAddress[] var7 = null;
      Pair var8 = this.recoverMethod_a_59ce4b47(var1, var2, this.recoverField_a, this.recoverField_b);
      if (var8 != null) {
         if (var8.first != null) {
            var7 = (InetAddress[])var8.first;
         }

         if (!(Boolean)var8.second) {
            if (var5) {
               FutureTask var13 = new FutureTask(new RecoverTypeaf2ab8ed.Member8f60366a(var1, var2, this));
               RecoverTypeaf2aa628.recoverMethod_e_a4fc3973(var13);
            }

            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("DNSLoader", "use cache=" + Arrays.toString(var7));
            return var7;
         }
      }

      FutureTask var9 = new FutureTask(new RecoverTypeaf2ab8ed.Member8f60366a(var1, var2, this));
      RecoverTypeaf2aa628.recoverMethod_e_a4fc3973(var9);
      if (var3 == 0L) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("DNSLoader", "use cache=" + Arrays.toString(var7));
         return var7;
      }

      InetAddress[] var10 = null;

      try {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("DNSLoader", "waiting dns for " + var2);
         var10 = (InetAddress[])var9.get(var3, TimeUnit.MILLISECONDS);
      } catch (Throwable var12) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("DNSLoader", "run futureTask e:" + var12);
      }

      if (var10 != null) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("DNSLoader", "use resolved result=" + Arrays.toString(var10));
         return var10;
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("DNSLoader", "use cache=" + Arrays.toString(var7));
         return var7;
      }
   }

   private InetAddress[] recoverMethod_a_bd3e35d0(Context var1, String var2) {
      String var3 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var1, RecoverType5a690d1.recoverMethod_b_5ef90ef5(var2));
      if (TextUtils.isEmpty(var3)) {
         return null;
      }

      String[] var4 = var3.split(",");
      LinkedList var5 = new LinkedList();

      for (String var9 : var4) {
         InetAddress var10 = this.recoverMethod_a_a7a2e445(var9);
         if (var10 != null) {
            var5.add(var10);
         }
      }

      return var5.isEmpty() ? null : (InetAddress[])var5.toArray(new InetAddress[0]);
   }

   private Pair recoverMethod_a_59ce4b47(Context var1, String var2, long var3, long var5) {
      Pair var7 = (Pair)this.recoverField_c.get(var2);
      boolean var8 = var7 == null || var7.first == null;
      long var9;
      if (var8) {
         var9 = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(var1, RecoverType5a690d1.recoverMethod_c_5ef90ef5(var2));
      } else {
         var9 = (Long)var7.second;
      }

      long var11 = System.currentTimeMillis();
      if (var11 > var9 + var3) {
         return null;
      }

      InetAddress[] var13;
      if (var8) {
         var13 = this.recoverMethod_a_bd3e35d0(var1, var2);
         if (var13 != null) {
            Pair var14 = new Pair(var13, var9);
            this.recoverField_c.put(var2, var14);
         }
      } else {
         var13 = (InetAddress[])var7.first;
      }

      return var13 != null ? new Pair(var13, var11 > var9 + var5) : null;
   }

   static class Member8f60366a implements Callable {
      private Context recoverField_a;
      private String recoverField_b;
      private RecoverTypeaf2ab8ed recoverField_c;

      Member8f60366a(Context var1, String var2, RecoverTypeaf2ab8ed var3) {
         this.recoverField_a = var1;
         this.recoverField_b = var2;
         this.recoverField_c = var3;
      }

      public InetAddress[] recoverMethod_a_5fb32648() {
         InetAddress[] var1 = null;
         String var2 = RecoverTypeaf2ae5fa.recoverMethod_a_c69c3eec().recoverMethod_a_e96ea081(this.recoverField_b);

         try {
            var1 = InetAddress.getAllByName(this.recoverField_b);
            RecoverTypeaf2ae5fa.recoverMethod_a_c69c3eec().recoverMethod_a_afa300e4(var2, 0);
         } catch (UnknownHostException var13) {
            RecoverTypeaf2ae5fa.recoverMethod_a_c69c3eec().recoverMethod_a_afa300e4(var2, 1);
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("DNSLoader", "dns resolve failed:" + var13);
         }

         if (var1 != null && var1.length > 0) {
            int var3 = 0;
            int var4 = 0;
            ArrayList var5 = new ArrayList();
            StringBuilder var6 = new StringBuilder();

            for (InetAddress var10 : var1) {
               boolean var11 = false;
               if (var3 < 3 && var10 instanceof Inet4Address) {
                  var11 = true;
                  var3++;
               } else if (var4 < 3 && var10 instanceof Inet6Address) {
                  var11 = true;
                  var4++;
               }

               if (var11) {
                  var5.add(var10);
                  var6.append(var10.getHostAddress()).append(",");
               }

               if (var3 == 3 && var4 == 3) {
                  break;
               }
            }

            if (!var5.isEmpty()) {
               var1 = (InetAddress[])var5.toArray(new InetAddress[0]);
               long var14 = System.currentTimeMillis();
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("DNSLoader", "update dns cache url=" + this.recoverField_b + " resolved=" + Arrays.toString(var1));
               this.recoverField_c.recoverField_c.put(this.recoverField_b, new Pair(var1, var14));

               try {
                  var6.deleteCharAt(var6.length() - 1);
                  RecoverType5a690d2.recoverMethod_a_abcb8d40(
                     this.recoverField_a,
                     RecoverType5a690d1.recoverMethod_b_5ef90ef5(this.recoverField_b).recoverMethod_a_4e9722a3(var6.toString()),
                     RecoverType5a690d1.recoverMethod_c_5ef90ef5(this.recoverField_b).recoverMethod_a_4e9722a3(var14)
                  );
               } catch (Throwable var12) {
               }
            } else {
               var1 = null;
            }
         }

         return var1;
      }

      // $VF: synthetic method
      @Override
      public Object call() {
         return this.recoverMethod_a_5fb32648();
      }
   }

   private static final class Member8f60366b {
      private static final RecoverTypeaf2ab8ed recoverField_a = new RecoverTypeaf2ab8ed();
   }
}
