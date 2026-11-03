package cn.jiguang.ar;

import android.content.Context;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.aq.RecoverTypeaf2aa9e8;
import cn.jiguang.as.RecoverTypeaf2ab175;
import cn.jiguang.au.RecoverTypeaf2ab8ed;
import cn.jiguang.bk.RecoverTypeaf2b07c2;
import cn.jiguang.bk.RecoverTypeaf2b07d7;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.concurrent.Callable;

public class RecoverTypeaf2aadb1 implements Callable {
   private LinkedHashSet recoverField_a = new LinkedHashSet();
   private RecoverTypeaf2aadb3 recoverField_b;

   public RecoverTypeaf2aadb1(RecoverTypeaf2aadb3 var1) {
      this.recoverField_b = var1;
   }

   private boolean recoverMethod_a_74ffb70b(RecoverTypeaf2aadaf var1, RecoverTypeaf2aadb8 var2, RecoverTypeaf2aadae var3) {
      if (var2.recoverMethod_a_9b79()) {
         return true;
      }

      if (var1 != null && var1.recoverMethod_a_9b79() && !this.recoverField_a.contains(var1)) {
         InetAddress[] var4 = RecoverTypeaf2ab8ed.recoverMethod_a_c696ca59()
            .recoverMethod_b_d5a42320(this.recoverField_b.recoverField_a, var1.recoverField_a, 3000L, this.recoverField_b.recoverMethod_a_9b79());
         if (var4 != null && var4.length != 0) {
            LinkedList var5 = RecoverTypeaf2b07d7.recoverMethod_a_f17ad488(Arrays.asList(var4));

            for (Object recoveryElement_37 : RecoverTypeaf2b07d7.recoverMethod_a_bdfbcc03(this.recoverField_b.recoverField_a, var5)) {
               InetAddress var8 = (InetAddress)recoveryElement_37;
               if (var2.recoverMethod_a_9b79()) {
                  return true;
               }

               RecoverTypeaf2aadaf var9 = new RecoverTypeaf2aadaf(var8, var1.recoverField_b);
               if (!this.recoverField_a.contains(var9) && var3.recoverMethod_a_976926e1(var9)) {
                  this.recoverField_a.add(var9);
                  var2.recoverMethod_a_f64309fc(new RecoverTypeaf2aadb7(this.recoverField_b, var2, var3));
                  break;
               }
            }

            this.recoverField_a.add(var1);
            return false;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private void recoverMethod_a_3f6c059f(RecoverTypeaf2aadb8 var1, RecoverTypeaf2aadae var2) {
      LinkedHashSet var3 = RecoverTypeaf2aadaf.recoverMethod_a_7ec0a6ed(RecoverTypeaf2aa9e8.recoverMethod_a_d5e96a91());
      var3.removeAll(this.recoverField_a);
      boolean var4 = RecoverTypeaf2b07c2.recoverMethod_d_f0554df7(this.recoverField_b.recoverField_a);
      LinkedList var5 = RecoverTypeaf2b07d7.recoverMethod_a_f17ad488(var3);

      for (Object recoveryElement_66 : var4 ? RecoverTypeaf2b07d7.recoverMethod_a_3038db21(var5) : var5) {
         RecoverTypeaf2aadaf var8 = (RecoverTypeaf2aadaf)recoveryElement_66;
         if (this.recoverMethod_a_74ffb70b(var8, var1, var2)) {
            return;
         }
      }

      RecoverTypeaf2aadaf var9 = RecoverTypeaf2aadaf.recoverMethod_a_800e3061(
         (String)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_b.recoverField_a, RecoverType5a690d1.recoverMethod_b_cf96baf5(!var4))
      );
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("Sis", "main sis: last good sis " + (var4 ? "v6" : "v4") + " address=" + var9);
      if (!this.recoverMethod_a_74ffb70b(var9, var1, var2)) {
         var9 = RecoverTypeaf2aadaf.recoverMethod_a_800e3061(
            (String)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_b.recoverField_a, RecoverType5a690d1.recoverMethod_b_cf96baf5(var4))
         );
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("Sis", "main sis: last good sis " + (var4 ? "v4" : "v6") + " address=" + var9);
         this.recoverMethod_a_74ffb70b(var9, var1, var2);
      }
   }

   private void recoverMethod_b_3f6c059f(RecoverTypeaf2aadb8 var1, RecoverTypeaf2aadae var2) {
      LinkedHashSet var3 = this.recoverMethod_a_af90ccc9(this.recoverField_b.recoverField_a);
      var3.removeAll(this.recoverField_a);
      LinkedList var4 = RecoverTypeaf2b07d7.recoverMethod_a_f17ad488(var3);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("Sis", "main sis: default sis" + var4);

      for (Object recoveryElement_91 : var4) {
         RecoverTypeaf2aadaf var6 = (RecoverTypeaf2aadaf)recoveryElement_91;
         if (this.recoverMethod_a_74ffb70b(var6, var1, var2)) {
            return;
         }
      }

      var3 = RecoverTypeaf2ab175.recoverMethod_a_c695e2d1().recoverMethod_a_d8320d7b(RecoverTypeaf2aa9e8.recoverMethod_c_73cf11cb(), 10000L);
      var4.clear();
      if (var3 != null) {
         var3.removeAll(this.recoverField_a);
         var4 = RecoverTypeaf2b07d7.recoverMethod_a_f17ad488(var3);
      }

      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("Sis", "main sis: sis srv" + var4);

      for (Object recoveryElement_106 : var4) {
         RecoverTypeaf2aadaf var9 = (RecoverTypeaf2aadaf)recoveryElement_106;
         if (this.recoverMethod_a_74ffb70b(var9, var1, var2)) {
            return;
         }
      }
   }

   public RecoverTypeaf2aadb4 recoverMethod_a_c6956e72() {
      RecoverTypeaf2aadae var1 = new RecoverTypeaf2aadae();
      RecoverTypeaf2aadb8 var2 = new RecoverTypeaf2aadb8(5, 2000, null, "ss");
      this.recoverMethod_a_3f6c059f(var2, var1);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("Sis", "main sis: after host and last good, wait Result");
      Object var3 = var2.recoverMethod_a_3c710923(60000L);
      if (var3 instanceof RecoverTypeaf2aadb4) {
         return (RecoverTypeaf2aadb4)var3;
      }

      this.recoverMethod_b_3f6c059f(var2, var1);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("Sis", "main sis: after default and srv, wait Result");
      var3 = var2.recoverMethod_a_3c710923(60000L);
      var2.recoverMethod_a_1385ff(false);
      return var3 instanceof RecoverTypeaf2aadb4 ? (RecoverTypeaf2aadb4)var3 : null;
   }

   private LinkedHashSet recoverMethod_a_af90ccc9(Context var1) {
      LinkedHashSet var2 = RecoverTypeaf2aadaf.recoverMethod_c_6066ccd5(
         (String)RecoverType5a690d2.recoverMethod_a_413ed21(var1, RecoverType5a690d1.recoverMethod_av_48794abf())
      );
      if (var2 == null || var2.isEmpty()) {
         var2 = RecoverTypeaf2aadaf.recoverMethod_a_7ec0a6ed(RecoverTypeaf2aa9e8.recoverMethod_b_d5e96a91());
      }

      return var2;
   }

   // $VF: synthetic method
   @Override
   public Object call() {
      return this.recoverMethod_a_c6956e72();
   }
}
