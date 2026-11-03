package cn.jiguang.ar;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import cn.jiguang.al.RecoverTypeaf2a972c;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.aq.RecoverTypeaf2aa9e8;
import cn.jiguang.at.RecoverTypeaf2ab52f;
import cn.jiguang.bb.RecoverTypeaf2ae5f9;
import cn.jiguang.bb.RecoverTypeaf2ae5fa;
import cn.jiguang.bk.RecoverTypeaf2b07ce;
import cn.jiguang.d.RecoverType5a68d11;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Set;
import java.util.concurrent.Callable;

public class RecoverTypeaf2aadb7 implements Callable {
   private final RecoverTypeaf2aadb3 recoverField_a;
   private final RecoverTypeaf2aadb8 recoverField_b;
   private final RecoverTypeaf2aadae recoverField_c;
   private Set recoverField_d;
   private RecoverTypeaf2aadaf recoverField_e;

   public RecoverTypeaf2aadb7(RecoverTypeaf2aadb3 var1, RecoverTypeaf2aadb8 var2, RecoverTypeaf2aadae var3) {
      this.recoverField_a = var1;
      this.recoverField_b = var2;
      this.recoverField_c = var3;
   }

   public RecoverTypeaf2aadb7(RecoverTypeaf2aadb3 var1, RecoverTypeaf2aadaf var2, Set var3) {
      this.recoverField_a = var1;
      this.recoverField_b = null;
      this.recoverField_c = null;
      this.recoverField_e = var2;
      this.recoverField_d = var3;
   }

   private Object[] recoverMethod_a_5a89d31d(DatagramSocket var1, InetAddress var2, int var3) {
      Object[] var4 = new Object[]{null, 0};

      byte[] var5;
      try {
         var5 = this.recoverField_a.recoverMethod_a_cd67cb73(this.recoverField_d);
      } catch (RecoverTypeaf2ab52f var16) {
         var4[0] = var16.recoverMethod_a_9b68();
         return var4;
      }

      String var6 = RecoverTypeaf2ae5fa.recoverMethod_a_c69c3eec().recoverMethod_b_85e4583c(var2.getHostAddress(), var3);
      RecoverTypeaf2ae5f9.recoverMethod_a_c69c3ecd().recoverMethod_b_3b99bddb(var2.getHostAddress() + ":" + var3, "sis", var5.length);
      DatagramPacket var7 = new DatagramPacket(var5, var5.length, var2, var3);
      long var9 = SystemClock.uptimeMillis();

      byte[] var8;
      try {
         var8 = RecoverTypeaf2aadab.recoverMethod_a_6607361d(var1, var7);
      } catch (Exception var15) {
         RecoverTypeaf2ae5fa.recoverMethod_a_c69c3eec().recoverMethod_c_afa300e4(var6, 3);
         var4[0] = 3;
         var4[1] = SystemClock.uptimeMillis() - var9;
         return var4;
      }

      RecoverTypeaf2ae5f9.recoverMethod_a_c69c3ecd().recoverMethod_c_3b99bddb(var2.getHostAddress() + ":" + var3, "sis", var8.length);
      var4[1] = SystemClock.uptimeMillis() - var9;

      byte[] var11;
      try {
         var11 = RecoverTypeaf2aadab.recoverMethod_a_4962d961(var8);
      } catch (RecoverTypeaf2ab52f var14) {
         var4[0] = var14.recoverMethod_a_9b68();
         RecoverTypeaf2ae5fa.recoverMethod_a_c69c3eec().recoverMethod_c_afa300e4(var6, var14.recoverMethod_a_9b68());
         return var4;
      }

      String var12 = new String(var11);
      RecoverTypeaf2aadb4 var13 = new RecoverTypeaf2aadb4(var12);
      if (var13.recoverMethod_a_9b79()) {
         var4[0] = 6;
         RecoverTypeaf2ae5fa.recoverMethod_a_c69c3eec().recoverMethod_c_afa300e4(var6, 6);
         return var4;
      } else {
         RecoverTypeaf2ae5fa.recoverMethod_a_c69c3eec().recoverMethod_c_afa300e4(var6, 0);
         recoverMethod_a_89e45fa(this.recoverField_a.recoverField_a, var13);
         var13.recoverField_g = new RecoverTypeaf2aadaf(var2, var3);
         var4[0] = var13;
         return var4;
      }
   }

   private static void recoverMethod_a_89e45fa(Context var0, RecoverTypeaf2aadb4 var1) {
      if (var1 != null && !var1.recoverMethod_a_9b79()) {
         String var2 = RecoverTypeaf2aadaf.recoverMethod_a_13bfc96f(var1.recoverField_a);
         String var3 = RecoverTypeaf2aadaf.recoverMethod_a_13bfc96f(var1.recoverField_b);
         String var4 = RecoverTypeaf2b07ce.recoverMethod_a_e501eb0d(var0);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SisTask", "updateSisInfo ips=" + var2 + " sslIps=" + var3 + " net=" + var4);
         recoverMethod_a_9d23c587(var0, var2, var3);
         RecoverType5a690d2.recoverMethod_a_abcb8d40(
            var0,
            RecoverType5a690d1.recoverMethod_d_cf96baf5(false).recoverMethod_a_4e9722a3(RecoverTypeaf2aadaf.recoverMethod_a_13bfc96f(var1.recoverField_c)),
            RecoverType5a690d1.recoverMethod_d_cf96baf5(true).recoverMethod_a_4e9722a3(RecoverTypeaf2aadaf.recoverMethod_a_13bfc96f(var1.recoverField_d)),
            RecoverType5a690d1.recoverMethod_av_48794abf().recoverMethod_a_4e9722a3(RecoverTypeaf2aadaf.recoverMethod_a_13bfc96f(var1.recoverField_e)),
            RecoverType5a690d1.recoverMethod_ay_48794abf().recoverMethod_a_4e9722a3(var1.recoverField_f != null ? var1.recoverField_f.toString() : null),
            RecoverType5a690d1.recoverMethod_as_48794abf().recoverMethod_a_4e9722a3(var1.recoverField_h),
            RecoverType5a690d1.recoverMethod_at_48794abf().recoverMethod_a_4e9722a3(SystemClock.elapsedRealtime())
         );
         RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_al_48794abf().recoverMethod_a_4e9722a3(var4));
         RecoverTypeaf2a972c.recoverMethod_a_1a54e370(var0, var1.recoverField_i);
      }
   }

   private static void recoverMethod_a_9d23c587(Context var0, String var1, String var2) {
      long var3 = System.currentTimeMillis();
      String var5 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_ar_48794abf());
      String var6 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_aq_48794abf());
      if (!TextUtils.isEmpty(var6)) {
         String[] var7 = var6.split("-sis-");
         if (var7 != null && var7.length > 0) {
            if (!TextUtils.equals(var7[0], var1)) {
               RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_aq_48794abf().recoverMethod_a_4e9722a3(var1 + "-sis-" + var3));
            } else if (var7.length > 1 && RecoverTypeaf2aadaf.recoverMethod_b_aad3b203(var7[1])) {
               RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_aq_48794abf().recoverMethod_a_4e9722a3(var1 + "-sis-" + var3));
            }
         } else {
            RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_aq_48794abf().recoverMethod_a_4e9722a3(var1 + "-sis-" + var3));
         }
      } else {
         RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_aq_48794abf().recoverMethod_a_4e9722a3(var1 + "-sis-" + var3));
      }

      if (!TextUtils.isEmpty(var5)) {
         String[] var8 = var5.split("-sis-");
         if (var8 != null && var8.length > 0) {
            if (!TextUtils.equals(var8[0], var2)) {
               RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_ar_48794abf().recoverMethod_a_4e9722a3(var2 + "-sis-" + var3));
            } else if (var8.length > 1 && RecoverTypeaf2aadaf.recoverMethod_b_aad3b203(var8[1])) {
               RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_ar_48794abf().recoverMethod_a_4e9722a3(var2 + "-sis-" + var3));
            }
         } else {
            RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_ar_48794abf().recoverMethod_a_4e9722a3(var2 + "-sis-" + var3));
         }
      } else {
         RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_ar_48794abf().recoverMethod_a_4e9722a3(var2 + "-sis-" + var3));
      }
   }

   public RecoverTypeaf2aadb4 recoverMethod_a_8e8a29da(RecoverTypeaf2aadaf var1) {
      if (var1 != null && var1.recoverField_c != null) {
         DatagramSocket var2 = null;

         try {
            var2 = new DatagramSocket();
            if (!TextUtils.isEmpty(RecoverTypeaf2aa9e8.recoverField_l)) {
               var1.recoverField_c = InetAddress.getByName(RecoverTypeaf2aa9e8.recoverField_l);
            }

            if (RecoverTypeaf2aa9e8.recoverField_m > 0) {
               var1.recoverField_b = RecoverTypeaf2aa9e8.recoverField_m;
            }

            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SisTask", "send sis:" + var1.recoverField_c + " port:" + var1.recoverField_b);
            Object[] var3 = this.recoverMethod_a_5a89d31d(var2, var1.recoverField_c, var1.recoverField_b);
            if (var3[0] instanceof RecoverTypeaf2aadb4) {
               boolean var18 = var1.recoverField_c instanceof Inet4Address;
               this.recoverField_a.recoverMethod_a_1385ff(var18);
               RecoverType5a690d2.recoverMethod_a_abcb8d40(
                  this.recoverField_a.recoverField_a, RecoverType5a690d1.recoverMethod_b_cf96baf5(var18).recoverMethod_a_4e9722a3(var1.toString())
               );
               if (this.recoverField_b != null) {
                  this.recoverField_b.recoverMethod_a_5d527811(var3[0]);
               }

               return (RecoverTypeaf2aadb4)var3[0];
            }

            int var4 = (Integer)var3[0];
            long var5 = (Long)var3[1];
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("SisTask", "sis failed(" + var4 + "):" + var1.recoverField_c + " port:" + var1.recoverField_b);
            this.recoverField_a
               .recoverMethod_a_5fe03918(
                  1,
                  var1.recoverField_c.getHostAddress(),
                  var1.recoverField_b,
                  RecoverType5a68d11.recoverMethod_b_f0554de7(this.recoverField_a.recoverField_a),
                  var5,
                  var4
               );
         } catch (Throwable var16) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("SisTask", "sis e:" + var16);
         } finally {
            try {
               if (var2 != null) {
                  var2.close();
               }
            } catch (Throwable var15) {
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public RecoverTypeaf2aadb4 recoverMethod_a_c6956e72() {
      try {
         if (this.recoverField_c != null) {
            int var1 = this.recoverField_a.recoverMethod_b_9b68();
            return this.recoverMethod_a_8e8a29da(this.recoverField_c.recoverMethod_a_7ad58690(var1));
         } else {
            return this.recoverMethod_a_8e8a29da(this.recoverField_e);
         }
      } catch (Throwable var2) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("SisTask", "run e:" + var2);
         return null;
      }
   }

   // $VF: synthetic method
   @Override
   public Object call() {
      return this.recoverMethod_a_c6956e72();
   }
}
