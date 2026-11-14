package io.intercom.android.sdk.models;

import androidx.annotation.Nullable;
import io.intercom.android.sdk.utilities.commons.CollectionUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ConversationList$Builder {
   @Nullable
   List conversations;
   @Nullable
   List unread_conversation_ids;
   int total_count = 0;
   @Nullable
   EmptyState empty_state;
   boolean more_pages_available;

   public ConversationList build() {
      List var1 = new ArrayList(CollectionUtils.capacityFor(this.conversations));
      if (this.conversations != null) {
         var1.addAll(this.conversations);
      }

      Set var2 = new HashSet(CollectionUtils.capacityFor(this.unread_conversation_ids));
      if (this.unread_conversation_ids != null) {
         for (java.lang.Object recoveredEntryElement_0_28 : this.unread_conversation_ids) {
            String var4 = (String) recoveredEntryElement_0_28;
            if (var4 != null) {
               var2.add(var4);
            }
         }
      }

      EmptyState var5 = this.empty_state != null ? this.empty_state : EmptyState.Companion.getNULL();
      return ConversationList.create(var1, var2, var5, this.more_pages_available, this.total_count);
   }

   public ConversationList$Builder withConversations(List var1) {
      this.conversations = var1;
      return this;
   }

   public ConversationList$Builder withMorePagesAvailable(boolean var1) {
      this.more_pages_available = var1;
      return this;
   }

   public ConversationList$Builder withEmptyState(EmptyState var1) {
      this.empty_state = var1;
      return this;
   }

   public ConversationList$Builder withUnreadConversationsCount(int var1) {
      this.total_count = var1;
      return this;
   }

   public ConversationList$Builder withUnreadConversationIds(List var1) {
      this.unread_conversation_ids = var1;
      return this;
   }
}
