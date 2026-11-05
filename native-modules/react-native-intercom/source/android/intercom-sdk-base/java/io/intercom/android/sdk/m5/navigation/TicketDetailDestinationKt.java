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
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.platform.TestTagKt;
import androidx.navigation.NamedNavArgument;
import androidx.navigation.NamedNavArgumentKt;
import androidx.navigation.NavArgumentBuilder;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavGraphBuilder;
import androidx.navigation.NavHostController;
import androidx.navigation.NavOptions;
import androidx.navigation.NavOptionsBuilder;
import androidx.navigation.NavOptionsBuilderKt;
import androidx.navigation.NavType;
import androidx.navigation.PopUpToBuilder;
import androidx.navigation.compose.NavGraphBuilderKt;
import io.intercom.android.sdk.m5.navigation.transitions.TransitionArgs;
import io.intercom.android.sdk.m5.navigation.transitions.TransitionStyleKt;
import io.intercom.android.sdk.tickets.TicketDetailState;
import io.intercom.android.sdk.ui.extension.NavBackStackEntryExtensionKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000:\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0004\u001a\u001c\u0010\u0004\u001a\u00020\u0005*\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0000\u001aU\u0010\u000b\u001a\u00020\u00052\u0006\u0010\f\u001a\u00020\r2\u000e\b\u0002\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00050\u000f2\u001c\b\u0002\u0010\u0010\u001a\u0016\u0012\u0006\u0012\u0004\u0018\u00010\u0001\u0012\u0004\u0012\u00020\u0012\u0012\u0004\u0012\u00020\u00050\u00112\b\b\u0002\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0014\u001a\u00020\u0012H\u0001¢\u0006\u0002\u0010\u0015\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0003\u001a\u00020\u0001X\u0080T¢\u0006\u0002\n\u0000¨\u0006\u0016",
   d2 = {
         "SHOW_SUBMISSION_CARD",
         "",
         "TICKET_ID",
         "LAUNCHED_FROM",
         "ticketDetailDestination",
         "",
         "Landroidx/navigation/NavGraphBuilder;",
         "navController",
         "Landroidx/navigation/NavHostController;",
         "rootActivity",
         "Landroidx/activity/ComponentActivity;",
         "TicketDetailScreen",
         "ticketDetailState",
         "Lio/intercom/android/sdk/tickets/TicketDetailState;",
         "onBackClicked",
         "Lkotlin/Function0;",
         "onConversationCTAClicked",
         "Lkotlin/Function2;",
         "",
         "showSubmissionCard",
         "isLaunchedProgrammatically",
         "(Lio/intercom/android/sdk/tickets/TicketDetailState;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function2;ZZLandroidx/compose/runtime/Composer;II)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nTicketDetailDestination.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TicketDetailDestination.kt\nio/intercom/android/sdk/m5/navigation/TicketDetailDestinationKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,263:1\n1047#2,6:264\n1047#2,6:270\n*S KotlinDebug\n*F\n+ 1 TicketDetailDestination.kt\nio/intercom/android/sdk/m5/navigation/TicketDetailDestinationKt\n*L\n214#1:264,6\n215#1:270,6\n*E\n"
)
public final class TicketDetailDestinationKt {
   @NotNull
   public static final String SHOW_SUBMISSION_CARD = "show_submission_card";
   @NotNull
   public static final String TICKET_ID = "ticket_id";
   @NotNull
   public static final String LAUNCHED_FROM = "from";

   public static final void ticketDetailDestination(@NotNull NavGraphBuilder var0, @NotNull NavHostController var1, @NotNull ComponentActivity var2) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      Intrinsics.checkNotNullParameter(var1, "navController");
      Intrinsics.checkNotNullParameter(var2, "rootActivity");
      NamedNavArgument[] var3 = new NamedNavArgument[]{
         NamedNavArgumentKt.navArgument("show_submission_card", ((kotlin.jvm.functions.Function1) (recoveredArgument_1077_0) -> TicketDetailDestinationKt.ticketDetailDestination$lambda$2((androidx.navigation.NavArgumentBuilder)recoveredArgument_1077_0))),
         NamedNavArgumentKt.navArgument("transitionArgs", ((kotlin.jvm.functions.Function1) (recoveredArgument_1078_0) -> TicketDetailDestinationKt.ticketDetailDestination$lambda$3((androidx.navigation.NavArgumentBuilder)recoveredArgument_1078_0))),
         NamedNavArgumentKt.navArgument("isLaunchedProgrammatically", ((kotlin.jvm.functions.Function1) (recoveredArgument_1079_0) -> TicketDetailDestinationKt.ticketDetailDestination$lambda$4((androidx.navigation.NavArgumentBuilder)recoveredArgument_1079_0)))
      };
      NavGraphBuilderKt.composable$default(
         var0,
         "TICKET_DETAIL?show_submission_card={show_submission_card}&transitionArgs={transitionArgs}&isLaunchedProgrammatically={isLaunchedProgrammatically}",
         CollectionsKt.listOf(var3),
         null,
         ((kotlin.jvm.functions.Function1) (recoveredArgument_1080_0) -> TicketDetailDestinationKt.ticketDetailDestination$lambda$5((androidx.compose.animation.AnimatedContentTransitionScope)recoveredArgument_1080_0)),
         ((kotlin.jvm.functions.Function1) (recoveredArgument_1081_0) -> TicketDetailDestinationKt.ticketDetailDestination$lambda$6((androidx.compose.animation.AnimatedContentTransitionScope)recoveredArgument_1081_0)),
         ((kotlin.jvm.functions.Function1) (recoveredArgument_1082_0) -> TicketDetailDestinationKt.ticketDetailDestination$lambda$7((androidx.compose.animation.AnimatedContentTransitionScope)recoveredArgument_1082_0)),
         ((kotlin.jvm.functions.Function1) (recoveredArgument_1083_0) -> TicketDetailDestinationKt.ticketDetailDestination$lambda$8((androidx.compose.animation.AnimatedContentTransitionScope)recoveredArgument_1083_0)),
         null,
         ComposableLambdaKt.composableLambdaInstance(-1948427665, true, new TicketDetailDestinationKt$ticketDetailDestination$8(var2, var1)),
         132,
         null
      );
      var3 = new NamedNavArgument[]{
         NamedNavArgumentKt.navArgument("ticket_id", ((kotlin.jvm.functions.Function1) (recoveredArgument_1084_0) -> TicketDetailDestinationKt.ticketDetailDestination$lambda$9((androidx.navigation.NavArgumentBuilder)recoveredArgument_1084_0))),
         NamedNavArgumentKt.navArgument("from", ((kotlin.jvm.functions.Function1) (recoveredArgument_1085_0) -> TicketDetailDestinationKt.ticketDetailDestination$lambda$10((androidx.navigation.NavArgumentBuilder)recoveredArgument_1085_0))),
         NamedNavArgumentKt.navArgument("transitionArgs", ((kotlin.jvm.functions.Function1) (recoveredArgument_1071_0) -> TicketDetailDestinationKt.ticketDetailDestination$lambda$11((androidx.navigation.NavArgumentBuilder)recoveredArgument_1071_0))),
         NamedNavArgumentKt.navArgument("isLaunchedProgrammatically", ((kotlin.jvm.functions.Function1) (recoveredArgument_1072_0) -> TicketDetailDestinationKt.ticketDetailDestination$lambda$12((androidx.navigation.NavArgumentBuilder)recoveredArgument_1072_0)))
      };
      NavGraphBuilderKt.composable$default(
         var0,
         "TICKET_DETAIL/{ticket_id}?from={from}&transitionArgs={transitionArgs}&isLaunchedProgrammatically={isLaunchedProgrammatically}",
         CollectionsKt.listOf(var3),
         null,
         ((kotlin.jvm.functions.Function1) (recoveredArgument_1073_0) -> TicketDetailDestinationKt.ticketDetailDestination$lambda$13((androidx.compose.animation.AnimatedContentTransitionScope)recoveredArgument_1073_0)),
         ((kotlin.jvm.functions.Function1) (recoveredArgument_1074_0) -> TicketDetailDestinationKt.ticketDetailDestination$lambda$14((androidx.compose.animation.AnimatedContentTransitionScope)recoveredArgument_1074_0)),
         ((kotlin.jvm.functions.Function1) (recoveredArgument_1075_0) -> TicketDetailDestinationKt.ticketDetailDestination$lambda$15((androidx.compose.animation.AnimatedContentTransitionScope)recoveredArgument_1075_0)),
         ((kotlin.jvm.functions.Function1) (recoveredArgument_1076_0) -> TicketDetailDestinationKt.ticketDetailDestination$lambda$16((androidx.compose.animation.AnimatedContentTransitionScope)recoveredArgument_1076_0)),
         null,
         ComposableLambdaKt.composableLambdaInstance(1365826072, true, new TicketDetailDestinationKt$ticketDetailDestination$17(var1, var2)),
         132,
         null
      );
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TicketDetailScreen(
      @NotNull TicketDetailState var0,
      @Nullable Function0 var1,
      @Nullable Function2 var2,
      boolean var3,
      boolean var4,
      @Nullable Composer var5,
      int var6,
      int var7
   ) {
      Intrinsics.checkNotNullParameter(var0, "ticketDetailState");
      var5 = var5.startRestartGroup(226165078);
      ComposerKt.sourceInformation(
         var5,
         "C(TicketDetailScreen)P(4,1,2,3)213@8824L2,214@8887L11,220@9066L6,221@9130L13,223@9208L415,233@9631L1054,218@8990L1695:TicketDetailDestination.kt#m1zs2i"
      );
      int var8 = var6;
      if ((var7 & 1) != 0) {
         var8 |= 6;
      } else if ((var6 & 6) == 0) {
         var8 |= var5.changed(var0) ? 4 : 2;
      }

      if ((var7 & 2) != 0) {
         var8 |= 48;
      } else if ((var6 & 48) == 0) {
         var8 |= var5.changedInstance(var1) ? 32 : 16;
      }

      if ((var7 & 4) != 0) {
         var8 |= 384;
      } else if ((var6 & 384) == 0) {
         var8 |= var5.changedInstance(var2) ? 256 : 128;
      }

      if ((var7 & 8) != 0) {
         var8 |= 3072;
      } else if ((var6 & 3072) == 0) {
         var8 |= var5.changed(var3) ? 2048 : 1024;
      }

      if ((var7 & 16) != 0) {
         var8 |= 24576;
      } else if ((var6 & 24576) == 0) {
         var8 |= var5.changed(var4) ? 16384 : 8192;
      }

      if ((var8 & 9363) == 9362 && var5.getSkipping()) {
         var5.skipToGroupEnd();
      } else {
         if ((var7 & 2) != 0) {
            var5.startReplaceGroup(1672816883);
            ComposerKt.sourceInformation(var5, "CC(remember):TicketDetailDestination.kt#9igjgp");
            Composer var10 = var5;
            boolean var11 = false;
            int var12 = 0;
            Object var13 = var10.rememberedValue();
            int var14 = 0;
            Object var10000;
            if (var13 == Composer.Companion.getEmpty()) {
               int var15 = 0;
               Object var23 = ((kotlin.jvm.functions.Function0) () -> TicketDetailDestinationKt.TicketDetailScreen$lambda$18$lambda$17());
               var10.updateRememberedValue(var23);
               var10000 = var23;
            } else {
               var10000 = var13;
            }

            Function0 var9 = (Function0)var10000;
            var5.endReplaceGroup();
            var1 = var9;
         }

         if ((var7 & 4) != 0) {
            var5.startReplaceGroup(1672818908);
            ComposerKt.sourceInformation(var5, "CC(remember):TicketDetailDestination.kt#9igjgp");
            Composer var18 = var5;
            boolean var19 = false;
            int var20 = 0;
            Object var21 = var18.rememberedValue();
            int var22 = 0;
            Object var26;
            if (var21 == Composer.Companion.getEmpty()) {
               int var24 = 0;
               Object var25 = ((kotlin.jvm.functions.Function2) (recoveredArgument_1069_0, recoveredArgument_1069_1) -> TicketDetailDestinationKt.TicketDetailScreen$lambda$20$lambda$19((java.lang.String)recoveredArgument_1069_0, ((java.lang.Boolean)recoveredArgument_1069_1).booleanValue()));
               var18.updateRememberedValue(var25);
               var26 = var25;
            } else {
               var26 = var21;
            }

            Function2 var17 = (Function2)var26;
            var5.endReplaceGroup();
            var2 = var17;
         }

         if ((var7 & 8) != 0) {
            var3 = false;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(226165078, var8, -1, "io.intercom.android.sdk.m5.navigation.TicketDetailScreen (TicketDetailDestination.kt:217)");
         }

         ScaffoldKt.recovered$857cf99fc8161460(
            TestTagKt.testTag(
               WindowInsetsPaddingKt.windowInsetsPadding(
                  BackgroundKt.recovered$42a3174180b3fb7a(
                     Modifier.Companion, IntercomTheme.INSTANCE.getColors(var5, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(), null, 2, null
                  ),
                  WindowInsets_androidKt.getDisplayCutout(WindowInsets.Companion, var5, 6)
               ),
               "ticket_detail_screen"
            ),
            ComposableLambdaKt.rememberComposableLambda(-2018170598, true, new TicketDetailDestinationKt$TicketDetailScreen$3(var0, var1), var5, 54),
            null,
            null,
            null,
            0,
            0L,
            0L,
            null,
            ComposableLambdaKt.rememberComposableLambda(1401868325, true, new TicketDetailDestinationKt$TicketDetailScreen$4(var0, var2, var4, var3), var5, 54),
            var5,
            805306416,
            508
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var27 = var5.endRestartGroup();
      if (var27 != null) {
         final io.intercom.android.sdk.tickets.TicketDetailState recoveredCapture_1070_0 = var0;
         final kotlin.jvm.functions.Function0 recoveredCapture_1070_1 = var1;
         final kotlin.jvm.functions.Function2 recoveredCapture_1070_2 = var2;
         final boolean recoveredCapture_1070_3 = var3;
         final boolean recoveredCapture_1070_4 = var4;
         final int recoveredCapture_1070_5 = var6;
         final int recoveredCapture_1070_6 = var7;
         var27.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1070_0, recoveredArgument_1070_1) -> TicketDetailDestinationKt.TicketDetailScreen$lambda$21(recoveredCapture_1070_0, recoveredCapture_1070_1, recoveredCapture_1070_2, recoveredCapture_1070_3, recoveredCapture_1070_4, recoveredCapture_1070_5, recoveredCapture_1070_6, (androidx.compose.runtime.Composer)recoveredArgument_1070_0, ((java.lang.Integer)recoveredArgument_1070_1).intValue())));
      }
   }

   private static final void ticketDetailDestination$onBackClicked(NavHostController var0, ComponentActivity var1) {
      if (var0.getPreviousBackStackEntry() == null) {
         var1.getOnBackPressedDispatcher().onBackPressed();
      } else {
         var0.navigateUp();
      }
   }

   private static final Unit ticketDetailDestination$onConversationCTAClicked$lambda$1$lambda$0(PopUpToBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$popUpTo");
      var0.setInclusive(true);
      return Unit.INSTANCE;
   }

   private static final Unit ticketDetailDestination$onConversationCTAClicked$lambda$1(NavOptionsBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$navOptions");
      var0.popUpTo("CONVERSATION", ((kotlin.jvm.functions.Function1) (recoveredArgument_1087_0) -> TicketDetailDestinationKt.ticketDetailDestination$onConversationCTAClicked$lambda$1$lambda$0((androidx.navigation.PopUpToBuilder)recoveredArgument_1087_0)));
      return Unit.INSTANCE;
   }

   private static final void ticketDetailDestination$onConversationCTAClicked(NavHostController var0, String var1, boolean var2) {
      NavOptions var3 = NavOptionsBuilderKt.navOptions(((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1086_0) -> io.intercom.android.sdk.m5.navigation.TicketDetailDestinationKt.ticketDetailDestination$onConversationCTAClicked$lambda$1((androidx.navigation.NavOptionsBuilder) ((androidx.navigation.NavOptionsBuilder)recoveredExpressionArgument_1086_0))));
      IntercomRouterKt.openConversation$default(var0, var1, null, var2, null, var3, null, 42, null);
   }

   private static final Unit ticketDetailDestination$lambda$2(NavArgumentBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$navArgument");
      var0.setType(NavType.BoolType);
      return Unit.INSTANCE;
   }

   private static final Unit ticketDetailDestination$lambda$3(NavArgumentBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$navArgument");
      var0.setType(TransitionStyleKt.getTransitionArgNavType());
      var0.setDefaultValue(new TransitionArgs(null, null, null, null, 15, null));
      return Unit.INSTANCE;
   }

   private static final Unit ticketDetailDestination$lambda$4(NavArgumentBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$navArgument");
      var0.setType(NavType.BoolType);
      var0.setDefaultValue(false);
      return Unit.INSTANCE;
   }

   private static final EnterTransition ticketDetailDestination$lambda$5(AnimatedContentTransitionScope var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$composable");
      return ((TransitionArgs)NavBackStackEntryExtensionKt.getParcelableObject(
            (NavBackStackEntry)var0.getTargetState(), "transitionArgs", TransitionArgs.class, new TransitionArgs(null, null, null, null, 15, null)
         ))
         .getEnter()
         .transition();
   }

   private static final ExitTransition ticketDetailDestination$lambda$6(AnimatedContentTransitionScope var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$composable");
      return ((TransitionArgs)NavBackStackEntryExtensionKt.getParcelableObject(
            (NavBackStackEntry)var0.getInitialState(), "transitionArgs", TransitionArgs.class, new TransitionArgs(null, null, null, null, 15, null)
         ))
         .getExit()
         .transition();
   }

   private static final EnterTransition ticketDetailDestination$lambda$7(AnimatedContentTransitionScope var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$composable");
      return ((TransitionArgs)NavBackStackEntryExtensionKt.getParcelableObject(
            (NavBackStackEntry)var0.getTargetState(), "transitionArgs", TransitionArgs.class, new TransitionArgs(null, null, null, null, 15, null)
         ))
         .getPopEnter()
         .transition();
   }

   private static final ExitTransition ticketDetailDestination$lambda$8(AnimatedContentTransitionScope var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$composable");
      return ((TransitionArgs)NavBackStackEntryExtensionKt.getParcelableObject(
            (NavBackStackEntry)var0.getInitialState(), "transitionArgs", TransitionArgs.class, new TransitionArgs(null, null, null, null, 15, null)
         ))
         .getPopExit()
         .transition();
   }

   private static final Unit ticketDetailDestination$lambda$9(NavArgumentBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$navArgument");
      var0.setType(NavType.StringType);
      return Unit.INSTANCE;
   }

   private static final Unit ticketDetailDestination$lambda$10(NavArgumentBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$navArgument");
      var0.setType(NavType.StringType);
      var0.setNullable(true);
      var0.setDefaultValue("from_tickets_space");
      return Unit.INSTANCE;
   }

   private static final Unit ticketDetailDestination$lambda$11(NavArgumentBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$navArgument");
      var0.setType(TransitionStyleKt.getTransitionArgNavType());
      var0.setDefaultValue(new TransitionArgs(null, null, null, null, 15, null));
      return Unit.INSTANCE;
   }

   private static final Unit ticketDetailDestination$lambda$12(NavArgumentBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$navArgument");
      var0.setType(NavType.BoolType);
      var0.setDefaultValue(false);
      return Unit.INSTANCE;
   }

   private static final EnterTransition ticketDetailDestination$lambda$13(AnimatedContentTransitionScope var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$composable");
      return ((TransitionArgs)NavBackStackEntryExtensionKt.getParcelableObject(
            (NavBackStackEntry)var0.getTargetState(), "transitionArgs", TransitionArgs.class, new TransitionArgs(null, null, null, null, 15, null)
         ))
         .getEnter()
         .transition();
   }

   private static final ExitTransition ticketDetailDestination$lambda$14(AnimatedContentTransitionScope var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$composable");
      return ((TransitionArgs)NavBackStackEntryExtensionKt.getParcelableObject(
            (NavBackStackEntry)var0.getInitialState(), "transitionArgs", TransitionArgs.class, new TransitionArgs(null, null, null, null, 15, null)
         ))
         .getExit()
         .transition();
   }

   private static final EnterTransition ticketDetailDestination$lambda$15(AnimatedContentTransitionScope var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$composable");
      return ((TransitionArgs)NavBackStackEntryExtensionKt.getParcelableObject(
            (NavBackStackEntry)var0.getTargetState(), "transitionArgs", TransitionArgs.class, new TransitionArgs(null, null, null, null, 15, null)
         ))
         .getPopEnter()
         .transition();
   }

   private static final ExitTransition ticketDetailDestination$lambda$16(AnimatedContentTransitionScope var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$composable");
      return ((TransitionArgs)NavBackStackEntryExtensionKt.getParcelableObject(
            (NavBackStackEntry)var0.getInitialState(), "transitionArgs", TransitionArgs.class, new TransitionArgs(null, null, null, null, 15, null)
         ))
         .getPopExit()
         .transition();
   }

   private static final Unit TicketDetailScreen$lambda$18$lambda$17() {
      return Unit.INSTANCE;
   }

   private static final Unit TicketDetailScreen$lambda$20$lambda$19(String var0, boolean var1) {
      return Unit.INSTANCE;
   }

   private static final Unit TicketDetailScreen$lambda$21(
      TicketDetailState var0, Function0 var1, Function2 var2, boolean var3, boolean var4, int var5, int var6, Composer var7, int var8
   ) {
      TicketDetailScreen(var0, var1, var2, var3, var4, var7, RecomposeScopeImplKt.updateChangedFlags(var5 | 1), var6);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final void access$ticketDetailDestination$onBackClicked(NavHostController var0, ComponentActivity var1) {
      ticketDetailDestination$onBackClicked(var0, var1);
   }

   // $VF: synthetic method
   public static final void access$ticketDetailDestination$onConversationCTAClicked(NavHostController var0, String var1, boolean var2) {
      ticketDetailDestination$onConversationCTAClicked(var0, var1, var2);
   }
}
