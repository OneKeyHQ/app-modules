package cn.jiguang.h;

import android.content.Context;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bk.RecoverTypeaf2b07c9;
import cn.jiguang.bk.RecoverTypeaf2b07d4;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.i.RecoverType5a69fd5;
import cn.jiguang.i.RecoverType5a69fd7;
import java.util.HashMap;

public class RecoverType5a69c14 extends RecoverType5a69fd5 {
   private Context recoverField_a;
   private RecoverType5a69c14.Member366febd1 recoverField_b;
   private HashMap recoverField_c = new HashMap();
   private HashMap recoverField_d = new HashMap();

   private RecoverType5a69c14() {
   }

   public static RecoverType5a69c14 recoverMethod_a_487aa7dc() {
      return RecoverType5a69c14.Member366febd2.recoverField_a;
   }

   public void recoverMethod_a_a65619b3(RecoverType5a69c14.Member366febd1 var1) {
      this.recoverField_b = var1;
   }

   public RecoverType5a69c14.Member366febd1 recoverMethod_b_146fd67f() {
      return this.recoverField_b;
   }

   @Override
   protected String recoverMethod_a_e501eb0d(Context var1) {
      this.recoverField_a = var1;
      return "JAppProbe";
   }

   @Override
   protected boolean recoverMethod_a_5ffc0101(Context var1, String var2) {
      long var3 = RecoverType5a68d10.recoverMethod_g_f0554de7(var1);
      if (System.currentTimeMillis() < var3 + 604800000L) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JAppProbe", "can't probe because r time");
         return false;
      } else {
         return super.recoverMethod_a_5ffc0101(var1, var2);
      }
   }

   @Override
   protected boolean recoverMethod_b_5ffc0101(Context var1, String var2) {
      return true;
   }

   @Override
   protected void recoverMethod_c_5ffc00fd(Context var1, String var2) {
   }

   @Override
   protected void recoverMethod_d_5ffc00fd(Context var1, String var2) {
   }

   public void recoverMethod_e_5ffc00fd(Context var1, String var2) {
      if (!TextUtils.isEmpty(var2)) {
         try {
            String var3 = RecoverTypeaf2b07d4.recoverMethod_a_ed76890(
               var2.getBytes(), RecoverType5a69fd7.Member367e0ad4.recoverField_h, RecoverType5a69fd7.Member367e0ad4.recoverField_i
            );
            RecoverTypeaf2b07c9.recoverMethod_a_e925fa0d("prb.catch", var3);
         } catch (Throwable var4) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JAppProbe", "writeProbesToCache failed, " + var4.getMessage());
         }
      }
   }

   public String recoverMethod_b_e501eb0d(Context var1) {
      String var2 = RecoverTypeaf2b07c9.recoverMethod_e_e96ea081("prb.catch");
      if (!TextUtils.isEmpty(var2)) {
         try {
            byte[] var3 = RecoverTypeaf2b07d4.recoverMethod_a_78a45faa(
               var2, RecoverType5a69fd7.Member367e0ad4.recoverField_h, RecoverType5a69fd7.Member367e0ad4.recoverField_i
            );
            return new String(var3, "UTF-8");
         } catch (Throwable var4) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JAppProbe", "readProbesFromCache failed, " + var4.getMessage());
         }
      }

      return null;
   }

   public static class Member366febd1 {
      private String recoverField_a;
      private String recoverField_b;
      private boolean recoverField_c;

      public RecoverType5a69c14.Member366febd1 recoverMethod_a_8a0f6535(String var1) {
         this.recoverField_b = var1;
         return this;
      }

      public RecoverType5a69c14.Member366febd1 recoverMethod_a_49f21135(boolean var1) {
         this.recoverField_c = var1;
         return this;
      }

      public String recoverMethod_a_73cf11cb() {
         return this.recoverField_a;
      }

      public RecoverType5a69c14.Member366febd1 recoverMethod_b_8a0f6535(String var1) {
         this.recoverField_a = var1;
         return this;
      }
   }

   private static final class Member366febd2 {
      private static final RecoverType5a69c14 recoverField_a = new RecoverType5a69c14();
   }
}
