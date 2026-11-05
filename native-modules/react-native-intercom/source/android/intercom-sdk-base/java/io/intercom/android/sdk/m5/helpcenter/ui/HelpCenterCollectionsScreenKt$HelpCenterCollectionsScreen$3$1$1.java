package io.intercom.android.sdk.m5.helpcenter.ui;

import androidx.compose.foundation.lazy.LazyItemScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionsUiState;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionsUiState$Error;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class HelpCenterCollectionsScreenKt$HelpCenterCollectionsScreen$3$1$1 implements Function3 {
   // $VF: synthetic field
   final CollectionsUiState $uiState;

   HelpCenterCollectionsScreenKt$HelpCenterCollectionsScreen$3$1$1(CollectionsUiState var1) {
      this.$uiState = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(LazyItemScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$item");
      ComposerKt.sourceInformation(var2, "C65@2680L163:HelpCenterCollectionsScreen.kt#ln9mxm");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changed(var1) ? 4 : 2;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -2086280874,
               var4,
               -1,
               "io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterCollectionsScreen.<anonymous>.<anonymous>.<anonymous> (HelpCenterCollectionsScreen.kt:65)"
            );
         }

         Modifier var5 = LazyItemScope.fillParentMaxHeight$default(var1, Modifier.Companion, 0.0F, 1, null);
         HelpCenterErrorScreenKt.HelpCenterErrorScreen(((CollectionsUiState$Error)this.$uiState).getErrorState(), var5, var2, 0, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((LazyItemScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
