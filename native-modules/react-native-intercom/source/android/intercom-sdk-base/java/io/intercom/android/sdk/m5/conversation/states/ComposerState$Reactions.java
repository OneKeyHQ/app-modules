package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.models.ReactionReply;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\r\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005¢\u0006\u0004\b\u0007\u0010\bJ\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000f\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0005HÆ\u0003J'\u0010\u0011\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0005HÇ\u0001J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015H×\u0003J\t\u0010\u0016\u001a\u00020\u0017H×\u0001J\t\u0010\u0018\u001a\u00020\u0005H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\f¨\u0006\u0019",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/ComposerState$Reactions;",
         "Lio/intercom/android/sdk/m5/conversation/states/ComposerState;",
         "reactionReply",
         "Lio/intercom/android/sdk/models/ReactionReply;",
         "lastPartId",
         "",
         "conversationId",
         "<init>",
         "(Lio/intercom/android/sdk/models/ReactionReply;Ljava/lang/String;Ljava/lang/String;)V",
         "getReactionReply",
         "()Lio/intercom/android/sdk/models/ReactionReply;",
         "getLastPartId",
         "()Ljava/lang/String;",
         "getConversationId",
         "component1",
         "component2",
         "component3",
         "copy",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class ComposerState$Reactions implements ComposerState {
   @NotNull
   private final ReactionReply reactionReply;
   @NotNull
   private final String lastPartId;
   @NotNull
   private final String conversationId;
   public static final int $stable;

   static { $stable = 8; }

   public ComposerState$Reactions(@NotNull ReactionReply var1, @NotNull String var2, @NotNull String var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "reactionReply");
      Intrinsics.checkNotNullParameter(var2, "lastPartId");
      Intrinsics.checkNotNullParameter(var3, "conversationId");
      this.reactionReply = var1;
      this.lastPartId = var2;
      this.conversationId = var3;
   }

   @NotNull
   public final ReactionReply getReactionReply() {
      return this.reactionReply;
   }

   @NotNull
   public final String getLastPartId() {
      return this.lastPartId;
   }

   @NotNull
   public final String getConversationId() {
      return this.conversationId;
   }

   @NotNull
   public final ReactionReply component1() {
      return this.reactionReply;
   }

   @NotNull
   public final String component2() {
      return this.lastPartId;
   }

   @NotNull
   public final String component3() {
      return this.conversationId;
   }

   @NotNull
   public final ComposerState$Reactions copy(@NotNull ReactionReply var1, @NotNull String var2, @NotNull String var3) {
      Intrinsics.checkNotNullParameter(var1, "reactionReply");
      Intrinsics.checkNotNullParameter(var2, "lastPartId");
      Intrinsics.checkNotNullParameter(var3, "conversationId");
      return new ComposerState$Reactions(var1, var2, var3);
   }

   // $VF: synthetic method
   public static ComposerState$Reactions copy$default(ComposerState$Reactions var0, ReactionReply var1, String var2, String var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = var0.reactionReply;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.lastPartId;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.conversationId;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "Reactions(reactionReply=" + this.reactionReply + ", lastPartId=" + this.lastPartId + ", conversationId=" + this.conversationId + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.reactionReply.hashCode();
      var1 = var1 * 31 + this.lastPartId.hashCode();
      return var1 * 31 + this.conversationId.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ComposerState$Reactions)) {
         return false;
      } else {
         ComposerState$Reactions var2 = (ComposerState$Reactions)var1;
         if (!Intrinsics.areEqual(this.reactionReply, var2.reactionReply)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.lastPartId, var2.lastPartId) ? false : Intrinsics.areEqual(this.conversationId, var2.conversationId);
         }
      }
   }
}
