package io.intercom.android.sdk.identity;

import io.intercom.android.sdk.Intercom;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.models.AppLifecycleState;
import io.intercom.android.sdk.models.HostAppState;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0007R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u000f",
   d2 = {
         "Lio/intercom/android/sdk/identity/UserJwtRefresher$Companion;",
         "",
         "<init>",
         "()V",
         "PROVIDER_TIMEOUT_MILLIS",
         "",
         "FAILURE_COOL_DOWN_MILLIS",
         "PROACTIVE_REFRESH_WINDOW_MILLIS",
         "CANCELLATION_POLL_MILLIS",
         "create",
         "Lio/intercom/android/sdk/identity/UserJwtRefresher;",
         "userIdentity",
         "Lio/intercom/android/sdk/identity/UserIdentity;",
         "dataLayer",
         "Lio/intercom/android/sdk/m5/data/IntercomDataLayer;",
         "intercom-sdk-base_release"
   }
)
public final class UserJwtRefresher$Companion {
   private UserJwtRefresher$Companion() {
   }

   @JvmStatic
   @NotNull
   public final UserJwtRefresher create(@NotNull UserIdentity var1, @NotNull IntercomDataLayer var2) {
      Intrinsics.checkNotNullParameter(var1, "userIdentity");
      Intrinsics.checkNotNullParameter(var2, "dataLayer");
      final io.intercom.android.sdk.m5.data.IntercomDataLayer recoveredExpressionCapture_137_0 = var2;
      return new UserJwtRefresher(
         var1, ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.identity.UserJwtRefresher$Companion.create$lambda$0(recoveredExpressionCapture_137_0)), ((kotlin.jvm.functions.Function3) (recoveredArgument_138_0, recoveredArgument_138_1, recoveredArgument_138_2) -> UserJwtRefresher$Companion.create$lambda$1((java.lang.String)recoveredArgument_138_0, (java.lang.String)recoveredArgument_138_1, (java.lang.String)recoveredArgument_138_2)), null, null, null, null, null, 248, null
      );
   }

   private static final boolean create$lambda$0(IntercomDataLayer var0) {
      return ((HostAppState)var0.getHostAppState().getValue()).getLifecycleState() == AppLifecycleState.FOREGROUND;
   }

   private static final Unit create$lambda$1(String var0, String var1, String var2) {
      Intrinsics.checkNotNullParameter(var0, "jwt");
      Intrinsics.checkNotNullParameter(var1, "fingerprint");
      Intrinsics.checkNotNullParameter(var2, "replacing");
      Intercom.Companion.client().applyRefreshedUserJwt$intercom_sdk_base_release(var0, var1, var2);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public UserJwtRefresher$Companion(DefaultConstructorMarker var1) {
      this();
   }
}
