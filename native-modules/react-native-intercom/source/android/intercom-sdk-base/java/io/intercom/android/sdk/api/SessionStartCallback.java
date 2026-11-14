package io.intercom.android.sdk.api;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.IntercomError;
import io.intercom.android.sdk.IntercomStatusCallback;
import io.intercom.android.sdk.IntercomStatusCallbackKt;
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
   d1 = "\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0001\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0001¢\u0006\u0004\b\t\u0010\nJ\b\u0010\u000b\u001a\u00020\fH\u0016J\u0010\u0010\r\u001a\u00020\f2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0010",
   d2 = {
         "Lio/intercom/android/sdk/api/SessionStartCallback;",
         "Lio/intercom/android/sdk/IntercomStatusCallback;",
         "dataLayer",
         "Lio/intercom/android/sdk/m5/data/IntercomDataLayer;",
         "userUpdater",
         "Lio/intercom/android/sdk/api/UserUpdater;",
         "attemptId",
         "",
         "delegate",
         "<init>",
         "(Lio/intercom/android/sdk/m5/data/IntercomDataLayer;Lio/intercom/android/sdk/api/UserUpdater;JLio/intercom/android/sdk/IntercomStatusCallback;)V",
         "onSuccess",
         "",
         "onFailure",
         "intercomError",
         "Lio/intercom/android/sdk/IntercomError;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class SessionStartCallback implements IntercomStatusCallback {
   @NotNull
   private final IntercomDataLayer dataLayer;
   @NotNull
   private final UserUpdater userUpdater;
   private final long attemptId;
   @NotNull
   private final IntercomStatusCallback delegate;
   public static final int $stable;

   static { $stable = 8; }

   public SessionStartCallback(@NotNull IntercomDataLayer var1, @NotNull UserUpdater var2, long var3, @NotNull IntercomStatusCallback var5) {
      super();
      Intrinsics.checkNotNullParameter(var1, "dataLayer");
      Intrinsics.checkNotNullParameter(var2, "userUpdater");
      Intrinsics.checkNotNullParameter(var5, "delegate");
      this.dataLayer = var1;
      this.userUpdater = var2;
      this.attemptId = var3;
      this.delegate = var5;
   }

   @Override
   public void onSuccess() {
      this.dataLayer.sessionStartSucceeded(this.attemptId);
      this.delegate.onSuccess();
   }

   @Override
   public void onFailure(@NotNull IntercomError var1) {
      Intrinsics.checkNotNullParameter(var1, "intercomError");
      boolean var2 = this.dataLayer.sessionStartFailed(this.attemptId);
      this.delegate.onFailure(var1);
      if (var2) {
         boolean var3 = ((HostAppState)this.dataLayer.getHostAppState().getValue()).isBackgrounded();
         UserUpdater var10000 = this.userUpdater;
         UserUpdateRequest var10001 = UserUpdateRequest.create(true, var3, true);
         Intrinsics.checkNotNullExpressionValue(var10001, "create(...)");
         var10000.updateUser(var10001, IntercomStatusCallbackKt.getNoStatusCallback(), SessionStartClaim.IF_OWED);
      }
   }
}
