package io.intercom.android.sdk.models.carousel;

import io.intercom.android.sdk.blocks.lib.models.Block;
import java.util.List;
import org.jspecify.annotations.Nullable;

final class AutoValue_CarouselScreen extends CarouselScreen {
   private final String id;
   private final String backgroundColor;
   private final String textColor;
   private final List blocks;
   private final List actions;
   private final String verticalAlignment;

   AutoValue_CarouselScreen(String var1, String var2, String var3, List var4, List var5, String var6) {
      if (var1 == null) {
         throw new NullPointerException("Null id");
      }

      this.id = var1;
      if (var2 == null) {
         throw new NullPointerException("Null backgroundColor");
      }

      this.backgroundColor = var2;
      if (var3 == null) {
         throw new NullPointerException("Null textColor");
      }

      this.textColor = var3;
      if (var4 == null) {
         throw new NullPointerException("Null blocks");
      }

      this.blocks = var4;
      if (var5 == null) {
         throw new NullPointerException("Null actions");
      }

      this.actions = var5;
      if (var6 == null) {
         throw new NullPointerException("Null verticalAlignment");
      }

      this.verticalAlignment = var6;
   }

   @Override
   public String getId() {
      return this.id;
   }

   @Override
   public String getBackgroundColor() {
      return this.backgroundColor;
   }

   @Override
   public String getTextColor() {
      return this.textColor;
   }

   @Override
   public List getBlocks() {
      return this.blocks;
   }

   @Override
   public List getActions() {
      return this.actions;
   }

   @Override
   public String getVerticalAlignment() {
      return this.verticalAlignment;
   }

   @Override
   public String toString() {
      return "CarouselScreen{id="
         + this.id
         + ", backgroundColor="
         + this.backgroundColor
         + ", textColor="
         + this.textColor
         + ", blocks="
         + this.blocks
         + ", actions="
         + this.actions
         + ", verticalAlignment="
         + this.verticalAlignment
         + "}";
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (var1 == this) {
         return true;
      }

      if (!(var1 instanceof CarouselScreen)) {
         return false;
      }

      CarouselScreen var2 = (CarouselScreen)var1;
      return this.id.equals(var2.getId())
         && this.backgroundColor.equals(var2.getBackgroundColor())
         && this.textColor.equals(var2.getTextColor())
         && this.blocks.equals(var2.getBlocks())
         && this.actions.equals(var2.getActions())
         && this.verticalAlignment.equals(var2.getVerticalAlignment());
   }

   @Override
   public int hashCode() {
      int var1 = 1;
      var1 *= 1000003;
      var1 ^= this.id.hashCode();
      var1 *= 1000003;
      var1 ^= this.backgroundColor.hashCode();
      var1 *= 1000003;
      var1 ^= this.textColor.hashCode();
      var1 *= 1000003;
      var1 ^= this.blocks.hashCode();
      var1 *= 1000003;
      var1 ^= this.actions.hashCode();
      var1 *= 1000003;
      return var1 ^ this.verticalAlignment.hashCode();
   }
}
