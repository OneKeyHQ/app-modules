package io.intercom.android.sdk.survey.block;

import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.blocks.lib.models.TableCell;
import io.intercom.android.sdk.blocks.lib.models.TableRow;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\"\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\b\n\u0002\b\u0003\u001a\f\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u0000\u001a\f\u0010\u0003\u001a\u00020\u0004*\u00020\u0005H\u0002\u001a*\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0012\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u00072\u0006\u0010\n\u001a\u00020\bH\u0000¨\u0006\u000b",
   d2 = {
         "toTableModel",
         "Lio/intercom/android/sdk/survey/block/TableModel;",
         "Lio/intercom/android/sdk/blocks/lib/models/Block;",
         "toCell",
         "Lio/intercom/android/sdk/survey/block/Cell;",
         "Lio/intercom/android/sdk/blocks/lib/models/TableCell;",
         "columnWidths",
         "",
         "",
         "cellWidths",
         "maxColumnWidth",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nTableModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TableModel.kt\nio/intercom/android/sdk/survey/block/TableModelKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,57:1\n1#2:58\n1563#3:59\n1634#3,3:60\n1563#3:63\n1634#3,3:64\n*S KotlinDebug\n*F\n+ 1 TableModel.kt\nio/intercom/android/sdk/survey/block/TableModelKt\n*L\n39#1:59\n39#1:60,3\n35#1:63\n35#1:64,3\n*E\n"
)
public final class TableModelKt {
   @NotNull
   public static final TableModel toTableModel(@NotNull Block var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      List var1 = var0.getRows();
      if (var1.isEmpty()) {
         return new TableModel(CollectionsKt.emptyList(), CollectionsKt.emptyList());
      }

      Intrinsics.checkNotNull(var1);
      Iterator var4 = var1.iterator();
      if (!var4.hasNext()) {
         throw new NoSuchElementException();
      }

      TableRow var5 = (TableRow)var4.next();
      int var6 = 0;
      int var17 = var5.getCells().size();

      while (var4.hasNext()) {
         TableRow itx = (TableRow)var4.next();
         int var7/* $VF was: $i$a$-maxOf-TableModelKt$toTableModel$columnCount$1x */ = 0;
         var6 = itx.getCells().size();
         if (var17 < var6) {
            var17 = var6;
         }
      }

      int var2 = var17;
      List var10000 = toTableModel$toCells(((TableRow)CollectionsKt.first(var1)).getCells(), var2);
      Iterable var3 = CollectionsKt.drop(var1, 1);
      List var12 = var10000;
      int var16 = 0;
      Iterable var18 = var3;
      ArrayList var21 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var3, 10));
      int var22 = 0;

      for (Object var9 : var18) {
         TableRow var10 = (TableRow)var9;
         Collection var13 = var21;
         int var11 = 0;
         var13.add(toTableModel$toCells(var10.getCells(), var2));
      }

      List var14 = (List)var21;
      List var15 = var12;
      return new TableModel(var15, var14);
   }

   private static final Cell toCell(TableCell var0) {
      return new Cell(
         StringsKt.trim(CollectionsKt.joinToString$default(var0.getContent(), "\n", null, null, 0, null, ((kotlin.jvm.functions.Function1) (recoveredArgument_1298_0) -> TableModelKt.toCell$lambda$4((io.intercom.android.sdk.blocks.lib.models.Block)recoveredArgument_1298_0)), 30, null)).toString()
      );
   }

   @NotNull
   public static final List columnWidths(@NotNull List var0, int var1) {
      Intrinsics.checkNotNullParameter(var0, "cellWidths");
      if (var0.isEmpty()) {
         return CollectionsKt.emptyList();
      }

      int var2 = ((List)CollectionsKt.first(var0)).size();
      ArrayList var3 = new ArrayList(var2);

      for (int var4 = 0; var4 < var2; var4++) {
         int var5 = var4;
         int var6 = var5;
         ArrayList var12 = var3;
         int var7 = 0;
         Iterator var8 = var0.iterator();
         if (!var8.hasNext()) {
            throw new NoSuchElementException();
         }

         List var9 = (List)var8.next();
         int var10 = 0;
         int var13 = ((Number)var9.get(var6)).intValue();

         while (var8.hasNext()) {
            List itx = (List)var8.next();
            int var11/* $VF was: $i$a$-maxOf-TableModelKt$columnWidths$1$1x */ = 0;
            var10 = ((Number)itx.get(var6)).intValue();
            if (var13 < var10) {
               var13 = var10;
            }
         }

         var12.add(RangesKt.coerceAtMost(var13, var1));
      }

      return var3;
   }

   private static final List toTableModel$toCells(List var0, int var1) {
      Iterable var2 = var0;
      int var3 = 0;
      Iterable var4 = var2;
      ArrayList var5 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var2, 10));
      int var6 = 0;

      for (Object var8 : var4) {
         TableCell var9 = (TableCell)var8;
         Collection var11 = var5;
         int var10 = 0;
         var11.add(toCell(var9));
      }

      var var10000 = (Collection & List)var5;
      int var13 = var1 - var0.size();
      Collection var17 = var10000;
      ArrayList var14 = new ArrayList(var13);

      for (int var15 = 0; var15 < var13; var15++) {
         ArrayList var12 = var14;
         int var16 = 0;
         var12.add(new Cell(""));
      }

      return CollectionsKt.plus(var17, var14);
   }

   private static final CharSequence toCell$lambda$4(Block var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      String var10000 = var0.getText();
      Intrinsics.checkNotNullExpressionValue(var10000, "getText(...)");
      return var10000;
   }
}
