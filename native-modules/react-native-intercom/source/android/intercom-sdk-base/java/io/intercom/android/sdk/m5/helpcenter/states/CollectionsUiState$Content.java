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
   d1 = "\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u0015\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\u0006\u0010\t\u001a\u00020\nJ\u000e\u0010\u000b\u001a\u00020\u00002\u0006\u0010\f\u001a\u00020\rJ\u0006\u0010\u000e\u001a\u00020\u0000J\u000f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\u0019\u0010\u0010\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÇ\u0001J\u0013\u0010\u0011\u001a\u00020\n2\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013H×\u0003J\t\u0010\u0014\u001a\u00020\u0015H×\u0001J\t\u0010\u0016\u001a\u00020\u0017H×\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b¨\u0006\u0018",
   d2 = {
         "Lio/intercom/android/sdk/m5/helpcenter/states/CollectionsUiState$Content;",
         "Lio/intercom/android/sdk/m5/helpcenter/states/CollectionsUiState;",
         "collections",
         "",
         "Lio/intercom/android/sdk/m5/helpcenter/states/CollectionsRow;",
         "<init>",
         "(Ljava/util/List;)V",
         "getCollections",
         "()Ljava/util/List;",
         "hasSendMessageRow",
         "",
         "copyWithSingleSendMessageRow",
         "teamPresenceState",
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$TeamPresenceState;",
         "copyWithoutSendMessageRow",
         "component1",
         "copy",
         "equals",
         "other",
         "",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nCollectionsUiState.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CollectionsUiState.kt\nio/intercom/android/sdk/m5/helpcenter/states/CollectionsUiState$Content\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,35:1\n1761#2,3:36\n827#2:39\n855#2,2:40\n827#2:42\n855#2,2:43\n*S KotlinDebug\n*F\n+ 1 CollectionsUiState.kt\nio/intercom/android/sdk/m5/helpcenter/states/CollectionsUiState$Content\n*L\n17#1:36,3\n21#1:39\n21#1:40,2\n32#1:42\n32#1:43,2\n*E\n"
)
public final class CollectionsUiState$Content implements CollectionsUiState {
   @NotNull
   private final List collections;
   public static final int $stable;

   static { $stable = 8; }

   public CollectionsUiState$Content(@NotNull List var1) {
      super();
      Intrinsics.checkNotNullParameter(var1, "collections");
      this.collections = var1;
   }

   @NotNull
   public final List getCollections() {
      return this.collections;
   }

   public final boolean hasSendMessageRow() {
      var var1 = this.collections;
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
            CollectionsRow var5 = (CollectionsRow)var4;
            int var6 = 0;
            if (var5 instanceof CollectionsRow$SendMessageRow) {
               var10000 = true;
               break;
            }
         }
      }

      return var10000;
   }

   @NotNull
   public final CollectionsUiState$Content copyWithSingleSendMessageRow(@NotNull ArticleViewState$TeamPresenceState var1) {
      Intrinsics.checkNotNullParameter(var1, "teamPresenceState");
      CollectionsUiState$Content var10000;
      if (this.hasSendMessageRow()) {
         Iterable var3 = this.collections;
         int var4 = 0;
         Iterable var5 = var3;
         ArrayList var6 = new ArrayList();
         int var7 = 0;

         for (Object var9 : var5) {
            CollectionsRow var10 = (CollectionsRow)var9;
            int var11 = 0;
            if (!(var10 instanceof CollectionsRow$SendMessageRow)) {
               var6.add(var9);
            }
         }

         List var2 = (List)var6;
         var10000 = this.copy(CollectionsKt.plus(var2, new CollectionsRow$SendMessageRow(var1)));
      } else {
         var10000 = this.copy(CollectionsKt.plus(this.collections, new CollectionsRow$SendMessageRow(var1)));
      }

      return var10000;
   }

   @NotNull
   public final CollectionsUiState$Content copyWithoutSendMessageRow() {
      Iterable var1 = this.collections;
      CollectionsUiState$Content var10 = this;
      int var2 = 0;
      Iterable var3 = var1;
      ArrayList var4 = new ArrayList();
      int var5 = 0;

      for (Object var7 : var3) {
         CollectionsRow var8 = (CollectionsRow)var7;
         int var9 = 0;
         if (!(var8 instanceof CollectionsRow$SendMessageRow)) {
            var4.add(var7);
         }
      }

      return var10.copy((List)var4);
   }

   @NotNull
   public final List component1() {
      return this.collections;
   }

   @NotNull
   public final CollectionsUiState$Content copy(@NotNull List var1) {
      Intrinsics.checkNotNullParameter(var1, "collections");
      return new CollectionsUiState$Content(var1);
   }

   // $VF: synthetic method
   public static CollectionsUiState$Content copy$default(CollectionsUiState$Content var0, List var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = var0.collections;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "Content(collections=" + this.collections + ')';
   }

   @Override
   public int hashCode() {
      return this.collections.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof CollectionsUiState$Content)) {
         return false;
      }

      CollectionsUiState$Content var2 = (CollectionsUiState$Content)var1;
      return Intrinsics.areEqual(this.collections, var2.collections);
   }
}
