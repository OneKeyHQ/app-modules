package io.intercom.android.sdk.survey.block;

import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.material3.IconKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.MutableState;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.res.PainterResources_androidKt;
import io.intercom.android.sdk.ui.R.drawable;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class EnhancedCodeBlockKt$CodeBlockHeader$2$3 implements Function2 {
   // $VF: synthetic field
   final long $iconTint;
   // $VF: synthetic field
   final MutableState $copied$delegate;

   EnhancedCodeBlockKt$CodeBlockHeader$2$3(long var1, MutableState var3) {
      this.$iconTint = var1;
      this.$copied$delegate = var3;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C296@11571L133,295@11539L316:EnhancedCodeBlock.kt#jxhu3x");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1911786756, var2, -1, "io.intercom.android.sdk.survey.block.CodeBlockHeader.<anonymous>.<anonymous> (EnhancedCodeBlock.kt:295)"
            );
         }

         Painter var3 = PainterResources_androidKt.painterResource(
            EnhancedCodeBlockKt.access$CodeBlockHeader_RIQooxk$lambda$12(this.$copied$delegate) ? drawable.intercom_ic_check : drawable.intercom_ic_copy,
            var1,
            0
         );
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
