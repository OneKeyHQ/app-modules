package cn.jpush.android.f;

import org.json.JSONObject;

final class RecoverType2e4cd5ac implements Runnable {
   private String recoverField_a;
   private long recoverField_b;
   private JSONObject recoverField_c;

   public RecoverType2e4cd5ac(String var1, long var2, JSONObject var4) {
      this.recoverField_a = var1;
      this.recoverField_b = var2;
      this.recoverField_c = var4;
   }

   @Override
   public final void run() {
      RecoverType2e4cd5ab.recoverMethod_a_9abbf639(this.recoverField_a, this.recoverField_b, this.recoverField_c);
   }
}
