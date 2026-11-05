package io.intercom.android.sdk.models;

public class UsersResponse extends BaseResponse {
   private final ConversationList unreadConversations;
   private final UnreadTickets unreadTickets;

   UsersResponse(UsersResponse$Builder var1) {
      super(var1);
      this.unreadConversations = var1.unread_conversations == null ? new ConversationList$Builder().build() : var1.unread_conversations.build();
      this.unreadTickets = var1.unread_tickets == null ? UnreadTickets.Companion.getNULL() : var1.unread_tickets;
   }

   public ConversationList getUnreadConversations() {
      return this.unreadConversations;
   }

   public UnreadTickets getUnreadTickets() {
      return this.unreadTickets;
   }
}
