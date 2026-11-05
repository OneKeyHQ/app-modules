package io.intercom.android.sdk.m5.helpcenter.ui;

import android.content.Context;
import android.content.Intent;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.navigation.NavHostController;
import io.intercom.android.sdk.helpcenter.search.IntercomArticleSearchActivity;
import io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel;
import io.intercom.android.sdk.m5.helpcenter.ui.components.HelpCenterTopBarKt;
import io.intercom.android.sdk.ui.R.drawable;
import io.intercom.android.sdk.ui.common.StringProvider;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nHelpCenterScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HelpCenterScreen.kt\nio/intercom/android/sdk/m5/helpcenter/ui/HelpCenterScreenKt$HelpCenterContent$1$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,259:1\n1047#2,6:260\n1047#2,6:266\n*S KotlinDebug\n*F\n+ 1 HelpCenterScreen.kt\nio/intercom/android/sdk/m5/helpcenter/ui/HelpCenterScreenKt$HelpCenterContent$1$1\n*L\n130#1:260,6\n137#1:266,6\n*E\n"
)
final class HelpCenterScreenKt$HelpCenterContent$1$1 implements Function2 {
   // $VF: synthetic field
   final NavHostController $navController;
   // $VF: synthetic field
   final Function0 $onCloseClick;
   // $VF: synthetic field
   final Context $context;
   // $VF: synthetic field
   final int $navIcon;
   // $VF: synthetic field
   final HelpCenterViewModel $viewModel;

   HelpCenterScreenKt$HelpCenterContent$1$1(NavHostController var1, Function0 var2, Context var3, int var4, HelpCenterViewModel var5) {
      this.$navController = var1;
      this.$onCloseClick = var2;
      this.$context = var3;
      this.$navIcon = var4;
      this.$viewModel = var5;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C129@4463L256,136@4757L286,128@4411L927:HelpCenterScreen.kt#ln9mxm");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               375288220, var2, -1, "io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterContent.<anonymous>.<anonymous> (HelpCenterScreen.kt:128)"
            );
         }

         var1.startReplaceGroup(-969970035);
         ComposerKt.sourceInformation(var1, "CC(remember):HelpCenterScreen.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = var1.changedInstance(this.$navController) | var1.changed(this.$onCloseClick);
         NavHostController var6 = this.$navController;
         Function0 var7 = this.$onCloseClick;
         int var8 = 0;
         Object var9 = var4.rememberedValue();
         int var10 = 0;
         Object var10000;
         if (!var5 && var9 != Composer.Companion.getEmpty()) {
            var10000 = (Function0)var9;
         } else {
            int var11 = 0;
            final androidx.navigation.NavHostController recoveredCapture_839_0 = var6;
            final kotlin.jvm.functions.Function0 recoveredCapture_839_1 = var7;
            Object var12 = ((kotlin.jvm.functions.Function0) () -> HelpCenterScreenKt$HelpCenterContent$1$1.invoke$lambda$1$lambda$0(recoveredCapture_839_0, recoveredCapture_839_1));
            var4.updateRememberedValue(var12);
            var10000 = (Function0)var12;
         }

         Function0 var3 = (kotlin.jvm.functions.Function0) var10000;
         var1.endReplaceGroup();
         var10000 = var3;
         var1.startReplaceGroup(-969960597);
         ComposerKt.sourceInformation(var1, "CC(remember):HelpCenterScreen.kt#9igjgp");
         var4 = var1;
         var5 = var1.changedInstance(this.$context);
         Context var17 = this.$context;
         int $i$f$cachex = 0;
         Object recoveredSecondRemembered = var4.rememberedValue();
         int recoveredSecondCacheMarker = 0;
         Object var24;
         if (!var5 && recoveredSecondRemembered != Composer.Companion.getEmpty()) {
            var24 = recoveredSecondRemembered;
         } else {
            Function0 var13 = var3;
            var10 = 0;
            final android.content.Context recoveredCapture_838_0 = var17;
            var24 = ((kotlin.jvm.functions.Function0) () -> HelpCenterScreenKt$HelpCenterContent$1$1.invoke$lambda$3$lambda$2(recoveredCapture_838_0));
            var10000 = var13;
            Object var22 = var24;
            var4.updateRememberedValue(var22);
            var24 = (Function0)var22;
         }

         var3 = (Function0)var24;
         var1.endReplaceGroup();
         HelpCenterTopBarKt.HelpCenterTopBar(
            (Function0)var10000,
            var3,
            this.$navController.getPreviousBackStackEntry() == null ? this.$navIcon : drawable.intercom_ic_back,
            this.$viewModel.getScreenTitle(),
            var1,
            StringProvider.$stable << 9,
            0
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(NavHostController var0, Function0 var1) {
      if (var0.getPreviousBackStackEntry() == null) {
         var1.invoke();
      } else {
         var0.navigateUp();
      }

      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$3$lambda$2(Context var0) {
      Intent var1 = IntercomArticleSearchActivity.Companion.buildIntent(var0, false);
      var0.startActivity(var1);
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
