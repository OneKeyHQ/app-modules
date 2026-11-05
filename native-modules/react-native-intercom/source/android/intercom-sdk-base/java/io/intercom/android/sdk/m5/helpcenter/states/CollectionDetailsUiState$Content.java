package io.intercom.android.sdk.m5.helpcenter.states;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.helpcenter.articles.ArticleViewState$TeamPresenceState;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u0000\n\u0002\b\u0003\b\u0087\b\u0018\u00002\u00020\u0001BC\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t\u0012\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\t¢\u0006\u0004\b\r\u0010\u000eJ\u0006\u0010\u0018\u001a\u00020\u0019J\u000e\u0010\u001a\u001a\u00020\u00002\u0006\u0010\u001b\u001a\u00020\u001cJ\u0006\u0010\u001d\u001a\u00020\u0000J\t\u0010\u001e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001f\u001a\u00020\u0003HÆ\u0003J\t\u0010 \u001a\u00020\u0003HÆ\u0003J\t\u0010!\u001a\u00020\u0007HÆ\u0003J\u000f\u0010\"\u001a\b\u0012\u0004\u0012\u00020\n0\tHÆ\u0003J\u000f\u0010#\u001a\b\u0012\u0004\u0012\u00020\f0\tHÆ\u0003JQ\u0010$\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00072\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t2\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\tHÇ\u0001J\u0013\u0010%\u001a\u00020\u00192\b\u0010&\u001a\u0004\u0018\u00010'H×\u0003J\t\u0010(\u001a\u00020\u0007H×\u0001J\t\u0010)\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0010R\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0010R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\t¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0016¨\u0006*",
   d2 = {
         "Lio/intercom/android/sdk/m5/helpcenter/states/CollectionDetailsUiState$Content;",
         "Lio/intercom/android/sdk/m5/helpcenter/states/CollectionDetailsUiState;",
         "id",
         "",
         "title",
         "summary",
         "articlesCount",
         "",
         "authors",
         "",
         "Lio/intercom/android/sdk/helpcenter/sections/Author;",
         "collectionDetailsRows",
         "Lio/intercom/android/sdk/m5/helpcenter/states/CollectionDetailsRow;",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/util/List;Ljava/util/List;)V",
         "getId",
         "()Ljava/lang/String;",
         "getTitle",
         "getSummary",
         "getArticlesCount",
         "()I",
         "getAuthors",
         "()Ljava/util/List;",
         "getCollectionDetailsRows",
         "hasSendMessageRow",
         "",
         "copyWithSingleSendMessageRow",
         "teamPresenceState",
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$TeamPresenceState;",
         "copyWithoutSendMessageRow",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "copy",
         "equals",
         "other",
         "",
         "hashCode",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nCollectionDetailsUiState.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CollectionDetailsUiState.kt\nio/intercom/android/sdk/m5/helpcenter/states/CollectionDetailsUiState$Content\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,41:1\n1761#2,3:42\n827#2:45\n855#2,2:46\n827#2:48\n855#2,2:49\n*S KotlinDebug\n*F\n+ 1 CollectionDetailsUiState.kt\nio/intercom/android/sdk/m5/helpcenter/states/CollectionDetailsUiState$Content\n*L\n23#1:42,3\n27#1:45\n27#1:46,2\n38#1:48\n38#1:49,2\n*E\n"
)
public final class CollectionDetailsUiState$Content implements CollectionDetailsUiState {
   @NotNull
   private final String id;
   @NotNull
   private final String title;
   @NotNull
   private final String summary;
   private final int articlesCount;
   @NotNull
   private final List authors;
   @NotNull
   private final List collectionDetailsRows;
   public static final int $stable;

   static { $stable = 8; }

   public CollectionDetailsUiState$Content(@NotNull String var1, @NotNull String var2, @NotNull String var3, int var4, @NotNull List var5, @NotNull List var6) {
      super();
      Intrinsics.checkNotNullParameter(var1, "id");
      Intrinsics.checkNotNullParameter(var2, "title");
      Intrinsics.checkNotNullParameter(var3, "summary");
      Intrinsics.checkNotNullParameter(var5, "authors");
      Intrinsics.checkNotNullParameter(var6, "collectionDetailsRows");
      this.id = var1;
      this.title = var2;
      this.summary = var3;
      this.articlesCount = var4;
      this.authors = var5;
      this.collectionDetailsRows = var6;
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
   public final String getSummary() {
      return this.summary;
   }

   public final int getArticlesCount() {
      return this.articlesCount;
   }

   @NotNull
   public final List getAuthors() {
      return this.authors;
   }

   @NotNull
   public final List getCollectionDetailsRows() {
      return this.collectionDetailsRows;
   }

   public final boolean hasSendMessageRow() {
      var var1 = this.collectionDetailsRows;
      int var2 = 0;
      boolean var10000;
      if (var1 instanceof Collection && ((Collection)var1).isEmpty()) {
         var10000 = false;
      } else {
         Iterator var3 = var1.iterator();

         while (true) {
            if (!var3.hasNext()) {
               var10000 = false;
               break;
            }

            Object var4 = var3.next();
            CollectionDetailsRow var5 = (CollectionDetailsRow)var4;
            int var6 = 0;
            if (var5 instanceof CollectionDetailsRow$SendMessageRow) {
               var10000 = true;
               break;
            }
         }
      }

      return var10000;
   }

   @NotNull
   public final CollectionDetailsUiState$Content copyWithSingleSendMessageRow(@NotNull ArticleViewState$TeamPresenceState var1) {
      Intrinsics.checkNotNullParameter(var1, "teamPresenceState");
      CollectionDetailsUiState$Content var10000;
      if (this.hasSendMessageRow()) {
         Iterable var3 = this.collectionDetailsRows;
         int var4 = 0;
         Iterable var5 = var3;
         ArrayList var6 = new ArrayList();
         int var7 = 0;

         for (Object var9 : var5) {
            CollectionDetailsRow var10 = (CollectionDetailsRow)var9;
            int var11 = 0;
            if (!(var10 instanceof CollectionDetailsRow$SendMessageRow)) {
               var6.add(var9);
            }
         }

         List var2 = (List)var6;
         var10000 = copy$default(this, null, null, null, 0, null, CollectionsKt.plus(var2, new CollectionDetailsRow$SendMessageRow(var1)), 31, null);
      } else {
         var10000 = copy$default(
            this, null, null, null, 0, null, CollectionsKt.plus(this.collectionDetailsRows, new CollectionDetailsRow$SendMessageRow(var1)), 31, null
         );
      }

      return var10000;
   }

   @NotNull
   public final CollectionDetailsUiState$Content copyWithoutSendMessageRow() {
      Iterable var1 = this.collectionDetailsRows;
      Object var15 = null;
      byte var14 = 0;
      Object var13 = null;
      Object var12 = null;
      Object var11 = null;
      CollectionDetailsUiState$Content var10 = this;
      int var2 = 0;
      Iterable var3 = var1;
      ArrayList var4 = new ArrayList();
      int var5 = 0;

      for (Object var7 : var3) {
         CollectionDetailsRow var8 = (CollectionDetailsRow)var7;
         int var9 = 0;
         if (!(var8 instanceof CollectionDetailsRow$SendMessageRow)) {
            var4.add(var7);
         }
      }

      List var16 = (List)var4;
      return copy$default(var10, (String)var11, (String)var12, (String)var13, var14, (List)var15, var16, 31, null);
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
   public final String component3() {
      return this.summary;
   }

   public final int component4() {
      return this.articlesCount;
   }

   @NotNull
   public final List component5() {
      return this.authors;
   }

   @NotNull
   public final List component6() {
      return this.collectionDetailsRows;
   }

   @NotNull
   public final CollectionDetailsUiState$Content copy(
      @NotNull String var1, @NotNull String var2, @NotNull String var3, int var4, @NotNull List var5, @NotNull List var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "id");
      Intrinsics.checkNotNullParameter(var2, "title");
      Intrinsics.checkNotNullParameter(var3, "summary");
      Intrinsics.checkNotNullParameter(var5, "authors");
      Intrinsics.checkNotNullParameter(var6, "collectionDetailsRows");
      return new CollectionDetailsUiState$Content(var1, var2, var3, var4, var5, var6);
   }

   // $VF: synthetic method
   public static CollectionDetailsUiState$Content copy$default(
      CollectionDetailsUiState$Content var0, String var1, String var2, String var3, int var4, List var5, List var6, int var7, Object var8
   ) {
      if ((var7 & 1) != 0) {
         var1 = var0.id;
      }

      if ((var7 & 2) != 0) {
         var2 = var0.title;
      }

      if ((var7 & 4) != 0) {
         var3 = var0.summary;
      }

      if ((var7 & 8) != 0) {
         var4 = var0.articlesCount;
      }

      if ((var7 & 16) != 0) {
         var5 = var0.authors;
      }

      if ((var7 & 32) != 0) {
         var6 = var0.collectionDetailsRows;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6);
   }

   @NotNull
   @Override
   public String toString() {
      return "Content(id="
         + this.id
         + ", title="
         + this.title
         + ", summary="
         + this.summary
         + ", articlesCount="
         + this.articlesCount
         + ", authors="
         + this.authors
         + ", collectionDetailsRows="
         + this.collectionDetailsRows
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.id.hashCode();
      var1 = var1 * 31 + this.title.hashCode();
      var1 = var1 * 31 + this.summary.hashCode();
      var1 = var1 * 31 + Integer.hashCode(this.articlesCount);
      var1 = var1 * 31 + this.authors.hashCode();
      return var1 * 31 + this.collectionDetailsRows.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof CollectionDetailsUiState$Content)) {
         return false;
      } else {
         CollectionDetailsUiState$Content var2 = (CollectionDetailsUiState$Content)var1;
         if (!Intrinsics.areEqual(this.id, var2.id)) {
            return false;
         } else if (!Intrinsics.areEqual(this.title, var2.title)) {
            return false;
         } else if (!Intrinsics.areEqual(this.summary, var2.summary)) {
            return false;
         } else if (this.articlesCount != var2.articlesCount) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.authors, var2.authors) ? false : Intrinsics.areEqual(this.collectionDetailsRows, var2.collectionDetailsRows);
         }
      }
   }
}
