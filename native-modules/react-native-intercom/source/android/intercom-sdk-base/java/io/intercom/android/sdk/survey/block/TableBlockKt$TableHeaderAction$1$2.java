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
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class TableBlockKt$TableHeaderAction$1$2 implements Function2 {
   // $VF: synthetic field
   final int $iconRes;
   // $VF: synthetic field
   final long $tint;

   TableBlockKt$TableHeaderAction$1$2(int var1, long var2) {
      this.$iconRes = var1;
      this.$tint = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C321@15129L24,320@15097L203:TableBlock.kt#jxhu3x");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               297993664, var2, -1, "io.intercom.android.sdk.survey.block.TableHeaderAction.<anonymous>.<anonymous> (TableBlock.kt:320)"
            );
         }

         Painter var3 = PainterResources_androidKt.painterResource(this.$iconRes, var1, 0);
         Modifier var4 = SizeKt.recovered$111ef94c93e78c04(Modifier.Companion, TableBlockKt.access$getHEADER_ICON_SIZE$p());
         IconKt.recovered$e00adffb7fe45b02(var3, null, var4, this.$tint, var1, 432 | Painter.$stable, 0);
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
