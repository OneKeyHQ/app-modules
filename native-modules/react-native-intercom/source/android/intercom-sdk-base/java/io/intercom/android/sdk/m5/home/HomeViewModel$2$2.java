package io.intercom.android.sdk.m5.home;

import io.intercom.android.sdk.m5.conversation.data.ParsedNexusEvent$ConversationNexusEvent;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class HomeViewModel$2$2 implements FlowCollector {
   // $VF: synthetic field
   final HomeViewModel this$0;

   HomeViewModel$2$2(HomeViewModel var1) {
      this.this$0 = var1;
   }

   public final Object emit(ParsedNexusEvent$ConversationNexusEvent var1, Continuation var2) {
      HomeViewModel.access$fetchHomeData(this.this$0);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((ParsedNexusEvent$ConversationNexusEvent)var1, var2);
   }
}
