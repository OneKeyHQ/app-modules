package io.intercom.android.sdk.api;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.IntercomStatusCallback;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.m5.data.SessionStartClaim;
import io.intercom.android.sdk.metrics.ops.OpsMetricTracker;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0001\u0018\u00002\u00020\u0001B%\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\t\u0010\nJ\u0016\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010J\u001e\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012R\u0014\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0013",
   d2 = {
         "Lio/intercom/android/sdk/api/UserUpdater;",
         "",
         "apiProvider",
         "Lio/intercom/android/sdk/Provider;",
         "Lio/intercom/android/sdk/api/Api;",
         "opsMetricTracker",
         "Lio/intercom/android/sdk/metrics/ops/OpsMetricTracker;",
         "dataLayer",
         "Lio/intercom/android/sdk/m5/data/IntercomDataLayer;",
         "<init>",
         "(Lio/intercom/android/sdk/Provider;Lio/intercom/android/sdk/metrics/ops/OpsMetricTracker;Lio/intercom/android/sdk/m5/data/IntercomDataLayer;)V",
         "updateUser",
         "",
         "userUpdateRequest",
         "Lio/intercom/android/sdk/api/UserUpdateRequest;",
         "intercomStatusCallback",
         "Lio/intercom/android/sdk/IntercomStatusCallback;",
         "claim",
         "Lio/intercom/android/sdk/m5/data/SessionStartClaim;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class UserUpdater {
   @NotNull
   private final Provider apiProvider;
   @NotNull
   private final OpsMetricTracker opsMetricTracker;
   @NotNull
   private final IntercomDataLayer dataLayer;
   public static final int $stable;

   static { $stable = 8; }

   public UserUpdater(@NotNull Provider var1, @NotNull OpsMetricTracker var2, @NotNull IntercomDataLayer var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "apiProvider");
      Intrinsics.checkNotNullParameter(var2, "opsMetricTracker");
      Intrinsics.checkNotNullParameter(var3, "dataLayer");
      this.apiProvider = var1;
      this.opsMetricTracker = var2;
      this.dataLayer = var3;
   }

   public final void updateUser(@NotNull UserUpdateRequest var1, @NotNull IntercomStatusCallback var2) {
      Intrinsics.checkNotNullParameter(var1, "userUpdateRequest");
      Intrinsics.checkNotNullParameter(var2, "intercomStatusCallback");
      this.updateUser(var1, var2, SessionStartClaim.IF_IDLE);
   }

   public final synchronized void updateUser(@NotNull UserUpdateRequest var1, @NotNull IntercomStatusCallback var2, @NotNull SessionStartClaim var3) {
      Intrinsics.checkNotNullParameter(var1, "userUpdateRequest");
      Intrinsics.checkNotNullParameter(var2, "intercomStatusCallback");
      Intrinsics.checkNotNullParameter(var3, "claim");
      this.opsMetricTracker.trackEvent("start", "time-to-complete-ping-ms");
      if (var1.isValidUpdate()) {
         UserUpdateRequest var4 = var1;
         IntercomStatusCallback var5 = var2;
         if (var1.isNewSession()) {
            Long var6 = this.dataLayer.claimSessionStart(var3);
            if (var6 == null && var3 == SessionStartClaim.IF_OWED) {
               return;
            }

            if (var6 == null) {
               var4 = UserUpdateRequest.create(false, var1.isSentFromBackground(), var1.getAttributes(), var1.isInternalUpdate());
            } else {
               var5 = new SessionStartCallback(this.dataLayer, this, var6, var2);
            }
         }

         ((Api)this.apiProvider.get()).updateUser(var4, var5);
      }
   }
}
