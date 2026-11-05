package io.intercom.android.sdk.models;

public class ConversationsResponse extends BaseResponse {
   private final ConversationList conversationPage;

   ConversationsResponse(ConversationsResponse$Builder var1) {
      super(var1);
      this.conversationPage = var1.conversation_page == null ? new ConversationList$Builder().build() : var1.conversation_page.build();
   }

   public ConversationList getConversationPage() {
      return this.conversationPage;
   }
}
