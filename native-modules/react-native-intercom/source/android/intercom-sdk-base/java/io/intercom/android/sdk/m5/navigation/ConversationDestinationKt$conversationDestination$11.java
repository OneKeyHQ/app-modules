package io.intercom.android.sdk.m5.navigation;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.ComponentActivity;
import androidx.compose.animation.AnimatedContentScope;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavHostController;
import androidx.navigation.NavOptionsBuilder;
import androidx.navigation.NavOptionsBuilderKt;
import androidx.navigation.PopUpToBuilder;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.blocks.lib.models.TicketType;
import io.intercom.android.sdk.helpcenter.articles.ArticleMetadata;
import io.intercom.android.sdk.m5.conversation.ConversationViewModel;
import io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem;
import io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$Help;
import io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$Messages;
import io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$StartNewConversation;
import io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$Tickets;
import io.intercom.android.sdk.m5.conversation.ui.ConversationScreenKt;
import io.intercom.android.sdk.m5.navigation.transitions.EnterTransitionStyle;
import io.intercom.android.sdk.m5.navigation.transitions.ExitTransitionStyle;
import io.intercom.android.sdk.m5.navigation.transitions.TransitionArgs;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.jvm.internal.Ref.BooleanRef;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nConversationDestination.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationDestination.kt\nio/intercom/android/sdk/m5/navigation/ConversationDestinationKt$conversationDestination$11\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,281:1\n1047#2,6:282\n1047#2,6:288\n1047#2,6:294\n*S KotlinDebug\n*F\n+ 1 ConversationDestination.kt\nio/intercom/android/sdk/m5/navigation/ConversationDestinationKt$conversationDestination$11\n*L\n142#1:282,6\n149#1:288,6\n218#1:294,6\n*E\n"
)
final class ConversationDestinationKt$conversationDestination$11 implements Function4 {
   // $VF: synthetic field
   final ComponentActivity $rootActivity;
   // $VF: synthetic field
   final NavHostController $navController;

   ConversationDestinationKt$conversationDestination$11(ComponentActivity var1, NavHostController var2) {
      this.$rootActivity = var1;
      this.$navController = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(AnimatedContentScope var1, NavBackStackEntry var2, Composer var3, int var4) {
      Intrinsics.checkNotNullParameter(var1, "$this$composable");
      Intrinsics.checkNotNullParameter(var2, "backStackEntry");
      ComposerKt.sourceInformation(var3, "C127@5771L7,126@5700L452,141@6314L246,148@6601L214,217@9769L299,138@6162L4633:ConversationDestination.kt#m1zs2i");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            -1198092933, var4, -1, "io.intercom.android.sdk.m5.navigation.conversationDestination.<anonymous> (ConversationDestination.kt:96)"
         );
      }

      String var5 = null;
      String var6 = "";
      String var7 = null;
      String var8 = null;
      BooleanRef var9 = new BooleanRef();
      Intent var10000 = this.$rootActivity.getIntent();
      Intrinsics.checkNotNullExpressionValue(var10000, "getIntent(...)");
      IntercomRootActivityArgs var12 = IntercomRootActivityArgsKt.getArgsForIntent(var10000);
      int var13 = 0;
      IntercomRootActivityArgs$ConversationScreenArgs activityArgsx = var12 instanceof IntercomRootActivityArgs$ConversationScreenArgs
         ? (IntercomRootActivityArgs$ConversationScreenArgs)var12
         : null;
      Bundle var62 = var2.getArguments();
      if (var62 != null ? var62.containsKey("conversationId") : false) {
         Bundle var63 = var2.getArguments();
         var5 = var63 != null ? var63.getString("conversationId") : null;
         Bundle var64 = var2.getArguments();
         var6 = var64 != null ? var64.getString("initialMessage") : null;
         Bundle var65 = var2.getArguments();
         var7 = var65 != null ? var65.getString("articleId") : null;
         Bundle var66 = var2.getArguments();
         var8 = var66 != null ? var66.getString("articleTitle") : null;
         Bundle var10001 = var2.getArguments();
         var9.element = var10001 != null ? var10001.getBoolean("isLaunchedProgrammatically") : false;
      } else if (activityArgsx != null) {
         var5 = activityArgsx.getConversationId();
         var6 = activityArgsx.getEncodedInitialMessage();
         var9.element = activityArgsx.isLaunchedProgrammatically();
         var7 = activityArgsx.getArticleId();
         var8 = activityArgsx.getArticleTitle();
      }

      ViewModelStoreOwner var67 = LocalViewModelStoreOwner.INSTANCE.getCurrent(var3, LocalViewModelStoreOwner.$stable);
      if (var67 == null) {
         var67 = this.$rootActivity;
      }

      ViewModelStoreOwner var30 = var67;
      String var32 = var5;
      boolean var14 = var9.element;
      ArticleMetadata var15 = var7 != null ? new ArticleMetadata(var7, var8) : null;
      String var68 = var6;
      if (var6 == null) {
         var68 = "";
      }

      String var16 = var68;
      ConversationViewModel var11 = ConversationDestinationKt.access$getConversationViewModel(var30, var32, var16, var14, var15, var3, 0, 0);
      Modifier var31 = SizeKt.fillMaxSize$default(Modifier.Companion, 0.0F, 1, null);
      ConversationViewModel var69 = var11;
      Modifier var70 = var31;
      var3.startReplaceGroup(931012280);
      ComposerKt.sourceInformation(var3, "CC(remember):ConversationDestination.kt#9igjgp");
      Composer var36 = var3;
      boolean var39 = var3.changedInstance(this.$navController) | var3.changedInstance(this.$rootActivity);
      NavHostController var42 = this.$navController;
      ComponentActivity var17 = this.$rootActivity;
      int var18 = 0;
      Object var19 = var36.rememberedValue();
      int var20 = 0;
      Object var10002;
      if (!var39 && var19 != Composer.Companion.getEmpty()) {
         var10002 = (Function0)var19;
      } else {
         Modifier var24 = var31;
         ConversationViewModel var23 = var11;
         int var21 = 0;
         final androidx.navigation.NavHostController recoveredCapture_1004_0 = var42;
         final androidx.activity.ComponentActivity recoveredCapture_1004_1 = var17;
         Function0 var25 = ((kotlin.jvm.functions.Function0) () -> ConversationDestinationKt$conversationDestination$11.invoke$lambda$2$lambda$1(recoveredCapture_1004_0, recoveredCapture_1004_1));
         var69 = var23;
         var70 = var24;
         Object var22 = var25;
         var36.updateRememberedValue(var22);
         var10002 = (Function0)var22;
      }

      Function0 var33 = (kotlin.jvm.functions.Function0) var10002;
      var3.endReplaceGroup();
      var10002 = var33;
      var3.startReplaceGroup(931021432);
      ComposerKt.sourceInformation(var3, "CC(remember):ConversationDestination.kt#9igjgp");
      Composer var37 = var3;
      boolean var40 = var3.changedInstance(this.$navController);
      NavHostController var43 = this.$navController;
      int $i$f$cachex = 0;
      Object recoveredSecondRemembered = var37.rememberedValue();
      var19 = 0;
      Object var10003;
      if (!var40 && recoveredSecondRemembered != Composer.Companion.getEmpty()) {
         var10003 = recoveredSecondRemembered;
      } else {
         Function0 var59 = var33;
         Modifier var57 = var70;
         ConversationViewModel var55 = var69;
         var20 = 0;
         final androidx.navigation.NavHostController recoveredCapture_1005_0 = var43;
         Function0 var26 = ((kotlin.jvm.functions.Function0) () -> ConversationDestinationKt$conversationDestination$11.invoke$lambda$4$lambda$3(recoveredCapture_1005_0));
         var69 = var55;
         var70 = var57;
         var10002 = var59;
         Object var53 = var26;
         var37.updateRememberedValue(var53);
         var10003 = (Function0)var53;
      }

      Function0 var34 = (Function0)var10003;
      var3.endReplaceGroup();
      var10003 = var34;
      final androidx.navigation.NavHostController recoveredExpressionCapture_1006_0 = ((io.intercom.android.sdk.m5.navigation.ConversationDestinationKt$conversationDestination$11) this).$navController;
      final kotlin.jvm.internal.Ref.BooleanRef recoveredExpressionCapture_1006_1 = var9;
      Function0 var10004 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.navigation.ConversationDestinationKt$conversationDestination$11.invoke$lambda$5(recoveredExpressionCapture_1006_0, recoveredExpressionCapture_1006_1));
      final androidx.navigation.NavHostController recoveredExpressionCapture_1007_0 = ((io.intercom.android.sdk.m5.navigation.ConversationDestinationKt$conversationDestination$11) this).$navController;
      final kotlin.jvm.internal.Ref.BooleanRef recoveredExpressionCapture_1007_1 = var9;
      Function1 var10005 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1007_0) -> io.intercom.android.sdk.m5.navigation.ConversationDestinationKt$conversationDestination$11.invoke$lambda$6(recoveredExpressionCapture_1007_0, recoveredExpressionCapture_1007_1, (java.lang.String) ((java.lang.String)recoveredExpressionArgument_1007_0)));
      var3.startReplaceGroup(931122893);
      ComposerKt.sourceInformation(var3, "CC(remember):ConversationDestination.kt#9igjgp");
      Composer var38 = var3;
      boolean var41 = var3.changedInstance(this.$navController) | var3.changedInstance(var11);
      NavHostController var44 = this.$navController;
      int $i$f$cachexx = 0;
      Object recoveredThirdRemembered = var38.rememberedValue();
      var19 = 0;
      Object var10006;
      if (!var41 && recoveredThirdRemembered != Composer.Companion.getEmpty()) {
         var10006 = recoveredThirdRemembered;
      } else {
         Function1 var28 = var10005;
         Function0 var27 = var10004;
         Function0 var61 = var34;
         Function0 var60 = (kotlin.jvm.functions.Function0) var10002;
         Modifier var58 = var70;
         ConversationViewModel var56 = var69;
         var20 = 0;
         final androidx.navigation.NavHostController recoveredCapture_1008_0 = var44;
         final io.intercom.android.sdk.m5.conversation.ConversationViewModel recoveredCapture_1008_1 = var11;
         Function1 var29 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1008_0) -> ConversationDestinationKt$conversationDestination$11.invoke$lambda$8$lambda$7(recoveredCapture_1008_0, recoveredCapture_1008_1, (io.intercom.android.sdk.blocks.lib.models.TicketType)recoveredArgument_1008_0));
         var69 = var56;
         var70 = var58;
         var10002 = var60;
         var10003 = var61;
         var10004 = var27;
         var10005 = var28;
         Object var54 = var29;
         var38.updateRememberedValue(var54);
         var10006 = var54;
      }

      Function1 var35 = (Function1)var10006;
      var3.endReplaceGroup();
      final androidx.navigation.NavHostController recoveredExpressionCapture_1009_0 = ((io.intercom.android.sdk.m5.navigation.ConversationDestinationKt$conversationDestination$11) this).$navController;
      final kotlin.jvm.internal.Ref.BooleanRef recoveredExpressionCapture_1009_1 = var9;
      ConversationScreenKt.ConversationScreen(
         var69,
         var70,
         (Function0)var10002,
         (Function0)var10003,
         var10004,
         var10005,
         var35,
         ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1009_0) -> io.intercom.android.sdk.m5.navigation.ConversationDestinationKt$conversationDestination$11.invoke$lambda$9(recoveredExpressionCapture_1009_0, recoveredExpressionCapture_1009_1, (io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem) ((io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem)recoveredExpressionArgument_1009_0))),
         io.intercom.android.sdk.m5.navigation.ConversationDestinationKt$conversationDestination$11.recoveredExpressionFactory_1010(((io.intercom.android.sdk.m5.navigation.ConversationDestinationKt$conversationDestination$11) this).$navController, var9),
         var3,
         48,
         0
      );
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }
   }

   private static final Unit invoke$lambda$2$lambda$1(NavHostController var0, ComponentActivity var1) {
      if (var0.getPreviousBackStackEntry() == null) {
         var1.getOnBackPressedDispatcher().onBackPressed();
      } else {
         var0.navigateUp();
      }

      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$4$lambda$3(NavHostController var0) {
      Injector.get().getMetricTracker().viewedNewConversation("from_conversation");
      IntercomRouterKt.openNewConversation$default(var0, false, null, null, 7, null);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$5(NavHostController var0, BooleanRef var1) {
      IntercomRouterKt.openTicketDetailScreen$default(
         var0, false, new TransitionArgs(EnterTransitionStyle.SLIDE_UP, ExitTransitionStyle.SLIDE_DOWN, null, null, 12, null), var1.element, 1, null
      );
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$6(NavHostController var0, BooleanRef var1, String var2) {
      Intrinsics.checkNotNullParameter(var2, "ticketId");
      IntercomRouterKt.openTicketDetailScreen(
         var0,
         var2,
         "home_screen",
         new TransitionArgs(EnterTransitionStyle.SLIDE_UP, ExitTransitionStyle.NULL, EnterTransitionStyle.NULL, ExitTransitionStyle.SLIDE_DOWN),
         var1.element
      );
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$8$lambda$7(NavHostController var0, ConversationViewModel var1, TicketType var2) {
      Intrinsics.checkNotNullParameter(var2, "ticketType");
      IntercomRouterKt.openCreateTicketsScreen(var0, var2, var1.getConversationId(), "from_conversation");
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$9(NavHostController var0, BooleanRef var1, HeaderMenuItem var2) {
      Intrinsics.checkNotNullParameter(var2, "headerMenuItem");
      HeaderMenuItem var3 = var2;
      if (var3 instanceof HeaderMenuItem$Messages) {
         IntercomRouterKt.openMessages(
            var0, new TransitionArgs(EnterTransitionStyle.SLIDE_UP, ExitTransitionStyle.SLIDE_DOWN, null, null, 12, null), var1.element
         );
      } else if (var3 instanceof HeaderMenuItem$StartNewConversation) {
         IntercomRouterKt.openNewConversation$default(var0, var1.element, null, null, 6, null);
      } else if (var3 instanceof HeaderMenuItem$Tickets) {
         IntercomRouterKt.openTicketList(
            var0,
            new TransitionArgs(
               EnterTransitionStyle.SLIDE_UP, ExitTransitionStyle.SLIDE_OUT_LEFT, EnterTransitionStyle.SLIDE_IN_RIGHT, ExitTransitionStyle.SLIDE_DOWN
            ),
            true
         );
      } else {
         if (!(var3 instanceof HeaderMenuItem$Help)) {
            throw new NoWhenBranchMatchedException();
         }

         IntercomRouterKt.openHelpCenter(var0, new TransitionArgs(EnterTransitionStyle.SLIDE_UP, ExitTransitionStyle.SLIDE_DOWN, null, null, 12, null), true);
      }

      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$12$lambda$11$lambda$10(PopUpToBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$popUpTo");
      var0.setInclusive(true);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$12$lambda$11(NavOptionsBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$navOptions");
      var0.popUpTo("CONVERSATION", ((kotlin.jvm.functions.Function1) (recoveredArgument_1012_0) -> ConversationDestinationKt$conversationDestination$11.invoke$lambda$12$lambda$11$lambda$10((androidx.navigation.PopUpToBuilder)recoveredArgument_1012_0)));
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$12(NavHostController var0, BooleanRef var1, String var2) {
      Intrinsics.checkNotNullParameter(var2, "it");
      IntercomRouterKt.openConversation$default(
         var0,
         var2,
         null,
         var1.element,
         null,
         var1.element ? NavOptionsBuilderKt.navOptions(((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1011_0) -> io.intercom.android.sdk.m5.navigation.ConversationDestinationKt$conversationDestination$11.invoke$lambda$12$lambda$11((androidx.navigation.NavOptionsBuilder) ((androidx.navigation.NavOptionsBuilder)recoveredExpressionArgument_1011_0)))) : null,
         null,
         42,
         null
      );
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3, Object var4) {
      this.invoke((AnimatedContentScope)var1, (NavBackStackEntry)var2, (Composer)var3, ((Number)var4).intValue());
      return Unit.INSTANCE;
   }

   private static kotlin.jvm.functions.Function1 recoveredExpressionFactory_1010(androidx.navigation.NavHostController recoveredExpressionCapture_1010_0, kotlin.jvm.internal.Ref.BooleanRef recoveredExpressionCapture_1010_1) {
      return ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1010_0) -> io.intercom.android.sdk.m5.navigation.ConversationDestinationKt$conversationDestination$11.invoke$lambda$12(recoveredExpressionCapture_1010_0, recoveredExpressionCapture_1010_1, (java.lang.String) ((java.lang.String)recoveredExpressionArgument_1010_0)));
   }
}
