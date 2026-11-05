package io.intercom.android.sdk.m5.navigation;

import androidx.compose.animation.AnimatedContentTransitionScope;
import androidx.compose.animation.EnterTransition;
import androidx.compose.animation.ExitTransition;
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection;
import androidx.compose.animation.core.AnimationSpecKt;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\"\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\"+\u0010\u0000\u001a\u0019\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00030\u0002\u0012\u0004\u0012\u00020\u00040\u0001¢\u0006\u0002\b\u0005X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"+\u0010\b\u001a\u0019\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00030\u0002\u0012\u0004\u0012\u00020\t0\u0001¢\u0006\u0002\b\u0005X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u0007¨\u0006\u000b",
   d2 = {
         "slideUpEnterTransition",
         "Lkotlin/Function1;",
         "Landroidx/compose/animation/AnimatedContentTransitionScope;",
         "Landroidx/navigation/NavBackStackEntry;",
         "Landroidx/compose/animation/EnterTransition;",
         "Lkotlin/ExtensionFunctionType;",
         "getSlideUpEnterTransition",
         "()Lkotlin/jvm/functions/Function1;",
         "slideDownExitTransition",
         "Landroidx/compose/animation/ExitTransition;",
         "getSlideDownExitTransition",
         "intercom-sdk-base_release"
   }
)
public final class IntercomTransitionsKt {
   @NotNull
   private static final Function1 slideUpEnterTransition = ((kotlin.jvm.functions.Function1) (recoveredArgument_1052_0) -> IntercomTransitionsKt.slideUpEnterTransition$lambda$0((androidx.compose.animation.AnimatedContentTransitionScope)recoveredArgument_1052_0));
   @NotNull
   private static final Function1 slideDownExitTransition = ((kotlin.jvm.functions.Function1) (recoveredArgument_1053_0) -> IntercomTransitionsKt.slideDownExitTransition$lambda$1((androidx.compose.animation.AnimatedContentTransitionScope)recoveredArgument_1053_0));

   @NotNull
   public static final Function1 getSlideUpEnterTransition() {
      return slideUpEnterTransition;
   }

   @NotNull
   public static final Function1 getSlideDownExitTransition() {
      return slideDownExitTransition;
   }

   private static final EnterTransition slideUpEnterTransition$lambda$0(AnimatedContentTransitionScope var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      return AnimatedContentTransitionScope.recovered$37eef94facc5b0cc(
         var0, SlideDirection.Companion.recovered$5566c815befc57d7(), AnimationSpecKt.tween$default(300, 0, null, 6, null), null, 4, null
      );
   }

   private static final ExitTransition slideDownExitTransition$lambda$1(AnimatedContentTransitionScope var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      return AnimatedContentTransitionScope.recovered$9f42a095acc1a3be(
         var0, SlideDirection.Companion.recovered$313ea294e3a79420(), AnimationSpecKt.tween$default(300, 0, null, 6, null), null, 4, null
      );
   }
}
