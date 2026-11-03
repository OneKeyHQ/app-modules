package cn.jiguang.i;

import android.content.Context;
import android.os.Bundle;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.bh.RecoverTypeaf2afc7e;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.d.RecoverType5a68d11;
import cn.jiguang.g.RecoverType5a69853;
import org.json.JSONObject;

public abstract class RecoverType5a69fd5 {
   public void recoverMethod_c_f0554df3(Context var1) {
      try {
         String var2 = this.recoverMethod_a_e501eb0d(var1);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCommon", "executeAction: [" + var2 + "] from heartBeat");
         boolean var3 = this.recoverMethod_g_5ffc0101(var1, var2);
         boolean var4 = this.recoverMethod_a_5ffc0101(var1, var2);
         boolean var5 = this.recoverMethod_b_5ffc0101(var1, var2);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCommon", var2 + " - isActionEnable:" + var3 + ", isBusinessEnable:" + var4 + ", reportEnable:" + var5);
         Integer var6 = (Integer)RecoverType5a69853.recoverField_a.get(var2);
         if (!var4) {
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_d0657661(var6, 2026);
         }

         if (!var5) {
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_d0657661(var6, 2027);
         }

         if (var3 && (var4 || var5)) {
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_d0657661(var6, 2028);
            RecoverTypeaf2aa628.recoverMethod_b_a4fc3973(new RecoverType5a69fd5.Member367e0353(var1, var2));
         }
      } catch (Throwable var7) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCommon", "executeAction failed, error:" + var7);
      }
   }

   public void recoverMethod_d_f0554df3(Context var1) {
      String var2 = this.recoverMethod_a_e501eb0d(var1);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCommon", "executeCommandAction: [" + var2 + "] from cmd");
      boolean var3 = this.recoverMethod_g_5ffc0101(var1, var2);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCommon", var2 + " - isActionEnable:" + var3);
      Integer var4 = (Integer)RecoverType5a69853.recoverField_a.get(var2);
      if (var3) {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_d0657661(var4, 2028);
         RecoverTypeaf2aa628.recoverMethod_b_a4fc3973(new RecoverType5a69fd5.Member367e0353(var1, var2));
      }
   }

   public void recoverMethod_a_69c77401(Context var1, Bundle var2) {
      String var3 = this.recoverMethod_a_e501eb0d(var1);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCommon", "executeBundleAction: [" + var3 + "] from bundle");
      boolean var4 = this.recoverMethod_c_9b79();
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCommon", var3 + " isActionUserEnable:" + var4);
      Integer var5 = (Integer)RecoverType5a69853.recoverField_a.get(var3);
      if (!var4) {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_d0657661(var5, 2019);
      }

      if (var4) {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_d0657661(var5, 2028);
         RecoverTypeaf2aa628.recoverMethod_b_a4fc3973(new RecoverType5a69fd5.Member367e0352(var1, var3, var2));
      }
   }

   public void recoverMethod_a_1a54e370(Context var1, int var2) {
      String var3 = this.recoverMethod_a_e501eb0d(var1);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCommon", "executeActionSingle: [" + var3 + "] from heartBeat, will delay " + var2 + "ms execute");
      boolean var4 = this.recoverMethod_g_5ffc0101(var1, var3);
      boolean var5 = this.recoverMethod_a_5ffc0101(var1, var3);
      boolean var6 = this.recoverMethod_b_5ffc0101(var1, var3);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCommon", var3 + " isActionEnable:" + var4 + ", isBusinessEnable:" + var5 + ", reportEnable:" + var6);
      Integer var7 = (Integer)RecoverType5a69853.recoverField_a.get(var3);
      if (!var5) {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_d0657661(var7, 2026);
      }

      if (!var6) {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_d0657661(var7, 2027);
      }

      if (var4 && (var5 || var6)) {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_d0657661(var7, 2028);
         if (var2 > 0) {
            RecoverTypeaf2aa628.recoverMethod_a_fa8b67f0(new RecoverType5a69fd5.Member367e0353(var1, var3), var2);
         } else {
            RecoverTypeaf2aa628.recoverMethod_b_a4fc3973(new RecoverType5a69fd5.Member367e0353(var1, var3));
         }
      }
   }

   public void recoverMethod_a_3c4029ab(Context var1, JSONObject var2) {
      String var3 = this.recoverMethod_a_e501eb0d(var1);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCommon", "executeCommandActionSingle: [" + var3 + "] from cmd");
      boolean var4 = this.recoverMethod_c_9b79();
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCommon", var3 + " isActionUserEnable:" + var4);
      Integer var5 = (Integer)RecoverType5a69853.recoverField_a.get(var3);
      if (var4) {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_d0657661(var5, 2025);
         RecoverTypeaf2aa628.recoverMethod_b_a4fc3973(new RecoverType5a69fd5.Member367e0354(var1, var3, var2));
      } else {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_d0657661(var5, 2019);
      }
   }

   private void recoverMethod_e_5ffc00fd(Context var1, String var2) {
      boolean var3 = this.recoverMethod_a_5ffc0101(var1, var2);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCommon", var2 + " isBusinessEnable:" + var3);
      if (var3) {
         RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_a_aad3b1ff(var2);
         this.recoverMethod_c_5ffc00fd(var1, var2);
      }

      boolean var4 = this.recoverMethod_b_5ffc0101(var1, var2);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCommon", var2 + " isReportEnable:" + var4);
      if (var4) {
         this.recoverMethod_d_5ffc00fd(var1, var2);
      }
   }

   private void recoverMethod_a_f310a0e1(Context var1, String var2, JSONObject var3) {
      this.recoverMethod_a_27ad051f(var2, var3);
      int var4 = var3.optInt("cmd");
      if (var4 != 45) {
         boolean var5 = this.recoverMethod_d_9b79();
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCommon", var2 + " isActionCommandEnable:" + var5);
         if (var5) {
            this.recoverMethod_c_5ffc00fd(var1, var2);
            this.recoverMethod_d_5ffc00fd(var1, var2);
         }
      }
   }

   private void recoverMethod_a_d19840b7(Context var1, String var2, Bundle var3) {
      this.recoverMethod_a_5dc9c75(var2, var3);
      boolean var4 = this.recoverMethod_e_9b79();
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCommon", var2 + " isActionBundleEnable:" + var4);
      if (var4) {
         this.recoverMethod_c_5ffc00fd(var1, var2);
         this.recoverMethod_d_5ffc00fd(var1, var2);
      }
   }

   protected abstract String recoverMethod_a_e501eb0d(Context var1);

   protected boolean recoverMethod_c_9b79() {
      return true;
   }

   protected void recoverMethod_a_27ad051f(String var1, JSONObject var2) {
   }

   protected void recoverMethod_a_5dc9c75(String var1, Bundle var2) {
      if (var2 != null) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCommon", var1 + " parseJson:" + var2.toString());
      }
   }

   protected boolean recoverMethod_d_9b79() {
      return true;
   }

   protected boolean recoverMethod_e_9b79() {
      return true;
   }

   protected boolean recoverMethod_e_f0554df7(Context var1) {
      return RecoverType5a68d10.recoverMethod_f_f0554de7(var1) > 0L;
   }

   protected boolean recoverMethod_f_5ffc0101(Context var1, String var2) {
      long var3 = RecoverType5a68d11.recoverMethod_b_f0554de7(var1) * 1000L;
      long var5 = RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_c_aad3b1f3(var2);
      long var7 = RecoverType5a68d10.recoverMethod_g_f0554de7(var1);
      if (Math.abs(var3 - var7) < var5) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCommon", "isProtectionTime is false");
         return false;
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCommon", "isProtectionTime is true");
         return true;
      }
   }

   private boolean recoverMethod_g_5ffc0101(Context var1, String var2) {
      Integer var3 = (Integer)RecoverType5a69853.recoverField_a.get(var2);
      boolean var4 = this.recoverMethod_c_9b79();
      boolean var5 = this.recoverMethod_d_9b79();
      boolean var6 = this.recoverMethod_e_f0554df7(var1);
      boolean var7 = this.recoverMethod_f_5ffc0101(var1, var2);
      boolean var8 = var4 && var5 && var6 && var7;
      if (!var4) {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_d0657661(var3, 2019);
      }

      if (!var5) {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_d0657661(var3, 2020);
      }

      if (!var6) {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_d0657661(var3, 2021);
      }

      if (!var7) {
      }

      if (var8) {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_d0657661(var3, 2023);
      } else {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_d0657661(var3, 2055);
      }

      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
         "JCommon",
         var2
            + " isActionEnable:"
            + var8
            + ",actionUserEnable:"
            + var4
            + ",actionCommandEnable:"
            + var5
            + ",actionUidEnable:"
            + var6
            + ",actionProtectionEnable:"
            + var7
      );
      return var8;
   }

   protected boolean recoverMethod_a_5ffc0101(Context var1, String var2) {
      return RecoverType5a69fd6.recoverMethod_a_5ffc0101(var1, var2);
   }

   protected boolean recoverMethod_b_5ffc0101(Context var1, String var2) {
      return RecoverType5a69fd6.recoverMethod_b_5ffc0101(var1, var2);
   }

   protected void recoverMethod_c_5ffc00fd(Context var1, String var2) {
      RecoverType5a69fd6.recoverMethod_d_5ffc00fd(var1, var2);
   }

   protected void recoverMethod_d_5ffc00fd(Context var1, String var2) {
      RecoverType5a69fd6.recoverMethod_f_5ffc00fd(var1, var2);
   }

   public Object recoverMethod_f_f23f0d9f(Context var1) {
      return null;
   }

   public class Member367e0352 extends RecoverTypeaf2b0401 {
      private Context recoverField_b;
      private String recoverField_c;
      private Bundle recoverField_d;

      Member367e0352(Context var2, String var3, Bundle var4) {
         this.recoverField_b = var2;
         this.recoverField_c = var3;
         this.recoverField_d = var4;
         this.recoverField_h = var3 + "#BundleAction";
      }

      @Override
      public void recoverMethod_a_9b75() {
         try {
            RecoverType5a69fd5.this.recoverMethod_a_d19840b7(this.recoverField_b, this.recoverField_c, this.recoverField_d);
         } catch (Throwable var2) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCommon", "BundleAction failed:" + var2.getMessage());
         }
      }
   }

   public class Member367e0353 extends RecoverTypeaf2b0401 {
      private Context recoverField_b;
      private String recoverField_c;

      Member367e0353(Context var2, String var3) {
         this.recoverField_b = var2;
         this.recoverField_c = var3;
         this.recoverField_h = var3 + "#CommonAction";
      }

      @Override
      public void recoverMethod_a_9b75() {
         try {
            RecoverType5a69fd5.this.recoverMethod_e_5ffc00fd(this.recoverField_b, this.recoverField_c);
         } catch (Throwable var2) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCommon", "tag=" + this.recoverField_c + " dealAction failed:" + var2.getMessage());
         }
      }
   }

   public class Member367e0354 extends RecoverTypeaf2b0401 {
      private Context recoverField_b;
      private String recoverField_c;
      private JSONObject recoverField_d;

      Member367e0354(Context var2, String var3, JSONObject var4) {
         this.recoverField_b = var2;
         this.recoverField_c = var3;
         this.recoverField_d = var4;
         this.recoverField_h = var3 + "#JsonAction";
      }

      @Override
      public void recoverMethod_a_9b75() {
         try {
            RecoverType5a69fd5.this.recoverMethod_a_f310a0e1(this.recoverField_b, this.recoverField_c, this.recoverField_d);
         } catch (Throwable var2) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCommon", "JsonAction-deal failed:" + var2.getMessage());
         }
      }
   }
}
