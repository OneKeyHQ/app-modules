package io.intercom.android.sdk.helpcenter.articles;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0010\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B-\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0006¢\u0006\u0004\b\b\u0010\tJ\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0006HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0006HÆ\u0003J1\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u0006HÇ\u0001J\u0013\u0010\u0014\u001a\u00020\u00062\b\u0010\u0015\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0016\u001a\u00020\u0017H×\u0001J\t\u0010\u0018\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000bR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\rR\u0011\u0010\u0007\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\r¨\u0006\u0019",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/articles/IntercomArticleActivity$ArticleActivityArguments;",
         "",
         "articleId",
         "",
         "metricPlace",
         "isFromSearchBrowse",
         "",
         "shouldHideReactions",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;ZZ)V",
         "getArticleId",
         "()Ljava/lang/String;",
         "getMetricPlace",
         "()Z",
         "getShouldHideReactions",
         "component1",
         "component2",
         "component3",
         "component4",
         "copy",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class IntercomArticleActivity$ArticleActivityArguments {
   @NotNull
   private final String articleId;
   @NotNull
   private final String metricPlace;
   private final boolean isFromSearchBrowse;
   private final boolean shouldHideReactions;
   public static int $stable;

   static {}

   @JvmOverloads
   public IntercomArticleActivity$ArticleActivityArguments(@NotNull String var1, @NotNull String var2, boolean var3, boolean var4) {
      super();
      Intrinsics.checkNotNullParameter(var1, "articleId");
      Intrinsics.checkNotNullParameter(var2, "metricPlace");
      this.articleId = var1;
      this.metricPlace = var2;
      this.isFromSearchBrowse = var3;
      this.shouldHideReactions = var4;
   }

   // $VF: synthetic method
   public IntercomArticleActivity$ArticleActivityArguments(String var1, String var2, boolean var3, boolean var4, int var5, DefaultConstructorMarker var6) {
      this(var1, var2, (((var5 & 4) != 0) ? (boolean) (false) : var3), (((var5 & 8) != 0) ? (boolean) (false) : var4));
   }

   @NotNull
   public final String getArticleId() {
      return this.articleId;
   }

   @NotNull
   public final String getMetricPlace() {
      return this.metricPlace;
   }

   public final boolean isFromSearchBrowse() {
      return this.isFromSearchBrowse;
   }

   public final boolean getShouldHideReactions() {
      return this.shouldHideReactions;
   }

   @NotNull
   public final String component1() {
      return this.articleId;
   }

   @NotNull
   public final String component2() {
      return this.metricPlace;
   }

   public final boolean component3() {
      return this.isFromSearchBrowse;
   }

   public final boolean component4() {
      return this.shouldHideReactions;
   }

   @NotNull
   public final IntercomArticleActivity$ArticleActivityArguments copy(@NotNull String var1, @NotNull String var2, boolean var3, boolean var4) {
      Intrinsics.checkNotNullParameter(var1, "articleId");
      Intrinsics.checkNotNullParameter(var2, "metricPlace");
      return new IntercomArticleActivity$ArticleActivityArguments(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static IntercomArticleActivity$ArticleActivityArguments copy$default(
      IntercomArticleActivity$ArticleActivityArguments var0, String var1, String var2, boolean var3, boolean var4, int var5, Object var6
   ) {
      if ((var5 & 1) != 0) {
         var1 = var0.articleId;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.metricPlace;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.isFromSearchBrowse;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.shouldHideReactions;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "ArticleActivityArguments(articleId="
         + this.articleId
         + ", metricPlace="
         + this.metricPlace
         + ", isFromSearchBrowse="
         + this.isFromSearchBrowse
         + ", shouldHideReactions="
         + this.shouldHideReactions
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.articleId.hashCode();
      var1 = var1 * 31 + this.metricPlace.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.isFromSearchBrowse);
      return var1 * 31 + Boolean.hashCode(this.shouldHideReactions);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof IntercomArticleActivity$ArticleActivityArguments)) {
         return false;
      } else {
         IntercomArticleActivity$ArticleActivityArguments var2 = (IntercomArticleActivity$ArticleActivityArguments)var1;
         if (!Intrinsics.areEqual(this.articleId, var2.articleId)) {
            return false;
         } else if (!Intrinsics.areEqual(this.metricPlace, var2.metricPlace)) {
            return false;
         } else {
            return this.isFromSearchBrowse != var2.isFromSearchBrowse ? false : this.shouldHideReactions == var2.shouldHideReactions;
         }
      }
   }

   @JvmOverloads
   public IntercomArticleActivity$ArticleActivityArguments(@NotNull String var1, @NotNull String var2, boolean var3) {
      Intrinsics.checkNotNullParameter(var1, "articleId");
      Intrinsics.checkNotNullParameter(var2, "metricPlace");
      this(var1, var2, var3, false, 8, null);
   }

   @JvmOverloads
   public IntercomArticleActivity$ArticleActivityArguments(@NotNull String var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var1, "articleId");
      Intrinsics.checkNotNullParameter(var2, "metricPlace");
      this(var1, var2, false, false, 12, null);
   }
}
