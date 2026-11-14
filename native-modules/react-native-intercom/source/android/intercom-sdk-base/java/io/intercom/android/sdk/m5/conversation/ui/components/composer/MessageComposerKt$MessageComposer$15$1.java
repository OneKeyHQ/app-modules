package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import androidx.compose.runtime.MutableState;
import androidx.compose.ui.text.TextRangeKt;
import androidx.compose.ui.text.input.TextFieldValue;
import io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState;
import io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState$Completed;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(
   f = "MessageComposer.kt",
   l = {},
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.ui.components.composer.MessageComposerKt$MessageComposer$15$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class MessageComposerKt$MessageComposer$15$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final VoiceTranscriptionState $voiceTranscriptionState;
   // $VF: synthetic field
   final Function0 $onTranscribedTextConsumed;
   // $VF: synthetic field
   final MutableState $textFieldValue$delegate;

   MessageComposerKt$MessageComposer$15$1(VoiceTranscriptionState var1, Function0 var2, MutableState var3, Continuation var4) {
      super(2, var4);
      this.$voiceTranscriptionState = var1;
      this.$onTranscribedTextConsumed = var2;
      this.$textFieldValue$delegate = var3;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            VoiceTranscriptionState var2 = this.$voiceTranscriptionState;
            if (var2 instanceof VoiceTranscriptionState$Completed) {
               MessageComposerKt.access$MessageComposer$lambda$27(
                  this.$textFieldValue$delegate,
                  new TextFieldValue(
                     ((VoiceTranscriptionState$Completed)var2).getText(),
                     TextRangeKt.TextRange(((VoiceTranscriptionState$Completed)var2).getText().length()),
                     null,
                     4,
                     null
                  )
               );
               this.$onTranscribedTextConsumed.invoke();
            }

            return Unit.INSTANCE;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new MessageComposerKt$MessageComposer$15$1(this.$voiceTranscriptionState, this.$onTranscribedTextConsumed, this.$textFieldValue$delegate, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((MessageComposerKt$MessageComposer$15$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
