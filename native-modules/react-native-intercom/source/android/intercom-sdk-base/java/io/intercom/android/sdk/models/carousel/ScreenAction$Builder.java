package io.intercom.android.sdk.models.carousel;

import androidx.annotation.Nullable;
import io.intercom.android.sdk.utilities.NullSafety;
import io.intercom.android.sdk.utilities.commons.CollectionUtils;
import java.util.ArrayList;
import java.util.List;

public final class ScreenAction$Builder {
   @Nullable
   String id;
   @Nullable
   String type;
   @Nullable
   String title;
   @Nullable
   String text_color;
   @Nullable
   String background_color;
   @Nullable
   String uri;
   @Nullable
   List android_permissions;

   public ScreenAction$Builder withId(String var1) {
      this.id = var1;
      return this;
   }

   public ScreenAction$Builder withType(String var1) {
      this.type = var1;
      return this;
   }

   public ScreenAction$Builder withTitle(String var1) {
      this.title = var1;
      return this;
   }

   public ScreenAction$Builder withTextColor(String var1) {
      this.text_color = var1;
      return this;
   }

   public ScreenAction$Builder withBackgroundColor(String var1) {
      this.background_color = var1;
      return this;
   }

   public ScreenAction$Builder withUri(String var1) {
      this.uri = var1;
      return this;
   }

   public ScreenAction$Builder withPermissions(List var1) {
      this.android_permissions = var1;
      return this;
   }

   public ScreenAction build() {
      List var1 = new ArrayList(CollectionUtils.capacityFor(this.android_permissions));
      if (this.android_permissions != null) {
         for (java.lang.Object recoveredEntryElement_0_63 : this.android_permissions) {
            String var3 = (String) recoveredEntryElement_0_63;
            if (var3 != null) {
               var1.add(var3);
            }
         }
      }

      return ScreenAction.create(
         NullSafety.valueOrEmpty(this.id),
         NullSafety.valueOrEmpty(this.type),
         NullSafety.valueOrEmpty(this.title),
         NullSafety.valueOrEmpty(this.text_color),
         NullSafety.valueOrEmpty(this.background_color),
         NullSafety.valueOrEmpty(this.uri),
         var1
      );
   }
}
