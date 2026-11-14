package io.intercom.android.sdk.helpcenter.api;

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
   d1 = "\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003¢\u0006\u0004\b\u0007\u0010\bJ\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0003HÆ\u0003J1\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0016\u001a\u00020\u0017H×\u0001J\t\u0010\u0018\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\nR\u0011\u0010\u0006\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\n¨\u0006\u0019",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/api/HelpCenterArticleSearchResult;",
         "",
         "articleId",
         "",
         "title",
         "summary",
         "matchingSnippet",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
         "getArticleId",
         "()Ljava/lang/String;",
         "getTitle",
         "getSummary",
         "getMatchingSnippet",
         "component1",
         "component2",
         "component3",
         "component4",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class HelpCenterArticleSearchResult {
   @NotNull
   private final String articleId;
   @NotNull
   private final String title;
   @NotNull
   private final String summary;
   @NotNull
   private final String matchingSnippet;
   public static int $stable;

   static {}

   public HelpCenterArticleSearchResult(@NotNull String var1, @NotNull String var2, @NotNull String var3, @NotNull String var4) {
      super();
      Intrinsics.checkNotNullParameter(var1, "articleId");
      Intrinsics.checkNotNullParameter(var2, "title");
      Intrinsics.checkNotNullParameter(var3, "summary");
      Intrinsics.checkNotNullParameter(var4, "matchingSnippet");
      this.articleId = var1;
      this.title = var2;
      this.summary = var3;
      this.matchingSnippet = var4;
   }

   // $VF: synthetic method
   public HelpCenterArticleSearchResult(String var1, String var2, String var3, String var4, int var5, DefaultConstructorMarker var6) {
      this(var1, (((var5 & 2) != 0) ? (String) ("") : var2), (((var5 & 4) != 0) ? (String) ("") : var3), (((var5 & 8) != 0) ? (String) ("") : var4));
   }

   @NotNull
   public final String getArticleId() {
      return this.articleId;
   }

   @NotNull
   public final String getTitle() {
      return this.title;
   }

   @NotNull
   public final String getSummary() {
      return this.summary;
   }

   @NotNull
   public final String getMatchingSnippet() {
      return this.matchingSnippet;
   }

   @NotNull
   public final String component1() {
      return this.articleId;
   }

   @NotNull
   public final String component2() {
      return this.title;
   }

   @NotNull
   public final String component3() {
      return this.summary;
   }

   @NotNull
   public final String component4() {
      return this.matchingSnippet;
   }

   @NotNull
   public final HelpCenterArticleSearchResult copy(@NotNull String var1, @NotNull String var2, @NotNull String var3, @NotNull String var4) {
      Intrinsics.checkNotNullParameter(var1, "articleId");
      Intrinsics.checkNotNullParameter(var2, "title");
      Intrinsics.checkNotNullParameter(var3, "summary");
      Intrinsics.checkNotNullParameter(var4, "matchingSnippet");
      return new HelpCenterArticleSearchResult(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static HelpCenterArticleSearchResult copy$default(
      HelpCenterArticleSearchResult var0, String var1, String var2, String var3, String var4, int var5, Object var6
   ) {
      if ((var5 & 1) != 0) {
         var1 = var0.articleId;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.title;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.summary;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.matchingSnippet;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "HelpCenterArticleSearchResult(articleId="
         + this.articleId
         + ", title="
         + this.title
         + ", summary="
         + this.summary
         + ", matchingSnippet="
         + this.matchingSnippet
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.articleId.hashCode();
      var1 = var1 * 31 + this.title.hashCode();
      var1 = var1 * 31 + this.summary.hashCode();
      return var1 * 31 + this.matchingSnippet.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof HelpCenterArticleSearchResult)) {
         return false;
      } else {
         HelpCenterArticleSearchResult var2 = (HelpCenterArticleSearchResult)var1;
         if (!Intrinsics.areEqual(this.articleId, var2.articleId)) {
            return false;
         } else if (!Intrinsics.areEqual(this.title, var2.title)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.summary, var2.summary) ? false : Intrinsics.areEqual(this.matchingSnippet, var2.matchingSnippet);
         }
      }
   }
}
