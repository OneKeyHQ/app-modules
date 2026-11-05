package io.intercom.android.sdk.identity;

import android.text.TextUtils;
import com.google.auto.value.AutoValue;

@AutoValue
abstract class SoftUserIdentity {
   static final SoftUserIdentity NONE = create("", "", "", "", "", "", "", "", "");

   static SoftUserIdentity create(String var0, String var1, String var2, String var3, String var4, String var5, String var6, String var7, String var8) {
      return new AutoValue_SoftUserIdentity(var0, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   abstract String anonymousId();

   abstract String email();

   abstract String fingerprint();

   abstract String hmac();

   abstract String jwt();

   abstract String intercomId();

   abstract String userId();

   abstract String encryptedUserId();

   abstract String messengerEnvironment();

   boolean isSameUser(Registration var1) {
      String var2 = var1.getUserId();
      String var3 = var1.getEmail();
      boolean var4 = !TextUtils.isEmpty(var2) || !TextUtils.isEmpty(var3);
      if (!TextUtils.isEmpty(var2)) {
         var4 = var4 && var2.equals(this.userId());
      }

      if (!TextUtils.isEmpty(var3)) {
         var4 = var4 && var3.equals(this.email());
      }

      return var4;
   }

   boolean isPresent() {
      return !this.equals(NONE);
   }
}
