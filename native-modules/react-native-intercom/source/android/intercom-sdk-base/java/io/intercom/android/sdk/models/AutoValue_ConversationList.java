package io.intercom.android.sdk.models;

import java.util.List;
import java.util.Set;
import org.jspecify.annotations.Nullable;

final class AutoValue_ConversationList extends ConversationList {
   private final List getConversations;
   private final Set getUnreadConversationIds;
   private final int getUnreadConversationsCount;
   private final EmptyState getEmptyState;
   private final boolean hasMorePages;

   AutoValue_ConversationList(List var1, Set var2, int var3, EmptyState var4, boolean var5) {
      if (var1 == null) {
         throw new NullPointerException("Null getConversations");
      }

      this.getConversations = var1;
      if (var2 == null) {
         throw new NullPointerException("Null getUnreadConversationIds");
      }

      this.getUnreadConversationIds = var2;
      this.getUnreadConversationsCount = var3;
      if (var4 == null) {
         throw new NullPointerException("Null getEmptyState");
      }

      this.getEmptyState = var4;
      this.hasMorePages = var5;
   }

   @Override
   public List getConversations() {
      return this.getConversations;
   }

   @Override
   public Set getUnreadConversationIds() {
      return this.getUnreadConversationIds;
   }

   @Override
   public int getUnreadConversationsCount() {
      return this.getUnreadConversationsCount;
   }

   @Override
   public EmptyState getEmptyState() {
      return this.getEmptyState;
   }

   @Override
   public boolean hasMorePages() {
      return this.hasMorePages;
   }

   @Override
   public String toString() {
      return "ConversationList{getConversations="
         + this.getConversations
         + ", getUnreadConversationIds="
         + this.getUnreadConversationIds
         + ", getUnreadConversationsCount="
         + this.getUnreadConversationsCount
         + ", getEmptyState="
         + this.getEmptyState
         + ", hasMorePages="
         + this.hasMorePages
         + "}";
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (var1 == this) {
         return true;
      }

      if (!(var1 instanceof ConversationList)) {
         return false;
      }

      ConversationList var2 = (ConversationList)var1;
      return this.getConversations.equals(var2.getConversations())
         && this.getUnreadConversationIds.equals(var2.getUnreadConversationIds())
         && this.getUnreadConversationsCount == var2.getUnreadConversationsCount()
         && this.getEmptyState.equals(var2.getEmptyState())
         && this.hasMorePages == var2.hasMorePages();
   }

   @Override
   public int hashCode() {
      int var1 = 1;
      var1 *= 1000003;
      var1 ^= this.getConversations.hashCode();
      var1 *= 1000003;
      var1 ^= this.getUnreadConversationIds.hashCode();
      var1 *= 1000003;
      var1 ^= this.getUnreadConversationsCount;
      var1 *= 1000003;
      var1 ^= this.getEmptyState.hashCode();
      var1 *= 1000003;
      return var1 ^ (this.hasMorePages ? 1231 : 1237);
   }
}
