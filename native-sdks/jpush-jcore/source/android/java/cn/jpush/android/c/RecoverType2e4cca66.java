package cn.jpush.android.c;

import android.os.Bundle;

public class RecoverType2e4cca66 {
   private String recoverField_a;
   private int recoverField_b;
   private Bundle recoverField_c;

   public RecoverType2e4cca66(String var1, int var2) {
      this.recoverField_a = var1;
      this.recoverField_b = var2;
   }

   public int recoverMethod_a_9b68() {
      return this.recoverField_b;
   }

   public void recoverMethod_a_79e5e33f(Bundle var1) {
      this.recoverField_c = var1;
   }

   @Override
   public String toString() {
      return "CmdMessage{cmd='" + this.recoverField_a + '\'' + ", errorCode=" + this.recoverField_b + ", extra=" + this.recoverField_c + '}';
   }
}
