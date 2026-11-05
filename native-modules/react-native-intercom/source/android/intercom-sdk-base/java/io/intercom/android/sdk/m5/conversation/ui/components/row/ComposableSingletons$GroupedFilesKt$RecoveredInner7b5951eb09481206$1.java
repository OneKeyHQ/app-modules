package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.conversation.states.GroupingPosition;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$GroupedFilesKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$GroupedFilesKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$GroupedFilesKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C69@2359L167:GroupedFiles.kt#hr3bi5");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1293390519,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$GroupedFilesKt.lambda-1.<anonymous> (GroupedFiles.kt:69)"
            );
         }

         GroupedFilesKt.GroupedFiles(
            CollectionsKt.listOf(BubbleMessageRowKt.getAttachmentBlock().build()),
            BubbleMessageRowKt.recovered$f595cb41dc6eb0a1(GroupingPosition.STANDALONE, 0.0F, 0.0F, 6, null),
            null,
            null,
            var1,
            0,
            12
         );
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
