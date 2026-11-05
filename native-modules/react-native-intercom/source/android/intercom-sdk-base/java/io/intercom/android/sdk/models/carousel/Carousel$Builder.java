package io.intercom.android.sdk.models.carousel;

import androidx.annotation.Nullable;
import io.intercom.android.sdk.utilities.NullSafety;
import io.intercom.android.sdk.utilities.commons.CollectionUtils;
import java.util.ArrayList;
import java.util.List;

public final class Carousel$Builder {
   @Nullable
   List screens;
   @Nullable
   Boolean dismissible;
   @Nullable
   String instance_id;

   public Carousel$Builder withScreens(List var1) {
      this.screens = var1;
      return this;
   }

   public Carousel$Builder withDismissible(Boolean var1) {
      this.dismissible = var1;
      return this;
   }

   public Carousel$Builder withInstanceId(String var1) {
      this.instance_id = var1;
      return this;
   }

   public Carousel build() {
      List var1 = new ArrayList(CollectionUtils.capacityFor(this.screens));
      if (this.screens != null) {
         for (java.lang.Object recoveredEntryElement_0_35 : this.screens) {
            CarouselScreen$Builder var3 = (CarouselScreen$Builder) recoveredEntryElement_0_35;
            if (var3 != null) {
               var1.add(var3.build());
            }
         }
      }

      return Carousel.create(var1, NullSafety.valueOrDefault(this.dismissible, true), NullSafety.valueOrEmpty(this.instance_id));
   }
}
