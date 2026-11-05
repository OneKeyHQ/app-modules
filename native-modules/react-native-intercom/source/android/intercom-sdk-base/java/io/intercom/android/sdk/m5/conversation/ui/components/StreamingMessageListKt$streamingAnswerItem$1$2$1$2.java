package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.runtime.MutableState;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class StreamingMessageListKt$streamingAnswerItem$1$2$1$2 implements FlowCollector {
   // $VF: synthetic field
   final MutableState $following$delegate;

   StreamingMessageListKt$streamingAnswerItem$1$2$1$2(MutableState var1) {
      this.$following$delegate = var1;
   }

   public final Object emit(boolean var1, Continuation var2) {
      if (var1) {
         StreamingMessageListKt$streamingAnswerItem$1.access$invoke$lambda$5(this.$following$delegate, true);
      }

      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit(((Boolean)var1).booleanValue(), var2);
   }
}
