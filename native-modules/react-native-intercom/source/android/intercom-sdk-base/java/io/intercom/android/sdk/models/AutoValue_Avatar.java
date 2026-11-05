package io.intercom.android.sdk.models;

import io.intercom.android.sdk.m5.components.avatar.AvatarShape;
import org.jspecify.annotations.Nullable;

final class AutoValue_Avatar extends Avatar {
   private final String imageUrl;
   private final String initials;
   private final AvatarShape shape;
   private final String label;
   private final String imageDarkUrl;

   AutoValue_Avatar(String var1, String var2, AvatarShape var3, String var4, String var5) {
      if (var1 == null) {
         throw new NullPointerException("Null imageUrl");
      }

      this.imageUrl = var1;
      if (var2 == null) {
         throw new NullPointerException("Null initials");
      }

      this.initials = var2;
      if (var3 == null) {
         throw new NullPointerException("Null shape");
      }

      this.shape = var3;
      if (var4 == null) {
         throw new NullPointerException("Null label");
      }

      this.label = var4;
      if (var5 == null) {
         throw new NullPointerException("Null imageDarkUrl");
      }

      this.imageDarkUrl = var5;
   }

   @Override
   public String getImageUrl() {
      return this.imageUrl;
   }

   @Override
   public String getInitials() {
      return this.initials;
   }

   @Override
   public AvatarShape getShape() {
      return this.shape;
   }

   @Override
   public String getLabel() {
      return this.label;
   }

   @Override
   public String getImageDarkUrl() {
      return this.imageDarkUrl;
   }

   @Override
   public String toString() {
      return "Avatar{imageUrl="
         + this.imageUrl
         + ", initials="
         + this.initials
         + ", shape="
         + this.shape
         + ", label="
         + this.label
         + ", imageDarkUrl="
         + this.imageDarkUrl
         + "}";
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (var1 == this) {
         return true;
      }

      if (!(var1 instanceof Avatar)) {
         return false;
      }

      Avatar var2 = (Avatar)var1;
      return this.imageUrl.equals(var2.getImageUrl())
         && this.initials.equals(var2.getInitials())
         && this.shape.equals(var2.getShape())
         && this.label.equals(var2.getLabel())
         && this.imageDarkUrl.equals(var2.getImageDarkUrl());
   }

   @Override
   public int hashCode() {
      int var1 = 1;
      var1 *= 1000003;
      var1 ^= this.imageUrl.hashCode();
      var1 *= 1000003;
      var1 ^= this.initials.hashCode();
      var1 *= 1000003;
      var1 ^= this.shape.hashCode();
      var1 *= 1000003;
      var1 ^= this.label.hashCode();
      var1 *= 1000003;
      return var1 ^ this.imageDarkUrl.hashCode();
   }
}
