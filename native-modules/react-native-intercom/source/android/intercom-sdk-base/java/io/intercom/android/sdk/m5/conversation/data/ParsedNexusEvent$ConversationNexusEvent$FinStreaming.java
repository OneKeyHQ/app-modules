package io.intercom.android.sdk.m5.conversation.data;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.nexus.NexusEventType;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\b\u0087\b\u0018\u00002\u00020\u0001B5\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t¢\u0006\u0004\b\u000b\u0010\fJ\t\u0010\u0015\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0016\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0017\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0018\u001a\u00020\u0007HÆ\u0003J\u000f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\n0\tHÆ\u0003JA\u0010\u001a\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00072\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tHÇ\u0001J\u0013\u0010\u001b\u001a\u00020\u001c2\b\u0010\u001d\u001a\u0004\u0018\u00010\u001eH×\u0003J\t\u0010\u001f\u001a\u00020\u0007H×\u0001J\t\u0010 \u001a\u00020\u0003H×\u0001R\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000eR\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000eR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014¨\u0006!",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/data/ParsedNexusEvent$ConversationNexusEvent$FinStreaming;",
         "Lio/intercom/android/sdk/m5/conversation/data/ParsedNexusEvent$ConversationNexusEvent;",
         "conversationId",
         "",
         "clientAssignedUUID",
         "partType",
         "tokenSequenceIndex",
         "",
         "blocks",
         "",
         "Lio/intercom/android/sdk/blocks/lib/models/Block;",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/util/List;)V",
         "getConversationId",
         "()Ljava/lang/String;",
         "getClientAssignedUUID",
         "getPartType",
         "getTokenSequenceIndex",
         "()I",
         "getBlocks",
         "()Ljava/util/List;",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
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
public final class ParsedNexusEvent$ConversationNexusEvent$FinStreaming extends ParsedNexusEvent$ConversationNexusEvent {
   @NotNull
   private final String conversationId;
   @NotNull
   private final String clientAssignedUUID;
   @NotNull
   private final String partType;
   private final int tokenSequenceIndex;
   @NotNull
   private final List blocks;
   public static final int $stable;

   static { $stable = 8; }

   public ParsedNexusEvent$ConversationNexusEvent$FinStreaming(@NotNull String var1, @NotNull String var2, @NotNull String var3, int var4, @NotNull List var5) {
      Intrinsics.checkNotNullParameter(var1, "conversationId");
      Intrinsics.checkNotNullParameter(var2, "clientAssignedUUID");
      Intrinsics.checkNotNullParameter(var3, "partType");
      Intrinsics.checkNotNullParameter(var5, "blocks");
      super(var1, NexusEventType.ConversationPartToken);
      this.conversationId = var1;
      this.clientAssignedUUID = var2;
      this.partType = var3;
      this.tokenSequenceIndex = var4;
      this.blocks = var5;
   }

   @NotNull
   @Override
   public String getConversationId() {
      return this.conversationId;
   }

   @NotNull
   public final String getClientAssignedUUID() {
      return this.clientAssignedUUID;
   }

   @NotNull
   public final String getPartType() {
      return this.partType;
   }

   public final int getTokenSequenceIndex() {
      return this.tokenSequenceIndex;
   }

   @NotNull
   public final List getBlocks() {
      return this.blocks;
   }

   @NotNull
   public final String component1() {
      return this.conversationId;
   }

   @NotNull
   public final String component2() {
      return this.clientAssignedUUID;
   }

   @NotNull
   public final String component3() {
      return this.partType;
   }

   public final int component4() {
      return this.tokenSequenceIndex;
   }

   @NotNull
   public final List component5() {
      return this.blocks;
   }

   @NotNull
   public final ParsedNexusEvent$ConversationNexusEvent$FinStreaming copy(
      @NotNull String var1, @NotNull String var2, @NotNull String var3, int var4, @NotNull List var5
   ) {
      Intrinsics.checkNotNullParameter(var1, "conversationId");
      Intrinsics.checkNotNullParameter(var2, "clientAssignedUUID");
      Intrinsics.checkNotNullParameter(var3, "partType");
      Intrinsics.checkNotNullParameter(var5, "blocks");
      return new ParsedNexusEvent$ConversationNexusEvent$FinStreaming(var1, var2, var3, var4, var5);
   }

   // $VF: synthetic method
   public static ParsedNexusEvent$ConversationNexusEvent$FinStreaming copy$default(
      ParsedNexusEvent$ConversationNexusEvent$FinStreaming var0, String var1, String var2, String var3, int var4, List var5, int var6, Object var7
   ) {
      if ((var6 & 1) != 0) {
         var1 = var0.conversationId;
      }

      if ((var6 & 2) != 0) {
         var2 = var0.clientAssignedUUID;
      }

      if ((var6 & 4) != 0) {
         var3 = var0.partType;
      }

      if ((var6 & 8) != 0) {
         var4 = var0.tokenSequenceIndex;
      }

      if ((var6 & 16) != 0) {
         var5 = var0.blocks;
      }

      return var0.copy(var1, var2, var3, var4, var5);
   }

   @NotNull
   @Override
   public String toString() {
      return "FinStreaming(conversationId="
         + this.conversationId
         + ", clientAssignedUUID="
         + this.clientAssignedUUID
         + ", partType="
         + this.partType
         + ", tokenSequenceIndex="
         + this.tokenSequenceIndex
         + ", blocks="
         + this.blocks
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.conversationId.hashCode();
      var1 = var1 * 31 + this.clientAssignedUUID.hashCode();
      var1 = var1 * 31 + this.partType.hashCode();
      var1 = var1 * 31 + Integer.hashCode(this.tokenSequenceIndex);
      return var1 * 31 + this.blocks.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ParsedNexusEvent$ConversationNexusEvent$FinStreaming)) {
         return false;
      } else {
         ParsedNexusEvent$ConversationNexusEvent$FinStreaming var2 = (ParsedNexusEvent$ConversationNexusEvent$FinStreaming)var1;
         if (!Intrinsics.areEqual(this.conversationId, var2.conversationId)) {
            return false;
         } else if (!Intrinsics.areEqual(this.clientAssignedUUID, var2.clientAssignedUUID)) {
            return false;
         } else if (!Intrinsics.areEqual(this.partType, var2.partType)) {
            return false;
         } else {
            return this.tokenSequenceIndex != var2.tokenSequenceIndex ? false : Intrinsics.areEqual(this.blocks, var2.blocks);
         }
      }
   }
}
