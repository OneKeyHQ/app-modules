package io.intercom.android.sdk.identity;

import io.intercom.android.sdk.IntercomUserJwtCallback;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0082\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0016J\b\u0010\n\u001a\u00020\u0007H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000b",
   d2 = {
         "Lio/intercom/android/sdk/identity/UserJwtRefresher$Callback;",
         "Lio/intercom/android/sdk/IntercomUserJwtCallback;",
         "refresh",
         "Lio/intercom/android/sdk/identity/UserJwtRefresher$PendingRefresh;",
         "<init>",
         "(Lio/intercom/android/sdk/identity/UserJwtRefresher;Lio/intercom/android/sdk/identity/UserJwtRefresher$PendingRefresh;)V",
         "onSuccess",
         "",
         "jwt",
         "",
         "onFailure",
         "intercom-sdk-base_release"
   }
)
final class UserJwtRefresher$Callback implements IntercomUserJwtCallback {
   @NotNull
   private final UserJwtRefresher$PendingRefresh refresh;
   // $VF: synthetic field
   final UserJwtRefresher this$0;

   public UserJwtRefresher$Callback(@NotNull UserJwtRefresher var1, UserJwtRefresher$PendingRefresh var2) {
      super();
      Intrinsics.checkNotNullParameter(var2, "refresh");
      this.this$0 = var1;
      
      this.refresh = var2;
   }

   @Override
   public void onSuccess(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "jwt");
      UserJwtRefresher.access$complete(this.this$0, this.refresh, var1, true);
   }

   @Override
   public void onFailure() {
      UserJwtRefresher.access$complete(this.this$0, this.refresh, null, true);
   }
}
