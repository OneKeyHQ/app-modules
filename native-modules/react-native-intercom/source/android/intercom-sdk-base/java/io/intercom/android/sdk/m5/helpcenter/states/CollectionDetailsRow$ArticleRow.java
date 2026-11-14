package io.intercom.android.sdk.m5.helpcenter.states;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\t\u0010\n\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000b\u001a\u00020\u0003HÆ\u0003J\u001d\u0010\f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\r\u001a\u00020\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010H×\u0003J\t\u0010\u0011\u001a\u00020\u0012H×\u0001J\t\u0010\u0013\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\b¨\u0006\u0014",
   d2 = {
         "Lio/intercom/android/sdk/m5/helpcenter/states/CollectionDetailsRow$ArticleRow;",
         "Lio/intercom/android/sdk/m5/helpcenter/states/CollectionDetailsRow;",
         "id",
         "",
         "title",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;)V",
         "getId",
         "()Ljava/lang/String;",
         "getTitle",
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
public final class CollectionDetailsRow$ArticleRow extends CollectionDetailsRow {
   @NotNull
   private final String id;
   @NotNull
   private final String title;
   public static int $stable;

   static {}

   public CollectionDetailsRow$ArticleRow(@NotNull String var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var1, "id");
      Intrinsics.checkNotNullParameter(var2, "title");
      super(null);
      this.id = var1;
      this.title = var2;
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   @NotNull
   public final String getTitle() {
      return this.title;
   }

   @NotNull
   public final String component1() {
      return this.id;
   }

   @NotNull
   public final String component2() {
      return this.title;
   }

   @NotNull
   public final CollectionDetailsRow$ArticleRow copy(@NotNull String var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var1, "id");
      Intrinsics.checkNotNullParameter(var2, "title");
      return new CollectionDetailsRow$ArticleRow(var1, var2);
   }

   // $VF: synthetic method
   public static CollectionDetailsRow$ArticleRow copy$default(CollectionDetailsRow$ArticleRow var0, String var1, String var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.id;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.title;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "ArticleRow(id=" + this.id + ", title=" + this.title + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.id.hashCode();
      return var1 * 31 + this.title.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof CollectionDetailsRow$ArticleRow)) {
         return false;
      }

      CollectionDetailsRow$ArticleRow var2 = (CollectionDetailsRow$ArticleRow)var1;
      return !Intrinsics.areEqual(this.id, var2.id) ? false : Intrinsics.areEqual(this.title, var2.title);
   }
}
