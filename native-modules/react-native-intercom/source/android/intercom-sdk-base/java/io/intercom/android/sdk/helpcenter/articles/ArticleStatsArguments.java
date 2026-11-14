package io.intercom.android.sdk.helpcenter.articles;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.parcelize.Parcelize;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\t\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B\u001d\b\u0007\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\u000b\u001a\u00020\u0003HÆ\u0003J\t\u0010\f\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÇ\u0001J\b\u0010\u000e\u001a\u00020\u000fH\u0007J\u0013\u0010\u0010\u001a\u00020\u00052\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H×\u0003J\t\u0010\u0013\u001a\u00020\u000fH×\u0001J\t\u0010\u0014\u001a\u00020\u0003H×\u0001J\u0018\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u000fH\u0007R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\n¨\u0006\u001a",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleStatsArguments;",
         "Landroid/os/Parcelable;",
         "articleId",
         "",
         "isFromSearchBrowse",
         "",
         "<init>",
         "(Ljava/lang/String;Z)V",
         "getArticleId",
         "()Ljava/lang/String;",
         "()Z",
         "component1",
         "component2",
         "copy",
         "describeContents",
         "",
         "equals",
         "other",
         "",
         "hashCode",
         "toString",
         "writeToParcel",
         "",
         "dest",
         "Landroid/os/Parcel;",
         "flags",
         "intercom-sdk-base_release"
   }
)
@Parcelize
@StabilityInferred(parameters = 1)
public final class ArticleStatsArguments implements Parcelable {
   @NotNull
   private final String articleId;
   private final boolean isFromSearchBrowse;
   public static int $stable;

   static {}
   @NotNull
   public static final Creator CREATOR = new ArticleStatsArguments$Creator();

   @JvmOverloads
   public ArticleStatsArguments(@NotNull String var1, boolean var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "articleId");
      this.articleId = var1;
      this.isFromSearchBrowse = var2;
   }

   // $VF: synthetic method
   public ArticleStatsArguments(String var1, boolean var2, int var3, DefaultConstructorMarker var4) {
      this((((var3 & 1) != 0) ? (String) ("") : var1), (((var3 & 2) != 0) ? (boolean) (false) : var2));
   }

   @NotNull
   public final String getArticleId() {
      return this.articleId;
   }

   public final boolean isFromSearchBrowse() {
      return this.isFromSearchBrowse;
   }

   @Override
   public final void writeToParcel(@NotNull Parcel var1, int var2) {
      Intrinsics.checkNotNullParameter(var1, "dest");
      var1.writeString(this.articleId);
      var1.writeInt(this.isFromSearchBrowse ? 1 : 0);
   }

   @Override
   public final int describeContents() {
      return 0;
   }

   @NotNull
   public final String component1() {
      return this.articleId;
   }

   public final boolean component2() {
      return this.isFromSearchBrowse;
   }

   @NotNull
   public final ArticleStatsArguments copy(@NotNull String var1, boolean var2) {
      Intrinsics.checkNotNullParameter(var1, "articleId");
      return new ArticleStatsArguments(var1, var2);
   }

   // $VF: synthetic method
   public static ArticleStatsArguments copy$default(ArticleStatsArguments var0, String var1, boolean var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.articleId;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.isFromSearchBrowse;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "ArticleStatsArguments(articleId=" + this.articleId + ", isFromSearchBrowse=" + this.isFromSearchBrowse + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.articleId.hashCode();
      return var1 * 31 + Boolean.hashCode(this.isFromSearchBrowse);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof ArticleStatsArguments)) {
         return false;
      }

      ArticleStatsArguments var2 = (ArticleStatsArguments)var1;
      return !Intrinsics.areEqual(this.articleId, var2.articleId) ? false : this.isFromSearchBrowse == var2.isFromSearchBrowse;
   }

   @JvmOverloads
   public ArticleStatsArguments(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "articleId");
      this(var1, false, 2, null);
   }

   @JvmOverloads
   public ArticleStatsArguments() {
      this(null, false, 3, null);
   }
}
