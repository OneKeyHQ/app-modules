package cn.jiguang.ba;

import java.util.UUID;

public final class RecoverTypeaf2ae23b {
   private final UUID recoverField_b;
   public static final RecoverTypeaf2ae23b recoverField_a = new RecoverTypeaf2ae23b(new UUID(0L, 0L));

   public RecoverTypeaf2ae23b() {
      this((UUID)null);
   }

   public RecoverTypeaf2ae23b(UUID var1) {
      if (var1 == null) {
         var1 = UUID.randomUUID();
      }

      this.recoverField_b = var1;
   }

   @Override
   public String toString() {
      return this.recoverField_b.toString().replace("-", "");
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         RecoverTypeaf2ae23b var2 = (RecoverTypeaf2ae23b)var1;
         return this.recoverField_b.compareTo(var2.recoverField_b) == 0;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.recoverField_b.hashCode();
   }
}
