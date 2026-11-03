package cn.jiguang.aj;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import cn.jiguang.ak.RecoverTypeaf2a9367;

public class RecoverTypeaf2a8fa1 extends Handler {
   private final Context recoverField_a;

   public RecoverTypeaf2a8fa1(Context var1, Looper var2) {
      super(var2);
      this.recoverField_a = var1;
   }

   @Override
   public void handleMessage(Message var1) {
      try {
         int var2 = var1.what;
         Object var3 = var1.obj;
         RecoverTypeaf2a9367.recoverMethod_a_c6923f1f().recoverMethod_a_f1d0d74c(this.recoverField_a, var2, var3);
      } catch (Throwable var4) {
      }
   }
}
