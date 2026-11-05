package io.intercom.android.sdk.identity;

import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.UserAttributes;
import io.intercom.android.sdk.logger.LumberMill;

public class Registration {
   private final Twig twig = LumberMill.getLogger();
   private String email = "";
   private String userId = "";
   @Nullable
   private UserAttributes attributes;
   private Registration$Validity validity = Registration$Validity.NOT_SET;

   public static Registration create() {
      return new Registration();
   }

   public Registration withEmail(@NonNull String var1) {
      boolean var2 = !TextUtils.isEmpty(var1);
      if (var2) {
         this.email = var1;
      } else {
         this.twig.e("Email cannot be null or empty");
      }

      this.updateState(var2);
      return this;
   }

   public Registration withUserId(@NonNull String var1) {
      boolean var2 = !TextUtils.isEmpty(var1);
      if (var2) {
         this.userId = var1;
      } else {
         this.twig.e("UserId cannot be null or empty");
      }

      this.updateState(var2);
      return this;
   }

   public Registration withUserAttributes(@NonNull UserAttributes var1) {
      if (var1 == null) {
         this.validity = Registration$Validity.INVALID;
         this.twig.e("Registration.withUserAttributes method failed: the attributes Map provided is null");
      } else if (var1.isEmpty()) {
         this.validity = Registration$Validity.INVALID;
         this.twig.e("Registration.withUserAttributes method failed: the attributes Map provided is empty");
      } else {
         this.attributes = var1;
      }

      return this;
   }

   public String getEmail() {
      return this.email;
   }

   public String getUserId() {
      return this.userId;
   }

   @Nullable
   public UserAttributes getAttributes() {
      return this.attributes;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      }

      if (var1 != null && this.getClass() == var1.getClass()) {
         Registration var2 = (Registration)var1;
         if (!this.email.equals(var2.email)) {
            return false;
         } else if (!this.userId.equals(var2.userId)) {
            return false;
         } else {
            return this.attributes != null ? this.attributes.equals(var2.attributes) : var2.attributes == null;
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.email.hashCode();
      var1 = 31 * var1 + this.userId.hashCode();
      return 31 * var1 + (this.attributes != null ? this.attributes.hashCode() : 0);
   }

   @Override
   public String toString() {
      return "Registration{email='" + this.email + '\'' + ", userId='" + this.userId + '\'' + ", attributes=" + this.attributes + '}';
   }

   boolean isValidRegistration() {
      return Registration$Validity.VALID.equals(this.validity);
   }

   private void updateState(boolean var1) {
      if (this.validity == Registration$Validity.NOT_SET || this.validity == Registration$Validity.VALID) {
         this.validity = var1 ? Registration$Validity.VALID : Registration$Validity.INVALID;
      }
   }
}
