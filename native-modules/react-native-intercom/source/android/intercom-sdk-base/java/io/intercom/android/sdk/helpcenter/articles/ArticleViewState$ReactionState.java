package io.intercom.android.sdk.helpcenter.articles;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0012\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0087\b\u0018\u0000 \u001c2\u00020\u0001:\u0001\u001cB)\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\t\u0010\nJ\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0015\u001a\u00020\bHÆ\u0003J1\u0010\u0016\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\bHÇ\u0001J\u0013\u0010\u0017\u001a\u00020\b2\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0019\u001a\u00020\u0003H×\u0001J\t\u0010\u001a\u001a\u00020\u001bH×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0006\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\fR\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011¨\u0006\u001d",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$ReactionState;",
         "",
         "reactionComponentVisibility",
         "",
         "selectedReaction",
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$Reaction;",
         "teamHelpVisibility",
         "shouldScrollToBottom",
         "",
         "<init>",
         "(ILio/intercom/android/sdk/helpcenter/articles/ArticleViewState$Reaction;IZ)V",
         "getReactionComponentVisibility",
         "()I",
         "getSelectedReaction",
         "()Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$Reaction;",
         "getTeamHelpVisibility",
         "getShouldScrollToBottom",
         "()Z",
         "component1",
         "component2",
         "component3",
         "component4",
         "copy",
         "equals",
         "other",
         "hashCode",
         "toString",
         "",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class ArticleViewState$ReactionState {
   @NotNull
   public static final ArticleViewState$ReactionState$Companion Companion = new ArticleViewState$ReactionState$Companion(null);
   private final int reactionComponentVisibility;
   @NotNull
   private final ArticleViewState$Reaction selectedReaction;
   private final int teamHelpVisibility;
   private final boolean shouldScrollToBottom;
   public static int $stable;

   static {}
   @NotNull
   private static final ArticleViewState$ReactionState defaultReactionState = new ArticleViewState$ReactionState(8, null, 8, false, 2, null);

   public ArticleViewState$ReactionState(int var1, @NotNull ArticleViewState$Reaction var2, int var3, boolean var4) {
      super();
      Intrinsics.checkNotNullParameter(var2, "selectedReaction");
      this.reactionComponentVisibility = var1;
      this.selectedReaction = var2;
      this.teamHelpVisibility = var3;
      this.shouldScrollToBottom = var4;
   }

   // $VF: synthetic method
   public ArticleViewState$ReactionState(int var1, ArticleViewState$Reaction var2, int var3, boolean var4, int var5, DefaultConstructorMarker var6) {
      this(var1, (((var5 & 2) != 0) ? (ArticleViewState$Reaction) (ArticleViewState$Reaction.None) : var2), var3, var4);
   }

   public final int getReactionComponentVisibility() {
      return this.reactionComponentVisibility;
   }

   @NotNull
   public final ArticleViewState$Reaction getSelectedReaction() {
      return this.selectedReaction;
   }

   public final int getTeamHelpVisibility() {
      return this.teamHelpVisibility;
   }

   public final boolean getShouldScrollToBottom() {
      return this.shouldScrollToBottom;
   }

   public final int component1() {
      return this.reactionComponentVisibility;
   }

   @NotNull
   public final ArticleViewState$Reaction component2() {
      return this.selectedReaction;
   }

   public final int component3() {
      return this.teamHelpVisibility;
   }

   public final boolean component4() {
      return this.shouldScrollToBottom;
   }

   @NotNull
   public final ArticleViewState$ReactionState copy(int var1, @NotNull ArticleViewState$Reaction var2, int var3, boolean var4) {
      Intrinsics.checkNotNullParameter(var2, "selectedReaction");
      return new ArticleViewState$ReactionState(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static ArticleViewState$ReactionState copy$default(
      ArticleViewState$ReactionState var0, int var1, ArticleViewState$Reaction var2, int var3, boolean var4, int var5, Object var6
   ) {
      if ((var5 & 1) != 0) {
         var1 = var0.reactionComponentVisibility;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.selectedReaction;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.teamHelpVisibility;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.shouldScrollToBottom;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "ReactionState(reactionComponentVisibility="
         + this.reactionComponentVisibility
         + ", selectedReaction="
         + this.selectedReaction
         + ", teamHelpVisibility="
         + this.teamHelpVisibility
         + ", shouldScrollToBottom="
         + this.shouldScrollToBottom
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Integer.hashCode(this.reactionComponentVisibility);
      var1 = var1 * 31 + this.selectedReaction.hashCode();
      var1 = var1 * 31 + Integer.hashCode(this.teamHelpVisibility);
      return var1 * 31 + Boolean.hashCode(this.shouldScrollToBottom);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ArticleViewState$ReactionState)) {
         return false;
      } else {
         ArticleViewState$ReactionState var2 = (ArticleViewState$ReactionState)var1;
         if (this.reactionComponentVisibility != var2.reactionComponentVisibility) {
            return false;
         } else if (this.selectedReaction != var2.selectedReaction) {
            return false;
         } else {
            return this.teamHelpVisibility != var2.teamHelpVisibility ? false : this.shouldScrollToBottom == var2.shouldScrollToBottom;
         }
      }
   }

   // $VF: synthetic method
   public static final ArticleViewState$ReactionState access$getDefaultReactionState$cp() {
      return defaultReactionState;
   }
}
