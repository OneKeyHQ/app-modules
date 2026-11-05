package io.intercom.android.sdk.m5.conversation;

import io.intercom.android.sdk.m5.conversation.states.ConversationClientState;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.flow.FlowCollector;
import kotlinx.coroutines.flow.MutableStateFlow;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nConversationViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationViewModel.kt\nio/intercom/android/sdk/m5/conversation/ConversationViewModel$10$1\n+ 2 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n*L\n1#1,864:1\n230#2,5:865\n*S KotlinDebug\n*F\n+ 1 ConversationViewModel.kt\nio/intercom/android/sdk/m5/conversation/ConversationViewModel$10$1\n*L\n423#1:865,5\n*E\n"
)
final class ConversationViewModel$10$1 implements FlowCollector {
   // $VF: synthetic field
   final ConversationViewModel this$0;

   ConversationViewModel$10$1(ConversationViewModel var1) {
      this.this$0 = var1;
   }

   public final Object emit(List var1, Continuation var2) {
      MutableStateFlow var3 = ConversationViewModel.access$getClientState$p(this.this$0);
      int var4 = 0;

      Object var5;
      ConversationClientState var8;
      do {
         var5 = var3.getValue();
         var8 = (ConversationClientState)var5;
         int var7 = 0;
         var8 = ConversationClientState.copy$default(
            (ConversationClientState)var8,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            0,
            0,
            null,
            null,
            false,
            false,
            null,
            null,
            var1,
            4194303,
            null
         );
      } while (!var3.compareAndSet(var5, var8));

      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((List)var1, var2);
   }
}
