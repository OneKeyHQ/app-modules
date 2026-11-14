package io.intercom.android.sdk.m5.conversation;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ConversationViewModel$9$3 implements FlowCollector {
   // $VF: synthetic field
   final ConversationViewModel this$0;

   ConversationViewModel$9$3(ConversationViewModel var1) {
      this.this$0 = var1;
   }

   public final Object emit(String var1, Continuation var2) {
      Object var10000 = ConversationViewModel.access$getLoadGifUseCase$p(this.this$0)
         .invoke(ConversationViewModel.access$getClientState$p(this.this$0), var1, var2);
      return var10000 == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? var10000 : Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((String)var1, var2);
   }
}
