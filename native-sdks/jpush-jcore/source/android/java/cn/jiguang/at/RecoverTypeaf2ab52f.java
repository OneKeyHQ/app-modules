package cn.jiguang.at;

public class RecoverTypeaf2ab52f extends Exception {
   public final int recoverField_a;

   public RecoverTypeaf2ab52f(int var1, String var2) {
      super(var2);
      this.recoverField_a = var1;
   }

   public int recoverMethod_a_9b68() {
      return this.recoverField_a;
   }

   @Override
   public String toString() {
      return "JException(" + this.recoverField_a + "):" + this.getLocalizedMessage();
   }
}
