package io.intercom.android.sdk.m5.navigation;

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
import io.intercom.android.sdk.tickets.TicketDetailState;
import io.intercom.android.sdk.tickets.TicketDetailViewModel;
import io.intercom.android.sdk.tickets.TicketDetailViewModel$Companion;
import io.intercom.android.sdk.tickets.TicketLaunchedFrom$Conversation;
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
   "SMAP\nTicketDetailDestination.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TicketDetailDestination.kt\nio/intercom/android/sdk/m5/navigation/TicketDetailDestinationKt$ticketDetailDestination$8\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,263:1\n1047#2,6:264\n1047#2,6:270\n*S KotlinDebug\n*F\n+ 1 TicketDetailDestination.kt\nio/intercom/android/sdk/m5/navigation/TicketDetailDestinationKt$ticketDetailDestination$8\n*L\n120#1:264,6\n121#1:270,6\n*E\n"
)
final class TicketDetailDestinationKt$ticketDetailDestination$8 implements Function4 {
   // $VF: synthetic field
   final ComponentActivity $rootActivity;
   // $VF: synthetic field
   final NavHostController $navController;

   TicketDetailDestinationKt$ticketDetailDestination$8(ComponentActivity var1, NavHostController var2) {
      this.$rootActivity = var1;
      this.$navController = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(AnimatedContentScope var1, NavBackStackEntry var2, Composer var3, int var4) {
      Intrinsics.checkNotNullParameter(var1, "$this$composable");
      Intrinsics.checkNotNullParameter(var2, "it");
      ComposerKt.sourceInformation(var3, "C112@4688L7,115@4837L16,119@5020L15,120@5076L26,116@4862L320:TicketDetailDestination.kt#m1zs2i");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            -1948427665, var4, -1, "io.intercom.android.sdk.m5.navigation.ticketDetailDestination.<anonymous> (TicketDetailDestination.kt:108)"
         );
      }

      Bundle var10000 = var2.getArguments();
      boolean var5 = var10000 != null ? var10000.getBoolean("show_submission_card") : false;
      Bundle var32 = var2.getArguments();
      boolean var6 = var32 != null ? var32.getBoolean("isLaunchedProgrammatically") : false;
      io.intercom.android.sdk.tickets.TicketDetailViewModel$Companion var33 = TicketDetailViewModel.Companion;
      ViewModelStoreOwner var10001 = LocalViewModelStoreOwner.INSTANCE.getCurrent(var3, LocalViewModelStoreOwner.$stable);
      if (var10001 == null) {
         var10001 = this.$rootActivity;
      }

      TicketDetailViewModel var7 = var33.create(var10001, new io.intercom.android.sdk.tickets.TicketLaunchedFrom$Conversation(null, 1, null));
      State var8 = SnapshotStateKt.collectAsState(var7.getStateFlow(), null, var3, 0, 1);
      TicketDetailState var34 = (TicketDetailState)var8.getValue();
      var3.startReplaceGroup(447655767);
      ComposerKt.sourceInformation(var3, "CC(remember):TicketDetailDestination.kt#9igjgp");
      Composer var10 = var3;
      boolean var11 = var3.changedInstance(this.$navController) | var3.changedInstance(this.$rootActivity);
      NavHostController var12 = this.$navController;
      ComponentActivity var13 = this.$rootActivity;
      int var14 = 0;
      Object var15 = var10.rememberedValue();
      int var16 = 0;
      Object var36;
      if (!var11 && var15 != Composer.Companion.getEmpty()) {
         var36 = var15;
      } else {
         TicketDetailState var19 = var34;
         int var17 = 0;
         KFunction var35 = new TicketDetailDestinationKt$ticketDetailDestination$8$1$1(var12, var13);
         var34 = var19;
         Object var18 = var35;
         var10.updateRememberedValue(var18);
         var36 = var18;
      }

      KFunction var9 = (KFunction)var36;
      var3.endReplaceGroup();
      Function0 var37 = (Function0)var9;
      var3.startReplaceGroup(447657570);
      ComposerKt.sourceInformation(var3, "CC(remember):TicketDetailDestination.kt#9igjgp");
      Composer var23 = var3;
      boolean var24 = var3.changedInstance(this.$navController);
      NavHostController var25 = this.$navController;
      int $i$f$cachex = 0;
      Object it$ivx = var23.rememberedValue();
      int var28/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
      Object var10002;
      if (!var24 && it$ivx != Composer.Companion.getEmpty()) {
         var10002 = it$ivx;
      } else {
         Function0 var20 = var37;
         TicketDetailState var31 = var34;
         int var29 = 0;
         KFunction var21 = new TicketDetailDestinationKt$ticketDetailDestination$8$2$1(var25);
         var34 = var31;
         var37 = var20;
         Object var30 = var21;
         var23.updateRememberedValue(var30);
         var10002 = var30;
      }

      KFunction var22 = (KFunction)var10002;
      var3.endReplaceGroup();
      TicketDetailDestinationKt.TicketDetailScreen(var34, var37, (Function2)var22, var5, var6, var3, 0, 0);
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
