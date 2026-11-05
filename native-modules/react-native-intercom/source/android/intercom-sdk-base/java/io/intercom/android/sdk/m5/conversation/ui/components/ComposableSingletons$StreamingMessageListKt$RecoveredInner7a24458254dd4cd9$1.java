package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.conversation.states.ContentRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$BubbleMessageRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$BubbleMessageRow$PartWrapper;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$TeamIntroRow;
import io.intercom.android.sdk.m5.conversation.states.GroupingPosition;
import io.intercom.android.sdk.m5.conversation.streaming.StreamingMessageState$Streaming;
import io.intercom.android.sdk.m5.conversation.ui.components.row.BubbleMessageRowKt;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.models.Part$Builder;
import io.intercom.android.sdk.models.StreamingPart;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.flow.StateFlowKt;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$StreamingMessageListKt$RecoveredInner7a24458254dd4cd9$1 implements Function2 {
   public static final ComposableSingletons$StreamingMessageListKt$RecoveredInner7a24458254dd4cd9$1 INSTANCE = new ComposableSingletons$StreamingMessageListKt$RecoveredInner7a24458254dd4cd9$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C546@24477L1371:StreamingMessageList.kt#o19g89");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1846813932,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.ComposableSingletons$StreamingMessageListKt.lambda-2.<anonymous> (StreamingMessageList.kt:546)"
            );
         }

         ContentRow[] var3 = new ContentRow[]{new ContentRow$TeamIntroRow("Our team is here to help you with any questions you have"), null};
         Part var10006 = new Part$Builder().withParticipantIsAdmin(false).withBlocks(CollectionsKt.listOf(BubbleMessageRowKt.getParagraphBlock())).build();
         Intrinsics.checkNotNullExpressionValue(var10006, "build(...)");
         var3[1] = new ContentRow$BubbleMessageRow(
            new ContentRow$BubbleMessageRow$PartWrapper(var10006, true, false, null, null, 24, null),
            GroupingPosition.STANDALONE,
            null,
            false,
            false,
            true,
            null,
            80,
            null
         );
         StreamingMessageListKt.StreamingMessageList(
            CollectionsKt.listOf(var3),
            StateFlowKt.MutableStateFlow(
               new StreamingMessageState$Streaming(
                  "preview-uuid",
                  CollectionsKt.listOf(BubbleMessageRowKt.getParagraphBlock().build()),
                  new StreamingPart(BubbleMessageRowKt.getHumanMetadata()),
                  0
               )
            ),
            StateFlowKt.MutableStateFlow(true),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            false,
            null,
            false,
            null,
            false,
            null,
            var1,
            0,
            0,
            524280
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
