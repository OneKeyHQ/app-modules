package io.intercom.android.sdk.helpcenter.search;

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
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\b\u0087\b\u0018\u00002\u00020\u0001B+\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0007HÆ\u0003J1\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u0007HÇ\u0001J\u0013\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018H×\u0003J\t\u0010\u0019\u001a\u00020\u0007H×\u0001J\t\u0010\u001a\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000bR\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000bR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u001b",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/search/ArticleSearchResultRow$ArticleResultRow;",
         "Lio/intercom/android/sdk/helpcenter/search/ArticleSearchResultRow;",
         "id",
         "",
         "titleText",
         "summaryText",
         "summaryVisibility",
         "",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V",
         "getId",
         "()Ljava/lang/String;",
         "getTitleText",
         "getSummaryText",
         "getSummaryVisibility",
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
@StabilityInferred(parameters = 1)
public final class ArticleSearchResultRow$ArticleResultRow extends ArticleSearchResultRow {
   @NotNull
   private final String id;
   @NotNull
   private final String titleText;
   @NotNull
   private final String summaryText;
   private final int summaryVisibility;
   public static int $stable;

   static {}

   public ArticleSearchResultRow$ArticleResultRow(@NotNull String var1, @NotNull String var2, @NotNull String var3, int var4) {
      Intrinsics.checkNotNullParameter(var1, "id");
      Intrinsics.checkNotNullParameter(var2, "titleText");
      Intrinsics.checkNotNullParameter(var3, "summaryText");
      super(null);
      this.id = var1;
      this.titleText = var2;
      this.summaryText = var3;
      this.summaryVisibility = var4;
   }

   // $VF: synthetic method
   public ArticleSearchResultRow$ArticleResultRow(String var1, String var2, String var3, int var4, int var5, DefaultConstructorMarker var6) {
      this(var1, var2, (((var5 & 4) != 0) ? (String) ("") : var3), (((var5 & 8) != 0) ? (int) (8) : var4));
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   @NotNull
   public final String getTitleText() {
      return this.titleText;
   }

   @NotNull
   public final String getSummaryText() {
      return this.summaryText;
   }

   public final int getSummaryVisibility() {
      return this.summaryVisibility;
   }

   @NotNull
   public final String component1() {
      return this.id;
   }

   @NotNull
   public final String component2() {
      return this.titleText;
   }

   @NotNull
   public final String component3() {
      return this.summaryText;
   }

   public final int component4() {
      return this.summaryVisibility;
   }

   @NotNull
   public final ArticleSearchResultRow$ArticleResultRow copy(@NotNull String var1, @NotNull String var2, @NotNull String var3, int var4) {
      Intrinsics.checkNotNullParameter(var1, "id");
      Intrinsics.checkNotNullParameter(var2, "titleText");
      Intrinsics.checkNotNullParameter(var3, "summaryText");
      return new ArticleSearchResultRow$ArticleResultRow(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static ArticleSearchResultRow$ArticleResultRow copy$default(
      ArticleSearchResultRow$ArticleResultRow var0, String var1, String var2, String var3, int var4, int var5, Object var6
   ) {
      if ((var5 & 1) != 0) {
         var1 = var0.id;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.titleText;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.summaryText;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.summaryVisibility;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "ArticleResultRow(id="
         + this.id
         + ", titleText="
         + this.titleText
         + ", summaryText="
         + this.summaryText
         + ", summaryVisibility="
         + this.summaryVisibility
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.id.hashCode();
      var1 = var1 * 31 + this.titleText.hashCode();
      var1 = var1 * 31 + this.summaryText.hashCode();
      return var1 * 31 + Integer.hashCode(this.summaryVisibility);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ArticleSearchResultRow$ArticleResultRow)) {
         return false;
      } else {
         ArticleSearchResultRow$ArticleResultRow var2 = (ArticleSearchResultRow$ArticleResultRow)var1;
         if (!Intrinsics.areEqual(this.id, var2.id)) {
            return false;
         } else if (!Intrinsics.areEqual(this.titleText, var2.titleText)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.summaryText, var2.summaryText) ? false : this.summaryVisibility == var2.summaryVisibility;
         }
      }
   }
}
