package cn.jiguang.ak;

import android.content.Context;
import android.os.Message;
import cn.jiguang.ah.RecoverTypeaf2a881f;
import cn.jiguang.ai.RecoverTypeaf2a8be0;
import cn.jiguang.ai.RecoverTypeaf2a8be2;
import cn.jiguang.aj.RecoverTypeaf2a8fa1;
import cn.jiguang.aj.RecoverTypeaf2a8fa2;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.List;

public class RecoverTypeaf2a9367 {
   private RecoverTypeaf2a8be0 recoverField_a;
   private RecoverTypeaf2a8fa1 recoverField_b;
   private RecoverTypeaf2a8fa2 recoverField_c;
   private long recoverField_d = System.currentTimeMillis();

   private RecoverTypeaf2a9367() {
   }

   public static RecoverTypeaf2a9367 recoverMethod_a_c6923f1f() {
      return RecoverTypeaf2a9367.Member8ed35a64.recoverField_a;
   }

   private synchronized RecoverTypeaf2a8fa1 recoverMethod_a_5eddc0a3(Context var1) {
      if (this.recoverField_c == null || this.recoverField_c.getState() == State.TERMINATED) {
         this.recoverField_c = new RecoverTypeaf2a8fa2("jg_wk_thread");
      }

      if (this.recoverField_c.getState() == State.NEW) {
         this.recoverField_c.start();
         this.recoverField_b = null;
      }

      if (this.recoverField_b == null) {
         this.recoverField_b = new RecoverTypeaf2a8fa1(var1, this.recoverField_c.getLooper());
      }

      return this.recoverField_b;
   }

   public void recoverMethod_a_1a552341(Context var1, boolean var2) {
      if (var2) {
         this.recoverField_d = System.currentTimeMillis();
      }
   }

   public boolean recoverMethod_a_134632(int var1) {
      return RecoverTypeaf2aa628.recoverField_b.get() && var1 > 0 && System.currentTimeMillis() - this.recoverField_d < var1;
   }

   public void recoverMethod_a_fa4794e4(Context var1, List var2) {
      RecoverTypeaf2a8be0 var3 = RecoverTypeaf2a9363.recoverMethod_b_5edd4c44(var1);
      List var4 = RecoverTypeaf2a881f.recoverMethod_a_3407328(var1, var3, var2);
      this.recoverMethod_a_979dd93f(var1, var3, var4);
   }

   public void recoverMethod_a_979dd93f(Context var1, RecoverTypeaf2a8be0 var2, List var3) {
      this.recoverField_a = var2;
      ArrayList var4 = new ArrayList();
      if (null != var3 && !var3.isEmpty()) {
         for (Object recoveryElement_66 : var3) {
            RecoverTypeaf2a8be2 var6 = (RecoverTypeaf2a8be2)recoveryElement_66;
            if (this.recoverMethod_a_134632(var6.recoverField_k * 1000)) {
               var4.add(var6);
               this.recoverMethod_a_f93e910c(var1, var6);
            }
         }

         if (!var4.isEmpty()) {
            var3.removeAll(var4);
         }
      }

      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
         "JWakeTaskHelper", "start w task, wakeTarget size: " + (null != var3 ? var3.size() : 0) + ", dWakeTarget size: " + var4.size()
      );
      List var7 = RecoverTypeaf2a881f.recoverMethod_a_c98d58e3(var1, var3);
      RecoverTypeaf2a9364.recoverMethod_a_979dd93f(var1, this.recoverField_a, var7);
   }

   public void recoverMethod_a_f1d0d74c(Context var1, int var2, Object var3) {
      switch (var2) {
         case 1000:
            if (var3 instanceof RecoverTypeaf2a8be2) {
               this.recoverMethod_b_f93e910c(var1, (RecoverTypeaf2a8be2)var3);
            }
         case 1001:
      }
   }

   private void recoverMethod_a_f93e910c(Context var1, RecoverTypeaf2a8be2 var2) {
      long var3 = Math.max(0L, System.currentTimeMillis() - this.recoverField_d);
      long var5 = Math.max(0L, var2.recoverField_k * 1000 - var3);
      Message var7 = Message.obtain();
      var7.what = 1000;
      var7.obj = var2;
      this.recoverMethod_a_5eddc0a3(var1).sendMessageDelayed(var7, var5);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakeTaskHelper", "add delay w task, delayTime: " + var5 + ", tPkg: " + var2.recoverField_a);
   }

   private void recoverMethod_b_f93e910c(Context var1, final RecoverTypeaf2a8be2 var2) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakeTaskHelper", "the delay w task time up, start to process it, tPkg: " + var2.recoverField_a);
      List var3 = RecoverTypeaf2a881f.recoverMethod_a_c98d58e3(var1, new ArrayList() {
         {
            this.add(var2);
         }
      });
      if (null != var3 && !var3.isEmpty()) {
         RecoverTypeaf2a9364.recoverMethod_a_979dd93f(var1, this.recoverField_a, var3);
      }
   }

   private static final class Member8ed35a64 {
      private static final RecoverTypeaf2a9367 recoverField_a = new RecoverTypeaf2a9367();
   }
}
