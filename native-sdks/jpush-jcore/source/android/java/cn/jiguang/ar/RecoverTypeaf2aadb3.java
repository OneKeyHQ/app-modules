package cn.jiguang.ar;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.al.RecoverTypeaf2a972c;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.at.RecoverTypeaf2ab52f;
import cn.jiguang.au.RecoverTypeaf2ab8ee;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.bk.RecoverTypeaf2b07c2;
import cn.jiguang.bk.RecoverTypeaf2b07ce;
import cn.jiguang.bk.RecoverTypeaf2b07d6;
import cn.jiguang.bk.RecoverTypeaf2b07d7;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import cn.jiguang.m.RecoverType5a6aed9;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.json.JSONArray;

public class RecoverTypeaf2aadb3 {
   private int recoverField_c = 0;
   Context recoverField_a;
   public final LinkedList recoverField_b;
   private byte[] recoverField_d;
   private int recoverField_e;

   public boolean recoverMethod_a_9b79() {
      return false;
   }

   public int recoverMethod_b_9b68() {
      this.recoverField_c = 3;
      int var1 = RecoverTypeaf2ab8ee.recoverMethod_a_c696ca78().recoverMethod_b_134621(this.recoverField_c);
      if (RecoverTypeaf2b07c2.recoverMethod_d_f0554df7(this.recoverField_a)) {
         var1 = 3;
      }

      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SisConnContext", "ipvsupport=" + this.recoverField_c + ", prefer=" + var1);
      return var1;
   }

   public void recoverMethod_a_1385ff(boolean var1) {
      this.recoverField_c |= var1 ? 1 : 2;
      RecoverTypeaf2ab8ee.recoverMethod_a_c696ca78().recoverMethod_a_1a54e370(this.recoverField_a, this.recoverField_c);
   }

   public RecoverTypeaf2aadb4 recoverMethod_a_487fe8ec(long var1) {
      FutureTask var3 = new FutureTask(new RecoverTypeaf2aadb1(this));
      this.recoverField_c = 0;
      RecoverTypeaf2aa628.recoverMethod_e_a4fc3973(var3);

      try {
         if (var1 < 10L) {
            var1 = 10L;
         }

         return (RecoverTypeaf2aadb4)var3.get(var1, TimeUnit.MILLISECONDS);
      } catch (ExecutionException var5) {
      } catch (InterruptedException var6) {
      } catch (TimeoutException var7) {
      }

      return null;
   }

   public RecoverTypeaf2aadb3(Context var1) {
      this.recoverField_a = var1;
      String var2 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var1, RecoverType5a690d1.recoverMethod_am_48794abf());
      this.recoverField_b = RecoverTypeaf2aadb5.recoverMethod_a_3e0e9f8f(var2);
   }

   public byte[] recoverMethod_a_cd67cb73(Set var1) throws RecoverTypeaf2ab52f {
      int var2 = RecoverTypeaf2b07ce.recoverMethod_b_f0554de6(this.recoverField_a);
      if (this.recoverField_d == null || var2 != this.recoverField_e) {
         this.recoverField_e = var2;

         try {
            this.recoverField_d = RecoverTypeaf2aadab.recoverMethod_a_3b9947f4(
               "UG", this.recoverMethod_a_7ad58769(var2).recoverMethod_a_6b240298(var1).toString()
            );
         } catch (Exception var4) {
            throw new RecoverTypeaf2ab52f(2, "Failed to package data - " + var4);
         }
      }

      return this.recoverField_d;
   }

   private RecoverTypeaf2aadb6 recoverMethod_a_7ad58769(int var1) {
      String var2 = RecoverType5a68d10.recoverMethod_e_e501eb0d(this.recoverField_a);
      long var3 = RecoverType5a68d10.recoverMethod_f_f0554de7(this.recoverField_a);
      String var5 = RecoverTypeaf2b07d6.recoverMethod_a_5e799b7f(this.recoverField_a, false);
      double var6 = 200.0;
      double var8 = 200.0;
      long var10 = System.currentTimeMillis();
      Object var12 = RecoverType5a6aed9.recoverMethod_a_b00c10b9(this.recoverField_a, "get_loc_info", null);
      if (var12 instanceof Bundle) {
         try {
            Bundle var13 = (Bundle)var12;
            var6 = var13.getDouble("lat");
            var8 = var13.getDouble("lot");
            var10 = var13.getLong("time");
         } catch (Throwable var14) {
         }
      }

      int var15 = 0;
      if (RecoverTypeaf2aadab.recoverMethod_d_f0554df7(this.recoverField_a)) {
         var15 = RecoverTypeaf2a972c.recoverMethod_b_9b68();
      }

      return new RecoverTypeaf2aadb6(var1, var2, RecoverType5a681cd.recoverField_b, var3, var5, var6, var8, var10).recoverMethod_a_7ad58769(var15);
   }

   public void recoverMethod_a_5fe03918(int var1, String var2, int var3, long var4, long var6, int var8) {
      if (RecoverTypeaf2aadaf.recoverMethod_a_afa300e8(var2, var3)) {
         RecoverTypeaf2aadb5 var9 = new RecoverTypeaf2aadb5();
         var9.recoverField_a = RecoverType5a68d10.recoverMethod_e_e501eb0d(this.recoverField_a);
         var9.recoverField_b = var1;
         var9.recoverField_c = new RecoverTypeaf2aadaf(var2, var3);
         var9.recoverField_e = var4;
         var9.recoverField_f = var6;
         var9.recoverField_k = var8;
         var9.recoverField_g = RecoverTypeaf2b07ce.recoverMethod_b_f0554de6(this.recoverField_a);
         var9.recoverField_d = RecoverType5a68d10.recoverMethod_f_f0554de7(this.recoverField_a);
         var9.recoverField_h = 200.0;
         var9.recoverField_i = 200.0;
         var9.recoverField_j = System.currentTimeMillis();
         Object var10 = RecoverType5a6aed9.recoverMethod_a_b00c10b9(this.recoverField_a, "get_loc_info", null);
         if (var10 instanceof Bundle) {
            try {
               Bundle var11 = (Bundle)var10;
               var9.recoverField_h = var11.getDouble("lat");
               var9.recoverField_i = var11.getDouble("lot");
               var9.recoverField_j = var11.getLong("time");
            } catch (Throwable var12) {
            }
         }

         this.recoverMethod_a_976be117(var9);
      }
   }

   private synchronized void recoverMethod_a_976be117(RecoverTypeaf2aadb5 var1) {
      this.recoverField_b.add(var1);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SisConnContext", "addSisReportInfo:" + var1.recoverMethod_a_a7dbfe3().toString());

      while (this.recoverField_b.size() > 30) {
         this.recoverField_b.removeFirst();
      }

      JSONArray var2 = new JSONArray();

      for (Object recoveryElement_167 : this.recoverField_b) {
         RecoverTypeaf2aadb5 var4 = (RecoverTypeaf2aadb5)recoveryElement_167;
         var2.put(var4.recoverMethod_a_a7dbfe3());
      }

      RecoverType5a690d2.recoverMethod_a_abcb8d40(this.recoverField_a, RecoverType5a690d1.recoverMethod_am_48794abf().recoverMethod_a_4e9722a3(var2.toString()));
   }

   public void recoverMethod_a_976926dd(final RecoverTypeaf2aadaf var1) {
      if ((Boolean)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_a, RecoverType5a690d1.recoverMethod_as_48794abf())) {
         long var2 = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_a, RecoverType5a690d1.recoverMethod_au_48794abf());
         if (RecoverTypeaf2b07d7.recoverMethod_b_2566ab9(var2, 3600000L)) {
            RecoverTypeaf2aa628.recoverMethod_c_a4fc3973(
               new RecoverTypeaf2b0401("SisConnContext#asyncSisReportIfNeed") {
                  @Override
                  public void recoverMethod_a_9b75() {
                     DatagramSocket var1x = null;

                     try {
                        var1x = new DatagramSocket();
                        String var2x = (String)RecoverType5a690d2.recoverMethod_a_413ed21(
                           RecoverTypeaf2aadb3.this.recoverField_a, RecoverType5a690d1.recoverMethod_am_48794abf()
                        );
                        if (!TextUtils.isEmpty(var2x)) {
                           byte[] var3 = RecoverTypeaf2aadab.recoverMethod_a_3b9947f4("DG", var2x);
                           RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
                              "SisConnContext", "sis report data(" + var3.length + ") at " + var1.recoverField_c + ":" + var1.recoverField_b
                           );
                           boolean var4 = RecoverTypeaf2aadb3.this.recoverMethod_a_3551b259(var1.recoverField_c, var1.recoverField_b, var1x, var3);
                           if (var4) {
                              RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SisConnContext", "report succeed : " + var2x);
                              RecoverType5a690d2.recoverMethod_a_abcb8d40(
                                 RecoverTypeaf2aadb3.this.recoverField_a,
                                 RecoverType5a690d1.recoverMethod_au_48794abf().recoverMethod_a_4e9722a3(SystemClock.elapsedRealtime())
                              );
                              RecoverType5a690d2.recoverMethod_a_abcb8d40(
                                 RecoverTypeaf2aadb3.this.recoverField_a, RecoverType5a690d1.recoverMethod_am_48794abf().recoverMethod_a_4e9722a3(null)
                              );
                           } else {
                              RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("SisConnContext", "report failed" + var2x);
                           }

                           return;
                        }

                        RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SisConnContext", "reportInfo is Empty, quit report");
                     } catch (Throwable var14) {
                        RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("SisConnContext", "sisReport failed, error:" + var14);
                        return;
                     } finally {
                        if (var1x != null) {
                           try {
                              var1x.close();
                           } catch (Throwable var13) {
                              RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("SisConnContext", "sisReport ,close udpsocket error:" + var13.getMessage());
                           }
                        }
                     }
                  }
               }
            );
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SisConnContext", "sis report: not yet");
         }
      }
   }

   private boolean recoverMethod_a_3551b259(InetAddress var1, int var2, DatagramSocket var3, byte[] var4) {
      try {
         DatagramPacket var5 = new DatagramPacket(var4, var4.length, var1, var2);
         byte[] var6 = RecoverTypeaf2aadab.recoverMethod_a_6607361d(var3, var5);
         byte[] var7 = RecoverTypeaf2aadab.recoverMethod_a_4962d961(var6);
         return RecoverTypeaf2b07d7.recoverMethod_c_25e06f9(var7) == 0;
      } catch (Throwable var8) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("SisConnContext", "report failed : " + var8);
         return false;
      }
   }
}
