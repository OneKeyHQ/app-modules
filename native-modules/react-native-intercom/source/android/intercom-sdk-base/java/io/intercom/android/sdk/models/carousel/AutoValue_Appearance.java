package io.intercom.android.sdk.models.carousel;

import org.jspecify.annotations.Nullable;

final class AutoValue_Appearance extends Appearance {
   private final String type;
   private final String alignment;
   private final String textColor;

   AutoValue_Appearance(String var1, String var2, String var3) {
      if (var1 == null) {
         throw new NullPointerException("Null type");
      }

      this.type = var1;
      if (var2 == null) {
         throw new NullPointerException("Null alignment");
      }

      this.alignment = var2;
      if (var3 == null) {
         throw new NullPointerException("Null textColor");
      }

      this.textColor = var3;
   }

   @Override
   public String getType() {
      return this.type;
   }

   @Override
   public String getAlignment() {
      return this.alignment;
   }

   @Override
   public String getTextColor() {
      return this.textColor;
   }

   @Override
   public String toString() {
      return "Appearance{type=" + this.type + ", alignment=" + this.alignment + ", textColor=" + this.textColor + "}";
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (var1 == this) {
         return true;
      }

      if (!(var1 instanceof Appearance)) {
         return false;
      }

      Appearance var2 = (Appearance)var1;
      return this.type.equals(var2.getType()) && this.alignment.equals(var2.getAlignment()) && this.textColor.equals(var2.getTextColor());
   }

   @Override
   public int hashCode() {
      int var1 = 1;
      var1 *= 1000003;
      var1 ^= this.type.hashCode();
      var1 *= 1000003;
      var1 ^= this.alignment.hashCode();
      var1 *= 1000003;
      return var1 ^ this.textColor.hashCode();
   }
}
