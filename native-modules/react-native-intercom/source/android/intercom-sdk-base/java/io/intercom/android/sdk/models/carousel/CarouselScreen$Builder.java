package io.intercom.android.sdk.models.carousel;

import androidx.annotation.Nullable;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.utilities.NullSafety;
import io.intercom.android.sdk.utilities.commons.CollectionUtils;
import java.util.ArrayList;
import java.util.List;

public final class CarouselScreen$Builder {
   @Nullable
   String id;
   @Nullable
   String background_color;
   @Nullable
   String text_color;
   @Nullable
   List blocks;
   @Nullable
   List actions;
   @Nullable
   String vertical_alignment;

   public CarouselScreen$Builder withId(String var1) {
      this.id = var1;
      return this;
   }

   public CarouselScreen$Builder withBackgroundColor(String var1) {
      this.background_color = var1;
      return this;
   }

   public CarouselScreen$Builder withTextColor(String var1) {
      this.text_color = var1;
      return this;
   }

   public CarouselScreen$Builder withBlocks(List var1) {
      this.blocks = var1;
      return this;
   }

   public CarouselScreen$Builder withActions(List var1) {
      this.actions = var1;
      return this;
   }

   public CarouselScreen$Builder withVerticalAlignment(String var1) {
      this.vertical_alignment = var1;
      return this;
   }

   public CarouselScreen build() {
      List var1 = new ArrayList(CollectionUtils.capacityFor(this.actions));
      if (this.actions != null) {
         for (java.lang.Object recoveredEntryElement_0_58 : this.actions) {
            ScreenAction$Builder var3 = (ScreenAction$Builder) recoveredEntryElement_0_58;
            if (var3 != null) {
               var1.add(var3.build());
            }
         }
      }

      List var5 = new ArrayList(CollectionUtils.capacityFor(this.blocks));
      if (this.blocks != null) {
         for (java.lang.Object recoveredEntryElement_0_67 : this.blocks) {
            Block$Builder var4 = (Block$Builder) recoveredEntryElement_0_67;
            if (var4 != null) {
               var5.add(var4.build());
            }
         }
      }

      return CarouselScreen.create(
         NullSafety.valueOrEmpty(this.id),
         NullSafety.valueOrEmpty(this.background_color),
         NullSafety.valueOrEmpty(this.text_color),
         var5,
         var1,
         NullSafety.valueOrEmpty(this.vertical_alignment)
      );
   }
}
