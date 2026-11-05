package io.intercom.android.sdk.utilities;

import android.view.View;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.core.view.ViewCompat;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\bÁ\u0002\u0018\u00002\u00020\u0001B\t\b\u0003¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\u000e\u0010\b\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\u000e\u0010\t\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007¨\u0006\n",
   d2 = {
         "Lio/intercom/android/sdk/utilities/AccessibilityUtils;",
         "",
         "<init>",
         "()V",
         "addClickAbilityAnnouncement",
         "",
         "view",
         "Landroid/view/View;",
         "removeClickAbilityAnnouncement",
         "addHeadingAnnouncement",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class AccessibilityUtils {
   @NotNull
   public static final AccessibilityUtils INSTANCE = new AccessibilityUtils();
   public static int $stable;

   static {}

   private AccessibilityUtils() {
   }

   public final void addClickAbilityAnnouncement(@NotNull View var1) {
      Intrinsics.checkNotNullParameter(var1, "view");
      ViewCompat.setAccessibilityDelegate(var1, new AccessibilityUtils$addClickAbilityAnnouncement$1());
   }

   public final void removeClickAbilityAnnouncement(@NotNull View var1) {
      Intrinsics.checkNotNullParameter(var1, "view");
      ViewCompat.setAccessibilityDelegate(var1, new AccessibilityUtils$removeClickAbilityAnnouncement$1());
   }

   public final void addHeadingAnnouncement(@NotNull View var1) {
      Intrinsics.checkNotNullParameter(var1, "view");
      ViewCompat.setAccessibilityDelegate(var1, new AccessibilityUtils$addHeadingAnnouncement$1());
   }
}
