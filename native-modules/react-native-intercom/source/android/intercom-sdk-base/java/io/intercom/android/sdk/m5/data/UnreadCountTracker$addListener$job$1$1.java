package io.intercom.android.sdk.m5.data;

import io.intercom.android.sdk.UnreadConversationCountListener;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class UnreadCountTracker$addListener$job$1$1 implements FlowCollector {
   // $VF: synthetic field
   final UnreadConversationCountListener $listener;

   UnreadCountTracker$addListener$job$1$1(UnreadConversationCountListener var1) {
      this.$listener = var1;
   }

   public final Object emit(Set var1, Continuation var2) {
      this.$listener.onCountUpdate(var1.size());
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((Set)var1, var2);
   }
}
