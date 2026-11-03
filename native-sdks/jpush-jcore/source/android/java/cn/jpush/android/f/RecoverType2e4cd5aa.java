package cn.jpush.android.f;

import android.annotation.TargetApi;
import java.util.Objects;

public final class RecoverType2e4cd5aa {
   public String recoverField_a;
   public long recoverField_b;

   public RecoverType2e4cd5aa(String var1, long var2) {
      this.recoverField_a = var1;
      this.recoverField_b = var2;
   }

   @TargetApi(19)
   @Override
   public final boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         var1 = var1;
         return this.recoverField_b == ((RecoverType2e4cd5aa)var1).recoverField_b && Objects.equals(this.recoverField_a, ((RecoverType2e4cd5aa)var1).recoverField_a);
      } else {
         return false;
      }
   }

   @TargetApi(19)
   @Override
   public final int hashCode() {
      return Objects.hash(this.recoverField_a, this.recoverField_b);
   }
}
