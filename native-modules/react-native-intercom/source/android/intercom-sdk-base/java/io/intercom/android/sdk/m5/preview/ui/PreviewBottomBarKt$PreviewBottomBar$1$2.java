package io.intercom.android.sdk.m5.preview.ui;

import androidx.compose.foundation.layout.RowScope;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.preview.model.PreviewUiState;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class PreviewBottomBarKt$PreviewBottomBar$1$2 implements Function3 {
   // $VF: synthetic field
   final PreviewUiState $uiState;

   PreviewBottomBarKt$PreviewBottomBar$1$2(PreviewUiState var1) {
      this.$uiState = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(RowScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$Button");
      ComposerKt.sourceInformation(var2, "C76@2996L37:PreviewBottomBar.kt#hhbc0c");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1096022047, var3, -1, "io.intercom.android.sdk.m5.preview.ui.PreviewBottomBar.<anonymous>.<anonymous> (PreviewBottomBar.kt:76)"
            );
         }

         TextKt.recovered$5b114ab32a4f4190(
            this.$uiState.getConfirmationText(), null, 0L, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, var2, 0, 0, 262142
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((RowScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
