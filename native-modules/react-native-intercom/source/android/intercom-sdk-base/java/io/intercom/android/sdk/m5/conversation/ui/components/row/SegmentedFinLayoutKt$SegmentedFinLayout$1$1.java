package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class SegmentedFinLayoutKt$SegmentedFinLayout$1$1 implements Function2 {
   // $VF: synthetic field
   final Function3 $metadata;
   // $VF: synthetic field
   final boolean $leadsWithTable;

   SegmentedFinLayoutKt$SegmentedFinLayout$1$1(Function3 var1, boolean var2) {
      this.$metadata = var1;
      this.$leadsWithTable = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C76@3877L24:SegmentedFinLayout.kt#hr3bi5");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -354473133,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.SegmentedFinLayout.<anonymous>.<anonymous> (SegmentedFinLayout.kt:76)"
            );
         }

         this.$metadata.invoke(this.$leadsWithTable, var1, 0);
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
