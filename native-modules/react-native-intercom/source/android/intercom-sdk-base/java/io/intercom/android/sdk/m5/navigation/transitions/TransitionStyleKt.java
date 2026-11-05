package io.intercom.android.sdk.m5.navigation.transitions;

import androidx.navigation.NavType;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0014\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u001a\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006¨\u0006\u0007",
   d2 = {
         "PREDICTIVE_BACK_SCALE",
         "",
         "TransitionArgNavType",
         "Landroidx/navigation/NavType;",
         "Lio/intercom/android/sdk/m5/navigation/transitions/TransitionArgs;",
         "getTransitionArgNavType",
         "()Landroidx/navigation/NavType;",
         "intercom-sdk-base_release"
   }
)
public final class TransitionStyleKt {
   private static final float PREDICTIVE_BACK_SCALE = 0.92F;
   @NotNull
   private static final NavType TransitionArgNavType = new TransitionStyleKt$TransitionArgNavType$1();

   @NotNull
   public static final NavType getTransitionArgNavType() {
      return TransitionArgNavType;
   }
}
