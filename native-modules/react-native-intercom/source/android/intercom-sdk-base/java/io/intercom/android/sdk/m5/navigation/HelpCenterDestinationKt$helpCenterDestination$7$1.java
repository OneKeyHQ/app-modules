package io.intercom.android.sdk.m5.navigation;

import androidx.activity.ComponentActivity;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.MutableIntState;
import androidx.navigation.NavHostController;
import io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel;
import io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterScreenKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nHelpCenterDestination.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HelpCenterDestination.kt\nio/intercom/android/sdk/m5/navigation/HelpCenterDestinationKt$helpCenterDestination$7$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,117:1\n1047#2,6:118\n*S KotlinDebug\n*F\n+ 1 HelpCenterDestination.kt\nio/intercom/android/sdk/m5/navigation/HelpCenterDestinationKt$helpCenterDestination$7$1\n*L\n103#1:118,6\n*E\n"
)
final class HelpCenterDestinationKt$helpCenterDestination$7$1 implements Function2 {
   // $VF: synthetic field
   final HelpCenterViewModel $viewModel;
   // $VF: synthetic field
   final IntercomRootActivityArgs $intercomRootActivityArgs;
   // $VF: synthetic field
   final NavHostController $navController;
   // $VF: synthetic field
   final ComponentActivity $rootActivity;
   // $VF: synthetic field
   final MutableIntState $navIcon;

   HelpCenterDestinationKt$helpCenterDestination$7$1(
      HelpCenterViewModel var1, IntercomRootActivityArgs var2, NavHostController var3, ComponentActivity var4, MutableIntState var5
   ) {
      this.$viewModel = var1;
      this.$intercomRootActivityArgs = var2;
      this.$navController = var3;
      this.$rootActivity = var4;
      this.$navIcon = var5;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C102@4358L143,92@3859L701:HelpCenterDestination.kt#m1zs2i");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1743507320, var2, -1, "io.intercom.android.sdk.m5.navigation.helpCenterDestination.<anonymous>.<anonymous> (HelpCenterDestination.kt:92)"
            );
         }

         HelpCenterViewModel var10000 = this.$viewModel;
         IntercomRootActivityArgs var3 = this.$intercomRootActivityArgs;
         List var10001 = var3 instanceof IntercomRootActivityArgs$HelpCenterCollectionsArgs
            ? ((IntercomRootActivityArgs$HelpCenterCollectionsArgs)this.$intercomRootActivityArgs).getCollectionIds()
            : (
               var3 instanceof IntercomRootActivityArgs$HelpCenterCollectionArgs
                  ? CollectionsKt.listOf(((IntercomRootActivityArgs$HelpCenterCollectionArgs)this.$intercomRootActivityArgs).getCollectionId())
                  : CollectionsKt.emptyList()
            );
         var1.startReplaceGroup(848858173);
         ComposerKt.sourceInformation(var1, "CC(remember):HelpCenterDestination.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = var1.changedInstance(this.$navController) | var1.changedInstance(this.$rootActivity);
         NavHostController var6 = this.$navController;
         ComponentActivity var7 = this.$rootActivity;
         int var8 = 0;
         Object var9 = var4.rememberedValue();
         int var10 = 0;
         Object var10002;
         if (!var5 && var9 != Composer.Companion.getEmpty()) {
            var10002 = var9;
         } else {
            List var14 = var10001;
            HelpCenterViewModel var13 = var10000;
            int var11 = 0;
            final androidx.navigation.NavHostController recoveredCapture_1030_0 = var6;
            final androidx.activity.ComponentActivity recoveredCapture_1030_1 = var7;
            Function0 var15 = ((kotlin.jvm.functions.Function0) () -> HelpCenterDestinationKt$helpCenterDestination$7$1.invoke$lambda$1$lambda$0(recoveredCapture_1030_0, recoveredCapture_1030_1));
            var10000 = var13;
            var10001 = var14;
            Object var12 = var15;
            var4.updateRememberedValue(var12);
            var10002 = var12;
         }

         Function0 var16 = (Function0)var10002;
         var1.endReplaceGroup();
         HelpCenterScreenKt.HelpCenterScreen(var10000, var10001, var16, this.$navIcon.getIntValue(), var1, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(NavHostController var0, ComponentActivity var1) {
      if (var0.getPreviousBackStackEntry() == null) {
         var1.finish();
      } else {
         var0.navigateUp();
      }

      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}
