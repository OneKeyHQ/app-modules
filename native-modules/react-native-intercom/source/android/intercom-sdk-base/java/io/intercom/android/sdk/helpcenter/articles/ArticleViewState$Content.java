package io.intercom.android.sdk.helpcenter.articles;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0013\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B1\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b¢\u0006\u0004\b\f\u0010\rJ\t\u0010\u0018\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\u0019\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\t\u0010\u001a\u001a\u00020\u0007HÆ\u0003J\t\u0010\u001b\u001a\u00020\tHÆ\u0003J\t\u0010\u001c\u001a\u00020\u000bHÆ\u0003J=\u0010\u001d\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000bHÇ\u0001J\u0013\u0010\u001e\u001a\u00020\u001f2\b\u0010 \u001a\u0004\u0018\u00010!H×\u0003J\t\u0010\"\u001a\u00020#H×\u0001J\t\u0010$\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\n\u001a\u00020\u000b¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017¨\u0006%",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$Content;",
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState;",
         "articleUrl",
         "",
         "articleMetadata",
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleMetadata;",
         "webViewStatus",
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$WebViewStatus;",
         "reactionState",
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$ReactionState;",
         "teamPresenceState",
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$TeamPresenceState;",
         "<init>",
         "(Ljava/lang/String;Lio/intercom/android/sdk/helpcenter/articles/ArticleMetadata;Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$WebViewStatus;Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$ReactionState;Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$TeamPresenceState;)V",
         "getArticleUrl",
         "()Ljava/lang/String;",
         "getArticleMetadata",
         "()Lio/intercom/android/sdk/helpcenter/articles/ArticleMetadata;",
         "getWebViewStatus",
         "()Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$WebViewStatus;",
         "getReactionState",
         "()Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$ReactionState;",
         "getTeamPresenceState",
         "()Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$TeamPresenceState;",
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
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class ArticleViewState$Content extends ArticleViewState {
   @NotNull
   private final String articleUrl;
   @Nullable
   private final ArticleMetadata articleMetadata;
   @NotNull
   private final ArticleViewState$WebViewStatus webViewStatus;
   @NotNull
   private final ArticleViewState$ReactionState reactionState;
   @NotNull
   private final ArticleViewState$TeamPresenceState teamPresenceState;
   public static int $stable;

   static {}

   public ArticleViewState$Content(
      @NotNull String var1,
      @Nullable ArticleMetadata var2,
      @NotNull ArticleViewState$WebViewStatus var3,
      @NotNull ArticleViewState$ReactionState var4,
      @NotNull ArticleViewState$TeamPresenceState var5
   ) {
      Intrinsics.checkNotNullParameter(var1, "articleUrl");
      Intrinsics.checkNotNullParameter(var3, "webViewStatus");
      Intrinsics.checkNotNullParameter(var4, "reactionState");
      Intrinsics.checkNotNullParameter(var5, "teamPresenceState");
      super(null);
      this.articleUrl = var1;
      this.articleMetadata = var2;
      this.webViewStatus = var3;
      this.reactionState = var4;
      this.teamPresenceState = var5;
   }

   @NotNull
   public final String getArticleUrl() {
      return this.articleUrl;
   }

   @Nullable
   public final ArticleMetadata getArticleMetadata() {
      return this.articleMetadata;
   }

   @NotNull
   public final ArticleViewState$WebViewStatus getWebViewStatus() {
      return this.webViewStatus;
   }

   @NotNull
   public final ArticleViewState$ReactionState getReactionState() {
      return this.reactionState;
   }

   @NotNull
   public final ArticleViewState$TeamPresenceState getTeamPresenceState() {
      return this.teamPresenceState;
   }

   @NotNull
   public final String component1() {
      return this.articleUrl;
   }

   @Nullable
   public final ArticleMetadata component2() {
      return this.articleMetadata;
   }

   @NotNull
   public final ArticleViewState$WebViewStatus component3() {
      return this.webViewStatus;
   }

   @NotNull
   public final ArticleViewState$ReactionState component4() {
      return this.reactionState;
   }

   @NotNull
   public final ArticleViewState$TeamPresenceState component5() {
      return this.teamPresenceState;
   }

   @NotNull
   public final ArticleViewState$Content copy(
      @NotNull String var1,
      @Nullable ArticleMetadata var2,
      @NotNull ArticleViewState$WebViewStatus var3,
      @NotNull ArticleViewState$ReactionState var4,
      @NotNull ArticleViewState$TeamPresenceState var5
   ) {
      Intrinsics.checkNotNullParameter(var1, "articleUrl");
      Intrinsics.checkNotNullParameter(var3, "webViewStatus");
      Intrinsics.checkNotNullParameter(var4, "reactionState");
      Intrinsics.checkNotNullParameter(var5, "teamPresenceState");
      return new ArticleViewState$Content(var1, var2, var3, var4, var5);
   }

   // $VF: synthetic method
   public static ArticleViewState$Content copy$default(
      ArticleViewState$Content var0,
      String var1,
      ArticleMetadata var2,
      ArticleViewState$WebViewStatus var3,
      ArticleViewState$ReactionState var4,
      ArticleViewState$TeamPresenceState var5,
      int var6,
      Object var7
   ) {
      if ((var6 & 1) != 0) {
         var1 = var0.articleUrl;
      }

      if ((var6 & 2) != 0) {
         var2 = var0.articleMetadata;
      }

      if ((var6 & 4) != 0) {
         var3 = var0.webViewStatus;
      }

      if ((var6 & 8) != 0) {
         var4 = var0.reactionState;
      }

      if ((var6 & 16) != 0) {
         var5 = var0.teamPresenceState;
      }

      return var0.copy(var1, var2, var3, var4, var5);
   }

   @NotNull
   @Override
   public String toString() {
      return "Content(articleUrl="
         + this.articleUrl
         + ", articleMetadata="
         + this.articleMetadata
         + ", webViewStatus="
         + this.webViewStatus
         + ", reactionState="
         + this.reactionState
         + ", teamPresenceState="
         + this.teamPresenceState
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.articleUrl.hashCode();
      var1 = var1 * 31 + (this.articleMetadata == null ? 0 : this.articleMetadata.hashCode());
      var1 = var1 * 31 + this.webViewStatus.hashCode();
      var1 = var1 * 31 + this.reactionState.hashCode();
      return var1 * 31 + this.teamPresenceState.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ArticleViewState$Content)) {
         return false;
      } else {
         ArticleViewState$Content var2 = (ArticleViewState$Content)var1;
         if (!Intrinsics.areEqual(this.articleUrl, var2.articleUrl)) {
            return false;
         } else if (!Intrinsics.areEqual(this.articleMetadata, var2.articleMetadata)) {
            return false;
         } else if (this.webViewStatus != var2.webViewStatus) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.reactionState, var2.reactionState) ? false : Intrinsics.areEqual(this.teamPresenceState, var2.teamPresenceState);
         }
      }
   }
}
