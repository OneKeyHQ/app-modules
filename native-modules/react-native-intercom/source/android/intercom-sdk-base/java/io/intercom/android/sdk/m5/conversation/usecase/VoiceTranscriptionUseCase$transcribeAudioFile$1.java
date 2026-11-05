package io.intercom.android.sdk.m5.conversation.usecase;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DebugMetadata(
   f = "VoiceTranscriptionUseCase.kt",
   l = 98,
   i = {0, 0},
   s = {"L$0", "L$1"},
   n = {"this", "clientState"},
   m = "transcribeAudioFile",
   c = "io.intercom.android.sdk.m5.conversation.usecase.VoiceTranscriptionUseCase"
)
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class VoiceTranscriptionUseCase$transcribeAudioFile$1 extends ContinuationImpl {
   Object L$0;
   Object L$1;
   // $VF: synthetic field
   Object result;
   // $VF: synthetic field
   final VoiceTranscriptionUseCase this$0;
   int label;

   VoiceTranscriptionUseCase$transcribeAudioFile$1(VoiceTranscriptionUseCase var1, Continuation var2) {
      super(var2);
      this.this$0 = var1;
   }

   @Nullable
   @Override
   public final Object invokeSuspend(@NotNull Object var1) {
      this.result = var1;
      this.label |= Integer.MIN_VALUE;
      return VoiceTranscriptionUseCase.access$transcribeAudioFile(this.this$0, null, null, this);
   }
}
