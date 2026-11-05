package io.intercom.android.sdk.helpcenter.search;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0081\b\u0018\u00002\u00020\u0001B\u0011\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\u0007\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\t\u001a\u00020\u00032\b\u0010\n\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u000b\u001a\u00020\fH×\u0001J\t\u0010\r\u001a\u00020\u000eH×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0006¨\u0006\u000f",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/search/IntercomArticleSearchActivity$ArticleSearchArgs;",
         "",
         "isFromSearchBrowse",
         "",
         "<init>",
         "(Z)V",
         "()Z",
         "component1",
         "copy",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class IntercomArticleSearchActivity$ArticleSearchArgs {
   private final boolean isFromSearchBrowse;
   public static int $stable;

   static {}

   public IntercomArticleSearchActivity$ArticleSearchArgs(boolean var1) {
      this.isFromSearchBrowse = var1;
   }

   // $VF: synthetic method
   public IntercomArticleSearchActivity$ArticleSearchArgs(boolean var1, int var2, DefaultConstructorMarker var3) {
      this((((var2 & 1) != 0) ? (boolean) (false) : var1));
   }

   public final boolean isFromSearchBrowse() {
      return this.isFromSearchBrowse;
   }

   public final boolean component1() {
      return this.isFromSearchBrowse;
   }

   @NotNull
   public final IntercomArticleSearchActivity$ArticleSearchArgs copy(boolean var1) {
      return new IntercomArticleSearchActivity$ArticleSearchArgs(var1);
   }

   // $VF: synthetic method
   public static IntercomArticleSearchActivity$ArticleSearchArgs copy$default(
      IntercomArticleSearchActivity$ArticleSearchArgs var0, boolean var1, int var2, Object var3
   ) {
      if ((var2 & 1) != 0) {
         var1 = var0.isFromSearchBrowse;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "ArticleSearchArgs(isFromSearchBrowse=" + this.isFromSearchBrowse + ')';
   }

   @Override
   public int hashCode() {
      return Boolean.hashCode(this.isFromSearchBrowse);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof IntercomArticleSearchActivity$ArticleSearchArgs)) {
         return false;
      }

      IntercomArticleSearchActivity$ArticleSearchArgs var2 = (IntercomArticleSearchActivity$ArticleSearchArgs)var1;
      return this.isFromSearchBrowse == var2.isFromSearchBrowse;
   }

   public IntercomArticleSearchActivity$ArticleSearchArgs() {
      this(false, 1, null);
   }
}
