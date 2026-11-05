package io.intercom.android.sdk.blocks.lib.models;

import io.intercom.android.sdk.models.carousel.Appearance;
import org.jspecify.annotations.Nullable;

final class AutoValue_BlockMetadata extends BlockMetadata {
   private final boolean firstObject;
   private final boolean lastObject;
   private final Appearance appearance;

   AutoValue_BlockMetadata(boolean var1, boolean var2, Appearance var3) {
      this.firstObject = var1;
      this.lastObject = var2;
      if (var3 == null) {
         throw new NullPointerException("Null appearance");
      }

      this.appearance = var3;
   }

   @Override
   public boolean isFirstObject() {
      return this.firstObject;
   }

   @Override
   public boolean isLastObject() {
      return this.lastObject;
   }

   @Override
   public Appearance getAppearance() {
      return this.appearance;
   }

   @Override
   public String toString() {
      return "BlockMetadata{firstObject=" + this.firstObject + ", lastObject=" + this.lastObject + ", appearance=" + this.appearance + "}";
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (var1 == this) {
         return true;
      }

      if (!(var1 instanceof BlockMetadata)) {
         return false;
      }

      BlockMetadata var2 = (BlockMetadata)var1;
      return this.firstObject == var2.isFirstObject() && this.lastObject == var2.isLastObject() && this.appearance.equals(var2.getAppearance());
   }

   @Override
   public int hashCode() {
      int var1 = 1;
      var1 *= 1000003;
      var1 ^= this.firstObject ? 1231 : 1237;
      var1 *= 1000003;
      var1 ^= this.lastObject ? 1231 : 1237;
      var1 *= 1000003;
      return var1 ^ this.appearance.hashCode();
   }
}
