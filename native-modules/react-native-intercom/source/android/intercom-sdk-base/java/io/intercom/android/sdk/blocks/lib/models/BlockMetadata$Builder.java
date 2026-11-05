package io.intercom.android.sdk.blocks.lib.models;

import androidx.annotation.Nullable;
import io.intercom.android.sdk.models.carousel.Appearance;
import io.intercom.android.sdk.models.carousel.Appearance$Builder;
import io.intercom.android.sdk.utilities.NullSafety;

public final class BlockMetadata$Builder {
   @Nullable
   Boolean isFirstObject;
   @Nullable
   Boolean isLastObject;
   @Nullable
   Appearance$Builder appearance;

   public BlockMetadata$Builder isFirstObject(Boolean var1) {
      this.isFirstObject = var1;
      return this;
   }

   public BlockMetadata$Builder isLastObject(Boolean var1) {
      this.isLastObject = var1;
      return this;
   }

   public BlockMetadata$Builder withAppearance(Appearance$Builder var1) {
      this.appearance = var1;
      return this;
   }

   public BlockMetadata build() {
      Appearance var1 = this.appearance == null ? Appearance.NULL : this.appearance.build();
      return BlockMetadata.create(NullSafety.valueOrDefault(this.isFirstObject, false), NullSafety.valueOrDefault(this.isLastObject, false), var1);
   }
}
