package io.intercom.android.sdk.api;

import androidx.annotation.Nullable;
import com.google.auto.value.AutoValue;
import java.util.Map;

@AutoValue
public abstract class UserUpdateRequest {
   public static UserUpdateRequest create(boolean var0, boolean var1, @Nullable Map var2, boolean var3) {
      return new UserUpdateRequest$Builder().isNewSession(var0).isSentFromBackground(var1).withAttributes(var2).isInternalUpdate(var3).build();
   }

   public static UserUpdateRequest create(boolean var0, boolean var1, boolean var2) {
      return create(var0, var1, null, var2);
   }

   public abstract boolean isNewSession();

   public abstract boolean isSentFromBackground();

   public abstract boolean isInternalUpdate();

   public abstract Map getAttributes();

   public boolean isValidUpdate() {
      return this.isInternalUpdate() || !this.getAttributes().isEmpty();
   }
}
