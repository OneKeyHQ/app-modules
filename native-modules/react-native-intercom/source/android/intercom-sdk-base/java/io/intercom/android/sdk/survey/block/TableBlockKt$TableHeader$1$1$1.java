package io.intercom.android.sdk.survey.block;

import androidx.compose.animation.AnimatedVisibilityScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.ui.R.drawable;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class TableBlockKt$TableHeader$1$1$1 implements Function3 {
   // $VF: synthetic field
   final long $iconTint;
   // $VF: synthetic field
   final Function0 $onExpand;
   // $VF: synthetic field
   final float $actionLayoutHeight;

   TableBlockKt$TableHeader$1$1$1(long var1, Function0 var3, float var4) {
      this.$iconTint = var1;
      this.$onExpand = var3;
      this.$actionLayoutHeight = var4;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(AnimatedVisibilityScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$AnimatedVisibility");
      ComposerKt.sourceInformation(var2, "C277@13750L305:TableBlock.kt#jxhu3x");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            -2123260731, var3, -1, "io.intercom.android.sdk.survey.block.TableHeader.<anonymous>.<anonymous>.<anonymous> (TableBlock.kt:277)"
         );
      }

      TableBlockKt.recovered$1d9343dec5b3a765(drawable.intercom_ic_expand, "Expand table", this.$iconTint, this.$onExpand, this.$actionLayoutHeight, var2, 48);
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((AnimatedVisibilityScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
