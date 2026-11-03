package cn.jiguang.push.asus;

import android.content.Context;
import android.content.Intent;

final class RecoverTypebe7e3e91 implements Runnable {
   // $VF: synthetic field
   final Context recoverField_a;
   // $VF: synthetic field
   final Intent recoverField_b;
   // $VF: synthetic field
   final PushMessageReceiver recoverField_c;

   RecoverTypebe7e3e91(PushMessageReceiver var1, Context var2, Intent var3) {
      this.recoverField_c = var1;
      this.recoverField_a = var2;
      this.recoverField_b = var3;
   }

   @Override
   public final void run() {
      this.recoverField_c.recoverMethod_a_c4d2ecb0(this.recoverField_a, this.recoverField_b);
   }
}
