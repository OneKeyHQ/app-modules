package io.intercom.android.sdk.api;

import java.util.Map;
import org.jspecify.annotations.Nullable;

final class AutoValue_UserUpdateRequest extends UserUpdateRequest {
   private final boolean newSession;
   private final boolean sentFromBackground;
   private final boolean internalUpdate;
   private final Map attributes;

   AutoValue_UserUpdateRequest(boolean var1, boolean var2, boolean var3, Map var4) {
      this.newSession = var1;
      this.sentFromBackground = var2;
      this.internalUpdate = var3;
      if (var4 == null) {
         throw new NullPointerException("Null attributes");
      }

      this.attributes = var4;
   }

   @Override
   public boolean isNewSession() {
      return this.newSession;
   }

   @Override
   public boolean isSentFromBackground() {
      return this.sentFromBackground;
   }

   @Override
   public boolean isInternalUpdate() {
      return this.internalUpdate;
   }

   @Override
   public Map getAttributes() {
      return this.attributes;
   }

   @Override
   public String toString() {
      return "UserUpdateRequest{newSession="
         + this.newSession
         + ", sentFromBackground="
         + this.sentFromBackground
         + ", internalUpdate="
         + this.internalUpdate
         + ", attributes="
         + this.attributes
         + "}";
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (var1 == this) {
         return true;
      }

      if (!(var1 instanceof UserUpdateRequest)) {
         return false;
      }

      UserUpdateRequest var2 = (UserUpdateRequest)var1;
      return this.newSession == var2.isNewSession()
         && this.sentFromBackground == var2.isSentFromBackground()
         && this.internalUpdate == var2.isInternalUpdate()
         && this.attributes.equals(var2.getAttributes());
   }

   @Override
   public int hashCode() {
      int var1 = 1;
      var1 *= 1000003;
      var1 ^= this.newSession ? 1231 : 1237;
      var1 *= 1000003;
      var1 ^= this.sentFromBackground ? 1231 : 1237;
      var1 *= 1000003;
      var1 ^= this.internalUpdate ? 1231 : 1237;
      var1 *= 1000003;
      return var1 ^ this.attributes.hashCode();
   }
}
