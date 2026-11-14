package io.intercom.android.sdk.api;

import androidx.compose.runtime.internal.StabilityInferred;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.identity.UserJwtRefresher;
import io.intercom.android.sdk.logger.LumberMill;
import java.io.IOException;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Interceptor.Chain;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0003\b\u0001\u0018\u0000 \u00162\u00020\u0001:\u0001\u0016B\u0019\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0016J\u000e\u0010\f\u001a\u0004\u0018\u00010\r*\u00020\u000eH\u0002J*\u0010\u000f\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\u0010\u001a\u00020\u000e2\u0006\u0010\u0011\u001a\u00020\r2\u0006\u0010\u0012\u001a\u00020\rH\u0002J\u0010\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\tH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0017",
   d2 = {
         "Lio/intercom/android/sdk/api/UserJwtExpiryInterceptor;",
         "Lokhttp3/Interceptor;",
         "refresher",
         "Lio/intercom/android/sdk/identity/UserJwtRefresher;",
         "twig",
         "Lcom/intercom/twig/Twig;",
         "<init>",
         "(Lio/intercom/android/sdk/identity/UserJwtRefresher;Lcom/intercom/twig/Twig;)V",
         "intercept",
         "Lokhttp3/Response;",
         "chain",
         "Lokhttp3/Interceptor$Chain;",
         "jwtIfRefreshable",
         "",
         "Lokhttp3/Request;",
         "withRefreshedJwt",
         "request",
         "staleJwt",
         "fingerprint",
         "isUserJwtExpired",
         "",
         "response",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nUserJwtExpiryInterceptor.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UserJwtExpiryInterceptor.kt\nio/intercom/android/sdk/api/UserJwtExpiryInterceptor\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,66:1\n1#2:67\n*E\n"
)
public final class UserJwtExpiryInterceptor implements Interceptor {
   @NotNull
   private static final UserJwtExpiryInterceptor$Companion Companion = new UserJwtExpiryInterceptor$Companion(null);
   @NotNull
   private final UserJwtRefresher refresher;
   @NotNull
   private final Twig twig;
   public static final int $stable;

   static { $stable = 8; }
   @Deprecated
   public static final int HTTP_BAD_REQUEST = 400;
   @Deprecated
   public static final long MAX_ERROR_BODY_BYTES = 16384L;

   public UserJwtExpiryInterceptor(@NotNull UserJwtRefresher var1, @NotNull Twig var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "refresher");
      Intrinsics.checkNotNullParameter(var2, "twig");
      this.refresher = var1;
      this.twig = var2;
   }

   // $VF: synthetic method
   public UserJwtExpiryInterceptor(UserJwtRefresher var1, Twig var2, int var3, DefaultConstructorMarker var4) {
      this(var1, (((var3 & 2) != 0) ? (Twig) (LumberMill.getLogger()) : var2));
   }

   @NotNull
   @Override
   public Response intercept(@NotNull Chain var1) throws java.io.IOException {
      String var3;
      Request var10000;
      label34: {
         Intrinsics.checkNotNullParameter(var1, "chain");
         Request var2 = var1.request();
         var3 = this.refresher.currentFingerprint();
         String var4 = this.jwtIfRefreshable(var2);
         if (var4 != null) {
            String var7 = var4;
            String var8 = var7;
            int var9 = 0;
            String var6 = this.refresher.shouldRefreshBeforeSending(var8) ? var7 : null;
            if (var6 != null) {
               String itx = var6;
               int var10 = 0;
               Request var13 = this.withRefreshedJwt(var1, var2, itx, var3);
               if (var13 != null) {
                  var10000 = var13;
                  break label34;
               }
            }
         }

         var10000 = var2;
      }

      Request var5 = var10000;
      Response var12 = var1.proceed(var5);
      if (var12.code() == 400 && this.isUserJwtExpired(var12)) {
         String var17 = this.jwtIfRefreshable(var5);
         if (var17 != null) {
            String var16 = var17;
            int var11 = 0;
            var10000 = this.withRefreshedJwt(var1, var5, var16, var3);
         } else {
            var10000 = null;
         }

         Request var14 = var10000;
         if (var14 == null) {
            this.twig
               .w(
                  "Intercom rejected a request because the user JWT has expired. Call Intercom.client().setUserJwt() with a fresh token, or register a provider with Intercom.client().setUserJwtProvider()."
               );
            return var12;
         } else {
            this.twig.i("Replaying a request with a refreshed user JWT");
            var12.close();
            return var1.proceed(var14);
         }
      } else {
         return var12;
      }
   }

   private final String jwtIfRefreshable(Request var1) throws java.io.IOException {
      return this.refresher.getProvider() == null ? null : UserJwtRequestBody.INSTANCE.readJwt(var1);
   }

   private final Request withRefreshedJwt(Chain var1, Request var2, String var3, String var4) throws java.io.IOException {
      final okhttp3.Interceptor.Chain recoveredExpressionCapture_55_0 = var1;
      String var5 = this.refresher.refresh(var3, var4, ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.api.UserJwtExpiryInterceptor.withRefreshedJwt$lambda$3(recoveredExpressionCapture_55_0)));
      if (var1.call().isCanceled()) {
         throw new IOException("Canceled");
      }

      if (var5 != null) {
         String var6 = var5;
         String var7 = var6;
         int var8 = 0;
         String var10000 = !Intrinsics.areEqual(var7, var3) ? var6 : null;
         if (var10000 != null) {
            var7 = var10000;
            var8 = 0;
            return UserJwtRequestBody.INSTANCE.withJwt(var2, var7);
         }
      }

      return null;
   }

   private final boolean isUserJwtExpired(Response var1) {
      boolean var2;
      try {
         var2 = Intrinsics.areEqual(ServerErrorCodeKt.parseServerErrorCode(var1.peekBody(16384L).string()), "intercom_user_jwt_expired");
      } catch (IOException var4) {
         var2 = false;
      }

      return var2;
   }

   private static final boolean withRefreshedJwt$lambda$3(Chain var0) {
      return var0.call().isCanceled();
   }
}
