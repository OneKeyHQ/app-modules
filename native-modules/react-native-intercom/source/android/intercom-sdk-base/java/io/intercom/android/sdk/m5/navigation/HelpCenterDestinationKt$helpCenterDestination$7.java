package io.intercom.android.sdk.m5.navigation;

import android.os.Bundle;
import androidx.activity.ComponentActivity;
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner;
import androidx.compose.animation.AnimatedContentScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalKt;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.MutableIntState;
import androidx.compose.runtime.ProvidedValue;
import androidx.compose.runtime.SnapshotIntStateKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavHostController;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.helpcenter.api.HelpCenterApi;
import io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel;
import io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel$Companion;
import io.intercom.android.sdk.ui.R.drawable;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nHelpCenterDestination.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HelpCenterDestination.kt\nio/intercom/android/sdk/m5/navigation/HelpCenterDestinationKt$helpCenterDestination$7\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,117:1\n1047#2,6:118\n1047#2,6:124\n*S KotlinDebug\n*F\n+ 1 HelpCenterDestination.kt\nio/intercom/android/sdk/m5/navigation/HelpCenterDestinationKt$helpCenterDestination$7\n*L\n80#1:118,6\n111#1:124,6\n*E\n"
)
final class HelpCenterDestinationKt$helpCenterDestination$7 implements Function4 {
   // $VF: synthetic field
   final ComponentActivity $rootActivity;
   // $VF: synthetic field
   final NavHostController $navController;
   // $VF: synthetic field
   final IntercomRootActivityArgs $intercomRootActivityArgs;

   HelpCenterDestinationKt$helpCenterDestination$7(ComponentActivity var1, NavHostController var2, IntercomRootActivityArgs var3) {
      this.$rootActivity = var1;
      this.$navController = var2;
      this.$intercomRootActivityArgs = var3;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(AnimatedContentScope var1, NavBackStackEntry var2, Composer var3, int var4) {
      Intrinsics.checkNotNullParameter(var1, "$this$composable");
      Intrinsics.checkNotNullParameter(var2, "it");
      ComposerKt.sourceInformation(var3, "C79@3122L352,91@3845L725,91@3763L807,110@4638L105,108@4579L175:HelpCenterDestination.kt#m1zs2i");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            -2139595832, var4, -1, "io.intercom.android.sdk.m5.navigation.helpCenterDestination.<anonymous> (HelpCenterDestination.kt:70)"
         );
      }

      Bundle var10000 = var2.getArguments();
      boolean var5 = var10000 != null ? var10000.getBoolean("isLaunchedProgrammatically") : false;
      io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel$Companion var26 = HelpCenterViewModel.Companion;
      ViewModelStoreOwner var10001 = this.$rootActivity;
      HelpCenterApi var10002 = Injector.get().getHelpCenterApi();
      Intrinsics.checkNotNullExpressionValue(var10002, "getHelpCenterApi(...)");
      HelpCenterViewModel var6 = var26.create(var10001, var10002, "collection_list");
      var3.startReplaceGroup(-102146289);
      ComposerKt.sourceInformation(var3, "CC(remember):HelpCenterDestination.kt#9igjgp");
      Composer var9 = var3;
      boolean var10 = false;
      NavHostController var11 = this.$navController;
      int var12 = 0;
      Object var13 = var9.rememberedValue();
      int var14 = 0;
      Object var27;
      if (var13 == Composer.Companion.getEmpty()) {
         int var15 = 0;
         Object var16 = SnapshotIntStateKt.mutableIntStateOf(
            var11.getPreviousBackStackEntry() == null ? drawable.intercom_ic_close : (var5 ? drawable.intercom_ic_chevron_down : drawable.intercom_ic_back)
         );
         var9.updateRememberedValue(var16);
         var27 = var16;
      } else {
         var27 = var13;
      }

      MutableIntState var8 = (MutableIntState)var27;
      var3.endReplaceGroup();
      MutableIntState var7 = var8;
      CompositionLocalKt.CompositionLocalProvider(
         LocalOnBackPressedDispatcherOwner.INSTANCE.provides(this.$rootActivity),
         ComposableLambdaKt.rememberComposableLambda(
            -1743507320,
            true,
            new HelpCenterDestinationKt$helpCenterDestination$7$1(var6, this.$intercomRootActivityArgs, this.$navController, this.$rootActivity, var7),
            var3,
            54
         ),
         var3,
         48 | ProvidedValue.$stable
      );
      String var28 = "";
      var3.startReplaceGroup(-102098024);
      ComposerKt.sourceInformation(var3, "CC(remember):HelpCenterDestination.kt#9igjgp");
      Composer var19 = var3;
      boolean var20 = false;
      int $i$f$cachex = 0;
      Object it$ivx = var19.rememberedValue();
      int var23/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
      Object var30;
      if (it$ivx == Composer.Companion.getEmpty()) {
         String var17 = "";
         int var24 = 0;
         Function2 var29 = new HelpCenterDestinationKt$helpCenterDestination$7$2$1(null);
         var28 = var17;
         Object var25 = var29;
         var19.updateRememberedValue(var25);
         var30 = var25;
      } else {
         var30 = it$ivx;
      }

      Function2 var18 = (Function2)var30;
      var3.endReplaceGroup();
      EffectsKt.LaunchedEffect(var28, var18, var3, 6);
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
