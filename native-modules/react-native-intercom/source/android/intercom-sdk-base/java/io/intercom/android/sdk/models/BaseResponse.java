package io.intercom.android.sdk.models;

public abstract class BaseResponse {
   private final Config config;
   private final boolean hasConversations;
   private final User user;

   protected BaseResponse(BaseResponse$Builder var1) {
      this.config = var1.config == null ? new Config() : var1.config;
      this.hasConversations = var1.has_conversations;
      this.user = var1.user == null ? User.NULL : var1.user.build();
   }

   public Config getConfig() {
      return this.config;
   }

   public boolean hasConversations() {
      return this.hasConversations;
   }

   public User getUser() {
      return this.user;
   }
}
