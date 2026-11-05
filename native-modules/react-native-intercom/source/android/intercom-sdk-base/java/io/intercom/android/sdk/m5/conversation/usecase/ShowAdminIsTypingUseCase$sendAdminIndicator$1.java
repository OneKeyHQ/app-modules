package io.intercom.android.sdk.m5.conversation.usecase;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DebugMetadata(
   f = "ShowAdminIsTypingUseCase.kt",
   l = 49,
   i = 0,
   s = "L$0",
   n = "clientStateFlow",
   m = "sendAdminIndicator",
   c = "io.intercom.android.sdk.m5.conversation.usecase.ShowAdminIsTypingUseCase"
)
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ShowAdminIsTypingUseCase$sendAdminIndicator$1 extends ContinuationImpl {
   Object L$0;
   // $VF: synthetic field
   Object result;
   // $VF: synthetic field
   final ShowAdminIsTypingUseCase this$0;
   int label;

   ShowAdminIsTypingUseCase$sendAdminIndicator$1(ShowAdminIsTypingUseCase var1, Continuation var2) {
      super(var2);
      this.this$0 = var1;
   }

   @Nullable
   @Override
   public final Object invokeSuspend(@NotNull Object var1) {
      this.result = var1;
      this.label |= Integer.MIN_VALUE;
      return ShowAdminIsTypingUseCase.access$sendAdminIndicator(this.this$0, null, null, false, false, this);
   }
}
