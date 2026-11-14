package io.intercom.android.sdk.m5.conversation.usecase;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.m5.conversation.data.ParsedNexusEvent$ConversationNexusEvent$FinStreaming;
import io.intercom.android.sdk.m5.conversation.states.ConversationClientState;
import io.intercom.android.sdk.m5.conversation.states.CurrentlyTypingState;
import io.intercom.android.sdk.m5.conversation.states.TypingIndicatorType;
import io.intercom.android.sdk.m5.conversation.streaming.FinStreamingStateHolder;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.models.ActiveBot;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.StreamingPart;
import io.intercom.android.sdk.models.TeamPresence;
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
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0001\u0018\u00002\u00020\u0001B\u0011\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J'\u0010\u0006\u001a\u00020\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0086\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000f",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/usecase/HandleFinStreamingDeltaUseCase;",
         "",
         "intercomDataLayer",
         "Lio/intercom/android/sdk/m5/data/IntercomDataLayer;",
         "<init>",
         "(Lio/intercom/android/sdk/m5/data/IntercomDataLayer;)V",
         "invoke",
         "",
         "clientStateFlow",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationClientState;",
         "holder",
         "Lio/intercom/android/sdk/m5/conversation/streaming/FinStreamingStateHolder;",
         "data",
         "Lio/intercom/android/sdk/m5/conversation/data/ParsedNexusEvent$ConversationNexusEvent$FinStreaming;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nHandleFinStreamingDeltaUseCase.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HandleFinStreamingDeltaUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/HandleFinStreamingDeltaUseCase\n+ 2 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n*L\n1#1,50:1\n230#2,5:51\n*S KotlinDebug\n*F\n+ 1 HandleFinStreamingDeltaUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/HandleFinStreamingDeltaUseCase\n*L\n24#1:51,5\n*E\n"
)
public final class HandleFinStreamingDeltaUseCase {
   @NotNull
   private final IntercomDataLayer intercomDataLayer;
   public static final int $stable;

   static { $stable = 8; }

   public HandleFinStreamingDeltaUseCase(@NotNull IntercomDataLayer var1) {
      super();
      Intrinsics.checkNotNullParameter(var1, "intercomDataLayer");
      this.intercomDataLayer = var1;
   }

   // $VF: synthetic method
   public HandleFinStreamingDeltaUseCase(IntercomDataLayer var1, int var2, DefaultConstructorMarker var3) {
      this((((var2 & 1) != 0) ? (IntercomDataLayer) (Injector.get().getDataLayer()) : var1));
   }

   public final void invoke(
      @NotNull MutableStateFlow var1, @NotNull FinStreamingStateHolder var2, @NotNull ParsedNexusEvent$ConversationNexusEvent$FinStreaming var3
   ) {
      Intrinsics.checkNotNullParameter(var1, "clientStateFlow");
      Intrinsics.checkNotNullParameter(var2, "holder");
      Intrinsics.checkNotNullParameter(var3, "data");
      ActiveBot var10000 = ((TeamPresence)this.intercomDataLayer.getTeamPresence().getValue()).getActiveBot();
      StreamingPart var4 = var10000 != null ? var10000.getStreamingPart() : null;
      boolean var5 = false;
      MutableStateFlow var6 = var1;
      int var7 = 0;

      Object var8;
      Object var11;
      do {
         var8 = var6.getValue();
         ConversationClientState var9 = (ConversationClientState)var8;
         int var10 = 0;
         var5 = var9.committedPartUuids$intercom_sdk_base_release().contains(var3.getClientAssignedUUID());
         ConversationClientState var13;
         if (var5) {
            var13 = var9;
         } else {
            Conversation var10002 = var9.getConversation();
            var13 = ConversationClientState.copy$default(
               var9,
               null,
               var10002 != null
                  ? Conversation.copy$default(
                     var10002,
                     null,
                     null,
                     false,
                     null,
                     null,
                     null,
                     null,
                     null,
                     false,
                     false,
                     null,
                     null,
                     false,
                     null,
                     null,
                     null,
                     null,
                     null,
                     null,
                     null,
                     null,
                     1966079,
                     null
                  )
                  : null,
               null,
               new CurrentlyTypingState(null, false, null, TypingIndicatorType.NONE, 7, null),
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               var9.getFinStreamingData().copy(true, var3.getClientAssignedUUID()),
               null,
               0,
               0,
               null,
               null,
               false,
               false,
               null,
               null,
               null,
               8384501,
               null
            );
         }

         var11 = var13;
      } while (!var6.compareAndSet(var8, var11));

      if (!var5 && var4 != null) {
         var2.onStreamingDelta(var3, var4);
      }
   }

   public HandleFinStreamingDeltaUseCase() {
      this(null, 1, null);
   }
}
