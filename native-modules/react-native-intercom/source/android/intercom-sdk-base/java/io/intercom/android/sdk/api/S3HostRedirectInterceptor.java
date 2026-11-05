package io.intercom.android.sdk.api;

import android.util.Log;
import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Interceptor.Chain;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0001\u0018\u00002\u00020\u0001B\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016¨\u0006\b",
   d2 = {
         "Lio/intercom/android/sdk/api/S3HostRedirectInterceptor;",
         "Lokhttp3/Interceptor;",
         "<init>",
         "()V",
         "intercept",
         "Lokhttp3/Response;",
         "chain",
         "Lokhttp3/Interceptor$Chain;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class S3HostRedirectInterceptor implements Interceptor {
   public static int $stable;

   static {}

   @NotNull
   @Override
   public Response intercept(@NotNull Chain var1) throws java.io.IOException {
      Intrinsics.checkNotNullParameter(var1, "chain");
      Request var2 = var1.request();
      HttpUrl var3 = var2.url();
      if (Intrinsics.areEqual(var3.host(), "uploads-development.s3.test")) {
         Log.d("IntercomUpload", "Intercepting S3 request: " + var3);
         HttpUrl var4 = var3.newBuilder().host("10.0.2.2").port(80).build();
         Request var5 = var2.newBuilder().url(var4).header("Host", "uploads-development.s3.test").build();
         Log.d("IntercomUpload", "Redirected to: " + var4 + " with Host: uploads-development.s3.test");
         return var1.proceed(var5);
      } else {
         return var1.proceed(var2);
      }
   }
}
