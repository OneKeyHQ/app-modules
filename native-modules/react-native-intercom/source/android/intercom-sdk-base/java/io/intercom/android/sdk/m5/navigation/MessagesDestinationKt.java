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
   d1 = "\u0000\u0018\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u001c\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0000¨\u0006\u0007",
   d2 = {
         "messagesDestination",
         "",
         "Landroidx/navigation/NavGraphBuilder;",
         "navController",
         "Landroidx/navigation/NavHostController;",
         "rootActivity",
         "Landroidx/activity/ComponentActivity;",
         "intercom-sdk-base_release"
   }
)
public final class MessagesDestinationKt {
   public static final void messagesDestination(@NotNull NavGraphBuilder var0, @NotNull NavHostController var1, @NotNull ComponentActivity var2) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      Intrinsics.checkNotNullParameter(var1, "navController");
      Intrinsics.checkNotNullParameter(var2, "rootActivity");
      NamedNavArgument[] var3 = new NamedNavArgument[]{
         NamedNavArgumentKt.navArgument("transitionArgs", ((kotlin.jvm.functions.Function1) (recoveredArgument_1054_0) -> MessagesDestinationKt.messagesDestination$lambda$0((androidx.navigation.NavArgumentBuilder)recoveredArgument_1054_0))),
         NamedNavArgumentKt.navArgument("isLaunchedProgrammatically", ((kotlin.jvm.functions.Function1) (recoveredArgument_1055_0) -> MessagesDestinationKt.messagesDestination$lambda$1((androidx.navigation.NavArgumentBuilder)recoveredArgument_1055_0)))
      };
      NavGraphBuilderKt.composable$default(
         var0,
         "MESSAGES?transitionArgs={transitionArgs}&isLaunchedProgrammatically={isLaunchedProgrammatically}",
         CollectionsKt.listOf(var3),
         null,
         ((kotlin.jvm.functions.Function1) (recoveredArgument_1056_0) -> MessagesDestinationKt.messagesDestination$lambda$2((androidx.compose.animation.AnimatedContentTransitionScope)recoveredArgument_1056_0)),
         ((kotlin.jvm.functions.Function1) (recoveredArgument_1057_0) -> MessagesDestinationKt.messagesDestination$lambda$3((androidx.compose.animation.AnimatedContentTransitionScope)recoveredArgument_1057_0)),
         ((kotlin.jvm.functions.Function1) (recoveredArgument_1058_0) -> MessagesDestinationKt.messagesDestination$lambda$4((androidx.compose.animation.AnimatedContentTransitionScope)recoveredArgument_1058_0)),
         ((kotlin.jvm.functions.Function1) (recoveredArgument_1059_0) -> MessagesDestinationKt.messagesDestination$lambda$5((androidx.compose.animation.AnimatedContentTransitionScope)recoveredArgument_1059_0)),
         null,
         ComposableLambdaKt.composableLambdaInstance(559331213, true, new MessagesDestinationKt$messagesDestination$7(var2, var1)),
         132,
         null
      );
   }

   private static final Unit messagesDestination$lambda$0(NavArgumentBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$navArgument");
      var0.setType(TransitionStyleKt.getTransitionArgNavType());
      var0.setDefaultValue(new TransitionArgs(null, null, null, null, 15, null));
      return Unit.INSTANCE;
   }

   private static final Unit messagesDestination$lambda$1(NavArgumentBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$navArgument");
      var0.setType(NavType.BoolType);
      var0.setDefaultValue(false);
      return Unit.INSTANCE;
   }

   private static final EnterTransition messagesDestination$lambda$2(AnimatedContentTransitionScope var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$composable");
      return ((TransitionArgs)NavBackStackEntryExtensionKt.getParcelableObject(
            (NavBackStackEntry)var0.getTargetState(), "transitionArgs", TransitionArgs.class, new TransitionArgs(null, null, null, null, 15, null)
         ))
         .getEnter()
         .transition();
   }

   private static final ExitTransition messagesDestination$lambda$3(AnimatedContentTransitionScope var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$composable");
      return ((TransitionArgs)NavBackStackEntryExtensionKt.getParcelableObject(
            (NavBackStackEntry)var0.getInitialState(), "transitionArgs", TransitionArgs.class, new TransitionArgs(null, null, null, null, 15, null)
         ))
         .getExit()
         .transition();
   }

   private static final EnterTransition messagesDestination$lambda$4(AnimatedContentTransitionScope var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$composable");
      return ((TransitionArgs)NavBackStackEntryExtensionKt.getParcelableObject(
            (NavBackStackEntry)var0.getTargetState(), "transitionArgs", TransitionArgs.class, new TransitionArgs(null, null, null, null, 15, null)
         ))
         .getPopEnter()
         .transition();
   }

   private static final ExitTransition messagesDestination$lambda$5(AnimatedContentTransitionScope var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$composable");
      return ((TransitionArgs)NavBackStackEntryExtensionKt.getParcelableObject(
            (NavBackStackEntry)var0.getInitialState(), "transitionArgs", TransitionArgs.class, new TransitionArgs(null, null, null, null, 15, null)
         ))
         .getPopExit()
         .transition();
   }
}
