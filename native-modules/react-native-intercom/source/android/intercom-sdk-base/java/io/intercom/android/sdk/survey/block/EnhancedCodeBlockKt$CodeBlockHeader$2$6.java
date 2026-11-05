package io.intercom.android.sdk.survey.block;

import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.material3.IconKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.res.PainterResources_androidKt;
import io.intercom.android.sdk.ui.R.drawable;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class EnhancedCodeBlockKt$CodeBlockHeader$2$6 implements Function2 {
   // $VF: synthetic field
   final long $iconTint;

   EnhancedCodeBlockKt$CodeBlockHeader$2$6(long var1) {
      this.$iconTint = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C336@13115L47,335@13075L270:EnhancedCodeBlock.kt#jxhu3x");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -955926597, var2, -1, "io.intercom.android.sdk.survey.block.CodeBlockHeader.<anonymous>.<anonymous> (EnhancedCodeBlock.kt:335)"
            );
         }

         Painter var3 = PainterResources_androidKt.painterResource(drawable.intercom_ic_close, var1, 0);
         Modifier var4 = SizeKt.recovered$111ef94c93e78c04(Modifier.Companion, EnhancedCodeBlockKt.access$getHEADER_ICON_SIZE$p());
         IconKt.recovered$e00adffb7fe45b02(var3, null, var4, this.$iconTint, var1, 432 | Painter.$stable, 0);
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
