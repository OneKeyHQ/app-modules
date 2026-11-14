package io.intercom.android.sdk.blocks.lib.models;

import com.google.auto.value.AutoValue;
import io.intercom.android.sdk.models.carousel.Appearance;

@AutoValue
public abstract class BlockMetadata {
   public static BlockMetadata NULL = new BlockMetadata$Builder().build();

   static BlockMetadata create(boolean var0, boolean var1, Appearance var2) {
      return new AutoValue_BlockMetadata(var0, var1, var2);
   }

   public abstract boolean isFirstObject();

   public abstract boolean isLastObject();

   public abstract Appearance getAppearance();

   public boolean isSingleObject() {
      return this.isFirstObject() && this.isLastObject();
   }
}
