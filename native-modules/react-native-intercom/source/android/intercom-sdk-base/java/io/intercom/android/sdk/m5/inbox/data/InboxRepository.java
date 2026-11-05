package io.intercom.android.sdk.m5.inbox.data;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.nexus.NexusClient;
import io.intercom.android.sdk.AblyManager;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.api.MessengerApi;
import io.intercom.android.sdk.api.MessengerApiHelper;
import io.intercom.android.sdk.m5.conversation.data.CombinedEventAsFlowKt;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.flow.Flow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0001\u0018\u00002\u00020\u0001B%\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bJ*\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e2\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u0013H\u0086@¢\u0006\u0002\u0010\u0014R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0015",
   d2 = {
         "Lio/intercom/android/sdk/m5/inbox/data/InboxRepository;",
         "",
         "api",
         "Lio/intercom/android/sdk/api/MessengerApi;",
         "nexusClient",
         "Lio/intercom/android/nexus/NexusClient;",
         "ablyManager",
         "Lio/intercom/android/sdk/AblyManager;",
         "<init>",
         "(Lio/intercom/android/sdk/api/MessengerApi;Lio/intercom/android/nexus/NexusClient;Lio/intercom/android/sdk/AblyManager;)V",
         "realTimeEvents",
         "Lkotlinx/coroutines/flow/Flow;",
         "Lio/intercom/android/sdk/m5/conversation/data/ParsedNexusEvent;",
         "getConversations",
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;",
         "Lio/intercom/android/sdk/models/ConversationsResponse$Builder;",
         "before",
         "",
         "pageSize",
         "",
         "(Ljava/lang/Long;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class InboxRepository {
   @NotNull
   private final MessengerApi api;
   @NotNull
   private final NexusClient nexusClient;
   @NotNull
   private final AblyManager ablyManager;
   public static final int $stable;

   static { $stable = 8; }

   public InboxRepository(@NotNull MessengerApi var1, @NotNull NexusClient var2, @NotNull AblyManager var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "api");
      Intrinsics.checkNotNullParameter(var2, "nexusClient");
      Intrinsics.checkNotNullParameter(var3, "ablyManager");
      this.api = var1;
      this.nexusClient = var2;
      this.ablyManager = var3;
   }

   // $VF: synthetic method
   public InboxRepository(MessengerApi var1, NexusClient var2, AblyManager var3, int var4, DefaultConstructorMarker var5) {
      this((((var4 & 1) != 0) ? (MessengerApi) (Injector.get().getMessengerApi()) : var1), (((var4 & 2) != 0) ? (NexusClient) (Injector.get().getNexusClient()) : var2), (((var4 & 4) != 0) ? (AblyManager) (Injector.get().getAblyManager()) : var3));
   }

   @NotNull
   public final Flow realTimeEvents() {
      return CombinedEventAsFlowKt.combinedEventAsFlow(this.nexusClient, this.ablyManager);
   }

   @Nullable
   public final Object getConversations(@Nullable Long var1, int var2, @NotNull Continuation var3) {
      MessengerApi var10000 = this.api;
      MessengerApiHelper var10001 = MessengerApiHelper.INSTANCE;
      Map var10002;
      if (var1 == null) {
         var10002 = MapsKt.mapOf(TuplesKt.to("per_page", Boxing.boxInt(var2)));
      } else {
         Pair[] var4 = new Pair[]{TuplesKt.to("per_page", Boxing.boxInt(var2)), TuplesKt.to("before", var1)};
         var10002 = MapsKt.mapOf(var4);
      }

      return var10000.getConversationsSuspend(var10001.getDefaultRequestBody$intercom_sdk_base_release(var10002), var3);
   }

   // $VF: synthetic method
   public static Object getConversations$default(InboxRepository var0, Long var1, int var2, Continuation var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = null;
      }

      if ((var4 & 2) != 0) {
         var2 = 20;
      }

      return var0.getConversations(var1, var2, var3);
   }

   public InboxRepository() {
      this(null, null, null, 7, null);
   }
}
