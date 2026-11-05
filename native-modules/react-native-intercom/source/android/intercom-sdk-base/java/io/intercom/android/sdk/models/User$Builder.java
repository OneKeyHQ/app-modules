package io.intercom.android.sdk.models;

public final class User$Builder {
   String intercom_id;
   String encrypted_user_id;
   String anonymous_id;
   String user_id;
   String email;
   private Avatar$Builder avatar;

   public User$Builder withIntercomId(String var1) {
      this.intercom_id = var1;
      return this;
   }

   public User$Builder withEncryptedUserId(String var1) {
      this.encrypted_user_id = var1;
      return this;
   }

   public User$Builder withAnonymousId(String var1) {
      this.anonymous_id = var1;
      return this;
   }

   public User$Builder withUserId(String var1) {
      this.user_id = var1;
      return this;
   }

   public User$Builder withEmail(String var1) {
      this.email = var1;
      return this;
   }

   public User$Builder withAvatar(Avatar$Builder var1) {
      this.avatar = var1;
      return this;
   }

   public User build() {
      return new User(this);
   }
}
