package io.intercom.android.sdk.m5.conversation.streaming;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.models.StreamingPart;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\b\u0087\b\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n¢\u0006\u0004\b\u000b\u0010\fJ\t\u0010\u0015\u001a\u00020\u0003HÆ\u0003J\u000f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005HÆ\u0003J\t\u0010\u0017\u001a\u00020\bHÆ\u0003J\t\u0010\u0018\u001a\u00020\nHÆ\u0003J7\u0010\u0019\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\nHÇ\u0001J\u0013\u0010\u001a\u001a\u00020\u001b2\b\u0010\u001c\u001a\u0004\u0018\u00010\u001dH×\u0003J\t\u0010\u001e\u001a\u00020\nH×\u0001J\t\u0010\u001f\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\t\u001a\u00020\n¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014¨\u0006 ",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/streaming/StreamingMessageState$Streaming;",
         "Lio/intercom/android/sdk/m5/conversation/streaming/StreamingMessageState;",
         "uuid",
         "",
         "blocks",
         "",
         "Lio/intercom/android/sdk/blocks/lib/models/Block;",
         "streamingPart",
         "Lio/intercom/android/sdk/models/StreamingPart;",
         "tokenSequenceIndex",
         "",
         "<init>",
         "(Ljava/lang/String;Ljava/util/List;Lio/intercom/android/sdk/models/StreamingPart;I)V",
         "getUuid",
         "()Ljava/lang/String;",
         "getBlocks",
         "()Ljava/util/List;",
         "getStreamingPart",
         "()Lio/intercom/android/sdk/models/StreamingPart;",
         "getTokenSequenceIndex",
         "()I",
         "component1",
         "component2",
         "component3",
         "component4",
         "copy",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class StreamingMessageState$Streaming implements StreamingMessageState {
   @NotNull
   private final String uuid;
   @NotNull
   private final List blocks;
   @NotNull
   private final StreamingPart streamingPart;
   private final int tokenSequenceIndex;
   public static final int $stable;

   static { $stable = 8; }

   public StreamingMessageState$Streaming(@NotNull String var1, @NotNull List var2, @NotNull StreamingPart var3, int var4) {
      super();
      Intrinsics.checkNotNullParameter(var1, "uuid");
      Intrinsics.checkNotNullParameter(var2, "blocks");
      Intrinsics.checkNotNullParameter(var3, "streamingPart");
      this.uuid = var1;
      this.blocks = var2;
      this.streamingPart = var3;
      this.tokenSequenceIndex = var4;
   }

   @NotNull
   public final String getUuid() {
      return this.uuid;
   }

   @NotNull
   public final List getBlocks() {
      return this.blocks;
   }

   @NotNull
   public final StreamingPart getStreamingPart() {
      return this.streamingPart;
   }

   public final int getTokenSequenceIndex() {
      return this.tokenSequenceIndex;
   }

   @NotNull
   public final String component1() {
      return this.uuid;
   }

   @NotNull
   public final List component2() {
      return this.blocks;
   }

   @NotNull
   public final StreamingPart component3() {
      return this.streamingPart;
   }

   public final int component4() {
      return this.tokenSequenceIndex;
   }

   @NotNull
   public final StreamingMessageState$Streaming copy(@NotNull String var1, @NotNull List var2, @NotNull StreamingPart var3, int var4) {
      Intrinsics.checkNotNullParameter(var1, "uuid");
      Intrinsics.checkNotNullParameter(var2, "blocks");
      Intrinsics.checkNotNullParameter(var3, "streamingPart");
      return new StreamingMessageState$Streaming(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static StreamingMessageState$Streaming copy$default(
      StreamingMessageState$Streaming var0, String var1, List var2, StreamingPart var3, int var4, int var5, Object var6
   ) {
      if ((var5 & 1) != 0) {
         var1 = var0.uuid;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.blocks;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.streamingPart;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.tokenSequenceIndex;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "Streaming(uuid="
         + this.uuid
         + ", blocks="
         + this.blocks
         + ", streamingPart="
         + this.streamingPart
         + ", tokenSequenceIndex="
         + this.tokenSequenceIndex
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.uuid.hashCode();
      var1 = var1 * 31 + this.blocks.hashCode();
      var1 = var1 * 31 + this.streamingPart.hashCode();
      return var1 * 31 + Integer.hashCode(this.tokenSequenceIndex);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof StreamingMessageState$Streaming)) {
         return false;
      } else {
         StreamingMessageState$Streaming var2 = (StreamingMessageState$Streaming)var1;
         if (!Intrinsics.areEqual(this.uuid, var2.uuid)) {
            return false;
         } else if (!Intrinsics.areEqual(this.blocks, var2.blocks)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.streamingPart, var2.streamingPart) ? false : this.tokenSequenceIndex == var2.tokenSequenceIndex;
         }
      }
   }
}
