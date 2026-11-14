package io.intercom.android.sdk.m5.conversation.usecase;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DebugMetadata(
   f = "FallbackPollingUseCase.kt",
   l = {32, 38},
   i = {0, 0, 1, 1},
   s = {"L$0", "L$1", "L$0", "L$1"},
   n = {"this", "clientStateFlow", "this", "clientStateFlow"},
   m = "invoke",
   c = "io.intercom.android.sdk.m5.conversation.usecase.FallbackPollingUseCase"
)
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class FallbackPollingUseCase$invoke$1 extends ContinuationImpl {
   Object L$0;
   Object L$1;
   // $VF: synthetic field
   Object result;
   // $VF: synthetic field
   final FallbackPollingUseCase this$0;
   int label;

   FallbackPollingUseCase$invoke$1(FallbackPollingUseCase var1, Continuation var2) {
      super(var2);
      this.this$0 = var1;
   }

   @Nullable
   @Override
   public final Object invokeSuspend(@NotNull Object var1) {
      this.result = var1;
      this.label |= Integer.MIN_VALUE;
      return this.this$0.invoke(null, this);
   }
}
