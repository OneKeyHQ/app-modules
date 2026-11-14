package io.intercom.android.sdk.m5.conversation.usecase;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.identity.PushTokenStore;
import io.intercom.android.sdk.m5.conversation.states.ConversationClientState;
import io.intercom.android.sdk.m5.conversation.states.PushNotificationsBannerState;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.flow.MutableStateFlow;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\b\u0001\u0018\u00002\u00020\u0001B\u001b\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u001f\u0010\b\u001a\u00020\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000b2\u0006\u0010\r\u001a\u00020\u000eH\u0086\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000f",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/usecase/PushNotificationsBannerUseCase;",
         "",
         "intercomDataLayer",
         "Lio/intercom/android/sdk/m5/data/IntercomDataLayer;",
         "pushTokenStore",
         "Lio/intercom/android/sdk/identity/PushTokenStore;",
         "<init>",
         "(Lio/intercom/android/sdk/m5/data/IntercomDataLayer;Lio/intercom/android/sdk/identity/PushTokenStore;)V",
         "invoke",
         "",
         "clientState",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationClientState;",
         "arePushNotificationsEnabled",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nPushNotificationsBannerUseCase.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PushNotificationsBannerUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/PushNotificationsBannerUseCase\n+ 2 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n*L\n1#1,35:1\n230#2,5:36\n*S KotlinDebug\n*F\n+ 1 PushNotificationsBannerUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/PushNotificationsBannerUseCase\n*L\n19#1:36,5\n*E\n"
)
public final class PushNotificationsBannerUseCase {
   @NotNull
   private final IntercomDataLayer intercomDataLayer;
   @NotNull
   private final PushTokenStore pushTokenStore;
   public static final int $stable;

   static { $stable = 8; }

   public PushNotificationsBannerUseCase(@NotNull IntercomDataLayer var1, @NotNull PushTokenStore var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "intercomDataLayer");
      Intrinsics.checkNotNullParameter(var2, "pushTokenStore");
      this.intercomDataLayer = var1;
      this.pushTokenStore = var2;
   }

   // $VF: synthetic method
   public PushNotificationsBannerUseCase(IntercomDataLayer var1, PushTokenStore var2, int var3, DefaultConstructorMarker var4) {
      this((((var3 & 1) != 0) ? (IntercomDataLayer) (Injector.get().getDataLayer()) : var1), (((var3 & 2) != 0) ? (PushTokenStore) (Injector.get().getPushTokenStore()) : var2));
   }

   public final void invoke(@NotNull MutableStateFlow var1, boolean var2) {
      Intrinsics.checkNotNullParameter(var1, "clientState");
      MutableStateFlow var3 = var1;
      int var4 = 0;

      Object var5;
      Object var8;
      do {
         var5 = var3.getValue();
         ConversationClientState var6 = (ConversationClientState)var5;
         int var7 = 0;
         var8 = ConversationClientState.copy$default(
            var6,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            0,
            0,
            null,
            null,
            false,
            false,
            this.pushTokenStore.isDeviceTokenSubmitted() && !var2 && !((Boolean)this.intercomDataLayer.getPushNotificationsBannerDismissed().getValue()).booleanValue()
               ? PushNotificationsBannerState.INSTANCE
               : null,
            null,
            null,
            7340031,
            null
         );
      } while (!var3.compareAndSet(var5, var8));
   }

   public PushNotificationsBannerUseCase() {
      this(null, null, 3, null);
   }
}
