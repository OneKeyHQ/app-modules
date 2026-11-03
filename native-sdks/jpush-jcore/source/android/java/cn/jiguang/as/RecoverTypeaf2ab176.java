package cn.jiguang.as;

public class RecoverTypeaf2ab176 extends RecoverTypeaf2ab172 {
   private int recoverField_e;
   private int recoverField_f;
   private int recoverField_g;
   private RecoverTypeaf2ab170 recoverField_h;

   RecoverTypeaf2ab176() {
   }

   @Override
   void recoverMethod_a_991bb9a1(RecoverTypeaf2ab16b var1) throws java.io.IOException {
      this.recoverField_e = var1.recoverMethod_g_9b68();
      this.recoverField_f = var1.recoverMethod_g_9b68();
      this.recoverField_g = var1.recoverMethod_g_9b68();
      this.recoverField_h = new RecoverTypeaf2ab170(var1);
   }

   @Override
   String recoverMethod_b_73cf11cb() {
      StringBuffer var1 = new StringBuffer();
      var1.append(this.recoverField_e + " ");
      var1.append(this.recoverField_f + " ");
      var1.append(this.recoverField_g + " ");
      var1.append(this.recoverField_h);
      return var1.toString();
   }

   public int recoverMethod_j_9b68() {
      return this.recoverField_g;
   }

   public RecoverTypeaf2ab170 recoverMethod_k_c695e236() {
      return this.recoverField_h;
   }

   @Override
   void recoverMethod_a_9ec3b9d(RecoverTypeaf2ab16c var1, RecoverTypeaf2ab16a var2, boolean var3) {
      var1.recoverMethod_c_13462e(this.recoverField_e);
      var1.recoverMethod_c_13462e(this.recoverField_f);
      var1.recoverMethod_c_13462e(this.recoverField_g);
      this.recoverField_h.recoverMethod_a_9ec3b9d(var1, null, var3);
   }
}
