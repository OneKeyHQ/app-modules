package io.intercom.android.sdk;

import com.intercom.twig.Twig;
import io.intercom.android.sdk.logger.LumberMill;
import java.util.List;
import java.util.Map;

public class UserAttributes {
   private static final Twig TWIG = LumberMill.getLogger();
   private static final String EMAIL = "email";
   private static final String USER_ID = "user_id";
   private static final String NAME = "name";
   private static final String PHONE = "phone";
   private static final String CUSTOM_ATTRIBUTES = "custom_attributes";
   private static final String COMPANIES = "companies";
   private static final String SIGNED_UP_AT = "signed_up_at";
   private static final String UNSUBSCRIBED_FROM_EMAILS = "unsubscribed_from_emails";
   private static final String LANGUAGE_OVERRIDE = "language_override";
   private final Map attributes;
   private final Map customAttributes;
   private final List companies;

   UserAttributes(UserAttributes$Builder var1) {
      this.attributes = var1.attributes;
      this.customAttributes = var1.customAttributes;
      this.companies = var1.companies;
   }

   public Map toMap() {
      if (!this.customAttributes.isEmpty()) {
         this.attributes.put("custom_attributes", this.customAttributes);
      }

      if (!this.companies.isEmpty()) {
         this.attributes.put("companies", this.companies);
      }

      return this.attributes;
   }

   public boolean isEmpty() {
      return this.attributes.isEmpty() && this.customAttributes.isEmpty() && this.companies.isEmpty();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      }

      if (var1 != null && this.getClass() == var1.getClass()) {
         UserAttributes var2 = (UserAttributes)var1;
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
      return "UserAttributes{attributes=" + this.attributes + ", customAttributes=" + this.customAttributes + ", companies=" + this.companies + '}';
   }

   // $VF: synthetic method
   static Twig access$000() {
      return TWIG;
   }
}
