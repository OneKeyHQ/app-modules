package cn.jiguang.ah;

import android.content.Context;
import cn.jiguang.ai.RecoverTypeaf2a8be0;
import cn.jiguang.ak.RecoverTypeaf2a9363;
import cn.jiguang.ak.RecoverTypeaf2a9364;
import cn.jiguang.ak.RecoverTypeaf2a9368;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ao.RecoverTypeaf2aa26b;
import cn.jiguang.i.RecoverType5a69fd5;
import cn.jiguang.i.RecoverType5a69fd6;
import org.json.JSONArray;

public class RecoverTypeaf2a8822 extends RecoverType5a69fd5 {
   private Context recoverField_a;
   private RecoverTypeaf2a8be0 recoverField_b;

   private RecoverTypeaf2a8822() {
   }

   public static RecoverTypeaf2a8822 recoverMethod_a_c690e1c4() {
      return RecoverTypeaf2a8822.Member8ea90c5f.recoverField_a;
   }

   @Override
   protected String recoverMethod_a_e501eb0d(Context var1) {
      this.recoverField_a = var1;
      RecoverTypeaf2a9368.recoverField_a = true;
      return "JWakeReport";
   }

   @Override
   protected boolean recoverMethod_a_5ffc0101(Context var1, String var2) {
      return true;
   }

   @Override
   protected void recoverMethod_c_5ffc00fd(Context var1, String var2) {
      this.recoverField_b = RecoverTypeaf2a9363.recoverMethod_b_5edd4c44(var1);
      super.recoverMethod_c_5ffc00fd(var1, var2);
   }

   @Override
   protected boolean recoverMethod_b_5ffc0101(Context var1, String var2) {
      return RecoverType5a69fd6.recoverMethod_c_5ffc0101(var1, var2);
   }

   @Override
   protected void recoverMethod_d_5ffc00fd(Context var1, String var2) {
      if (this.recoverField_b.recoverField_w) {
         JSONArray var3 = RecoverTypeaf2a9364.recoverMethod_b_5d5a71dd(var1);
         if (var3 != null && var3.length() != 0) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakeReport", "report wakeData:" + var3);
            RecoverTypeaf2aa26b.recoverMethod_a_127ac70f(var1, var3);
            RecoverTypeaf2a9364.recoverMethod_c_f0554df3(var1);
            super.recoverMethod_d_5ffc00fd(var1, var2);
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakeReport", "no report wakeData");
         }
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakeReport", "server set do not report wake data.");
      }

      if (this.recoverField_b.recoverField_x) {
         JSONArray var4 = RecoverTypeaf2a9368.recoverMethod_a_5d5a71dd(var1);
         if (var4 != null && var4.length() != 0) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakeReport", "report wakedData:" + var4);
            RecoverTypeaf2aa26b.recoverMethod_a_127ac70f(var1, var4);
            RecoverTypeaf2a9368.recoverMethod_b_f0554df3(var1);
            super.recoverMethod_d_5ffc00fd(var1, var2);
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakeReport", "no report wakedData");
         }
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakeReport", "server set do not report waked data.");
      }
   }

   private static final class Member8ea90c5f {
      private static final RecoverTypeaf2a8822 recoverField_a = new RecoverTypeaf2a8822();
   }
}
