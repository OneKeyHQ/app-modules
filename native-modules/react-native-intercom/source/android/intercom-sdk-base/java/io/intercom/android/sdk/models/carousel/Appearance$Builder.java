package io.intercom.android.sdk.models.carousel;

import androidx.annotation.Nullable;
import io.intercom.android.sdk.utilities.NullSafety;

public final class Appearance$Builder {
   @Nullable
   String type;
   @Nullable
   String alignment;
   @Nullable
   String text_color;

   public Appearance$Builder withType(String var1) {
      this.type = var1;
      return this;
   }

   public Appearance$Builder withTextAlignment(String var1) {
      this.alignment = var1;
      return this;
   }

   public Appearance$Builder withTextColor(String var1) {
      this.text_color = var1;
      return this;
   }

   public Appearance build() {
      return Appearance.create(NullSafety.valueOrEmpty(this.type), NullSafety.valueOrEmpty(this.alignment), NullSafety.valueOrEmpty(this.text_color));
   }
}
