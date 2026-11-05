package io.intercom.android.sdk.models;

import com.google.auto.value.AutoValue;
import java.util.List;
import java.util.Set;

@AutoValue
public abstract class ConversationList {
   public static ConversationList create(List var0, Set var1, EmptyState var2, boolean var3, int var4) {
      return new AutoValue_ConversationList(var0, var1, var4, var2, var3);
   }

   public abstract List getConversations();

   public abstract Set getUnreadConversationIds();

   public abstract int getUnreadConversationsCount();

   public abstract EmptyState getEmptyState();

   public abstract boolean hasMorePages();
}
