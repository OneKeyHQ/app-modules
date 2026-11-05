package io.intercom.android.sdk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.intercom.android.sdk.utilities.CustomAttributeValidator;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public final class UserAttributes$Builder {
   final Map attributes = new HashMap();
   final Map customAttributes = new HashMap();
   final List companies = new ArrayList();

   public UserAttributes build() {
      return new UserAttributes(this);
   }

   public UserAttributes$Builder withEmail(@Nullable String var1) {
      this.attributes.put("email", var1);
      return this;
   }

   public UserAttributes$Builder withUserId(@Nullable String var1) {
      this.attributes.put("user_id", var1);
      return this;
   }

   public UserAttributes$Builder withName(@Nullable String var1) {
      this.attributes.put("name", var1);
      return this;
   }

   public UserAttributes$Builder withPhone(@Nullable String var1) {
      this.attributes.put("phone", var1);
      return this;
   }

   public UserAttributes$Builder withSignedUpAt(@Nullable Long var1) {
      this.attributes.put("signed_up_at", var1);
      return this;
   }

   public UserAttributes$Builder withSignedUpAt(@Nullable Date var1) {
      return this.withSignedUpAt(var1 == null ? null : TimeUnit.MILLISECONDS.toSeconds(var1.getTime()));
   }

   public UserAttributes$Builder withUnsubscribedFromEmails(@Nullable Boolean var1) {
      this.attributes.put("unsubscribed_from_emails", var1);
      return this;
   }

   public UserAttributes$Builder withLanguageOverride(@Nullable String var1) {
      this.attributes.put("language_override", var1);
      return this;
   }

   public UserAttributes$Builder withCompany(@NonNull Company var1) {
      if (var1 == null) {
         UserAttributes.access$000().w("The company you provided was null");
      } else {
         this.companies.add(var1.getAttributes());
      }

      return this;
   }

   public UserAttributes$Builder withCustomAttribute(@NonNull String var1, @Nullable Object var2) {
      if (var1 == null) {
         UserAttributes.access$000().w("The key you provided was null for the attribute " + var2);
      } else if (CustomAttributeValidator.isValid(var2)) {
         this.customAttributes.put(var1, var2);
      } else {
         UserAttributes.access$000()
            .w(
               "The custom user attribute "
                  + var1
                  + " was of type "
                  + var2.getClass().getSimpleName()
                  + " We only accept the following types: "
                  + CustomAttributeValidator.getAcceptedTypes()
            );
      }

      return this;
   }

   public UserAttributes$Builder withCustomAttributes(@NonNull Map var1) {
      if (var1 == null) {
         UserAttributes.access$000().w("The map of attributes you provided was null.");
      } else {
         for (java.lang.Object recoveredEntryElement_0_96 : var1.entrySet()) {
            Entry var3 = (Entry) recoveredEntryElement_0_96;
            Object var4 = var3.getValue();
            if (CustomAttributeValidator.isValid(var4)) {
               this.customAttributes.put((String)var3.getKey(), var4);
            } else {
               UserAttributes.access$000()
                  .w(
                     "The custom user attribute "
                        + (String)var3.getKey()
                        + " was of type "
                        + var4.getClass().getSimpleName()
                        + " We only accept the following types: "
                        + CustomAttributeValidator.getAcceptedTypes()
                  );
            }
         }
      }

      return this;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      }

      if (var1 != null && this.getClass() == var1.getClass()) {
         UserAttributes$Builder var2 = (UserAttributes$Builder)var1;
         if (!this.attributes.equals(var2.attributes)) {
            return false;
         } else {
            return !this.customAttributes.equals(var2.customAttributes) ? false : this.companies.equals(var2.companies);
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.attributes.hashCode();
      var1 = 31 * var1 + this.customAttributes.hashCode();
      return 31 * var1 + this.companies.hashCode();
   }

   @Override
   public String toString() {
      return "Builder{attributes=" + this.attributes + ", customAttributes=" + this.customAttributes + ", companies=" + this.companies + '}';
   }
}
