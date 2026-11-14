package io.intercom.android.sdk.models;

import androidx.annotation.Nullable;

public final class ConversationsResponse$Builder extends BaseResponse$Builder {
   @Nullable
   ConversationList$Builder conversation_page;

   public ConversationsResponse build() {
      return new ConversationsResponse(this);
   }

   public ConversationsResponse$Builder withConversationPage(ConversationList$Builder var1) {
      this.conversation_page = var1;
      return this;
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
