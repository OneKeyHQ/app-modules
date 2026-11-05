package io.intercom.android.sdk.api;

import androidx.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public final class UserUpdateRequest$Builder {
   boolean isNewSession = false;
   boolean isSentFromBackground = true;
   boolean isInternalUpdate = false;
   @Nullable
   Map attributes;

   public UserUpdateRequest$Builder isNewSession(boolean var1) {
      this.isNewSession = var1;
      return this;
   }

   public UserUpdateRequest$Builder isSentFromBackground(boolean var1) {
      this.isSentFromBackground = var1;
      return this;
   }

   public UserUpdateRequest$Builder isInternalUpdate(boolean var1) {
      this.isInternalUpdate = var1;
      return this;
   }

   public UserUpdateRequest$Builder withAttributes(@Nullable Map var1) {
      this.attributes = var1;
      return this;
   }

   public UserUpdateRequest build() {
      Map var1 = new HashMap();
      if (this.attributes != null) {
         var1.putAll(this.attributes);
      }

      return new AutoValue_UserUpdateRequest(this.isNewSession, this.isSentFromBackground, this.isInternalUpdate, var1);
   }
}
