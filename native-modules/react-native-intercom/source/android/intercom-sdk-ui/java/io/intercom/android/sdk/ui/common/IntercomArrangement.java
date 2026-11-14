package io.intercom.android.sdk.ui.common;

import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.runtime.Immutable;
import androidx.compose.runtime.Stable;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\bÇ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0007¨\u0006\b",
   d2 = {
         "Lio/intercom/android/sdk/ui/common/IntercomArrangement;",
         "",
         "<init>",
         "()V",
         "itemAtBottom",
         "Landroidx/compose/foundation/layout/Arrangement$Vertical;",
         "itemIndex",
         "",
         "intercom-sdk-ui_release"
   }
)
@Immutable
public final class IntercomArrangement {
   @NotNull
   public static final IntercomArrangement INSTANCE = new IntercomArrangement();
   public static int $stable;

   static {}

   private IntercomArrangement() {
   }

   @Stable
   @NotNull
   public final Vertical itemAtBottom(int var1) {
      return new IntercomArrangement$itemAtBottom$1(var1);
   }
}
