package io.intercom.android.sdk.m5.conversation.usecase;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DebugMetadata(
   f = "SendQuickReplyUseCase.kt",
   l = {37, 54},
   i = {0, 0, 0},
   s = {"L$0", "L$1", "L$2"},
   n = {"this", "clientStateFlow", "uuid"},
   m = "invoke",
   c = "io.intercom.android.sdk.m5.conversation.usecase.SendQuickReplyUseCase"
)
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class SendQuickReplyUseCase$invoke$1 extends ContinuationImpl {
   Object L$0;
   Object L$1;
   Object L$2;
   // $VF: synthetic field
   Object result;
   // $VF: synthetic field
   final SendQuickReplyUseCase this$0;
   int label;

   SendQuickReplyUseCase$invoke$1(SendQuickReplyUseCase var1, Continuation var2) {
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
