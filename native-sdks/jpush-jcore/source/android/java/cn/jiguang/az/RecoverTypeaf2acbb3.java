package cn.jiguang.az;

import cn.jiguang.ba.RecoverTypeaf2ae239;

public final class RecoverTypeaf2acbb3 extends RuntimeException {
   private final RecoverTypeaf2ae239 recoverField_a;
   private final Throwable recoverField_b;
   private final Thread recoverField_c;
   private final boolean recoverField_d;

   public RecoverTypeaf2acbb3(RecoverTypeaf2ae239 var1, Throwable var2, Thread var3, boolean var4) {
      this.recoverField_a = var1;
      this.recoverField_b = var2;
      this.recoverField_c = var3;
      this.recoverField_d = var4;
   }

   public RecoverTypeaf2acbb3(RecoverTypeaf2ae239 var1, Throwable var2, Thread var3) {
      this(var1, var2, var3, false);
   }

   public RecoverTypeaf2ae239 recoverMethod_a_c69bca8d() {
      return this.recoverField_a;
   }

   public Throwable recoverMethod_b_1b7ba3da() {
      return this.recoverField_b;
   }

   public Thread recoverMethod_c_943922f2() {
      return this.recoverField_c;
   }

   public boolean recoverMethod_d_9b79() {
      return this.recoverField_d;
   }
}
