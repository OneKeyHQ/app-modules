package io.intercom.android.sdk.models;

import androidx.annotation.Nullable;

public class UsersResponse$Builder extends BaseResponse$Builder {
   @Nullable
   ConversationList$Builder unread_conversations;
   @Nullable
   UnreadTickets unread_tickets;

   public UsersResponse build() {
      return new UsersResponse(this);
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
