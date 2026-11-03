package cn.jiguang.at;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import cn.jiguang.al.RecoverTypeaf2a972a;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ax.RecoverTypeaf2ac42f;
import cn.jiguang.ax.RecoverTypeaf2ac430;
import cn.jiguang.bk.RecoverTypeaf2b07c4;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import cn.jiguang.m.RecoverType5a6aed9;

public class RecoverTypeaf2ab530 {
   private Context recoverField_a;
   private RecoverTypeaf2ac42f recoverField_b = new RecoverTypeaf2ac42f() {
      @Override
      public void recoverMethod_a_c050048(Message var1) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
            "PeriodWorker", "time is up, next period=" + RecoverTypeaf2ab531.recoverMethod_a_c6965695().recoverMethod_g_9b68() * 1000
         );
         RecoverTypeaf2ab530.this.recoverMethod_c_f0554df3(RecoverTypeaf2ab530.this.recoverField_a);
      }
   };
   private long recoverField_c;

   private RecoverTypeaf2ab530() {
      this.recoverField_a = RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d();
   }

   public static RecoverTypeaf2ab530 recoverMethod_a_c6965676() {
      return RecoverTypeaf2ab530.Member8f522ded.recoverField_a;
   }

   public void recoverMethod_a_f0554df3(Context var1) {
      this.recoverField_a = RecoverTypeaf2b07c4.recoverMethod_a_6263c3eb(var1);
      RecoverTypeaf2ac430.recoverMethod_a_c6982776()
         .recoverMethod_a_1a5239c(8000, RecoverTypeaf2ab531.recoverMethod_a_c6965695().recoverMethod_f_9b68() * 1000, this.recoverField_b);
   }

   public void recoverMethod_b_9b75() {
      this.recoverField_c = SystemClock.elapsedRealtime();
      RecoverTypeaf2ab52b.recoverMethod_a_f0554df3(this.recoverField_a);
      RecoverTypeaf2ac430.recoverMethod_a_c6982776()
         .recoverMethod_a_1a5239c(8000, RecoverTypeaf2ab531.recoverMethod_a_c6965695().recoverMethod_f_9b68() * 1000, this.recoverField_b);
   }

   private void recoverMethod_b_f0554df3(Context var1) {
      this.recoverField_c = SystemClock.elapsedRealtime();
      if ((Boolean)RecoverType5a690d2.recoverMethod_a_413ed21(var1, RecoverType5a690d1.recoverMethod_af_48794abf())) {
         RecoverTypeaf2ab52b.recoverMethod_a_f0554df3(var1);
      } else {
         RecoverTypeaf2ab52b.recoverMethod_b_f0554df3(var1);
      }
   }

   public void recoverMethod_a_1a552341(Context var1, boolean var2) {
      RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("PeriodWorker", "PeriodWorker resume");
      if (this.recoverField_c > 0L
         && SystemClock.elapsedRealtime() > this.recoverField_c + (RecoverTypeaf2ab531.recoverMethod_a_c6965695().recoverMethod_f_9b68() + 5) * 1000) {
         RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("PeriodWorker", "schedule time is expired, execute now");
         this.recoverMethod_a_f0554df3(var1);
         this.recoverMethod_c_f0554df3(var1);
      } else if (var2) {
         this.recoverMethod_a_f0554df3(var1);
         this.recoverMethod_b_f0554df3(var1);
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("PeriodWorker", "need not change period task");
      }
   }

   private void recoverMethod_c_f0554df3(Context var1) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("PeriodWorker", "periodTask...");
      if (RecoverTypeaf2a972a.recoverMethod_b_9b79()) {
         RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("PeriodWorker", "[key-step]Your sdk is disabled already, no receiver task can be handled.");
      } else {
         this.recoverMethod_b_f0554df3(var1);
         Bundle var2 = new Bundle();
         var2.putBoolean("force", false);
         var2.putLong("delay_time", 0L);
         this.recoverMethod_a_1a552341(var1, false);
         RecoverTypeaf2ab531.recoverMethod_a_c6965695().recoverMethod_a_79e5e33f(var2);
         RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_a_4abe089d(var1, 19, 0, "periodTask");
         RecoverType5a6aed9.recoverMethod_a_b00c10b9(var1, "periodtask", null);
      }
   }

   private static final class Member8f522ded {
      private static final RecoverTypeaf2ab530 recoverField_a = new RecoverTypeaf2ab530();
   }
}
