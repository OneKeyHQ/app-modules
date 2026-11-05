package io.intercom.android.sdk.m5.conversation.usecase;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.m5.conversation.states.ConversationClientState;
import io.intercom.android.sdk.m5.conversation.states.FinStreamingData;
import io.intercom.android.sdk.m5.conversation.streaming.FinStreamingStateHolder;
import java.util.Set;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.flow.MutableStateFlow;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0000\b\u0001\u0018\u00002\u00020\u0001B\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J-\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\t\u001a\u00020\n2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fH\u0086\u0002¨\u0006\u000e",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/usecase/SettleStreamingPartUseCase;",
         "",
         "<init>",
         "()V",
         "invoke",
         "",
         "clientStateFlow",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationClientState;",
         "holder",
         "Lio/intercom/android/sdk/m5/conversation/streaming/FinStreamingStateHolder;",
         "committedUuids",
         "",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
@SourceDebugExtension(
   "SMAP\nSettleStreamingPartUseCase.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SettleStreamingPartUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/SettleStreamingPartUseCase\n+ 2 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n*L\n1#1,26:1\n230#2,5:27\n*S KotlinDebug\n*F\n+ 1 SettleStreamingPartUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/SettleStreamingPartUseCase\n*L\n17#1:27,5\n*E\n"
)
public final class SettleStreamingPartUseCase {
   public static int $stable;

   static {}

   public final void invoke(@NotNull MutableStateFlow var1, @NotNull FinStreamingStateHolder var2, @NotNull Set var3) {
      Intrinsics.checkNotNullParameter(var1, "clientStateFlow");
      Intrinsics.checkNotNullParameter(var2, "holder");
      Intrinsics.checkNotNullParameter(var3, "committedUuids");
      var2.onCommittedPartUuids(var3);
      MutableStateFlow var4 = var1;
      int var5 = 0;

      Object var6;
      ConversationClientState var9;
      do {
         var6 = var4.getValue();
         var9 = (ConversationClientState)var6;
         int var8 = 0;
         var9 = ((ConversationClientState)var9).getFinStreamingData().isFinStreaming()
               && var3.contains(((ConversationClientState)var9).getFinStreamingData().getClientAssignedUUID())
            ? ConversationClientState.copy$default(
               (ConversationClientState)var9,
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
               new FinStreamingData(false, null, 3, null),
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
               8384511,
               null
            )
            : var9;
      } while (!var4.compareAndSet(var6, var9));
   }
}
