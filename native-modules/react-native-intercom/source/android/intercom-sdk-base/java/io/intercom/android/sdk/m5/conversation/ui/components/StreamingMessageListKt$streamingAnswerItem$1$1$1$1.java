package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.foundation.interaction.DragInteraction.Start;
import androidx.compose.runtime.MutableState;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class StreamingMessageListKt$streamingAnswerItem$1$1$1$1 implements FlowCollector {
   // $VF: synthetic field
   final MutableState $following$delegate;

   StreamingMessageListKt$streamingAnswerItem$1$1$1$1(MutableState var1) {
      this.$following$delegate = var1;
   }

   public final Object emit(Start var1, Continuation var2) {
      StreamingMessageListKt$streamingAnswerItem$1.access$invoke$lambda$5(this.$following$delegate, false);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((Start)var1, var2);
   }
}
