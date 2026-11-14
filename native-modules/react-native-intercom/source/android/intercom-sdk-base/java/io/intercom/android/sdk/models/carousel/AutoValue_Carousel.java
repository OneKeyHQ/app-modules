package io.intercom.android.sdk.models.carousel;

import java.util.List;
import org.jspecify.annotations.Nullable;

final class AutoValue_Carousel extends Carousel {
   private final List screens;
   private final boolean dismissible;
   private final String instanceId;

   AutoValue_Carousel(List var1, boolean var2, String var3) {
      if (var1 == null) {
         throw new NullPointerException("Null screens");
      }

      this.screens = var1;
      this.dismissible = var2;
      if (var3 == null) {
         throw new NullPointerException("Null instanceId");
      }

      this.instanceId = var3;
   }

   @Override
   public List getScreens() {
      return this.screens;
   }

   @Override
   public boolean isDismissible() {
      return this.dismissible;
   }

   @Override
   public String getInstanceId() {
      return this.instanceId;
   }

   @Override
   public String toString() {
      return "Carousel{screens=" + this.screens + ", dismissible=" + this.dismissible + ", instanceId=" + this.instanceId + "}";
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (var1 == this) {
         return true;
      }

      if (!(var1 instanceof Carousel)) {
         return false;
      }

      Carousel var2 = (Carousel)var1;
      return this.screens.equals(var2.getScreens()) && this.dismissible == var2.isDismissible() && this.instanceId.equals(var2.getInstanceId());
   }

   @Override
   public int hashCode() {
      int var1 = 1;
      var1 *= 1000003;
      var1 ^= this.screens.hashCode();
      var1 *= 1000003;
      var1 ^= this.dismissible ? 1231 : 1237;
      var1 *= 1000003;
      return var1 ^ this.instanceId.hashCode();
   }
}
