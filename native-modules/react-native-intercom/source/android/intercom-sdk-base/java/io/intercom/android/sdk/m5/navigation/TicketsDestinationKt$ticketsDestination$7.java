package io.intercom.android.sdk.m5.navigation;

import android.os.Bundle;
import androidx.activity.ComponentActivity;
import androidx.compose.animation.AnimatedContentScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.DisposableEffectResult;
import androidx.compose.runtime.DisposableEffectScope;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.MutableIntState;
import androidx.compose.runtime.SnapshotIntStateKt;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.compose.LocalLifecycleOwnerKt;
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavHostController;
import androidx.paging.LoadState.NotLoading;
import androidx.paging.compose.LazyPagingItems;
import androidx.paging.compose.LazyPagingItemsKt;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ClientError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$NetworkError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ServerError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.m5.navigation.transitions.EnterTransitionStyle;
import io.intercom.android.sdk.m5.navigation.transitions.ExitTransitionStyle;
import io.intercom.android.sdk.m5.navigation.transitions.TransitionArgs;
import io.intercom.android.sdk.tickets.list.data.TicketsScreenUiState;
import io.intercom.android.sdk.tickets.list.reducers.TicketsListReducerKt;
import io.intercom.android.sdk.tickets.list.ui.TicketsScreenKt;
import io.intercom.android.sdk.tickets.list.ui.TicketsScreenViewModel;
import io.intercom.android.sdk.tickets.list.ui.TicketsScreenViewModel$Companion;
import io.intercom.android.sdk.ui.R.drawable;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KProperty;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nTicketsDestination.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TicketsDestination.kt\nio/intercom/android/sdk/m5/navigation/TicketsDestinationKt$ticketsDestination$7\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 4 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n+ 5 Effects.kt\nandroidx/compose/runtime/DisposableEffectScope\n*L\n1#1,244:1\n1047#2,6:245\n1047#2,6:251\n1047#2,6:257\n1047#2,6:264\n1047#2,6:270\n1047#2,6:276\n75#3:263\n85#4:282\n68#5,5:283\n*S KotlinDebug\n*F\n+ 1 TicketsDestination.kt\nio/intercom/android/sdk/m5/navigation/TicketsDestinationKt$ticketsDestination$7\n*L\n104#1:245,6\n113#1:251,6\n152#1:257,6\n160#1:264,6\n178#1:270,6\n206#1:276,6\n159#1:263\n117#1:282\n170#1:283,5\n*E\n"
)
final class TicketsDestinationKt$ticketsDestination$7 implements Function4 {
   // $VF: synthetic field
   final ComponentActivity $rootActivity;
   // $VF: synthetic field
   final NavHostController $navController;

   TicketsDestinationKt$ticketsDestination$7(ComponentActivity var1, NavHostController var2) {
      this.$rootActivity = var1;
      this.$navController = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(AnimatedContentScope var1, NavBackStackEntry var2, Composer var3, int var4) {
      Intrinsics.checkNotNullParameter(var1, "$this$composable");
      Intrinsics.checkNotNullParameter(var2, "it");
      ComposerKt.sourceInformation(var3, "C99@4417L7,103@4580L352,112@4977L127,116@5151L16:TicketsDestination.kt#m1zs2i");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(655582181, var4, -1, "io.intercom.android.sdk.m5.navigation.ticketsDestination.<anonymous> (TicketsDestination.kt:98)");
      }

      io.intercom.android.sdk.tickets.list.ui.TicketsScreenViewModel$Companion var10000 = TicketsScreenViewModel.Companion;
      ViewModelStoreOwner var10001 = LocalViewModelStoreOwner.INSTANCE.getCurrent(var3, LocalViewModelStoreOwner.$stable);
      if (var10001 == null) {
         var10001 = this.$rootActivity;
      }

      TicketsScreenViewModel var5 = var10000.create(var10001);
      Bundle var76 = var2.getArguments();
      boolean var6 = var76 != null ? var76.getBoolean("isLaunchedProgrammatically") : false;
      var3.startReplaceGroup(834704654);
      ComposerKt.sourceInformation(var3, "CC(remember):TicketsDestination.kt#9igjgp");
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
      var3.startReplaceGroup(834717133);
      ComposerKt.sourceInformation(var3, "CC(remember):TicketsDestination.kt#9igjgp");
      Composer var29 = var3;
      boolean var31 = var3.changedInstance(this.$navController) | var3.changedInstance(this.$rootActivity);
      NavHostController var33 = this.$navController;
      ComponentActivity var35 = this.$rootActivity;
      int $i$f$cachex = 0;
      Object it$ivx = var29.rememberedValue();
      int var50/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
      Object var78;
      if (!var31 && it$ivx != Composer.Companion.getEmpty()) {
         var78 = it$ivx;
      } else {
         int var17 = 0;
         final androidx.navigation.NavHostController recoveredExpressionCapture_1096_0 = var33;
         final androidx.activity.ComponentActivity recoveredExpressionCapture_1096_1 = var35;
         Object var18 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.navigation.TicketsDestinationKt$ticketsDestination$7.invoke$lambda$2$lambda$1(recoveredExpressionCapture_1096_0, recoveredExpressionCapture_1096_1));
         var29.updateRememberedValue(var18);
         var78 = var18;
      }

      Function0 var27 = (Function0)var78;
      var3.endReplaceGroup();
      Function0 var26 = var27;
      State var28 = SnapshotStateKt.collectAsState(var5.getOpenState(), null, var3, 0, 1);
      NetworkResponse var30 = invoke$lambda$3(var28);
      if (var30 == null) {
         var3.startReplaceGroup(106690880);
         ComposerKt.sourceInformation(var3, "120@5257L212");
         TicketsDestinationKt.access$TicketsOpenGateScaffold(
            var7.getIntValue(),
            var26,
            io.intercom.android.sdk.m5.navigation.ComposableSingletons$TicketsDestinationKt.INSTANCE.recovered$eac478aa65045e5d(),
            var3,
            384
         );
         var3.endReplaceGroup();
      } else if (!(var30 instanceof io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$NetworkError) && !(var30 instanceof io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ClientError) && !(var30 instanceof io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ServerError)) {
         if (!(var30 instanceof io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success)) {
            var3.startReplaceGroup(834727398);
            var3.endReplaceGroup();
            throw new NoWhenBranchMatchedException();
         }

         var3.startReplaceGroup(107790822);
         ComposerKt.sourceInformation(
            var3,
            "148@6325L26,149@6398L30,151@6467L243,151@6446L264,158@6768L7,159@6825L677,159@6792L710,177@7669L1173,174@7520L1389,205@9002L124,203@8927L218"
         );
         LazyPagingItems var32 = LazyPagingItemsKt.collectAsLazyPagingItems(var5.getPagerFlow(), null, var3, 0, 1);
         TicketsScreenUiState var34 = TicketsListReducerKt.reduceToTicketsScreenUiState(var32, null, var3, LazyPagingItems.$stable, 1);
         Object var79 = null;
         var3.startReplaceGroup(834764929);
         ComposerKt.sourceInformation(var3, "CC(remember):TicketsDestination.kt#9igjgp");
         Composer var39 = var3;
         boolean var45 = var3.changedInstance(var5) | var3.changedInstance(var32);
         int $i$f$cachexx = 0;
         Object it$ivxx = var39.rememberedValue();
         int var61/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         Object var84;
         if (!var45 && it$ivxx != Composer.Companion.getEmpty()) {
            var84 = it$ivxx;
         } else {
            Object var23 = null;
            int var19 = 0;
            Function2 var83 = new TicketsDestinationKt$ticketsDestination$7$2$1(var5, var32, null);
            var79 = var23;
            Object var20 = var83;
            var39.updateRememberedValue(var20);
            var84 = var20;
         }

         Function2 var36 = (Function2)var84;
         var3.endReplaceGroup();
         EffectsKt.LaunchedEffect(var79, var36, var3, 6);
         CompositionLocal var40 = LocalLifecycleOwnerKt.getLocalLifecycleOwner();
         int var46 = 0;
         int var52 = 0;
         ComposerKt.sourceInformationMarkerStart(var3, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var57 = var3.consume(var40);
         ComposerKt.sourceInformationMarkerEnd(var3);
         LifecycleOwner var37 = (LifecycleOwner)var57;
         LifecycleOwner var80 = var37;
         var3.startReplaceGroup(834776819);
         ComposerKt.sourceInformation(var3, "CC(remember):TicketsDestination.kt#9igjgp");
         Composer var47 = var3;
         boolean invalid$ivx = var3.changedInstance(var32) | var3.changedInstance(var37);
         int $i$f$cachexxx = 0;
         Object it$ivxxx = var47.rememberedValue();
         int var65/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxx */ = 0;
         Object var86;
         if (!invalid$ivx && it$ivxxx != Composer.Companion.getEmpty()) {
            var86 = it$ivxxx;
         } else {
            LifecycleOwner var73 = var37;
            int var68 = 0;
            final androidx.lifecycle.LifecycleOwner recoveredExpressionCapture_1097_0 = var37;
            final androidx.paging.compose.LazyPagingItems recoveredExpressionCapture_1097_1 = var32;
            Function1 var85 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1097_0) -> io.intercom.android.sdk.m5.navigation.TicketsDestinationKt$ticketsDestination$7.invoke$lambda$8$lambda$7(recoveredExpressionCapture_1097_0, recoveredExpressionCapture_1097_1, (androidx.compose.runtime.DisposableEffectScope) ((androidx.compose.runtime.DisposableEffectScope)recoveredExpressionArgument_1097_0)));
            var80 = var73;
            Object var21 = var85;
            var47.updateRememberedValue(var21);
            var86 = var21;
         }

         Function1 var41 = (Function1)var86;
         var3.endReplaceGroup();
         EffectsKt.DisposableEffect(var80, var41, var3, 0);
         TicketsScreenUiState var81 = var34;
         Function0 var87 = var26;
         var3.startReplaceGroup(834804323);
         ComposerKt.sourceInformation(var3, "CC(remember):TicketsDestination.kt#9igjgp");
         Composer var48 = var3;
         boolean var54 = var3.changed(var6) | var3.changedInstance(this.$navController);
         NavHostController var59 = this.$navController;
         int $i$f$cachexxxx = 0;
         Object it$ivxxxx = var48.rememberedValue();
         int var69/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxx */ = 0;
         Object var10002;
         if (!var54 && it$ivxxxx != Composer.Companion.getEmpty()) {
            var10002 = it$ivxxxx;
         } else {
            Function0 var24 = var26;
            TicketsScreenUiState var74 = var34;
            int var72 = 0;
            final boolean recoveredExpressionCapture_1098_0 = var6;
            final androidx.navigation.NavHostController recoveredExpressionCapture_1098_1 = var59;
            Function1 var25 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1098_0) -> io.intercom.android.sdk.m5.navigation.TicketsDestinationKt$ticketsDestination$7.invoke$lambda$10$lambda$9(recoveredExpressionCapture_1098_0, recoveredExpressionCapture_1098_1, (java.lang.String) ((java.lang.String)recoveredExpressionArgument_1098_0)));
            var81 = var74;
            var87 = var24;
            Object var22 = var25;
            var48.updateRememberedValue(var22);
            var10002 = var22;
         }

         Function1 var42 = (Function1)var10002;
         var3.endReplaceGroup();
         TicketsScreenKt.TicketsScreen(var81, var87, var42, var7.getIntValue(), var3, 0, 0);
         String var82 = "";
         var3.startReplaceGroup(834845930);
         ComposerKt.sourceInformation(var3, "CC(remember):TicketsDestination.kt#9igjgp");
         Composer var49 = var3;
         boolean var55 = false;
         int $i$f$cachexxxxx = 0;
         Object it$ivxxxxx = var49.rememberedValue();
         int var67/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxx */ = 0;
         Object var89;
         if (it$ivxxxxx == Composer.Companion.getEmpty()) {
            String var75 = "";
            int var70 = 0;
            Function2 var88 = new TicketsDestinationKt$ticketsDestination$7$5$1(null);
            var82 = var75;
            Object var71 = var88;
            var49.updateRememberedValue(var71);
            var89 = var71;
         } else {
            var89 = it$ivxxxxx;
         }

         Function2 var43 = (Function2)var89;
         var3.endReplaceGroup();
         EffectsKt.LaunchedEffect(var82, var43, var3, 6);
         var3.endReplaceGroup();
      } else {
         var3.startReplaceGroup(107092392);
         ComposerKt.sourceInformation(var3, "135@5801L406,132@5651L556");
         TicketsDestinationKt.access$TicketsOpenGateScaffold(
            var7.getIntValue(),
            var26,
            ComposableLambdaKt.rememberComposableLambda(-341459361, true, new TicketsDestinationKt$ticketsDestination$7$1(var30, var5), var3, 54),
            var3,
            384
         );
         var3.endReplaceGroup();
      }

      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }
   }

   private static final Unit invoke$lambda$2$lambda$1(NavHostController var0, ComponentActivity var1) {
      if (var0.getPreviousBackStackEntry() == null) {
         var1.finish();
      } else {
         var0.navigateUp();
      }

      return Unit.INSTANCE;
   }

   private static final NetworkResponse invoke$lambda$3(State var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (NetworkResponse)var1.getValue();
   }

   private static final void invoke$lambda$8$lambda$7$lambda$5(LazyPagingItems var0, LifecycleOwner var1, Event var2) {
      Intrinsics.checkNotNullParameter(var1, "<unused var>");
      Intrinsics.checkNotNullParameter(var2, "event");
      if (var2 == Event.ON_RESUME && var0.getLoadState().getRefresh() instanceof NotLoading) {
         var0.refresh();
      }
   }

   private static final DisposableEffectResult invoke$lambda$8$lambda$7(LifecycleOwner var0, LazyPagingItems var1, DisposableEffectScope var2) {
      Intrinsics.checkNotNullParameter(var2, "$this$DisposableEffect");
      final androidx.paging.compose.LazyPagingItems recoveredExpressionCapture_1099_0 = var1;
      LifecycleEventObserver var3 = ((androidx.lifecycle.LifecycleEventObserver) (recoveredExpressionArgument_1099_0, recoveredExpressionArgument_1099_1) -> io.intercom.android.sdk.m5.navigation.TicketsDestinationKt$ticketsDestination$7.invoke$lambda$8$lambda$7$lambda$5(recoveredExpressionCapture_1099_0, (androidx.lifecycle.LifecycleOwner) (recoveredExpressionArgument_1099_0), (androidx.lifecycle.Lifecycle.Event) (recoveredExpressionArgument_1099_1)));
      var0.getLifecycle().addObserver(var3);
      int var5 = 0;
      return new TicketsDestinationKt$ticketsDestination$7$invoke$lambda$8$lambda$7$RecoveredInnere3b0c44298fc1c14$inlined$onDispose$1(var0, var3);
   }

   private static final Unit invoke$lambda$10$lambda$9(boolean var0, NavHostController var1, String var2) {
      Intrinsics.checkNotNullParameter(var2, "ticketId");
      TransitionArgs var3 = var0
         ? new TransitionArgs(
            EnterTransitionStyle.SLIDE_IN_LEFT, ExitTransitionStyle.SLIDE_DOWN, EnterTransitionStyle.NONE, ExitTransitionStyle.SLIDE_OUT_RIGHT
         )
         : new TransitionArgs(EnterTransitionStyle.SLIDE_UP, ExitTransitionStyle.NULL, EnterTransitionStyle.NULL, ExitTransitionStyle.SLIDE_DOWN);
      IntercomRouterKt.openTicketDetailScreen$default(var1, var2, "from_tickets_space", var3, false, 8, null);
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
