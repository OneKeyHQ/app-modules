package io.intercom.android.sdk.survey.block;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.models.InlineSource;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class TextBlockKt$TextBlock$2$3$1 implements Function3 {
   // $VF: synthetic field
   final InlineSource $inlineSource;

   TextBlockKt$TextBlock$2$3$1(InlineSource var1) {
      this.$inlineSource = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(String var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "it");
      ComposerKt.sourceInformation(var2, "C97@4062L102:TextBlock.kt#jxhu3x");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -804032742, var3, -1, "io.intercom.android.sdk.survey.block.TextBlock.<anonymous>.<anonymous>.<anonymous> (TextBlock.kt:97)"
            );
         }

         TextBlockKt.recovered$bc276efe291e8160(this.$inlineSource.getDataSourceIndex(), null, 0L, var2, 0, 6);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((String)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
