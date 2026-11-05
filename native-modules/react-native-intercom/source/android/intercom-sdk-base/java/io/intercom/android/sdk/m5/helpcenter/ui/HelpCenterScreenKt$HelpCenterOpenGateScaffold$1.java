package io.intercom.android.sdk.m5.helpcenter.ui;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.helpcenter.ui.components.HelpCenterTopBarKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nHelpCenterScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HelpCenterScreen.kt\nio/intercom/android/sdk/m5/helpcenter/ui/HelpCenterScreenKt$HelpCenterOpenGateScaffold$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,259:1\n1047#2,6:260\n*S KotlinDebug\n*F\n+ 1 HelpCenterScreen.kt\nio/intercom/android/sdk/m5/helpcenter/ui/HelpCenterScreenKt$HelpCenterOpenGateScaffold$1\n*L\n96#1:260,6\n*E\n"
)
final class HelpCenterScreenKt$HelpCenterOpenGateScaffold$1 implements Function2 {
   // $VF: synthetic field
   final Function0 $onCloseClick;
   // $VF: synthetic field
   final int $navIcon;

   HelpCenterScreenKt$HelpCenterOpenGateScaffold$1(Function0 var1, int var2) {
      this.$onCloseClick = var1;
      this.$navIcon = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C95@3546L2,93@3452L146:HelpCenterScreen.kt#ln9mxm");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1941106521, var2, -1, "io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterOpenGateScaffold.<anonymous> (HelpCenterScreen.kt:93)"
            );
         }

         Function0 var10000 = this.$onCloseClick;
         var1.startReplaceGroup(-1991364586);
         ComposerKt.sourceInformation(var1, "CC(remember):HelpCenterScreen.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         kotlin.jvm.functions.Function0 var12;
         if (var7 == Composer.Companion.getEmpty()) {
            Function0 var10 = var10000;
            int var9 = 0;
            var12 = ((kotlin.jvm.functions.Function0) () -> HelpCenterScreenKt$HelpCenterOpenGateScaffold$1.invoke$lambda$1$lambda$0());
            var10000 = var10;
            Object var11 = var12;
            var4.updateRememberedValue(var11);
            var12 = (Function0)var11;
         } else {
            var12 = (Function0)var7;
         }

         Function0 var3 = var12;
         var1.endReplaceGroup();
         HelpCenterTopBarKt.HelpCenterTopBar(var10000, var3, this.$navIcon, null, var1, 48, 8);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0() {
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
