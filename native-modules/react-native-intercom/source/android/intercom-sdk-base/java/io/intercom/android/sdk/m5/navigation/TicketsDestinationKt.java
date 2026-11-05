package io.intercom.android.sdk.m5.navigation;

import androidx.activity.ComponentActivity;
import androidx.compose.animation.AnimatedContentTransitionScope;
import androidx.compose.animation.EnterTransition;
import androidx.compose.animation.ExitTransition;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.layout.WindowInsets;
import androidx.compose.foundation.layout.WindowInsetsPaddingKt;
import androidx.compose.foundation.layout.WindowInsets_androidKt;
import androidx.compose.material3.ScaffoldKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableInferredTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
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
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00006\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u001a\u001c\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0000\u001a6\u0010\u0007\u001a\u00020\u00012\u0006\u0010\b\u001a\u00020\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00010\u000b2\u0011\u0010\f\u001a\r\u0012\u0004\u0012\u00020\u00010\u000b¢\u0006\u0002\b\rH\u0003¢\u0006\u0002\u0010\u000e¨\u0006\u000f²\u0006\u0012\u0010\u0010\u001a\n\u0012\u0004\u0012\u00020\u0012\u0018\u00010\u0011X\u008a\u0084\u0002",
   d2 = {
         "ticketsDestination",
         "",
         "Landroidx/navigation/NavGraphBuilder;",
         "navController",
         "Landroidx/navigation/NavHostController;",
         "rootActivity",
         "Landroidx/activity/ComponentActivity;",
         "TicketsOpenGateScaffold",
         "navIcon",
         "",
         "onBackButtonClick",
         "Lkotlin/Function0;",
         "content",
         "Landroidx/compose/runtime/Composable;",
         "(ILkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function2;Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release",
         "openState",
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;",
         "Lio/intercom/android/sdk/models/OpenMessengerResponse;"
   }
)
public final class TicketsDestinationKt {
   public static final void ticketsDestination(@NotNull NavGraphBuilder var0, @NotNull NavHostController var1, @NotNull ComponentActivity var2) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      Intrinsics.checkNotNullParameter(var1, "navController");
      Intrinsics.checkNotNullParameter(var2, "rootActivity");
      NamedNavArgument[] var3 = new NamedNavArgument[]{
         NamedNavArgumentKt.navArgument("transitionArgs", ((kotlin.jvm.functions.Function1) (recoveredArgument_1090_0) -> TicketsDestinationKt.ticketsDestination$lambda$0((androidx.navigation.NavArgumentBuilder)recoveredArgument_1090_0))),
         NamedNavArgumentKt.navArgument("isLaunchedProgrammatically", ((kotlin.jvm.functions.Function1) (recoveredArgument_1091_0) -> TicketsDestinationKt.ticketsDestination$lambda$1((androidx.navigation.NavArgumentBuilder)recoveredArgument_1091_0)))
      };
      NavGraphBuilderKt.composable$default(
         var0,
         "TICKETS?transitionArgs={transitionArgs}&isLaunchedProgrammatically={isLaunchedProgrammatically}",
         CollectionsKt.listOf(var3),
         null,
         ((kotlin.jvm.functions.Function1) (recoveredArgument_1092_0) -> TicketsDestinationKt.ticketsDestination$lambda$2((androidx.compose.animation.AnimatedContentTransitionScope)recoveredArgument_1092_0)),
         ((kotlin.jvm.functions.Function1) (recoveredArgument_1093_0) -> TicketsDestinationKt.ticketsDestination$lambda$3((androidx.compose.animation.AnimatedContentTransitionScope)recoveredArgument_1093_0)),
         ((kotlin.jvm.functions.Function1) (recoveredArgument_1094_0) -> TicketsDestinationKt.ticketsDestination$lambda$4((androidx.compose.animation.AnimatedContentTransitionScope)recoveredArgument_1094_0)),
         ((kotlin.jvm.functions.Function1) (recoveredArgument_1095_0) -> TicketsDestinationKt.ticketsDestination$lambda$5((androidx.compose.animation.AnimatedContentTransitionScope)recoveredArgument_1095_0)),
         null,
         ComposableLambdaKt.composableLambdaInstance(655582181, true, new TicketsDestinationKt$ticketsDestination$7(var2, var1)),
         132,
         null
      );
   }

   @Composable
   @ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
   private static final void TicketsOpenGateScaffold(int var0, Function0 var1, Function2 var2, Composer var3, int var4) {
      var3 = var3.startRestartGroup(2089400481);
      ComposerKt.sourceInformation(
         var3, "C(TicketsOpenGateScaffold)P(1,2)222@9402L6,223@9466L13,224@9499L343,233@9850L185,220@9326L709:TicketsDestination.kt#m1zs2i"
      );
      int var5 = var4;
      if ((var4 & 6) == 0) {
         var5 |= var3.changed(var0) ? 4 : 2;
      }

      if ((var4 & 48) == 0) {
         var5 |= var3.changedInstance(var1) ? 32 : 16;
      }

      if ((var4 & 384) == 0) {
         var5 |= var3.changedInstance(var2) ? 256 : 128;
      }

      if ((var5 & 147) == 146 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(2089400481, var5, -1, "io.intercom.android.sdk.m5.navigation.TicketsOpenGateScaffold (TicketsDestination.kt:219)");
         }

         ScaffoldKt.recovered$857cf99fc8161460(
            WindowInsetsPaddingKt.windowInsetsPadding(
               BackgroundKt.recovered$42a3174180b3fb7a(
                  Modifier.Companion, IntercomTheme.INSTANCE.getColors(var3, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(), null, 2, null
               ),
               WindowInsets_androidKt.getDisplayCutout(WindowInsets.Companion, var3, 6)
            ),
            ComposableLambdaKt.rememberComposableLambda(-1414555, true, new TicketsDestinationKt$TicketsOpenGateScaffold$1(var1, var0), var3, 54),
            null,
            null,
            null,
            0,
            0L,
            0L,
            null,
            ComposableLambdaKt.rememberComposableLambda(-1897413392, true, new TicketsDestinationKt$TicketsOpenGateScaffold$2(var2), var3, 54),
            var3,
            805306416,
            508
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var3.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1089_0 = var0;
         final kotlin.jvm.functions.Function0 recoveredCapture_1089_1 = var1;
         final kotlin.jvm.functions.Function2 recoveredCapture_1089_2 = var2;
         final int recoveredCapture_1089_3 = var4;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1089_0, recoveredArgument_1089_1) -> TicketsDestinationKt.TicketsOpenGateScaffold$lambda$6(recoveredCapture_1089_0, recoveredCapture_1089_1, recoveredCapture_1089_2, recoveredCapture_1089_3, (androidx.compose.runtime.Composer)recoveredArgument_1089_0, ((java.lang.Integer)recoveredArgument_1089_1).intValue())));
      }
   }

   private static final Unit ticketsDestination$lambda$0(NavArgumentBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$navArgument");
      var0.setType(TransitionStyleKt.getTransitionArgNavType());
      var0.setDefaultValue(new TransitionArgs(null, null, null, null, 15, null));
      return Unit.INSTANCE;
   }

   private static final Unit ticketsDestination$lambda$1(NavArgumentBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$navArgument");
      var0.setType(NavType.BoolType);
      var0.setDefaultValue(false);
      return Unit.INSTANCE;
   }

   private static final EnterTransition ticketsDestination$lambda$2(AnimatedContentTransitionScope var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$composable");
      return ((TransitionArgs)NavBackStackEntryExtensionKt.getParcelableObject(
            (NavBackStackEntry)var0.getTargetState(), "transitionArgs", TransitionArgs.class, new TransitionArgs(null, null, null, null, 15, null)
         ))
         .getEnter()
         .transition();
   }

   private static final ExitTransition ticketsDestination$lambda$3(AnimatedContentTransitionScope var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$composable");
      return ((TransitionArgs)NavBackStackEntryExtensionKt.getParcelableObject(
            (NavBackStackEntry)var0.getInitialState(), "transitionArgs", TransitionArgs.class, new TransitionArgs(null, null, null, null, 15, null)
         ))
         .getExit()
         .transition();
   }

   private static final EnterTransition ticketsDestination$lambda$4(AnimatedContentTransitionScope var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$composable");
      return ((TransitionArgs)NavBackStackEntryExtensionKt.getParcelableObject(
            (NavBackStackEntry)var0.getTargetState(), "transitionArgs", TransitionArgs.class, new TransitionArgs(null, null, null, null, 15, null)
         ))
         .getPopEnter()
         .transition();
   }

   private static final ExitTransition ticketsDestination$lambda$5(AnimatedContentTransitionScope var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$composable");
      return ((TransitionArgs)NavBackStackEntryExtensionKt.getParcelableObject(
            (NavBackStackEntry)var0.getInitialState(), "transitionArgs", TransitionArgs.class, new TransitionArgs(null, null, null, null, 15, null)
         ))
         .getPopExit()
         .transition();
   }

   private static final Unit TicketsOpenGateScaffold$lambda$6(int var0, Function0 var1, Function2 var2, int var3, Composer var4, int var5) {
      TicketsOpenGateScaffold(var0, var1, var2, var4, RecomposeScopeImplKt.updateChangedFlags(var3 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final void access$TicketsOpenGateScaffold(int var0, Function0 var1, Function2 var2, Composer var3, int var4) {
      TicketsOpenGateScaffold(var0, var1, var2, var3, var4);
   }
}
