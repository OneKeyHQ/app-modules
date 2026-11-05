package io.intercom.android.sdk.api;

import com.intercom.twig.Twig;
import io.intercom.android.sdk.identity.UserIdentity;
import io.intercom.android.sdk.logger.LumberMill;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.Interceptor.Chain;

class UserIdentityInterceptor implements Interceptor {
   private static final String USER_IDENTITY_CHANGED = "registered user changed while this request was in flight";
   private static final String NO_USER_IDENTITY = "A network request was made with no user registered on this device.Please call registerUnidentifiedUser() or registerIdentifiedUser(Registration).";
   private final UserIdentity userIdentity;
   private final Twig twig = LumberMill.getLogger();

   public UserIdentityInterceptor(UserIdentity var1) {
      this.userIdentity = var1;
   }

   @Override
   public Response intercept(Chain var1) throws IOException {
      if (this.userIdentity.identityExists()) {
         String var2 = this.userIdentity.getFingerprint();
         Response var3 = var1.proceed(var1.request());
         if (var2.equals(this.userIdentity.getFingerprint())) {
            this.twig.internal("interceptor", "proceeding");
            return var3;
         }

         this.twig.internal("interceptor", "halting: user identity changed");
         if (var3 != null) {
            var3.body().close();
         }

         throw new IOException("registered user changed while this request was in flight");
      } else if (this.userIdentity.isSoftReset() && var1.request().url().toString().contains("device_tokens") && var1.request().method().equals("PUT")) {
         this.twig.internal("interceptor", "removing device token - proceeding");
         return var1.proceed(var1.request());
      } else {
         throw new IOException(
            "A network request was made with no user registered on this device.Please call registerUnidentifiedUser() or registerIdentifiedUser(Registration)."
         );
      }
   }
}
