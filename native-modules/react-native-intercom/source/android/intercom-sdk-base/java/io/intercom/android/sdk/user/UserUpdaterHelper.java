package io.intercom.android.sdk.user;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.IntercomStatusCallbackKt;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.api.UserUpdateRequest;
import io.intercom.android.sdk.api.UserUpdater;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.identity.UserIdentity;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.m5.data.SessionStartClaim;
import io.intercom.android.sdk.models.HostAppState;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\b\u0001\u0018\u00002\u00020\u0001B+\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0003¢\u0006\u0004\b\t\u0010\nJ\u000e\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015J\u0006\u0010\u0016\u001a\u00020\u0013R\u0014\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0019\u0010\u000b\u001a\n \f*\u0004\u0018\u00010\b0\b8F¢\u0006\u0006\u001a\u0004\b\r\u0010\u000eR\u0019\u0010\u000f\u001a\n \f*\u0004\u0018\u00010\u00040\u00048F¢\u0006\u0006\u001a\u0004\b\u0010\u0010\u0011¨\u0006\u0017",
   d2 = {
         "Lio/intercom/android/sdk/user/UserUpdaterHelper;",
         "",
         "userUpdaterProvider",
         "Lio/intercom/android/sdk/Provider;",
         "Lio/intercom/android/sdk/api/UserUpdater;",
         "dataLayer",
         "Lio/intercom/android/sdk/m5/data/IntercomDataLayer;",
         "userIdentityProvider",
         "Lio/intercom/android/sdk/identity/UserIdentity;",
         "<init>",
         "(Lio/intercom/android/sdk/Provider;Lio/intercom/android/sdk/m5/data/IntercomDataLayer;Lio/intercom/android/sdk/Provider;)V",
         "userIdentity",
         "kotlin.jvm.PlatformType",
         "getUserIdentity",
         "()Lio/intercom/android/sdk/identity/UserIdentity;",
         "userUpdater",
         "getUserUpdater",
         "()Lio/intercom/android/sdk/api/UserUpdater;",
         "update",
         "",
         "currentTime",
         "",
         "retryOwedSessionStart",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class UserUpdaterHelper {
   @NotNull
   private final Provider userUpdaterProvider;
   @NotNull
   private final IntercomDataLayer dataLayer;
   @NotNull
   private final Provider userIdentityProvider;
   public static final int $stable;

   static { $stable = 8; }

   public UserUpdaterHelper(@NotNull Provider var1, @NotNull IntercomDataLayer var2, @NotNull Provider var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "userUpdaterProvider");
      Intrinsics.checkNotNullParameter(var2, "dataLayer");
      Intrinsics.checkNotNullParameter(var3, "userIdentityProvider");
      this.userUpdaterProvider = var1;
      this.dataLayer = var2;
      this.userIdentityProvider = var3;
   }

   public final UserIdentity getUserIdentity() {
      return (UserIdentity)this.userIdentityProvider.get();
   }

   public final UserUpdater getUserUpdater() {
      return (UserUpdater)this.userUpdaterProvider.get();
   }

   public final void update(long var1) {
      HostAppState var3 = (HostAppState)this.dataLayer.getHostAppState().getValue();
      long var4 = var1 - var3.getBackgroundedTimestamp();
      boolean var6 = var4 > ((AppConfig)this.dataLayer.getConfig().getValue()).getNewSessionThresholdMs();
      boolean var7 = var3.getSessionStartOwed() && var3.getSessionStartAttemptId() == null;
      if ((var6 || var7) && this.getUserIdentity().identityExists()) {
         boolean var8 = var3.isBackgrounded();
         UserUpdateRequest var9 = UserUpdateRequest.create(true, var8, true);
         SessionStartClaim var10 = var6 ? SessionStartClaim.NEW_VISIT : SessionStartClaim.IF_OWED;
         UserUpdater var10000 = this.getUserUpdater();
         Intrinsics.checkNotNull(var9);
         var10000.updateUser(var9, IntercomStatusCallbackKt.getNoStatusCallback(), var10);
      }
   }

   public final void retryOwedSessionStart() {
      if (((HostAppState)this.dataLayer.getHostAppState().getValue()).isForeground() && this.getUserIdentity().identityExists()) {
         if (!this.dataLayer.requestRetryIfSessionStartInFlight()) {
            UserUpdater var10000 = this.getUserUpdater();
            UserUpdateRequest var10001 = UserUpdateRequest.create(true, false, true);
            Intrinsics.checkNotNullExpressionValue(var10001, "create(...)");
            var10000.updateUser(var10001, IntercomStatusCallbackKt.getNoStatusCallback(), SessionStartClaim.IF_OWED);
         }
      }
   }
}
