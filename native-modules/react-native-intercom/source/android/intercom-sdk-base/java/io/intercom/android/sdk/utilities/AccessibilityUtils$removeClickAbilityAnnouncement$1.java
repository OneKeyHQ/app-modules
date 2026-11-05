package io.intercom.android.sdk.utilities;

import android.view.View;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001d\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\u0018\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016¨\u0006\b",
   d2 = {
         "io/intercom/android/sdk/utilities/AccessibilityUtils$removeClickAbilityAnnouncement$1",
         "Landroidx/core/view/AccessibilityDelegateCompat;",
         "onInitializeAccessibilityNodeInfo",
         "",
         "host",
         "Landroid/view/View;",
         "info",
         "Landroidx/core/view/accessibility/AccessibilityNodeInfoCompat;",
         "intercom-sdk-base_release"
   }
)
public final class AccessibilityUtils$removeClickAbilityAnnouncement$1 extends AccessibilityDelegateCompat {
   AccessibilityUtils$removeClickAbilityAnnouncement$1() {
   }

   @Override
   public void onInitializeAccessibilityNodeInfo(View var1, AccessibilityNodeInfoCompat var2) {
      Intrinsics.checkNotNullParameter(var1, "host");
      Intrinsics.checkNotNullParameter(var2, "info");
      super.onInitializeAccessibilityNodeInfo(var1, var2);
      var2.removeAction(AccessibilityActionCompat.ACTION_CLICK);
      var2.removeAction(AccessibilityActionCompat.ACTION_LONG_CLICK);
      var2.setClickable(false);
      var2.setLongClickable(false);
   }
}
