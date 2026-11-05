package io.intercom.android.sdk.identity;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class AppIdentity {
   public static AppIdentity create(String var0, String var1) {
      return new AutoValue_AppIdentity(var0, var1);
   }

   public abstract String apiKey();

   public abstract String appId();
}
