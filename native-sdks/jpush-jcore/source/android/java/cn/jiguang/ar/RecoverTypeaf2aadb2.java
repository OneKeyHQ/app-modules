package cn.jiguang.ar;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.aq.RecoverTypeaf2aa9e8;
import cn.jiguang.as.RecoverTypeaf2ab175;
import cn.jiguang.at.RecoverTypeaf2ab52f;
import cn.jiguang.au.RecoverTypeaf2ab8ed;
import cn.jiguang.aw.RecoverTypeaf2ac06e;
import cn.jiguang.bk.RecoverTypeaf2b07c2;
import cn.jiguang.bk.RecoverTypeaf2b07ce;
import cn.jiguang.bk.RecoverTypeaf2b07d7;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class RecoverTypeaf2aadb2 {
   private LinkedHashSet recoverField_a = new LinkedHashSet();
   private RecoverTypeaf2aadb3 recoverField_b;
   private RecoverTypeaf2aadb0 recoverField_c;
   private RecoverTypeaf2aadb8 recoverField_d = new RecoverTypeaf2aadb8(5, 2000, null, "ssn");
   private RecoverTypeaf2aadae recoverField_e = new RecoverTypeaf2aadae();
   private RecoverTypeaf2aadb7 recoverField_f;

   public RecoverTypeaf2aadb2(RecoverTypeaf2aadb3 var1) {
      this.recoverField_b = var1;
   }

   public RecoverTypeaf2ac06e recoverMethod_a_762e44bf(RecoverTypeaf2aadb0 var1) throws RecoverTypeaf2ab52f, java.lang.Exception {
      this.recoverField_c = var1;
      RecoverTypeaf2a9ae9.recoverMethod_e_e925fa09("SisConn", "start sisAndConnect...");
      this.recoverField_e = new RecoverTypeaf2aadae();
      long var2 = SystemClock.uptimeMillis() + 12000L;
      boolean var4 = RecoverTypeaf2aadab.recoverMethod_d_f0554df7(this.recoverField_b.recoverField_a);
      String var5 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(
         this.recoverField_b.recoverField_a, var4 ? RecoverType5a690d1.recoverMethod_ar_48794abf() : RecoverType5a690d1.recoverMethod_aq_48794abf()
      );
      LinkedHashSet var6 = RecoverTypeaf2aadaf.recoverMethod_c_6066ccd5(var5);
      boolean var7 = var6 != null && !var6.isEmpty();
      boolean var8 = this.recoverMethod_a_9b79();
      if (var7 && var8) {
         this.recoverMethod_a_a6639751(var6);
      }

      RecoverTypeaf2aadb4 var9 = this.recoverField_b.recoverMethod_a_487fe8ec(12000L);
      LinkedHashSet var10 = var9 != null ? (var4 ? var9.recoverField_b : var9.recoverField_a) : null;
      if (var10 != null && !var10.isEmpty()) {
         this.recoverField_b.recoverMethod_a_976926dd(var9.recoverField_g);
         this.recoverMethod_a_260fc993(var10, var2);
      } else if (var7 && !var8) {
         this.recoverMethod_a_a6639751(var6);
      }

      this.recoverMethod_b_9b75();
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SisConn", "wait final result...");
      Object var11 = this.recoverField_d.recoverMethod_a_3c710923(60000L);
      this.recoverField_d.recoverMethod_a_1385ff(false);
      if (var11 instanceof RecoverTypeaf2ac06e) {
         RecoverTypeaf2a9ae9.recoverMethod_e_e925fa09(
            "SisConn",
            "connect succeed connResult hostName=" + ((RecoverTypeaf2ac06e)var11).recoverField_h + " port=" + ((RecoverTypeaf2ac06e)var11).recoverField_i
         );
         return (RecoverTypeaf2ac06e)var11;
      } else if (var11 instanceof Exception) {
         RecoverTypeaf2a9ae9.recoverMethod_e_e925fa09("SisConn", "all sis and connect failed, e:" + var11);
         RecoverTypeaf2aadab.Member8f35f3a8.recoverMethod_a_3f52113a(this.recoverField_b.recoverField_a, 1402, "Exception: " + var11);
         throw (Exception)var11;
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_k_e925fa09("SisConn", "all sis and connect failed:" + var11);
         RecoverTypeaf2aadab.Member8f35f3a8.recoverMethod_a_3f52113a(this.recoverField_b.recoverField_a, 1403, "ERR_UNKNOWN: " + var11);
         throw new RecoverTypeaf2ab52f(1, null);
      }
   }

   private boolean recoverMethod_a_9b79() {
      String var1 = RecoverTypeaf2b07ce.recoverMethod_a_e501eb0d(this.recoverField_b.recoverField_a);
      boolean var2 = recoverMethod_a_5ffc0101(this.recoverField_b.recoverField_a, var1);
      long var3 = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_b.recoverField_a, RecoverType5a690d1.recoverMethod_at_48794abf());
      return !var2 && !RecoverTypeaf2b07d7.recoverMethod_b_2566ab9(var3, 180000L);
   }

   private static boolean recoverMethod_a_5ffc0101(Context var0, String var1) {
      if (TextUtils.isEmpty(var1)) {
         return true;
      }

      String var2 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_al_48794abf());
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SisConn", "newType=" + var1 + " last=" + var2);
      return !var1.equalsIgnoreCase(var2);
   }

   private boolean recoverMethod_a_976926e1(RecoverTypeaf2aadaf var1) {
      if (this.recoverField_c.recoverField_a) {
         RecoverTypeaf2ab52f var8 = new RecoverTypeaf2ab52f(-991, null);
         this.recoverField_d.recoverMethod_a_5d527811(var8);
         return true;
      }

      if (this.recoverField_d.recoverMethod_a_9b79()) {
         return true;
      }

      if (var1 != null && var1.recoverMethod_a_9b79() && !this.recoverField_a.contains(var1)) {
         InetAddress[] var2 = RecoverTypeaf2ab8ed.recoverMethod_a_c696ca59()
            .recoverMethod_b_d5a42320(this.recoverField_b.recoverField_a, var1.recoverField_a, 3000L, this.recoverField_b.recoverMethod_a_9b79());
         if (var2 != null && var2.length != 0) {
            LinkedList var3 = RecoverTypeaf2b07d7.recoverMethod_a_f17ad488(Arrays.asList(var2));

            for (Object recoveryElement_118 : RecoverTypeaf2b07d7.recoverMethod_a_bdfbcc03(this.recoverField_b.recoverField_a, var3)) {
               InetAddress var6 = (InetAddress)recoveryElement_118;
               if (this.recoverField_c.recoverField_a) {
                  RecoverTypeaf2ab52f var9 = new RecoverTypeaf2ab52f(-991, null);
                  this.recoverField_d.recoverMethod_a_5d527811(var9);
                  return true;
               }

               if (this.recoverField_d.recoverMethod_a_9b79()) {
                  return true;
               }

               RecoverTypeaf2aadaf var7 = new RecoverTypeaf2aadaf(var6, var1.recoverField_b);
               if (!this.recoverField_a.contains(var7) && this.recoverField_e.recoverMethod_a_976926e1(var7)) {
                  this.recoverField_a.add(var7);
                  this.recoverField_d
                     .recoverMethod_a_f64309fc(new RecoverTypeaf2aadaa(this.recoverField_b, this.recoverField_d, this.recoverField_e, this.recoverField_c));
                  break;
               }
            }

            this.recoverField_a.add(var1);
            return false;
         } else {
            RecoverTypeaf2aadab.Member8f35f3a8.recoverMethod_a_3f52113a(this.recoverField_b.recoverField_a, 1404, "DNS解析失败: " + var1.recoverField_a);
            return false;
         }
      } else {
         return false;
      }
   }

   private void recoverMethod_a_a6639751(LinkedHashSet var1) {
      var1.removeAll(this.recoverField_a);
      if (!var1.isEmpty()) {
         var1 = RecoverTypeaf2aadad.recoverMethod_a_56f73f67(this.recoverField_b.recoverField_a, var1, 0L);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SisConn", "connect: last good sis info" + var1);

         for (Object recoveryElement_155 : var1) {
            RecoverTypeaf2aadaf var3 = (RecoverTypeaf2aadaf)recoveryElement_155;
            if (this.recoverMethod_a_976926e1(var3)) {
               break;
            }
         }
      } else {
         RecoverTypeaf2aadab.Member8f35f3a8.recoverMethod_a_3f52113a(this.recoverField_b.recoverField_a, 1405, "没有可用的 IP 地址进行连接-旧");
      }
   }

   private void recoverMethod_a_260fc993(LinkedHashSet var1, long var2) {
      this.recoverField_e.recoverMethod_a_9b75();
      this.recoverField_d.recoverMethod_b_9b75();
      var1.removeAll(this.recoverField_a);
      var1 = RecoverTypeaf2aadad.recoverMethod_a_56f73f67(this.recoverField_b.recoverField_a, var1, System.currentTimeMillis());
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SisConn", "connect: new sis info=" + var1);
      if (var1.isEmpty()) {
         RecoverTypeaf2aadab.Member8f35f3a8.recoverMethod_a_3f52113a(this.recoverField_b.recoverField_a, 1406, "没有可用的 IP 地址进行连接-新");
      } else {
         for (Object recoveryElement_174 : var1) {
            RecoverTypeaf2aadaf var5 = (RecoverTypeaf2aadaf)recoveryElement_174;
            if (SystemClock.uptimeMillis() >= var2 || this.recoverMethod_a_976926e1(var5)) {
               return;
            }
         }

         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SisConn", "after connect use new sis, wait connect Result");
         long var19 = var2 - SystemClock.uptimeMillis();
         if (var19 > 0L) {
            Object var6 = this.recoverField_d.recoverMethod_a_3c710923(var19);
            if (var6 == null) {
               if (this.recoverField_f == null) {
                  HashSet var7 = new HashSet();

                  for (Object recoveryElement_188 : var1) {
                     RecoverTypeaf2aadaf var9 = (RecoverTypeaf2aadaf)recoveryElement_188;
                     if (this.recoverField_a.contains(var9)) {
                        var7.add(var9.recoverField_a);
                     }
                  }

                  int var20 = this.recoverField_b.recoverMethod_b_9b68();
                  boolean var21 = var20 == 1 || var20 == 0;
                  RecoverTypeaf2aadaf var10 = RecoverTypeaf2aadaf.recoverMethod_a_800e3061(
                     (String)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_b.recoverField_a, RecoverType5a690d1.recoverMethod_b_cf96baf5(var21))
                  );
                  if (var10 != null) {
                     InetAddress[] var11 = RecoverTypeaf2ab8ed.recoverMethod_a_c696ca59()
                        .recoverMethod_b_d5a42320(this.recoverField_b.recoverField_a, var10.recoverField_a, 3000L, false);
                     if (var11 != null && var11.length > 0) {
                        var10.recoverField_c = var11[0];
                        long var12 = var2 - SystemClock.uptimeMillis();
                        if (var12 < 10L) {
                           return;
                        }

                        this.recoverField_f = new RecoverTypeaf2aadb7(this.recoverField_b, var10, var7);
                        RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SisConn", "second sis, addr=" + var10 + ", failIps=" + var7);
                        FutureTask var14 = new FutureTask(this.recoverField_f);

                        try {
                           RecoverTypeaf2aa628.recoverMethod_e_a4fc3973(var14);
                           RecoverTypeaf2aadb4 var15 = (RecoverTypeaf2aadb4)var14.get(var12, TimeUnit.MILLISECONDS);
                           var1 = var15 != null
                              ? (RecoverTypeaf2aadab.recoverMethod_d_f0554df7(this.recoverField_b.recoverField_a) ? var15.recoverField_b : var15.recoverField_a)
                              : null;
                           if (var1 != null && !var1.isEmpty()) {
                              this.recoverField_b.recoverMethod_a_976926dd(var15.recoverField_g);
                              this.recoverMethod_a_260fc993(var1, var2);
                           }
                        } catch (Throwable var16) {
                           RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("SisConn", "second sis e:" + var16);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void recoverMethod_b_9b75() {
      boolean var1 = RecoverTypeaf2b07c2.recoverMethod_d_f0554df7(this.recoverField_b.recoverField_a);
      RecoverTypeaf2aadaf var2 = RecoverTypeaf2aadaf.recoverMethod_a_800e3061(
         (String)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_b.recoverField_a, RecoverType5a690d1.recoverMethod_c_cf96baf5(!var1))
      );
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SisConn", "connect: use last good" + (var1 ? "v6" : "v4") + " address=" + var2);
      if (!this.recoverMethod_a_976926e1(var2)) {
         var2 = RecoverTypeaf2aadaf.recoverMethod_a_800e3061(
            (String)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_b.recoverField_a, RecoverType5a690d1.recoverMethod_c_cf96baf5(var1))
         );
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SisConn", "connect: use last good" + (var1 ? "v4" : "v6") + " address=" + var2);
         if (!this.recoverMethod_a_976926e1(var2)) {
            LinkedHashSet var3 = this.recoverMethod_c_d5ec344b();
            if (var3 != null) {
               var3.removeAll(this.recoverField_a);
            }

            LinkedList var4 = RecoverTypeaf2b07d7.recoverMethod_a_f17ad488(var3);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SisConn", "connect: use defaultConn=" + var4);

            for (Object recoveryElement_254 : var4) {
               RecoverTypeaf2aadaf var6 = (RecoverTypeaf2aadaf)recoveryElement_254;
               if (this.recoverMethod_a_976926e1(var6)) {
                  return;
               }
            }

            var3 = RecoverTypeaf2ab175.recoverMethod_a_c695e2d1()
               .recoverMethod_a_d8320d7b(RecoverTypeaf2aa9e8.recoverMethod_b_e501eb0d(this.recoverField_b.recoverField_a), 10000L);
            var4.clear();
            if (var3 != null) {
               var3.removeAll(this.recoverField_a);
               var4 = RecoverTypeaf2b07d7.recoverMethod_a_f17ad488(var3);
            }

            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SisConn", "connect: use srv address" + var4);

            for (Object recoveryElement_270 : var4) {
               RecoverTypeaf2aadaf var10 = (RecoverTypeaf2aadaf)recoveryElement_270;
               if (this.recoverMethod_a_976926e1(var10)) {
                  return;
               }
            }
         }
      }
   }

   private LinkedHashSet recoverMethod_c_d5ec344b() {
      try {
         String var1 = RecoverTypeaf2aa9e8.recoverMethod_a_e501eb0d(this.recoverField_b.recoverField_a);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SisConn", "load Default Conn, from host=" + var1);
         if (TextUtils.isEmpty(var1)) {
            return null;
         }

         InetAddress[] var2 = RecoverTypeaf2ab8ed.recoverMethod_a_c696ca59()
            .recoverMethod_b_d5a42320(this.recoverField_b.recoverField_a, var1, 3000L, this.recoverField_b.recoverMethod_a_9b79());
         if (var2 != null && var2.length != 0) {
            LinkedList var3 = RecoverTypeaf2b07d7.recoverMethod_a_f17ad488(Arrays.asList(var2));
            if (var3 != null && !var3.isEmpty()) {
               String var4 = ((InetAddress)var3.get(0)).getHostAddress();
               LinkedHashSet var5 = new LinkedHashSet();
               var5.add(new RecoverTypeaf2aadaf(var4, 7000));
               var5.add(new RecoverTypeaf2aadaf(var4, 7002));
               var5.add(new RecoverTypeaf2aadaf(var4, 7003));
               var5.add(new RecoverTypeaf2aadaf(var4, 7004));
               var5.add(new RecoverTypeaf2aadaf(var4, 7005));
               var5.add(new RecoverTypeaf2aadaf(var4, 7006));
               var5.add(new RecoverTypeaf2aadaf(var4, 7007));
               var5.add(new RecoverTypeaf2aadaf(var4, 7008));
               var5.add(new RecoverTypeaf2aadaf(var4, 7009));
               return var5;
            } else {
               return null;
            }
         } else {
            return null;
         }
      } catch (Throwable var6) {
         return null;
      }
   }
}
