package cn.jiguang.ba;

public final class RecoverTypeaf2ae237 extends RuntimeException {
   private final Thread recoverField_a;

   public RecoverTypeaf2ae237(String var1, Thread var2) {
      super(var1);
      this.recoverField_a = var2;
      this.setStackTrace(this.recoverField_a.getStackTrace());
   }

   public Thread recoverMethod_a_943922f2() {
      return this.recoverField_a;
   }
}
