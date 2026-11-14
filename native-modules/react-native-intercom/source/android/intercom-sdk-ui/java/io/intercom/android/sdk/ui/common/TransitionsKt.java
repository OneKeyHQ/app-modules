package io.intercom.android.sdk.ui.common;

import androidx.compose.animation.EnterExitTransitionKt;
import androidx.compose.animation.EnterTransition;
import androidx.compose.animation.core.AnimationSpecKt;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\u001a\u000e\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003¨\u0006\u0004",
   d2 = {"floatingButtonEnterTransition", "Landroidx/compose/animation/EnterTransition;", "slideOffset", "", "intercom-sdk-ui_release"}
)
public final class TransitionsKt {
   @NotNull
   public static final EnterTransition floatingButtonEnterTransition(int var0) {
      final int recoveredCapture_1_0 = var0;
      return EnterExitTransitionKt.recovered$4a6f8ad6d6a7c790(null, 0.8F, 0L, 5, null)
         .plus(
            EnterExitTransitionKt.slideInVertically(
               AnimationSpecKt.spring$default(0.75F, 500.0F, null, 4, null), ((kotlin.jvm.functions.Function1) (recoveredArgument_1_0) -> TransitionsKt.floatingButtonEnterTransition$lambda$0(recoveredCapture_1_0, ((java.lang.Integer)recoveredArgument_1_0).intValue()))
            )
         );
   }

   private static final int floatingButtonEnterTransition$lambda$0(int var0, int var1) {
      return var1 + var0;
   }
}
