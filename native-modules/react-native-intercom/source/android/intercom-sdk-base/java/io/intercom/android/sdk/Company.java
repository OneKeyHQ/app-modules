package io.intercom.android.sdk;

import com.intercom.twig.Twig;
import io.intercom.android.sdk.logger.LumberMill;
import java.util.Map;

public class Company {
   private static final Twig TWIG = LumberMill.getLogger();
   private static final String COMPANY_ID = "id";
   private static final String NAME = "name";
   private static final String CUSTOM_ATTRIBUTES = "custom_attributes";
   private static final String CREATED_AT = "created_at";
   private static final String MONTHLY_SPEND = "monthly_spend";
   private static final String PLAN = "plan";
   private final Map attributes;
   private final Map customAttributes;

   Company(Company$Builder var1) {
      this.attributes = var1.attributes;
      this.customAttributes = var1.customAttributes;
   }

   Map getAttributes() {
      if (!this.customAttributes.isEmpty()) {
         this.attributes.put("custom_attributes", this.customAttributes);
      }

      return this.attributes;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Company var2 = (Company)var1;
         return !this.attributes.equals(var2.attributes) ? false : this.customAttributes.equals(var2.customAttributes);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.attributes.hashCode();
      return 31 * var1 + this.customAttributes.hashCode();
   }

   @Override
   public String toString() {
      return "Company{attributes=" + this.attributes + ", customAttributes=" + this.customAttributes + '}';
   }

   // $VF: synthetic method
   static Twig access$000() {
      return TWIG;
   }
}
