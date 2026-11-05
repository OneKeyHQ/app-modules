package io.intercom.android.sdk.m5.navigation;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.ComponentActivity;
import androidx.compose.animation.AnimatedContentScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavHostController;
import io.intercom.android.sdk.m5.navigation.IntercomRootActivityArgs$TicketDetailsScreenArgs;
import io.intercom.android.sdk.tickets.TicketDetailState;
import io.intercom.android.sdk.tickets.TicketDetailViewModel;
import io.intercom.android.sdk.tickets.TicketDetailViewModel$Companion;
import io.intercom.android.sdk.tickets.TicketLaunchedFrom$Conversation;
import io.intercom.android.sdk.tickets.TicketLaunchedFrom$Other;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KFunction;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nTicketDetailDestination.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TicketDetailDestination.kt\nio/intercom/android/sdk/m5/navigation/TicketDetailDestinationKt$ticketDetailDestination$17\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,263:1\n1047#2,6:264\n1047#2,6:270\n*S KotlinDebug\n*F\n+ 1 TicketDetailDestination.kt\nio/intercom/android/sdk/m5/navigation/TicketDetailDestinationKt$ticketDetailDestination$17\n*L\n204#1:264,6\n205#1:270,6\n*E\n"
)
final class TicketDetailDestinationKt$ticketDetailDestination$17 implements Function4 {
   // $VF: synthetic field
   final NavHostController $navController;
   // $VF: synthetic field
   final ComponentActivity $rootActivity;

   TicketDetailDestinationKt$ticketDetailDestination$17(NavHostController var1, ComponentActivity var2) {
      this.$navController = var1;
      this.$rootActivity = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(AnimatedContentScope var1, NavBackStackEntry var2, Composer var3, int var4) {
      Intrinsics.checkNotNullParameter(var1, "$this$composable");
      Intrinsics.checkNotNullParameter(var2, "it");
      ComposerKt.sourceInformation(var3, "C192@8054L7,199@8363L16,203@8533L15,204@8589L26,200@8388L307:TicketDetailDestination.kt#m1zs2i");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            1365826072, var4, -1, "io.intercom.android.sdk.m5.navigation.ticketDetailDestination.<anonymous> (TicketDetailDestination.kt:174)"
         );
      }

      String var34;
      label64: {
         Bundle var10000 = var2.getArguments();
         if (var10000 != null) {
            var34 = var10000.getString("ticket_id");
            if (var34 != null) {
               break label64;
            }
         }

         var34 = "";
      }

      String var5;
      boolean var6;
      String var37;
      label58: {
         var5 = var34;
         Bundle var35 = var2.getArguments();
         var6 = var35 != null ? var35.getBoolean("isLaunchedProgrammatically") : false;
         Bundle var36 = var2.getArguments();
         if (var36 != null) {
            var37 = var36.getString("from");
            if (var37 != null) {
               break label58;
            }
         }

         var37 = "from_tickets_space";
      }

      String var7 = var37;
      if (this.$navController.getPreviousBackStackEntry() == null) {
         Intent var38 = this.$rootActivity.getIntent();
         Intrinsics.checkNotNullExpressionValue(var38, "getIntent(...)");
         IntercomRootActivityArgs var8 = IntercomRootActivityArgsKt.getArgsForIntent(var38);
         if (var8 instanceof io.intercom.android.sdk.m5.navigation.IntercomRootActivityArgs$TicketDetailsScreenArgs) {
            var5 = ((io.intercom.android.sdk.m5.navigation.IntercomRootActivityArgs$TicketDetailsScreenArgs)var8).getTicketId();
            var7 = ((io.intercom.android.sdk.m5.navigation.IntercomRootActivityArgs$TicketDetailsScreenArgs)var8).getFrom();
         }
      }

      io.intercom.android.sdk.tickets.TicketDetailViewModel$Companion var39 = TicketDetailViewModel.Companion;
      ViewModelStoreOwner var10001 = LocalViewModelStoreOwner.INSTANCE.getCurrent(var3, LocalViewModelStoreOwner.$stable);
      if (var10001 == null) {
         var10001 = this.$rootActivity;
      }

      TicketDetailViewModel var23 = var39.create(var10001, Intrinsics.areEqual(var7, "conversation") ? new io.intercom.android.sdk.tickets.TicketLaunchedFrom$Conversation(null, 1, null) : new io.intercom.android.sdk.tickets.TicketLaunchedFrom$Other(var5, var7));
      State var9 = SnapshotStateKt.collectAsState(var23.getStateFlow(), null, var3, 0, 1);
      TicketDetailState var40 = (TicketDetailState)var9.getValue();
      var3.startReplaceGroup(447768183);
      ComposerKt.sourceInformation(var3, "CC(remember):TicketDetailDestination.kt#9igjgp");
      Composer var11 = var3;
      boolean var12 = var3.changedInstance(this.$navController) | var3.changedInstance(this.$rootActivity);
      NavHostController var13 = this.$navController;
      ComponentActivity var14 = this.$rootActivity;
      int var15 = 0;
      Object var16 = var11.rememberedValue();
      int var17 = 0;
      Object var42;
      if (!var12 && var16 != Composer.Companion.getEmpty()) {
         var42 = var16;
      } else {
         TicketDetailState var20 = var40;
         int var18 = 0;
         KFunction var41 = new TicketDetailDestinationKt$ticketDetailDestination$17$1$1(var13, var14);
         var40 = var20;
         Object var19 = var41;
         var11.updateRememberedValue(var19);
         var42 = var19;
      }

      KFunction var10 = (KFunction)var42;
      var3.endReplaceGroup();
      Function0 var43 = (Function0)var10;
      var3.startReplaceGroup(447769986);
      ComposerKt.sourceInformation(var3, "CC(remember):TicketDetailDestination.kt#9igjgp");
      Composer var25 = var3;
      boolean var26 = var3.changedInstance(this.$navController);
      NavHostController var27 = this.$navController;
      int $i$f$cachex = 0;
      Object it$ivx = var25.rememberedValue();
      int var30/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
      Object var10002;
      if (!var26 && it$ivx != Composer.Companion.getEmpty()) {
         var10002 = it$ivx;
      } else {
         Function0 var21 = var43;
         TicketDetailState var33 = var40;
         int var31 = 0;
         KFunction var22 = new TicketDetailDestinationKt$ticketDetailDestination$17$2$1(var27);
         var40 = var33;
         var43 = var21;
         Object var32 = var22;
         var25.updateRememberedValue(var32);
         var10002 = var32;
      }

      KFunction var24 = (KFunction)var10002;
      var3.endReplaceGroup();
      TicketDetailDestinationKt.TicketDetailScreen(var40, var43, (Function2)var24, false, var6, var3, 3072, 0);
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3, Object var4) {
      this.invoke((AnimatedContentScope)var1, (NavBackStackEntry)var2, (Composer)var3, ((Number)var4).intValue());
      return Unit.INSTANCE;
   }
}
