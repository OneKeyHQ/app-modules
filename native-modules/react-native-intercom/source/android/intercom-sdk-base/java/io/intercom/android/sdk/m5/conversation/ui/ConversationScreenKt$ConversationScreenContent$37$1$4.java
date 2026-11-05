package io.intercom.android.sdk.m5.conversation.ui;

import androidx.compose.foundation.lazy.LazyListState;
import io.intercom.android.sdk.m5.conversation.usecase.ConversationScrolledState;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function1;
import kotlin.ranges.RangesKt;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ConversationScreenKt$ConversationScreenContent$37$1$4 implements FlowCollector {
   // $VF: synthetic field
   final Function1 $onConversationScrolled;
   // $VF: synthetic field
   final LazyListState $lazyListState;
   // $VF: synthetic field
   final boolean $isLandscape;
   // $VF: synthetic field
   final boolean $isLargeFont;

   ConversationScreenKt$ConversationScreenContent$37$1$4(Function1 var1, LazyListState var2, boolean var3, boolean var4) {
      this.$onConversationScrolled = var1;
      this.$lazyListState = var2;
      this.$isLandscape = var3;
      this.$isLargeFont = var4;
   }

   public final Object emit(boolean var1, Continuation var2) {
      Function1 var10000 = this.$onConversationScrolled;
      int var3 = RangesKt.coerceAtLeast(this.$lazyListState.getLayoutInfo().getTotalItemsCount() - 1, 0);
      var10000.invoke(new ConversationScrolledState(var1, this.$isLandscape, this.$isLargeFont, var3));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit(((Boolean)var1).booleanValue(), var2);
   }
}
