package io.intercom.android.sdk.m5.navigation;

import android.os.Bundle;
import androidx.activity.ComponentActivity;
import androidx.compose.animation.AnimatedContentScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.MutableIntState;
import androidx.compose.runtime.SnapshotIntStateKt;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import androidx.navigation.NavOptionsBuilder;
import androidx.navigation.NavOptionsBuilderKt;
import androidx.navigation.PopUpToBuilder;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.m5.inbox.InboxViewModel;
import io.intercom.android.sdk.m5.inbox.InboxViewModel$Companion;
import io.intercom.android.sdk.m5.inbox.states.InboxUiEffects$NavigateToConversation;
import io.intercom.android.sdk.m5.inbox.ui.InboxScreenKt;
import io.intercom.android.sdk.m5.navigation.transitions.EnterTransitionStyle;
import io.intercom.android.sdk.m5.navigation.transitions.ExitTransitionStyle;
import io.intercom.android.sdk.m5.navigation.transitions.TransitionArgs;
import io.intercom.android.sdk.ui.R.drawable;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nMessagesDestination.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MessagesDestination.kt\nio/intercom/android/sdk/m5/navigation/MessagesDestinationKt$messagesDestination$7\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,150:1\n1047#2,6:151\n1047#2,6:157\n1047#2,6:163\n1047#2,6:169\n1047#2,6:175\n1047#2,6:181\n*S KotlinDebug\n*F\n+ 1 MessagesDestination.kt\nio/intercom/android/sdk/m5/navigation/MessagesDestinationKt$messagesDestination$7\n*L\n83#1:151,6\n94#1:157,6\n107#1:163,6\n110#1:169,6\n113#1:175,6\n144#1:181,6\n*E\n"
)
final class MessagesDestinationKt$messagesDestination$7 implements Function4 {
   // $VF: synthetic field
   final ComponentActivity $rootActivity;
   // $VF: synthetic field
   final NavHostController $navController;

   MessagesDestinationKt$messagesDestination$7(ComponentActivity var1, NavHostController var2) {
      this.$rootActivity = var1;
      this.$navController = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(AnimatedContentScope var1, NavBackStackEntry var2, Composer var3, int var4) {
      Intrinsics.checkNotNullParameter(var1, "$this$composable");
      Intrinsics.checkNotNullParameter(var2, "it");
      ComposerKt.sourceInformation(
         var3, "C78@3065L7,82@3230L352,93@3678L518,106@4242L92,109@4368L135,112@4541L1185,91@3591L2186,143@5845L109,141@5786L179:MessagesDestination.kt#m1zs2i"
      );
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(559331213, var4, -1, "io.intercom.android.sdk.m5.navigation.messagesDestination.<anonymous> (MessagesDestination.kt:78)");
      }

      io.intercom.android.sdk.m5.inbox.InboxViewModel$Companion var10000 = InboxViewModel.Companion;
      ViewModelStoreOwner var10001 = LocalViewModelStoreOwner.INSTANCE.getCurrent(var3, LocalViewModelStoreOwner.$stable);
      if (var10001 == null) {
         var10001 = this.$rootActivity;
      }

      InboxViewModel var5 = var10000.create(var10001);
      Bundle var76 = var2.getArguments();
      boolean var6 = var76 != null ? var76.getBoolean("isLaunchedProgrammatically") : false;
      var3.startReplaceGroup(596908281);
      ComposerKt.sourceInformation(var3, "CC(remember):MessagesDestination.kt#9igjgp");
      Composer var9 = var3;
      boolean var10 = false;
      NavHostController var11 = this.$navController;
      int var12 = 0;
      Object var13 = var9.rememberedValue();
      int var14 = 0;
      Object var77;
      if (var13 == Composer.Companion.getEmpty()) {
         int var15 = 0;
         Object var16 = SnapshotIntStateKt.mutableIntStateOf(
            var11.getPreviousBackStackEntry() == null ? drawable.intercom_ic_close : (var6 ? drawable.intercom_ic_chevron_down : drawable.intercom_ic_back)
         );
         var9.updateRememberedValue(var16);
         var77 = var16;
      } else {
         var77 = var13;
      }

      MutableIntState var8 = (MutableIntState)var77;
      var3.endReplaceGroup();
      MutableIntState var7 = var8;
      InboxViewModel var78 = var5;
      var3.startReplaceGroup(596922783);
      ComposerKt.sourceInformation(var3, "CC(remember):MessagesDestination.kt#9igjgp");
      Composer var28 = var3;
      boolean var33 = var3.changedInstance(this.$navController) | var3.changed(var6);
      NavHostController var38 = this.$navController;
      int $i$f$cachex = 0;
      Object it$ivx = var28.rememberedValue();
      int var53/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
      Object var81;
      if (!var33 && it$ivx != Composer.Companion.getEmpty()) {
         var81 = it$ivx;
      } else {
         InboxViewModel var18 = var5;
         int var59 = 0;
         final androidx.navigation.NavHostController recoveredExpressionCapture_1060_0 = var38;
         final boolean recoveredExpressionCapture_1060_1 = var6;
         Function0 var80 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.navigation.MessagesDestinationKt$messagesDestination$7.invoke$lambda$4$lambda$3(recoveredExpressionCapture_1060_0, recoveredExpressionCapture_1060_1));
         var78 = var18;
         Object var63 = var80;
         var28.updateRememberedValue(var63);
         var81 = var63;
      }

      Function0 var23 = (Function0)var81;
      var3.endReplaceGroup();
      Function0 var82 = var23;
      var3.startReplaceGroup(596940405);
      ComposerKt.sourceInformation(var3, "CC(remember):MessagesDestination.kt#9igjgp");
      Composer var29 = var3;
      boolean var34 = var3.changedInstance(this.$navController);
      NavHostController var39 = this.$navController;
      int $i$f$cachexx = 0;
      Object it$ivxx = var29.rememberedValue();
      int var54/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
      Object var10002;
      if (!var34 && it$ivxx != Composer.Companion.getEmpty()) {
         var10002 = it$ivxx;
      } else {
         Function0 var19 = var23;
         InboxViewModel var67 = var78;
         int var60 = 0;
         final androidx.navigation.NavHostController recoveredExpressionCapture_1061_0 = var39;
         Function0 var20 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.navigation.MessagesDestinationKt$messagesDestination$7.invoke$lambda$6$lambda$5(recoveredExpressionCapture_1061_0));
         var78 = var67;
         var82 = var19;
         Object var64 = var20;
         var29.updateRememberedValue(var64);
         var10002 = var64;
      }

      Function0 var24 = (Function0)var10002;
      var3.endReplaceGroup();
      Function0 var85 = var24;
      var3.startReplaceGroup(596944480);
      ComposerKt.sourceInformation(var3, "CC(remember):MessagesDestination.kt#9igjgp");
      Composer var30 = var3;
      boolean var35 = var3.changedInstance(this.$navController) | var3.changedInstance(this.$rootActivity);
      NavHostController var40 = this.$navController;
      ComponentActivity var45 = this.$rootActivity;
      int $i$f$cachexxx = 0;
      Object it$ivxxx = var30.rememberedValue();
      int var61/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxx */ = 0;
      Object var10003;
      if (!var35 && it$ivxxx != Composer.Companion.getEmpty()) {
         var10003 = it$ivxxx;
      } else {
         Function0 var73 = var24;
         Function0 var71 = var82;
         InboxViewModel var68 = var78;
         int var65 = 0;
         final androidx.navigation.NavHostController recoveredExpressionCapture_1062_0 = var40;
         final androidx.activity.ComponentActivity recoveredExpressionCapture_1062_1 = var45;
         Function0 var21 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.navigation.MessagesDestinationKt$messagesDestination$7.invoke$lambda$8$lambda$7(recoveredExpressionCapture_1062_0, recoveredExpressionCapture_1062_1));
         var78 = var68;
         var82 = var71;
         var85 = var73;
         Object var17 = var21;
         var30.updateRememberedValue(var17);
         var10003 = var17;
      }

      Function0 var25 = (Function0)var10003;
      var3.endReplaceGroup();
      Function0 var86 = var25;
      var3.startReplaceGroup(596951066);
      ComposerKt.sourceInformation(var3, "CC(remember):MessagesDestination.kt#9igjgp");
      Composer var31 = var3;
      boolean var36 = var3.changed(var6) | var3.changedInstance(this.$navController);
      NavHostController var41 = this.$navController;
      int $i$f$cachexxxx = 0;
      Object it$ivxxxx = var31.rememberedValue();
      int var56/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxx */ = 0;
      Object var10004;
      if (!var36 && it$ivxxxx != Composer.Companion.getEmpty()) {
         var10004 = it$ivxxxx;
      } else {
         Function0 var75 = var25;
         Function0 var74 = var85;
         Function0 var72 = var82;
         InboxViewModel var69 = var78;
         int var62 = 0;
         final boolean recoveredExpressionCapture_1063_0 = var6;
         final androidx.navigation.NavHostController recoveredExpressionCapture_1063_1 = var41;
         Function1 var22 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1063_0) -> io.intercom.android.sdk.m5.navigation.MessagesDestinationKt$messagesDestination$7.invoke$lambda$12$lambda$11(recoveredExpressionCapture_1063_0, recoveredExpressionCapture_1063_1, (io.intercom.android.sdk.m5.inbox.states.InboxUiEffects$NavigateToConversation) ((io.intercom.android.sdk.m5.inbox.states.InboxUiEffects$NavigateToConversation)recoveredExpressionArgument_1063_0)));
         var78 = var69;
         var82 = var72;
         var85 = var74;
         var86 = var75;
         Object var66 = var22;
         var31.updateRememberedValue(var66);
         var10004 = var66;
      }

      Function1 var26 = (Function1)var10004;
      var3.endReplaceGroup();
      InboxScreenKt.InboxScreen(var78, var82, var85, var86, var26, var7.getIntValue(), var3, 0);
      String var79 = "";
      var3.startReplaceGroup(596991718);
      ComposerKt.sourceInformation(var3, "CC(remember):MessagesDestination.kt#9igjgp");
      Composer var32 = var3;
      boolean var37 = false;
      int $i$f$cachexxxxx = 0;
      Object it$ivxxxxx = var32.rememberedValue();
      int var52/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxx */ = 0;
      Object var84;
      if (it$ivxxxxx == Composer.Companion.getEmpty()) {
         String var70 = "";
         int var57 = 0;
         Function2 var83 = new MessagesDestinationKt$messagesDestination$7$5$1(null);
         var79 = var70;
         Object var58 = var83;
         var32.updateRememberedValue(var58);
         var84 = var58;
      } else {
         var84 = it$ivxxxxx;
      }

      Function2 var27 = (Function2)var84;
      var3.endReplaceGroup();
      EffectsKt.LaunchedEffect(var79, var27, var3, 6);
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }
   }

   private static final Unit invoke$lambda$4$lambda$3$lambda$2$lambda$1(PopUpToBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$popUpTo");
      var0.setInclusive(true);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$4$lambda$3$lambda$2(NavOptionsBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$navOptions");
      var0.popUpTo("CONVERSATION", ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1067_0) -> io.intercom.android.sdk.m5.navigation.MessagesDestinationKt$messagesDestination$7.invoke$lambda$4$lambda$3$lambda$2$lambda$1((androidx.navigation.PopUpToBuilder) ((androidx.navigation.PopUpToBuilder)recoveredExpressionArgument_1067_0))));
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$4$lambda$3(NavHostController var0, boolean var1) {
      Injector.get().getMetricTracker().viewedNewConversation("messages");
      IntercomRouterKt.openNewConversation$default(
         var0, var1, NavOptionsBuilderKt.navOptions(((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1066_0) -> io.intercom.android.sdk.m5.navigation.MessagesDestinationKt$messagesDestination$7.invoke$lambda$4$lambda$3$lambda$2((androidx.navigation.NavOptionsBuilder) ((androidx.navigation.NavOptionsBuilder)recoveredExpressionArgument_1066_0)))), null, 4, null
      );
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$6$lambda$5(NavHostController var0) {
      NavController.navigate$default(var0, "HELP_CENTER", null, null, 6, null);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$8$lambda$7(NavHostController var0, ComponentActivity var1) {
      if (var0.getPreviousBackStackEntry() == null) {
         var1.finish();
      } else {
         var0.navigateUp();
      }

      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$12$lambda$11$lambda$10$lambda$9(PopUpToBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$popUpTo");
      var0.setInclusive(true);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$12$lambda$11$lambda$10(NavOptionsBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$navOptions");
      var0.popUpTo("CONVERSATION", ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1065_0) -> io.intercom.android.sdk.m5.navigation.MessagesDestinationKt$messagesDestination$7.invoke$lambda$12$lambda$11$lambda$10$lambda$9((androidx.navigation.PopUpToBuilder) ((androidx.navigation.PopUpToBuilder)recoveredExpressionArgument_1065_0))));
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$12$lambda$11(boolean var0, NavHostController var1, io.intercom.android.sdk.m5.inbox.states.InboxUiEffects$NavigateToConversation var2) {
      Intrinsics.checkNotNullParameter(var2, "it");
      Injector.get().getMetricTracker().viewedConversation("messages", var2.getConversation().getId());
      TransitionArgs var3 = var0
         ? new TransitionArgs(null, null, null, null, 15, null)
         : new TransitionArgs(EnterTransitionStyle.SLIDE_IN_LEFT, ExitTransitionStyle.NULL, EnterTransitionStyle.NULL, ExitTransitionStyle.SLIDE_OUT_RIGHT);
      IntercomRouterKt.openConversation$default(
         var1,
         var2.getConversation().getId(),
         null,
         var0,
         null,
         NavOptionsBuilderKt.navOptions(((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1064_0) -> io.intercom.android.sdk.m5.navigation.MessagesDestinationKt$messagesDestination$7.invoke$lambda$12$lambda$11$lambda$10((androidx.navigation.NavOptionsBuilder) ((androidx.navigation.NavOptionsBuilder)recoveredExpressionArgument_1064_0)))),
         var3,
         10,
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
}
