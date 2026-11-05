package io.intercom.android.sdk.m5.navigation;

import androidx.activity.ComponentActivity;
import androidx.compose.animation.AnimatedContentTransitionScope;
import androidx.compose.animation.EnterTransition;
import androidx.compose.animation.ExitTransition;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.navigation.NamedNavArgument;
import androidx.navigation.NamedNavArgumentKt;
import androidx.navigation.NavArgumentBuilder;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavGraphBuilder;
import androidx.navigation.NavHostController;
import androidx.navigation.NavType;
import androidx.navigation.compose.NavGraphBuilderKt;
import io.intercom.android.sdk.m5.navigation.transitions.TransitionArgs;
import io.intercom.android.sdk.m5.navigation.transitions.TransitionStyleKt;
import io.intercom.android.sdk.ui.extension.NavBackStackEntryExtensionKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u001e\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a$\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0000¨\u0006\t",
   d2 = {
         "helpCenterDestination",
         "",
         "Landroidx/navigation/NavGraphBuilder;",
         "rootActivity",
         "Landroidx/activity/ComponentActivity;",
         "navController",
         "Landroidx/navigation/NavHostController;",
         "intercomRootActivityArgs",
         "Lio/intercom/android/sdk/m5/navigation/IntercomRootActivityArgs;",
         "intercom-sdk-base_release"
   }
)
public final class HelpCenterDestinationKt {
   public static final void helpCenterDestination(
      @NotNull NavGraphBuilder var0, @NotNull ComponentActivity var1, @NotNull NavHostController var2, @NotNull IntercomRootActivityArgs var3
   ) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      Intrinsics.checkNotNullParameter(var1, "rootActivity");
      Intrinsics.checkNotNullParameter(var2, "navController");
      Intrinsics.checkNotNullParameter(var3, "intercomRootActivityArgs");
      NamedNavArgument[] var4 = new NamedNavArgument[]{
         NamedNavArgumentKt.navArgument("transitionArgs", ((kotlin.jvm.functions.Function1) (recoveredArgument_1024_0) -> HelpCenterDestinationKt.helpCenterDestination$lambda$0((androidx.navigation.NavArgumentBuilder)recoveredArgument_1024_0))),
         NamedNavArgumentKt.navArgument("isLaunchedProgrammatically", ((kotlin.jvm.functions.Function1) (recoveredArgument_1025_0) -> HelpCenterDestinationKt.helpCenterDestination$lambda$1((androidx.navigation.NavArgumentBuilder)recoveredArgument_1025_0)))
      };
      NavGraphBuilderKt.composable$default(
         var0,
         "HELP_CENTER?transitionArgs={transitionArgs}&isLaunchedProgrammatically={isLaunchedProgrammatically}",
         CollectionsKt.listOf(var4),
         null,
         ((kotlin.jvm.functions.Function1) (recoveredArgument_1026_0) -> HelpCenterDestinationKt.helpCenterDestination$lambda$2((androidx.compose.animation.AnimatedContentTransitionScope)recoveredArgument_1026_0)),
         ((kotlin.jvm.functions.Function1) (recoveredArgument_1027_0) -> HelpCenterDestinationKt.helpCenterDestination$lambda$3((androidx.compose.animation.AnimatedContentTransitionScope)recoveredArgument_1027_0)),
         ((kotlin.jvm.functions.Function1) (recoveredArgument_1028_0) -> HelpCenterDestinationKt.helpCenterDestination$lambda$4((androidx.compose.animation.AnimatedContentTransitionScope)recoveredArgument_1028_0)),
         ((kotlin.jvm.functions.Function1) (recoveredArgument_1029_0) -> HelpCenterDestinationKt.helpCenterDestination$lambda$5((androidx.compose.animation.AnimatedContentTransitionScope)recoveredArgument_1029_0)),
         null,
         ComposableLambdaKt.composableLambdaInstance(-2139595832, true, new HelpCenterDestinationKt$helpCenterDestination$7(var1, var2, var3)),
         132,
         null
      );
   }

   private static final Unit helpCenterDestination$lambda$0(NavArgumentBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$navArgument");
      var0.setType(TransitionStyleKt.getTransitionArgNavType());
      var0.setDefaultValue(new TransitionArgs(null, null, null, null, 15, null));
      return Unit.INSTANCE;
   }

   private static final Unit helpCenterDestination$lambda$1(NavArgumentBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$navArgument");
      var0.setType(NavType.BoolType);
      var0.setNullable(false);
      var0.setDefaultValue(false);
      return Unit.INSTANCE;
   }

   private static final EnterTransition helpCenterDestination$lambda$2(AnimatedContentTransitionScope var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$composable");
      return ((TransitionArgs)NavBackStackEntryExtensionKt.getParcelableObject(
            (NavBackStackEntry)var0.getTargetState(), "transitionArgs", TransitionArgs.class, new TransitionArgs(null, null, null, null, 15, null)
         ))
         .getEnter()
         .transition();
   }

   private static final ExitTransition helpCenterDestination$lambda$3(AnimatedContentTransitionScope var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$composable");
      return ((TransitionArgs)NavBackStackEntryExtensionKt.getParcelableObject(
            (NavBackStackEntry)var0.getInitialState(), "transitionArgs", TransitionArgs.class, new TransitionArgs(null, null, null, null, 15, null)
         ))
         .getExit()
         .transition();
   }

   private static final EnterTransition helpCenterDestination$lambda$4(AnimatedContentTransitionScope var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$composable");
      return ((TransitionArgs)NavBackStackEntryExtensionKt.getParcelableObject(
            (NavBackStackEntry)var0.getTargetState(), "transitionArgs", TransitionArgs.class, new TransitionArgs(null, null, null, null, 15, null)
         ))
         .getPopEnter()
         .transition();
   }

   private static final ExitTransition helpCenterDestination$lambda$5(AnimatedContentTransitionScope var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$composable");
      return ((TransitionArgs)NavBackStackEntryExtensionKt.getParcelableObject(
            (NavBackStackEntry)var0.getInitialState(), "transitionArgs", TransitionArgs.class, new TransitionArgs(null, null, null, null, 15, null)
         ))
         .getPopExit()
         .transition();
   }
}
