package io.intercom.android.sdk.m5.conversation.data;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.nexus.NexusClient;
import io.intercom.android.nexus.NexusEvent;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.identity.UserIdentity;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.MutableSharedFlow;
import kotlinx.coroutines.flow.SharedFlowKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0001\u0018\u00002\u00020\u0001B#\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\u000e\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010J\u000e\u0010\u0011\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0012",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/data/NexusEventsRepository;",
         "",
         "nexusClient",
         "Lio/intercom/android/nexus/NexusClient;",
         "userIdentity",
         "Lio/intercom/android/sdk/identity/UserIdentity;",
         "scope",
         "Lkotlinx/coroutines/CoroutineScope;",
         "<init>",
         "(Lio/intercom/android/nexus/NexusClient;Lio/intercom/android/sdk/identity/UserIdentity;Lkotlinx/coroutines/CoroutineScope;)V",
         "typingEventsFlow",
         "Lkotlinx/coroutines/flow/MutableSharedFlow;",
         "Lio/intercom/android/nexus/NexusEvent;",
         "markAsSeen",
         "",
         "conversationId",
         "",
         "userTyping",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class NexusEventsRepository {
   @NotNull
   private final NexusClient nexusClient;
   @NotNull
   private final UserIdentity userIdentity;
   @NotNull
   private final CoroutineScope scope;
   @NotNull
   private final MutableSharedFlow typingEventsFlow;
   public static final int $stable;

   static { $stable = 8; }

   public NexusEventsRepository(@NotNull NexusClient var1, @NotNull UserIdentity var2, @NotNull CoroutineScope var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "nexusClient");
      Intrinsics.checkNotNullParameter(var2, "userIdentity");
      Intrinsics.checkNotNullParameter(var3, "scope");
      this.nexusClient = var1;
      this.userIdentity = var2;
      this.scope = var3;
      this.typingEventsFlow = SharedFlowKt.MutableSharedFlow$default(0, 0, null, 7, null);
      BuildersKt.launch$default(this.scope, null, null, new NexusEventsRepository$1(this, null), 3, null);
   }

   // $VF: synthetic method
   public NexusEventsRepository(NexusClient var1, UserIdentity var2, CoroutineScope var3, int var4, DefaultConstructorMarker var5) {
      this((((var4 & 1) != 0) ? (NexusClient) (Injector.get().getNexusClient()) : var1), (((var4 & 2) != 0) ? (UserIdentity) (Injector.get().getUserIdentity()) : var2), var3);
   }

   public final void markAsSeen(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "conversationId");
      this.nexusClient.fire(NexusEvent.getConversationSeenEvent(var1, this.userIdentity.getIntercomId()));
   }

   public final void userTyping(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "conversationId");
      BuildersKt.launch$default(this.scope, null, null, new NexusEventsRepository$userTyping$1(this, var1, null), 3, null);
   }

   // $VF: synthetic method
   public static final MutableSharedFlow access$getTypingEventsFlow$p(NexusEventsRepository var0) {
      return var0.typingEventsFlow;
   }

   // $VF: synthetic method
   public static final UserIdentity access$getUserIdentity$p(NexusEventsRepository var0) {
      return var0.userIdentity;
   }

   // $VF: synthetic method
   public static final NexusClient access$getNexusClient$p(NexusEventsRepository var0) {
      return var0.nexusClient;
   }
}
