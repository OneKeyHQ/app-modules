package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.m5.conversation.streaming.StreamingMessageState$Streaming;
import io.intercom.android.sdk.models.StreamingPart;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.flow.StateFlowKt;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$StreamingMessageContentKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$StreamingMessageContentKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$StreamingMessageContentKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C138@6355L692:StreamingMessageContent.kt#hr3bi5");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               441109385,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$StreamingMessageContentKt.lambda-1.<anonymous> (StreamingMessageContent.kt:138)"
            );
         }

         Block[] var3 = new Block[]{
            new Block$Builder().withType("paragraph").withText("Streaming answers render inside the Fin bubble.").build(),
            new Block$Builder().withType("paragraph").withText("The tail of the last line fades in").build()
         };
         StreamingMessageContentKt.StreamingMessageContent(
            StateFlowKt.MutableStateFlow(
               new StreamingMessageState$Streaming(
                  "preview-uuid", CollectionsKt.listOf(var3), new StreamingPart(StreamingMessageContentKt.access$getPreviewMetadata$p()), 0
               )
            ),
            null,
            false,
            var1,
            0,
            6
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
