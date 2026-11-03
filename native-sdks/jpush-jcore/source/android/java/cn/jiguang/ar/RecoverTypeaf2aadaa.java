package cn.jiguang.ar;

import android.os.SystemClock;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.aq.RecoverTypeaf2aa9e8;
import cn.jiguang.at.RecoverTypeaf2ab52f;
import cn.jiguang.aw.RecoverTypeaf2ac06e;
import cn.jiguang.aw.RecoverTypeaf2ac06f;
import cn.jiguang.aw.RecoverTypeaf2ac070;
import cn.jiguang.bb.RecoverTypeaf2ae5fa;
import cn.jiguang.bk.RecoverTypeaf2b07d5;
import cn.jiguang.bk.RecoverTypeaf2b07d7;
import cn.jiguang.d.RecoverType5a68d11;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.concurrent.Callable;

public class RecoverTypeaf2aadaa implements Callable {
   private final RecoverTypeaf2aadb3 recoverField_a;
   private final RecoverTypeaf2aadb8 recoverField_b;
   private final RecoverTypeaf2aadae recoverField_c;
   private final RecoverTypeaf2aadb0 recoverField_d;

   public RecoverTypeaf2aadaa(RecoverTypeaf2aadb3 var1, RecoverTypeaf2aadb8 var2, RecoverTypeaf2aadae var3, RecoverTypeaf2aadb0 var4) {
      this.recoverField_b = var2;
      this.recoverField_a = var1;
      this.recoverField_c = var3;
      this.recoverField_d = var4;
   }

   public RecoverTypeaf2ac06e recoverMethod_a_c697b2f8() {
      try {
         if (this.recoverField_c != null) {
            int var1 = this.recoverField_a.recoverMethod_b_9b68();
            return this.recoverMethod_a_8e8c6e60(this.recoverField_c.recoverMethod_a_7ad58690(var1));
         }
      } catch (Throwable var3) {
         String var2 = "run e:" + var3;
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ConnTask", var2);
         RecoverTypeaf2aadab.Member8f35f3a8.recoverMethod_a_3f52113a(this.recoverField_a.recoverField_a, 1408, "连接某个ip 未知异常: " + var2);
      }

      return null;
   }

   public RecoverTypeaf2ac06e recoverMethod_a_8e8c6e60(RecoverTypeaf2aadaf var1) throws java.net.UnknownHostException {
      if (this.recoverField_b.recoverMethod_a_9b79()) {
         return null;
      }

      if (this.recoverField_d == null || this.recoverField_d.recoverField_a) {
         RecoverTypeaf2ab52f var12 = new RecoverTypeaf2ab52f(-991, null);
         this.recoverField_b.recoverMethod_a_5d527811(var12);
         return null;
      }

      if (var1 == null) {
         return null;
      }

      if (!TextUtils.isEmpty(RecoverTypeaf2aa9e8.recoverField_n)) {
         var1.recoverField_a = RecoverTypeaf2aa9e8.recoverField_n;
         var1.recoverField_c = InetAddress.getByName(RecoverTypeaf2aa9e8.recoverField_n);
      }

      if (RecoverTypeaf2aa9e8.recoverField_o > 0) {
         var1.recoverField_b = RecoverTypeaf2aa9e8.recoverField_o;
      }

      RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("ConnTask", "Open connection with ip=" + var1.recoverField_c + ", port:" + var1.recoverField_b);
      long var2 = SystemClock.uptimeMillis();
      RecoverTypeaf2ac06e var4 = null;
      if (RecoverTypeaf2aadab.recoverMethod_d_f0554df7(this.recoverField_a.recoverField_a)) {
         RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("ConnTask", "start connect ssl socket");
         var4 = new RecoverTypeaf2ac06f(8128, 20);
      } else {
         var4 = new RecoverTypeaf2ac070(8128, 20);
      }

      String var5 = RecoverTypeaf2ae5fa.recoverMethod_a_c69c3eec().recoverMethod_a_782597e1(var1.recoverField_a, var1.recoverField_b, var4.recoverField_g);
      int var6 = var4.recoverMethod_a_afa300d7(var1.recoverField_a, var1.recoverField_b);
      if (this.recoverField_b.recoverMethod_a_9b79()) {
         RecoverTypeaf2b07d7.recoverMethod_a_d17885d6(var4);
         RecoverTypeaf2ae5fa.recoverMethod_a_c69c3eec().recoverMethod_d_afa300e4(var5, -991);
         return null;
      } else if (this.recoverField_d.recoverField_a) {
         RecoverTypeaf2ab52f var15 = new RecoverTypeaf2ab52f(-991, null);
         this.recoverField_b.recoverMethod_a_5d527811(var15);
         RecoverTypeaf2b07d7.recoverMethod_a_d17885d6(var4);
         RecoverTypeaf2ae5fa.recoverMethod_a_c69c3eec().recoverMethod_d_afa300e4(var5, -991);
         return null;
      } else {
         RecoverTypeaf2ae5fa.recoverMethod_a_c69c3eec().recoverMethod_d_afa300e4(var5, var6);
         if (var6 != 0) {
            long var14 = SystemClock.uptimeMillis() - var2;
            long var9 = RecoverType5a68d11.recoverMethod_b_f0554de7(this.recoverField_a.recoverField_a);
            this.recoverField_a.recoverMethod_a_5fe03918(2, var1.recoverField_a, var1.recoverField_b, var9, var14, var6);
            String var11 = "Failed(" + var6 + ") to open connection - ip:" + var1.recoverField_c + ", port:" + var1.recoverField_b + ", cost:" + var14;
            RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("ConnTask", var11);
            RecoverTypeaf2aadad.recoverMethod_a_51aad880(this.recoverField_a.recoverField_a, var1, -1, var14);
            RecoverTypeaf2b07d7.recoverMethod_a_d17885d6(var4);
            RecoverTypeaf2aadab.Member8f35f3a8.recoverMethod_a_3f52113a(this.recoverField_a.recoverField_a, 1407, "连接某个ip失败: " + var11);
            return null;
         } else {
            boolean var7 = var1.recoverField_c instanceof Inet4Address || RecoverTypeaf2b07d5.recoverMethod_f_aad3b203(var1.recoverField_a);
            RecoverType5a690d2.recoverMethod_a_abcb8d40(
               this.recoverField_a.recoverField_a, RecoverType5a690d1.recoverMethod_c_cf96baf5(var7).recoverMethod_a_4e9722a3(var1.toString())
            );
            RecoverTypeaf2a9ae9.recoverMethod_g_e925fa09("ConnTask", "Succeed to open connection - ip:" + var1.recoverField_c + ", port:" + var1.recoverField_b);
            this.recoverField_b.recoverMethod_a_5d527811(var4);
            RecoverTypeaf2aadad.recoverMethod_a_51aad880(this.recoverField_a.recoverField_a, var1, 1, 0L);
            return var4;
         }
      }
   }

   // $VF: synthetic method
   @Override
   public Object call() {
      return this.recoverMethod_a_c697b2f8();
   }
}
