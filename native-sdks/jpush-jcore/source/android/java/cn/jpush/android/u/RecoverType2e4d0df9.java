package cn.jpush.android.u;

import android.content.Context;
import android.graphics.Point;
import android.view.View;
import cn.jpush.android.ad.RecoverType9b4c5af2;
import cn.jpush.android.d.RecoverType2e4cce2a;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.r.RecoverType2e4d02b5;
import cn.jpush.android.s.RecoverType2e4d0677;
import cn.jpush.android.s.RecoverType2e4d0678;
import cn.jpush.android.t.RecoverType2e4d0a37;

public abstract class RecoverType2e4d0df9 {
   protected RecoverType2e4d0df9.Membercf417876 recoverField_a;
   private RecoverType2e4d0678 recoverField_f;
   protected RecoverType2e4cce2a recoverField_b;
   protected RecoverType2e4d0a37 recoverField_c;
   protected Context recoverField_d;
   protected int recoverField_e;

   public static RecoverType2e4d0df9 recoverMethod_a_c9df8259(Context var0, RecoverType2e4d0a37 var1, RecoverType2e4cce2a var2, int var3) {
      switch (var3) {
         case 10:
         case 11:
            return new RecoverType2e4d0df8(var0, var1, var2, var3);
         case 20:
         case 21:
            return new RecoverType2e4d0dfb(var0, var1, var2, var3);
         case 30:
         case 31:
            return new RecoverType2e4d0dfa(var0, var1, var2, var3);
         case 40:
            return new RecoverType2e4d0dfd(var0, var1, var2, var3);
         default:
            return null;
      }
   }

   public RecoverType2e4d0df9(Context var1, RecoverType2e4d0a37 var2, RecoverType2e4cce2a var3, int var4) {
      this.recoverField_c = var2;
      this.recoverField_b = var3;
      this.recoverField_d = var1;
      this.recoverField_e = var4;
   }

   public abstract View recoverMethod_e_d08d2077();

   public abstract boolean recoverMethod_f_9b79();

   public abstract void recoverMethod_c_9b75();

   public abstract void recoverMethod_d_9b75();

   public abstract void recoverMethod_g_9b75();

   public void recoverMethod_h_9b75() {
      try {
         View var1 = this.recoverMethod_e_d08d2077();
         RecoverType2e4d0a37 var2 = this.recoverMethod_k_cd378259();
         if (null == var2 || var1 == null) {
            Logger.recoverMethod_w_e925fa09("BaseInAppWrapper", "startViewAnimation param is null, view:" + var1 + "config:" + var2);
            return;
         }

         this.recoverMethod_a_c381373d(var1);
         if (10 == this.recoverField_e || 11 == this.recoverField_e || 40 == this.recoverField_e) {
            this.recoverMethod_c_c381373d(var1);
            this.recoverMethod_b_c381373d(var1);
         }
      } catch (Throwable var3) {
         Logger.recoverMethod_w_e925fa09("BaseInAppWrapper", "start in-app with animation error. " + var3.getMessage());
      }
   }

   private void recoverMethod_a_c381373d(final View var1) {
      int var2 = RecoverType9b4c5af2.recoverMethod_a_1a54e363(this.recoverField_d, 94);
      Point var3 = new Point(0, -var2);
      Point var4 = new Point(0, 0);
      long var5 = (long)this.recoverField_c.recoverMethod_f_9b65();
      Logger.recoverMethod_d_e925fa09("BaseInAppWrapper", "slideIntoView startPoint: " + var3 + ", endPoint: " + var4 + ", toShowTime: " + var5);
      RecoverType2e4d02b5.recoverMethod_a_a0c706de(var1, var3, var4, var5, new RecoverType2e4d02b5.Membercf172e32() {
         @Override
         public void recoverMethod_a_9b75() {
            Logger.recoverMethod_d_e925fa09("BaseInAppWrapper", "notify inapp show animation end");
            if (null != RecoverType2e4d0df9.this.recoverField_a) {
               RecoverType2e4d0df9.this.recoverField_a.recoverMethod_a_b362c7ff(RecoverType2e4d0df9.this.recoverField_d, var1);
            }
         }
      });
   }

   private void recoverMethod_b_c381373d(final View var1) {
      if (this.recoverField_f == null) {
         this.recoverField_f = new RecoverType2e4d0678();
      }

      long var2 = (long)(this.recoverField_c.recoverMethod_e_9b65() + this.recoverField_c.recoverMethod_f_9b65());
      Logger.recoverMethod_d_e925fa09("BaseInAppWrapper", "configAutoDismiss autoTickTime: " + var2);
      this.recoverField_f
         .recoverMethod_a_957fb98f(
            new RecoveredDismissTask(var1),
            var2,
            1000L
         );
   }

   private void recoverMethod_c_c381373d(View var1) {
      RecoverType2e4d0677 var2 = new RecoverType2e4d0677(
         this,
         null,
         this.recoverField_b.recoverField_aH,
         new RecoverType2e4d0677.Membercf254974() {
            @Override
            public boolean recoverMethod_a_5d527815(Object var1) {
               return true;
            }

            @Override
            public void recoverMethod_a_171eb1d9(View var1, Object var2x) {
               if (null != RecoverType2e4d0df9.this.recoverField_a) {
                  RecoverType2e4d0df9.this.recoverField_a
                     .recoverMethod_a_e2da561b(RecoverType2e4d0df9.this.recoverField_d, var1, RecoverType2e4d0df9.this.recoverField_b);
               }
            }
         }
      );
      var1.setOnTouchListener(var2);
   }

   protected void recoverMethod_i_9b75() {
      if (null != this.recoverField_f) {
         this.recoverField_f.recoverMethod_a_9b75();
      }
   }

   public RecoverType2e4cce2a recoverMethod_j_cd303cc6() {
      return this.recoverField_b;
   }

   public RecoverType2e4d0a37 recoverMethod_k_cd378259() {
      return this.recoverField_c;
   }

   public void recoverMethod_a_151159ee(RecoverType2e4d0df9.Membercf417876 var1) {
      this.recoverField_a = var1;
   }

   public int recoverMethod_a_134621(int var1) {
      return RecoverType9b4c5af2.recoverMethod_b_1a54e363(this.recoverField_d, var1);
   }

   public int recoverMethod_b_134621(int var1) {
      return RecoverType9b4c5af2.recoverMethod_c_1a54e363(this.recoverField_d, var1);
   }

   public static boolean recoverMethod_c_134632(int var0) {
      return 10 == var0 || 11 == var0;
   }

   // Preserve the parent-only capture in the original completion object.
   class RecoveredDismissTask implements RecoverType2e4d0678.Membercf254d35 {
      final View recoverField_a;

      RecoveredDismissTask(View view) { this.recoverField_a = view; }
               @Override
               public void recoverMethod_a_9b75() {
                  try {
                     Logger.recoverMethod_d_e925fa09("BaseInAppWrapper", "dismiss timer reach, dismiss notify inapp message");
                     int var1x = RecoverType9b4c5af2.recoverMethod_a_1a54e363(RecoverType2e4d0df9.this.recoverField_d, 94);
                     Point var2x = new Point(0, -var1x);
                     long var3 = (long)RecoverType2e4d0df9.this.recoverField_c.recoverMethod_g_9b65();
                     RecoverType2e4d02b5.recoverMethod_a_30959f21(
                        recoverField_a,
                        var2x,
                        var3,
                        new RecoverType2e4d02b5.Membercf172e32() {
                           @Override
                           public void recoverMethod_a_9b75() {
                              if (null != RecoverType2e4d0df9.this.recoverField_a) {
                                 RecoverType2e4d0df9.this.recoverField_a
                                    .recoverMethod_a_e2da561b(RecoverType2e4d0df9.this.recoverField_d, RecoveredDismissTask.this.recoverField_a, RecoverType2e4d0df9.this.recoverField_b);
                              }
                           }
                        }
                     );
                  } catch (Throwable var5) {
                     Logger.recoverMethod_w_e925fa09("BaseInAppWrapper", "in-app slide to dismiss error." + var5.getMessage());
                  }
               }
            }

   public interface Membercf417876 {
      void recoverMethod_a_e2da561b(Context var1, View var2, Object var3);

      void recoverMethod_a_b362c7ff(Context var1, View var2);
   }
}
