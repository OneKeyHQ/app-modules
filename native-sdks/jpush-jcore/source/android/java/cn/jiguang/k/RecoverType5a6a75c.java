package cn.jiguang.k;

import android.content.Context;
import android.os.Build.VERSION;
import cn.jiguang.ab.RecoverTypeaf2a719a;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.i.RecoverType5a69fd5;

public class RecoverType5a6a75c extends RecoverType5a69fd5 {
   private Context recoverField_a;
   private String recoverField_b = "";
   private String recoverField_c = "";
   private String recoverField_d = "";
   private RecoverTypeaf2a719a recoverField_e = null;

   private RecoverType5a6a75c() {
   }

   public static RecoverType5a6a75c recoverMethod_a_487c0594() {
      return RecoverType5a6a75c.Member369a4519.recoverField_a;
   }

   @Override
   protected String recoverMethod_a_e501eb0d(Context var1) {
      this.recoverField_a = var1;
      return "JDeviceMac";
   }

   @Override
   protected boolean recoverMethod_a_5ffc0101(Context var1, String var2) {
      if (VERSION.SDK_INT < 29) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDeviceMac", "it need not business, because os version less than 29, current is " + VERSION.SDK_INT);
         return false;
      } else {
         return super.recoverMethod_a_5ffc0101(var1, var2);
      }
   }

   @Override
   protected void recoverMethod_c_5ffc00fd(Context var1, String var2) {
   }

   @Override
   protected boolean recoverMethod_b_5ffc0101(Context var1, String var2) {
      return true;
   }

   @Override
   protected void recoverMethod_d_5ffc00fd(Context var1, String var2) {
   }

   private static final class Member369a4519 {
      private static final RecoverType5a6a75c recoverField_a = new RecoverType5a6a75c();
   }
}
