package io.intercom.android.sdk.m5.helpcenter.ui;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$HelpCenterLoadingScreenKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$HelpCenterLoadingScreenKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$HelpCenterLoadingScreenKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C46@1528L25:HelpCenterLoadingScreen.kt#ln9mxm");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1677282275,
               var2,
               -1,
               "io.intercom.android.sdk.m5.helpcenter.ui.ComposableSingletons$HelpCenterLoadingScreenKt.lambda-1.<anonymous> (HelpCenterLoadingScreen.kt:46)"
            );
         }

         HelpCenterLoadingScreenKt.HelpCenterLoadingScreen(null, var1, 0, 1);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}
