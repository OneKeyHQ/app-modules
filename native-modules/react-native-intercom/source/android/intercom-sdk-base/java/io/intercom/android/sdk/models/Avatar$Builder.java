package io.intercom.android.sdk.models;

import androidx.annotation.Nullable;
import io.intercom.android.sdk.m5.components.avatar.AvatarShape;
import io.intercom.android.sdk.utilities.NullSafety;
import java.util.Objects;

public final class Avatar$Builder {
   @Nullable
   String image_url;
   @Nullable
   String image_dark_url;
   @Nullable
   String initials;
   @Nullable
   String square_128;
   @Nullable
   AvatarShape shape;
   @Nullable
   String label;

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Avatar$Builder var2 = (Avatar$Builder)var1;
         return Objects.equals(this.image_url, var2.image_url)
            && Objects.equals(this.image_dark_url, var2.image_dark_url)
            && Objects.equals(this.initials, var2.initials)
            && Objects.equals(this.square_128, var2.square_128)
            && Objects.equals(this.shape, var2.shape)
            && Objects.equals(this.label, var2.label);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.image_url, this.image_dark_url, this.initials, this.square_128, this.shape);
   }

   public Avatar$Builder withInitials(@Nullable String var1) {
      this.initials = var1;
      return this;
   }

   public Avatar$Builder withImageUrl(@Nullable String var1) {
      this.image_url = var1;
      return this;
   }

   public Avatar$Builder withImageDarkUrl(@Nullable String var1) {
      this.image_dark_url = var1;
      return this;
   }

   public Avatar$Builder withShape(AvatarShape var1) {
      this.shape = var1;
      return this;
   }

   public Avatar build() {
      String var1 = this.image_url;
      if (this.image_url == null) {
         var1 = this.square_128;
      }

      AvatarShape var2 = this.shape;
      if (var2 == null) {
         var2 = AvatarShape.CIRCLE;
      }

      String var3 = NullSafety.valueOrEmpty(this.initials);
      String var4;
      if (this.label == null) {
         var4 = var3;
      } else {
         var4 = this.label;
      }

      String var5 = NullSafety.valueOrEmpty(this.image_dark_url);
      return Avatar.create(NullSafety.valueOrEmpty(var1), var3, var2, var4, var5);
   }
}
