package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import androidx.compose.runtime.MutableState;
import androidx.compose.ui.text.TextRangeKt;
import androidx.compose.ui.text.input.TextFieldValue;
import io.intercom.android.sdk.m5.conversation.states.ComposerUiEffect;
import io.intercom.android.sdk.m5.conversation.states.ComposerUiEffect$ReportAiAnswer;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class MessageComposerKt$MessageComposer$14$1$1 implements FlowCollector {
   // $VF: synthetic field
   final String $reportAiMessage;
   // $VF: synthetic field
   final MutableState $textFieldValue$delegate;

   MessageComposerKt$MessageComposer$14$1$1(String var1, MutableState var2) {
      this.$reportAiMessage = var1;
      this.$textFieldValue$delegate = var2;
   }

   public final Object emit(ComposerUiEffect var1, Continuation var2) {
      MutableState var10000 = this.$textFieldValue$delegate;
      if (Intrinsics.areEqual(var1, ComposerUiEffect$ReportAiAnswer.INSTANCE)) {
         MessageComposerKt.access$MessageComposer$lambda$27(
            var10000, new TextFieldValue(this.$reportAiMessage, TextRangeKt.TextRange(this.$reportAiMessage.length()), null, 4, null)
         );
         return Unit.INSTANCE;
      } else {
         throw new NoWhenBranchMatchedException();
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((ComposerUiEffect)var1, var2);
   }
}
