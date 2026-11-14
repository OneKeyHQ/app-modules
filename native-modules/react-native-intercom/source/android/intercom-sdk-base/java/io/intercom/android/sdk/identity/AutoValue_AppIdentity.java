package io.intercom.android.sdk.identity;

import org.jspecify.annotations.Nullable;

final class AutoValue_AppIdentity extends AppIdentity {
   private final String apiKey;
   private final String appId;

   AutoValue_AppIdentity(String var1, String var2) {
      if (var1 == null) {
         throw new NullPointerException("Null apiKey");
      }

      this.apiKey = var1;
      if (var2 == null) {
         throw new NullPointerException("Null appId");
      }

      this.appId = var2;
   }

   @Override
   public String apiKey() {
      return this.apiKey;
   }

   @Override
   public String appId() {
      return this.appId;
   }

   @Override
   public String toString() {
      return "AppIdentity{apiKey=" + this.apiKey + ", appId=" + this.appId + "}";
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (var1 == this) {
         return true;
      }

      if (!(var1 instanceof AppIdentity)) {
         return false;
      }

      AppIdentity var2 = (AppIdentity)var1;
      return this.apiKey.equals(var2.apiKey()) && this.appId.equals(var2.appId());
   }

   @Override
   public int hashCode() {
      int var1 = 1;
      var1 *= 1000003;
      var1 ^= this.apiKey.hashCode();
      var1 *= 1000003;
      return var1 ^ this.appId.hashCode();
   }
}
