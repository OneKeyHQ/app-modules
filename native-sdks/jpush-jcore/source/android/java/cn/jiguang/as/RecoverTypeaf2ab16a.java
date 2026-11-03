package cn.jiguang.as;

public class RecoverTypeaf2ab16a {
   private RecoverTypeaf2ab16a.Member8f4403a7[] recoverField_a = new RecoverTypeaf2ab16a.Member8f4403a7[17];

   public void recoverMethod_a_6ce0fde3(int var1, RecoverTypeaf2ab170 var2) {
      if (var1 <= 16383) {
         int var3 = (var2.hashCode() & 2147483647) % 17;
         RecoverTypeaf2ab16a.Member8f4403a7 var4 = new RecoverTypeaf2ab16a.Member8f4403a7();
         var4.recoverField_a = var2;
         var4.recoverField_b = var1;
         var4.recoverField_c = this.recoverField_a[var3];
         this.recoverField_a[var3] = var4;
      }
   }

   public int recoverMethod_a_991dff6f(RecoverTypeaf2ab170 var1) {
      int var2 = (var1.hashCode() & 2147483647) % 17;
      int var3 = -1;

      for (RecoverTypeaf2ab16a.Member8f4403a7 var4 = this.recoverField_a[var2]; var4 != null; var4 = var4.recoverField_c) {
         if (var4.recoverField_a.equals(var1)) {
            var3 = var4.recoverField_b;
         }
      }

      return var3;
   }

   private static class Member8f4403a7 {
      RecoverTypeaf2ab170 recoverField_a;
      int recoverField_b;
      RecoverTypeaf2ab16a.Member8f4403a7 recoverField_c;

      private Member8f4403a7() {
      }
   }
}
