package io.intercom.android.sdk.m5.conversation.streaming;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.m5.conversation.data.ParsedNexusEvent$ConversationNexusEvent$FinStreaming;
import io.intercom.android.sdk.models.StreamingPart;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.flow.FlowKt;
import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.StateFlow;
import kotlinx.coroutines.flow.StateFlowKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0001\u0018\u00002\u00020\u0001B\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013J\u0014\u0010\u0014\u001a\u00020\u000f2\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\r0\fR\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00060\b¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0016",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/streaming/FinStreamingStateHolder;",
         "",
         "<init>",
         "()V",
         "_state",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "Lio/intercom/android/sdk/m5/conversation/streaming/StreamingMessageState;",
         "state",
         "Lkotlinx/coroutines/flow/StateFlow;",
         "getState",
         "()Lkotlinx/coroutines/flow/StateFlow;",
         "committedUuids",
         "",
         "",
         "onStreamingDelta",
         "",
         "data",
         "Lio/intercom/android/sdk/m5/conversation/data/ParsedNexusEvent$ConversationNexusEvent$FinStreaming;",
         "streamingPart",
         "Lio/intercom/android/sdk/models/StreamingPart;",
         "onCommittedPartUuids",
         "uuids",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class FinStreamingStateHolder {
   @NotNull
   private final MutableStateFlow _state = StateFlowKt.MutableStateFlow(StreamingMessageState$Idle.INSTANCE);
   @NotNull
   private final StateFlow state = FlowKt.asStateFlow(this._state);
   @NotNull
   private Set committedUuids = SetsKt.emptySet();
   public static final int $stable;

   static { $stable = 8; }

   @NotNull
   public final StateFlow getState() {
      return this.state;
   }

   public final synchronized void onStreamingDelta(@NotNull ParsedNexusEvent$ConversationNexusEvent$FinStreaming var1, @NotNull StreamingPart var2) {
      Intrinsics.checkNotNullParameter(var1, "data");
      Intrinsics.checkNotNullParameter(var2, "streamingPart");
      if (!this.committedUuids.contains(var1.getClientAssignedUUID())) {
         StreamingMessageState var3 = (StreamingMessageState)this._state.getValue();
         if (!(var3 instanceof StreamingMessageState$Streaming)
            || !Intrinsics.areEqual(((StreamingMessageState$Streaming)var3).getUuid(), var1.getClientAssignedUUID())
            || var1.getTokenSequenceIndex() >= ((StreamingMessageState$Streaming)var3).getTokenSequenceIndex()) {
            this._state.setValue(new StreamingMessageState$Streaming(var1.getClientAssignedUUID(), var1.getBlocks(), var2, var1.getTokenSequenceIndex()));
         }
      }
   }

   public final synchronized void onCommittedPartUuids(@NotNull Set var1) {
      Intrinsics.checkNotNullParameter(var1, "uuids");
      this.committedUuids = var1;
      StreamingMessageState var2 = (StreamingMessageState)this._state.getValue();
      if (var2 instanceof StreamingMessageState$Streaming && var1.contains(((StreamingMessageState$Streaming)var2).getUuid())) {
         this._state.setValue(StreamingMessageState$Idle.INSTANCE);
      }
   }
}
