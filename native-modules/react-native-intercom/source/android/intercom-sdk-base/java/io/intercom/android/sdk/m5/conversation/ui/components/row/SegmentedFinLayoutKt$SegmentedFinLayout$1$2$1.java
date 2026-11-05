package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.blocks.lib.models.Block;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nSegmentedFinLayout.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SegmentedFinLayout.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/SegmentedFinLayoutKt$SegmentedFinLayout$1$2$1\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,168:1\n1869#2,2:169\n*S KotlinDebug\n*F\n+ 1 SegmentedFinLayout.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/SegmentedFinLayoutKt$SegmentedFinLayout$1$2$1\n*L\n89#1:169,2\n*E\n"
)
final class SegmentedFinLayoutKt$SegmentedFinLayout$1$2$1 implements Function2 {
   // $VF: synthetic field
   final int $index;
   // $VF: synthetic field
   final boolean $firstSegmentIsTextRun;
   // $VF: synthetic field
   final Function3 $metadata;
   // $VF: synthetic field
   final ContentSegment $segment;
   // $VF: synthetic field
   final Function3 $textRunBlock;

   SegmentedFinLayoutKt$SegmentedFinLayout$1$2$1(int var1, boolean var2, Function3 var3, ContentSegment var4, Function3 var5) {
      this.$index = var1;
      this.$firstSegmentIsTextRun = var2;
      this.$metadata = var3;
      this.$segment = var4;
      this.$textRunBlock = var5;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C*89@4380L19:SegmentedFinLayout.kt#hr3bi5");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               256662105,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.SegmentedFinLayout.<anonymous>.<anonymous>.<anonymous> (SegmentedFinLayout.kt:85)"
            );
         }

         var1.startReplaceGroup(386095379);
         ComposerKt.sourceInformation(var1, "86@4264L15");
         if (this.$index == 0 && this.$firstSegmentIsTextRun) {
            this.$metadata.invoke(false, var1, 6);
         }

         var1.endReplaceGroup();
         Iterable var3 = ((ContentSegment$TextRun)this.$segment).getBlocks();
         Function3 var4 = this.$textRunBlock;
         int var5 = 0;

         for (Object var7 : var3) {
            Block var8 = (Block)var7;
            int var9 = 0;
            var4.invoke(var8, var1, 0);
         }

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
