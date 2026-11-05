package io.intercom.android.sdk.blocks.lib.models;

import androidx.compose.runtime.internal.StabilityInferred;
import java.util.ArrayList;
import java.util.Collection;
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
   d1 = "\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u000b\u001a\u00020\u00002\u000e\u0010\u0004\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010\u0005J\u0006\u0010\f\u001a\u00020\rJ\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u0096\u0002J\b\u0010\u0011\u001a\u00020\u0012H\u0016R\"\u0010\u0004\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\n¨\u0006\u0013",
   d2 = {
         "Lio/intercom/android/sdk/blocks/lib/models/TableCell$Builder;",
         "",
         "<init>",
         "()V",
         "content",
         "",
         "Lio/intercom/android/sdk/blocks/lib/models/Block$Builder;",
         "getContent",
         "()Ljava/util/List;",
         "setContent",
         "(Ljava/util/List;)V",
         "withContent",
         "build",
         "Lio/intercom/android/sdk/blocks/lib/models/TableCell;",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nTableCell.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TableCell.kt\nio/intercom/android/sdk/blocks/lib/models/TableCell$Builder\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,34:1\n1#2:35\n1#2:46\n1617#3,9:36\n1869#3:45\n1870#3:47\n1626#3:48\n*S KotlinDebug\n*F\n+ 1 TableCell.kt\nio/intercom/android/sdk/blocks/lib/models/TableCell$Builder\n*L\n23#1:46\n23#1:36,9\n23#1:45\n23#1:47\n23#1:48\n*E\n"
)
public final class TableCell$Builder {
   @Nullable
   private List content;
   public static final int $stable;

   static { $stable = 8; }

   @Nullable
   public final List getContent() {
      return this.content;
   }

   public final void setContent(@Nullable List var1) {
      this.content = var1;
   }

   @NotNull
   public final TableCell$Builder withContent(@Nullable List var1) {
      TableCell$Builder var2 = this;
      TableCell$Builder var3 = var2;
      int var4 = 0;
      var3.content = var1;
      return var2;
   }

   @NotNull
   public final TableCell build() {
      List var10000 = this.content;
      if (this.content == null) {
         var10000 = CollectionsKt.emptyList();
      }

      Iterable var1 = var10000;
      int var2 = 0;
      Iterable var3 = var1;
      ArrayList var4 = new ArrayList();
      int var5 = 0;
      Iterable var6 = var3;
      int var7 = 0;

      for (Object var9 : var6) {
         Object var10 = var9;
         int var11 = 0;
         Block$Builder var12 = (Block$Builder)var10;
         int var13 = 0;
         Block var17 = var12 != null ? var12.build() : null;
         if (var17 != null) {
            Object var14 = var17;
            int var15 = 0;
            var4.add(var14);
         }
      }

      List var16 = (List)var4;
      return new TableCell(var16);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else {
         return !(var1 instanceof TableCell$Builder) ? false : Intrinsics.areEqual(this.content, ((TableCell$Builder)var1).content);
      }
   }

   @Override
   public int hashCode() {
      return this.content != null ? this.content.hashCode() : 0;
   }
}
