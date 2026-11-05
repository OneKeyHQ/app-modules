package io.intercom.android.sdk.m5.conversation.usecase;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DebugMetadata(
   f = "SendMessageUseCase.kt",
   l = {76, 109, 133, 142, 159},
   i = {0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3},
   s = {"L$0", "L$1", "L$2", "L$0", "L$1", "L$2", "L$3", "L$0", "L$1", "L$2", "L$3", "L$0", "L$1", "L$2"},
   n = {
         "this",
         "clientState",
         "clientUUID",
         "this",
         "clientState",
         "clientUUID",
         "conversationResponse",
         "this",
         "clientState",
         "blocks",
         "clientUUID",
         "this",
         "clientState",
         "clientUUID"
   },
   m = "invoke",
   c = "io.intercom.android.sdk.m5.conversation.usecase.SendMessageUseCase"
)
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class SendMessageUseCase$invoke$2 extends ContinuationImpl {
   Object L$0;
   Object L$1;
   Object L$2;
   Object L$3;
   // $VF: synthetic field
   Object result;
   // $VF: synthetic field
   final SendMessageUseCase this$0;
   int label;

   SendMessageUseCase$invoke$2(SendMessageUseCase var1, Continuation var2) {
      super(var2);
      this.this$0 = var1;
   }

   @Nullable
   @Override
   public final Object invokeSuspend(@NotNull Object var1) {
      this.result = var1;
      this.label |= Integer.MIN_VALUE;
      return this.this$0.invoke(null, null, null, this);
   }
}
