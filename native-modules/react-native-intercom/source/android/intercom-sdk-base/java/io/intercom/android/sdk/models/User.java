package io.intercom.android.sdk.models;

import com.google.gson.annotations.SerializedName;
import io.intercom.android.sdk.utilities.NullSafety;

public class User {
   public static final User NULL = new User();
   @SerializedName("intercom_id")
   private final String intercomId;
   @SerializedName("anonymous_id")
   private final String anonymousId;
   @SerializedName("user_id")
   private final String userId;
   @SerializedName("encrypted_user_id")
   private final String encryptedUserId;
   private final String email;

   public User() {
      this.intercomId = "";
      this.encryptedUserId = "";
      this.anonymousId = "";
      this.userId = "";
      this.email = "";
   }

   User(User$Builder var1) {
      this.intercomId = NullSafety.valueOrEmpty(var1.intercom_id);
      this.encryptedUserId = NullSafety.valueOrEmpty(var1.encrypted_user_id);
      this.anonymousId = NullSafety.valueOrEmpty(var1.anonymous_id);
      this.userId = NullSafety.valueOrEmpty(var1.user_id);
      this.email = NullSafety.valueOrEmpty(var1.email);
   }

   public String getIntercomId() {
      return this.intercomId;
   }

   public String getEncryptedUserId() {
      return this.encryptedUserId;
   }

   public String getUserId() {
      return this.userId;
   }

   public String getEmail() {
      return this.email;
   }

   public String getAnonymousId() {
      return this.anonymousId;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      }

      if (var1 != null && this.getClass() == var1.getClass()) {
         User var2 = (User)var1;
         if (!this.intercomId.equals(var2.intercomId)) {
            return false;
         } else if (!this.encryptedUserId.equals(var2.encryptedUserId)) {
            return false;
         } else if (!this.anonymousId.equals(var2.anonymousId)) {
            return false;
         } else {
            return !this.userId.equals(var2.userId) ? false : this.email.equals(var2.email);
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.intercomId.hashCode();
      var1 = 31 * var1 + this.encryptedUserId.hashCode();
      var1 = 31 * var1 + this.anonymousId.hashCode();
      var1 = 31 * var1 + this.userId.hashCode();
      return 31 * var1 + this.email.hashCode();
   }
}
