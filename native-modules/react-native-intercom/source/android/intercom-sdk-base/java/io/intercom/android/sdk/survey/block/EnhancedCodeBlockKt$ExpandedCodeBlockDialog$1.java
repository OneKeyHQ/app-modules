package io.intercom.android.sdk.survey.block;

import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.WindowInsets;
import androidx.compose.foundation.layout.WindowInsetsPaddingKt;
import androidx.compose.foundation.layout.WindowInsets_androidKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.blocks.lib.models.Block;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class EnhancedCodeBlockKt$ExpandedCodeBlockDialog$1 implements Function2 {
   // $VF: synthetic field
   final Function0 $onDismiss;
   // $VF: synthetic field
   final Block $block;

   EnhancedCodeBlockKt$ExpandedCodeBlockDialog$1(Function0 var1, Block var2) {
      this.$onDismiss = var1;
      this.$block = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C362@13870L11,358@13678L295:EnhancedCodeBlock.kt#jxhu3x");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -851181400, var2, -1, "io.intercom.android.sdk.survey.block.ExpandedCodeBlockDialog.<anonymous> (EnhancedCodeBlock.kt:358)"
            );
         }

         CodeBlockMode$Expanded var3 = new CodeBlockMode$Expanded(this.$onDismiss);
         Modifier var4 = SizeKt.fillMaxSize$default(
            PaddingKt.recovered$eb6a744cd98cdbca(
               WindowInsetsPaddingKt.windowInsetsPadding(Modifier.Companion, WindowInsets_androidKt.getSafeDrawing(WindowInsets.Companion, var1, 6)),
               EnhancedCodeBlockKt.access$getEXPANDED_DIALOG_MARGIN$p()
            ),
            0.0F,
            1,
            null
         );
         EnhancedCodeBlockKt.EnhancedCodeBlock(this.$block, var4, var3, null, var1, 0, 8);
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
