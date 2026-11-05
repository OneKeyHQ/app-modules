package io.intercom.android.sdk.m5.home.data;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.nexus.NexusClient;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.api.MessengerApi;
import io.intercom.android.sdk.api.MessengerApi$DefaultImpls;
import io.intercom.android.sdk.m5.conversation.data.NexusEventAsFlowKt;
import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.flow.Flow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0001\u0018\u00002\u00020\u0001B\u001b\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tJ\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fH\u0086@¢\u0006\u0002\u0010\u000eR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000f",
   d2 = {
         "Lio/intercom/android/sdk/m5/home/data/HomeRepository;",
         "",
         "messengerApi",
         "Lio/intercom/android/sdk/api/MessengerApi;",
         "nexusClient",
         "Lio/intercom/android/nexus/NexusClient;",
         "<init>",
         "(Lio/intercom/android/sdk/api/MessengerApi;Lio/intercom/android/nexus/NexusClient;)V",
         "realTimeEvents",
         "Lkotlinx/coroutines/flow/Flow;",
         "Lio/intercom/android/sdk/m5/conversation/data/ParsedNexusEvent;",
         "getHomeCards",
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;",
         "Lio/intercom/android/sdk/m5/home/data/HomeV2Response;",
         "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class HomeRepository {
   @NotNull
   private final MessengerApi messengerApi;
   @NotNull
   private final NexusClient nexusClient;
   public static final int $stable;

   static { $stable = 8; }

   public HomeRepository(@NotNull MessengerApi var1, @NotNull NexusClient var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "messengerApi");
      Intrinsics.checkNotNullParameter(var2, "nexusClient");
      this.messengerApi = var1;
      this.nexusClient = var2;
   }

   // $VF: synthetic method
   public HomeRepository(MessengerApi var1, NexusClient var2, int var3, DefaultConstructorMarker var4) {
      this((((var3 & 1) != 0) ? (MessengerApi) (Injector.get().getMessengerApi()) : var1), (((var3 & 2) != 0) ? (NexusClient) (Injector.get().getNexusClient()) : var2));
   }

   @NotNull
   public final Flow realTimeEvents() {
      return NexusEventAsFlowKt.nexusEventAsFlow(this.nexusClient);
   }

   @Nullable
   public final Object getHomeCards(@NotNull Continuation var1) {
      return MessengerApi$DefaultImpls.getHomeCardsV2Suspend$default(this.messengerApi, null, var1, 1, null);
   }

   public HomeRepository() {
      this(null, null, 3, null);
   }
}
