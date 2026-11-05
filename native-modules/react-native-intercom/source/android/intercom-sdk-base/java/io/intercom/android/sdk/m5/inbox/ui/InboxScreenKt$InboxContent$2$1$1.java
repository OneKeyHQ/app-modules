package io.intercom.android.sdk.m5.inbox.ui;

import androidx.paging.compose.LazyPagingItems;
import io.intercom.android.sdk.m5.inbox.states.InboxUiEffects;
import io.intercom.android.sdk.m5.inbox.states.InboxUiEffects$NavigateToConversation;
import io.intercom.android.sdk.m5.inbox.states.InboxUiEffects$RefreshInbox;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function1;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class InboxScreenKt$InboxContent$2$1$1 implements FlowCollector {
   // $VF: synthetic field
   final Function1 $onConversationClicked;
   // $VF: synthetic field
   final LazyPagingItems $lazyPagingItems;

   InboxScreenKt$InboxContent$2$1$1(Function1 var1, LazyPagingItems var2) {
      this.$onConversationClicked = var1;
      this.$lazyPagingItems = var2;
   }

   public final Object emit(InboxUiEffects var1, Continuation var2) {
      InboxUiEffects var3 = var1;
      if (var3 instanceof InboxUiEffects$NavigateToConversation) {
         this.$onConversationClicked.invoke(var1);
      } else {
         if (!(var3 instanceof InboxUiEffects$RefreshInbox)) {
            throw new NoWhenBranchMatchedException();
         }

         this.$lazyPagingItems.refresh();
      }

      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((InboxUiEffects)var1, var2);
   }
}
