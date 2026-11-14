package io.intercom.android.sdk.utilities;

import android.app.Activity;
import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.activities.IntercomActivity;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\bÁ\u0002\u0018\u00002\u00020\u0001B\t\b\u0003¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0007¨\u0006\b",
   d2 = {
         "Lio/intercom/android/sdk/utilities/ActivityUtils;",
         "",
         "<init>",
         "()V",
         "isHostActivity",
         "",
         "activity",
         "Landroid/app/Activity;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class ActivityUtils {
   @NotNull
   public static final ActivityUtils INSTANCE = new ActivityUtils();
   public static int $stable;

   static {}

   private ActivityUtils() {
   }

   @JvmStatic
   public static final boolean isHostActivity(@Nullable Activity var0) {
      return var0 != null && !(var0 instanceof IntercomActivity);
   }
}
