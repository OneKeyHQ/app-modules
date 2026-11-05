package io.intercom.android.sdk.m5.conversation.usecase;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DebugMetadata(
   f = "SendMediaUseCase.kt",
   l = {94, 100, 116},
   i = {0, 0, 0, 0, 0},
   s = {"L$0", "L$1", "L$2", "L$3", "L$4"},
   n = {"this", "clientState", "uiEffect", "mediaData", "uuid"},
   m = "invoke",
   c = "io.intercom.android.sdk.m5.conversation.usecase.SendMediaUseCase"
)
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class SendMediaUseCase$invoke$1 extends ContinuationImpl {
   Object L$0;
   Object L$1;
   Object L$2;
   Object L$3;
   Object L$4;
   // $VF: synthetic field
   Object result;
   // $VF: synthetic field
   final SendMediaUseCase this$0;
   int label;

   SendMediaUseCase$invoke$1(SendMediaUseCase var1, Continuation var2) {
      super(var2);
      this.this$0 = var1;
   }

   @Nullable
   @Override
   public final Object invokeSuspend(@NotNull Object var1) {
      this.result = var1;
      this.label |= Integer.MIN_VALUE;
      return this.this$0.invoke(null, null, null, null, this);
   }
}
