package io.intercom.android.sdk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.intercom.android.sdk.utilities.CustomAttributeValidator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class Company$Builder {
   final Map attributes = new HashMap();
   final Map customAttributes = new HashMap();

   public Company build() {
      return new Company(this);
   }

   public Company$Builder withCompanyId(@Nullable String var1) {
      this.attributes.put("id", var1);
      return this;
   }

   public Company$Builder withName(@Nullable String var1) {
      this.attributes.put("name", var1);
      return this;
   }

   public Company$Builder withCreatedAt(@Nullable Long var1) {
      this.attributes.put("created_at", var1);
      return this;
   }

   public Company$Builder withMonthlySpend(@Nullable Integer var1) {
      this.attributes.put("monthly_spend", var1);
      return this;
   }

   public Company$Builder withPlan(@Nullable String var1) {
      this.attributes.put("plan", var1);
      return this;
   }

   public Company$Builder withCustomAttribute(@NonNull String var1, @Nullable Object var2) {
      if (var1 == null) {
         Company.access$000().w("The key you provided was null for the attribute " + var2);
      } else if (CustomAttributeValidator.isValid(var2)) {
         this.customAttributes.put(var1, var2);
      } else {
         Company.access$000()
            .w(
               "The custom company attribute "
                  + var1
                  + " was of type "
                  + var2.getClass().getSimpleName()
                  + " We only accept the following types: "
                  + CustomAttributeValidator.getAcceptedTypes()
            );
      }

      return this;
   }

   public Company$Builder withCustomAttributes(@NonNull Map var1) {
      if (var1 == null) {
         Company.access$000().w("The map of attributes you provided was null.");
      } else {
         for (java.lang.Object recoveredEntryElement_0_67 : var1.entrySet()) {
            Entry var3 = (Entry) recoveredEntryElement_0_67;
            if (CustomAttributeValidator.isValid(var3.getValue())) {
               this.customAttributes.put((String)var3.getKey(), var3.getValue());
            } else {
               Company.access$000()
                  .w(
                     "The custom company attribute "
                        + (String)var3.getKey()
                        + " was of type "
                        + var3.getClass().getSimpleName()
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
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Company$Builder var2 = (Company$Builder)var1;
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
      return "Builder{attributes=" + this.attributes + ", customAttributes=" + this.customAttributes + '}';
   }
}
