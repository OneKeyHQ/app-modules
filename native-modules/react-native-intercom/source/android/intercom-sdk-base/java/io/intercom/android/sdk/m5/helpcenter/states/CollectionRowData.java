package io.intercom.android.sdk.m5.helpcenter.states;

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
   d1 = "\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0015\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0081\b\u0018\u00002\u00020\u0001B;\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\b\b\u0002\u0010\b\u001a\u00020\u0006\u0012\b\b\u0002\u0010\t\u001a\u00020\u0006¢\u0006\u0004\b\n\u0010\u000bJ\t\u0010\u0014\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0015\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0016\u001a\u00020\u0006HÆ\u0003J\t\u0010\u0017\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0018\u001a\u00020\u0006HÆ\u0003J\t\u0010\u0019\u001a\u00020\u0006HÆ\u0003JE\u0010\u001a\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00062\b\b\u0002\u0010\t\u001a\u00020\u0006HÇ\u0001J\u0013\u0010\u001b\u001a\u00020\u001c2\b\u0010\u001d\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u001e\u001a\u00020\u0006H×\u0001J\t\u0010\u001f\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0007\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\rR\u0011\u0010\b\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0010R\u0011\u0010\t\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0010¨\u0006 ",
   d2 = {
         "Lio/intercom/android/sdk/m5/helpcenter/states/CollectionRowData;",
         "",
         "id",
         "",
         "titleText",
         "descriptionVisibility",
         "",
         "descriptionText",
         "articlesCount",
         "collectionsCount",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;II)V",
         "getId",
         "()Ljava/lang/String;",
         "getTitleText",
         "getDescriptionVisibility",
         "()I",
         "getDescriptionText",
         "getArticlesCount",
         "getCollectionsCount",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class CollectionRowData {
   @NotNull
   private final String id;
   @NotNull
   private final String titleText;
   private final int descriptionVisibility;
   @NotNull
   private final String descriptionText;
   private final int articlesCount;
   private final int collectionsCount;
   public static int $stable;

   static {}

   public CollectionRowData(@NotNull String var1, @NotNull String var2, int var3, @NotNull String var4, int var5, int var6) {
      super();
      Intrinsics.checkNotNullParameter(var1, "id");
      Intrinsics.checkNotNullParameter(var2, "titleText");
      Intrinsics.checkNotNullParameter(var4, "descriptionText");
      this.id = var1;
      this.titleText = var2;
      this.descriptionVisibility = var3;
      this.descriptionText = var4;
      this.articlesCount = var5;
      this.collectionsCount = var6;
   }

   // $VF: synthetic method
   public CollectionRowData(String var1, String var2, int var3, String var4, int var5, int var6, int var7, DefaultConstructorMarker var8) {
      this(var1, var2, var3, var4, (((var7 & 16) != 0) ? (int) (0) : var5), (((var7 & 32) != 0) ? (int) (0) : var6));
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   @NotNull
   public final String getTitleText() {
      return this.titleText;
   }

   public final int getDescriptionVisibility() {
      return this.descriptionVisibility;
   }

   @NotNull
   public final String getDescriptionText() {
      return this.descriptionText;
   }

   public final int getArticlesCount() {
      return this.articlesCount;
   }

   public final int getCollectionsCount() {
      return this.collectionsCount;
   }

   @NotNull
   public final String component1() {
      return this.id;
   }

   @NotNull
   public final String component2() {
      return this.titleText;
   }

   public final int component3() {
      return this.descriptionVisibility;
   }

   @NotNull
   public final String component4() {
      return this.descriptionText;
   }

   public final int component5() {
      return this.articlesCount;
   }

   public final int component6() {
      return this.collectionsCount;
   }

   @NotNull
   public final CollectionRowData copy(@NotNull String var1, @NotNull String var2, int var3, @NotNull String var4, int var5, int var6) {
      Intrinsics.checkNotNullParameter(var1, "id");
      Intrinsics.checkNotNullParameter(var2, "titleText");
      Intrinsics.checkNotNullParameter(var4, "descriptionText");
      return new CollectionRowData(var1, var2, var3, var4, var5, var6);
   }

   // $VF: synthetic method
   public static CollectionRowData copy$default(
      CollectionRowData var0, String var1, String var2, int var3, String var4, int var5, int var6, int var7, Object var8
   ) {
      if ((var7 & 1) != 0) {
         var1 = var0.id;
      }

      if ((var7 & 2) != 0) {
         var2 = var0.titleText;
      }

      if ((var7 & 4) != 0) {
         var3 = var0.descriptionVisibility;
      }

      if ((var7 & 8) != 0) {
         var4 = var0.descriptionText;
      }

      if ((var7 & 16) != 0) {
         var5 = var0.articlesCount;
      }

      if ((var7 & 32) != 0) {
         var6 = var0.collectionsCount;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6);
   }

   @NotNull
   @Override
   public String toString() {
      return "CollectionRowData(id="
         + this.id
         + ", titleText="
         + this.titleText
         + ", descriptionVisibility="
         + this.descriptionVisibility
         + ", descriptionText="
         + this.descriptionText
         + ", articlesCount="
         + this.articlesCount
         + ", collectionsCount="
         + this.collectionsCount
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.id.hashCode();
      var1 = var1 * 31 + this.titleText.hashCode();
      var1 = var1 * 31 + Integer.hashCode(this.descriptionVisibility);
      var1 = var1 * 31 + this.descriptionText.hashCode();
      var1 = var1 * 31 + Integer.hashCode(this.articlesCount);
      return var1 * 31 + Integer.hashCode(this.collectionsCount);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof CollectionRowData)) {
         return false;
      } else {
         CollectionRowData var2 = (CollectionRowData)var1;
         if (!Intrinsics.areEqual(this.id, var2.id)) {
            return false;
         } else if (!Intrinsics.areEqual(this.titleText, var2.titleText)) {
            return false;
         } else if (this.descriptionVisibility != var2.descriptionVisibility) {
            return false;
         } else if (!Intrinsics.areEqual(this.descriptionText, var2.descriptionText)) {
            return false;
         } else {
            return this.articlesCount != var2.articlesCount ? false : this.collectionsCount == var2.collectionsCount;
         }
      }
   }
}
