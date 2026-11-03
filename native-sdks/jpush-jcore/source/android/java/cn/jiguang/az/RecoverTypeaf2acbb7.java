package cn.jiguang.az;

import cn.jiguang.ba.RecoverTypeaf2ae23f;

public final class RecoverTypeaf2acbb7 {
   private volatile RecoverTypeaf2ae23f recoverField_a;
   private final Object recoverField_b = new Object();

   public RecoverTypeaf2ae23f recoverMethod_a_c69bcb47() {
      RecoverTypeaf2ae23f var1 = null;
      synchronized (this.recoverField_b) {
         if (this.recoverField_a != null) {
            var1 = this.recoverField_a.recoverMethod_j_c69bcb47();
         }

         return var1;
      }
   }

   public RecoverTypeaf2acbb7.Member8fa6beb4 recoverMethod_b_8497ceb2() {
      synchronized (this.recoverField_b) {
         if (this.recoverField_a != null) {
            this.recoverField_a.recoverMethod_i_9b75();
         }

         RecoverTypeaf2ae23f var1 = this.recoverField_a;
         this.recoverField_a = new RecoverTypeaf2ae23f();
         RecoverTypeaf2ae23f var4 = var1 != null ? var1.recoverMethod_j_c69bcb47() : null;
         return new RecoverTypeaf2acbb7.Member8fa6beb4(this.recoverField_a.recoverMethod_j_c69bcb47(), var4);
      }
   }

   static final class Member8fa6beb4 {
      private final RecoverTypeaf2ae23f recoverField_a;
      private final RecoverTypeaf2ae23f recoverField_b;

      public Member8fa6beb4(RecoverTypeaf2ae23f var1, RecoverTypeaf2ae23f var2) {
         this.recoverField_b = var1;
         this.recoverField_a = var2;
      }
   }
}
