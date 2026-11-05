package io.intercom.android.sdk.helpcenter.search;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.helpcenter.articles.ArticleViewState$TeamPresenceState;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\t\u0010\r\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÇ\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H×\u0003J\t\u0010\u0013\u001a\u00020\u0014H×\u0001J\t\u0010\u0015\u001a\u00020\u0005H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0016",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/search/ArticleSearchState$NoResults;",
         "Lio/intercom/android/sdk/helpcenter/search/ArticleSearchState;",
         "teamPresenceState",
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$TeamPresenceState;",
         "searchTerm",
         "",
         "<init>",
         "(Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$TeamPresenceState;Ljava/lang/String;)V",
         "getTeamPresenceState",
         "()Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$TeamPresenceState;",
         "getSearchTerm",
         "()Ljava/lang/String;",
         "component1",
         "component2",
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
public final class ArticleSearchState$NoResults extends ArticleSearchState {
   @NotNull
   private final ArticleViewState$TeamPresenceState teamPresenceState;
   @NotNull
   private final String searchTerm;
   public static int $stable;

   static {}

   public ArticleSearchState$NoResults(@NotNull ArticleViewState$TeamPresenceState var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var1, "teamPresenceState");
      Intrinsics.checkNotNullParameter(var2, "searchTerm");
      super(null);
      this.teamPresenceState = var1;
      this.searchTerm = var2;
   }

   @NotNull
   public final ArticleViewState$TeamPresenceState getTeamPresenceState() {
      return this.teamPresenceState;
   }

   @NotNull
   public final String getSearchTerm() {
      return this.searchTerm;
   }

   @NotNull
   public final ArticleViewState$TeamPresenceState component1() {
      return this.teamPresenceState;
   }

   @NotNull
   public final String component2() {
      return this.searchTerm;
   }

   @NotNull
   public final ArticleSearchState$NoResults copy(@NotNull ArticleViewState$TeamPresenceState var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var1, "teamPresenceState");
      Intrinsics.checkNotNullParameter(var2, "searchTerm");
      return new ArticleSearchState$NoResults(var1, var2);
   }

   // $VF: synthetic method
   public static ArticleSearchState$NoResults copy$default(
      ArticleSearchState$NoResults var0, ArticleViewState$TeamPresenceState var1, String var2, int var3, Object var4
   ) {
      if ((var3 & 1) != 0) {
         var1 = var0.teamPresenceState;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.searchTerm;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "NoResults(teamPresenceState=" + this.teamPresenceState + ", searchTerm=" + this.searchTerm + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.teamPresenceState.hashCode();
      return var1 * 31 + this.searchTerm.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof ArticleSearchState$NoResults)) {
         return false;
      }

      ArticleSearchState$NoResults var2 = (ArticleSearchState$NoResults)var1;
      return !Intrinsics.areEqual(this.teamPresenceState, var2.teamPresenceState) ? false : Intrinsics.areEqual(this.searchTerm, var2.searchTerm);
   }
}
