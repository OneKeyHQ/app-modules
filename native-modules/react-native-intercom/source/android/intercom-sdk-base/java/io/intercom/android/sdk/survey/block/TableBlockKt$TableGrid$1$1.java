package io.intercom.android.sdk.survey.block;

import androidx.compose.foundation.ScrollState;
import androidx.compose.ui.layout.Measurable;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.layout.MeasureResult;
import androidx.compose.ui.layout.MeasureScope;
import androidx.compose.ui.layout.Placeable;
import androidx.compose.ui.layout.Placeable.PlacementScope;
import androidx.compose.ui.unit.Constraints;
import androidx.compose.ui.unit.ConstraintsKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nTableBlock.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TableBlock.kt\nio/intercom/android/sdk/survey/block/TableBlockKt$TableGrid$1$1\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,747:1\n1563#2:748\n1634#2,3:749\n1563#2:752\n1634#2,2:753\n1563#2:755\n1634#2,3:756\n1636#2:759\n1563#2:760\n1634#2,3:761\n1563#2:764\n1634#2,3:765\n1869#2,2:768\n*S KotlinDebug\n*F\n+ 1 TableBlock.kt\nio/intercom/android/sdk/survey/block/TableBlockKt$TableGrid$1$1\n*L\n462#1:748\n462#1:749,3\n466#1:752\n466#1:753,2\n467#1:755\n467#1:756,3\n466#1:759\n473#1:760\n473#1:761,3\n488#1:764\n488#1:765,3\n503#1:768,2\n*E\n"
)
final class TableBlockKt$TableGrid$1$1 implements MeasurePolicy {
   // $VF: synthetic field
   final List $allRows;
   // $VF: synthetic field
   final int $columnCount;
   // $VF: synthetic field
   final int $dividerCount;
   // $VF: synthetic field
   final int $maxColumnWidthPx;
   // $VF: synthetic field
   final int $columnSpacingPx;
   // $VF: synthetic field
   final int $horizontalPaddingPx;
   // $VF: synthetic field
   final int $availableWidthPx;
   // $VF: synthetic field
   final int $bottomPaddingPx;
   // $VF: synthetic field
   final int $verticalPaddingPx;
   // $VF: synthetic field
   final int $dividerHeightPx;
   // $VF: synthetic field
   final ScrollState $verticalScrollState;

   TableBlockKt$TableGrid$1$1(List var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, ScrollState var11) {
      this.$allRows = var1;
      this.$columnCount = var2;
      this.$dividerCount = var3;
      this.$maxColumnWidthPx = var4;
      this.$columnSpacingPx = var5;
      this.$horizontalPaddingPx = var6;
      this.$availableWidthPx = var7;
      this.$bottomPaddingPx = var8;
      this.$verticalPaddingPx = var9;
      this.$dividerHeightPx = var10;
      this.$verticalScrollState = var11;
   }

   @Override
   public final MeasureResult recovered$3f4264a38d526aac(MeasureScope var1, List var2, long var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$Layout");
      Intrinsics.checkNotNullParameter(var2, "measurables");
      int var5 = this.$allRows.size() * this.$columnCount;
      List var6 = var2.subList(0, var5);
      List var7 = var2.subList(var5, var5 + this.$dividerCount);
      Measurable var8 = (Measurable)var2.get(var5 + this.$dividerCount);
      List var10 = var6;
      int var11 = this.$maxColumnWidthPx;
      int var12 = 0;
      Iterable var13 = var10;
      ArrayList var14 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var10, 10));
      int var15 = 0;

      for (Object var17 : var13) {
         Measurable var18 = (Measurable)var17;
         Collection var31 = var14;
         int var19 = 0;
         var31.add(var18.recovered$ffe562697a83387a(ConstraintsKt.Constraints$default(0, var11, 0, 0, 13, null)));
      }

      List var9 = (List)var14;
      Iterable var33 = CollectionsKt.getIndices(this.$allRows);
      var12 = this.$columnCount;
      int $i$f$mapx = 0;
      Iterable var40 = var33;
      var destination$iv$ivx = new ArrayList(CollectionsKt.collectionSizeOrDefault(var33, 10));
      int $i$f$mapTox = 0;
      Iterator var49 = var40.iterator();

      while (var49.hasNext()) {
         int var52 = ((IntIterator)var49).nextInt();
         int var55 = var52;
         Collection var80 = destination$iv$ivx;
         int var20 = 0;
         Iterable var21 = RangesKt.until((int)0, (int)var12);
         int $i$f$mapxx = 0;
         Iterable var23 = var21;
         var destination$iv$ivxx = new ArrayList(CollectionsKt.collectionSizeOrDefault(var21, 10));
         int $i$f$mapToxx = 0;
         Iterator var26 = var23.iterator();

         while (var26.hasNext()) {
            int item$iv$ivx = ((IntIterator)var26).nextInt();
            int var28 = item$iv$ivx;
            Collection var29 = destination$iv$ivxx;
            int var30 = 0;
            var29.add(((Placeable)var9.get(var55 * var12 + var28)).getWidth());
         }

         var80.add((List)destination$iv$ivxx);
      }

      var10 = (List)destination$iv$ivx;
      List var34 = TableModelKt.columnWidths(var10, this.$maxColumnWidthPx);
      var13 = CollectionsKt.getIndices(this.$allRows);
      int var41 = this.$columnCount;
      var15 = this.$verticalPaddingPx;
      $i$f$mapTox = 0;
      Iterable var50 = var13;
      var destination$iv$ivxx = new ArrayList(CollectionsKt.collectionSizeOrDefault(var13, 10));
      int $i$f$mapToxx = 0;
      Iterator var59 = var50.iterator();

      while (var59.hasNext()) {
         int var62 = ((IntIterator)var59).nextInt();
         int var64 = var62;
         Collection var81 = destination$iv$ivxx;
         int var66 = 0;
         Iterator var68 = RangesKt.until((int)0, (int)var41).iterator();
         if (!var68.hasNext()) {
            throw new NoSuchElementException();
         }

         int var70 = ((IntIterator)var68).nextInt();
         int var73 = 0;
         var70 = ((Placeable)var9.get(var64 * var41 + var70)).getHeight();

         while (var68.hasNext()) {
            var73 = ((IntIterator)var68).nextInt();
            int var77/* $VF was: $i$a$-maxOf-TableBlockKt$TableGrid$1$1$rowHeights$1$1x */ = 0;
            var73 = ((Placeable)var9.get(var64 * var41 + var73)).getHeight();
            if (var70 < var73) {
               var70 = var73;
            }
         }

         var81.add(var70 + var15 * 2);
      }

      List var36 = (List)destination$iv$ivxx;
      int var39 = CollectionsKt.sumOfInt(var34) + this.$columnSpacingPx * RangesKt.coerceAtLeast(this.$columnCount - 1, 0);
      int var42 = this.$horizontalPaddingPx * 2 + var39;
      var15 = Math.max(var42, Math.max(this.$availableWidthPx, Constraints.recovered$1c5987c83bea431e(var3)));
      $i$f$mapTox = CollectionsKt.sumOfInt(var36) + this.$bottomPaddingPx;
      int var51 = RangesKt.coerceAtLeast(var15 - this.$horizontalPaddingPx * 2, 0);
      Iterable var57 = var7;
      int var60 = this.$dividerHeightPx;
      int $i$f$mapxx = 0;
      Iterable var65 = var57;
      var destination$iv$ivxxx = new ArrayList(CollectionsKt.collectionSizeOrDefault(var57, 10));
      int $i$f$mapToxxx = 0;

      for (Object var76 : var65) {
         Measurable var78 = (Measurable)var76;
         Collection var82 = destination$iv$ivxxx;
         int var79 = 0;
         var82.add(var78.recovered$ffe562697a83387a(Constraints.Companion.recovered$0aced45817d6d166(var51, var60)));
      }

      List recoveredDividers = (List)destination$iv$ivxxx;
      $i$f$mapToxx = ((Number)CollectionsKt.first(var36)).intValue();
      Placeable var61 = var8.recovered$ffe562697a83387a(Constraints.Companion.recovered$0aced45817d6d166(var15, $i$f$mapToxx));
      final androidx.compose.foundation.ScrollState recoveredExpressionCapture_1297_0 = ((io.intercom.android.sdk.survey.block.TableBlockKt$TableGrid$1$1) this).$verticalScrollState;
      final java.util.List recoveredExpressionCapture_1297_1 = ((io.intercom.android.sdk.survey.block.TableBlockKt$TableGrid$1$1) this).$allRows;
      final int recoveredExpressionCapture_1297_2 = ((io.intercom.android.sdk.survey.block.TableBlockKt$TableGrid$1$1) this).$horizontalPaddingPx;
      final int recoveredExpressionCapture_1297_3 = ((io.intercom.android.sdk.survey.block.TableBlockKt$TableGrid$1$1) this).$columnCount;
      final java.util.List recoveredExpressionCapture_1297_4 = var9;
      final int recoveredExpressionCapture_1297_5 = ((io.intercom.android.sdk.survey.block.TableBlockKt$TableGrid$1$1) this).$verticalPaddingPx;
      final java.util.List recoveredExpressionCapture_1297_6 = var34;
      final int recoveredExpressionCapture_1297_7 = ((io.intercom.android.sdk.survey.block.TableBlockKt$TableGrid$1$1) this).$columnSpacingPx;
      final int recoveredExpressionCapture_1297_8 = ((io.intercom.android.sdk.survey.block.TableBlockKt$TableGrid$1$1) this).$dividerCount;
      final java.util.List recoveredExpressionCapture_1297_9 = recoveredDividers;
      final java.util.List recoveredExpressionCapture_1297_10 = var36;
      final int recoveredExpressionCapture_1297_11 = ((io.intercom.android.sdk.survey.block.TableBlockKt$TableGrid$1$1) this).$dividerHeightPx;
      final androidx.compose.ui.layout.Placeable recoveredExpressionCapture_1297_12 = var61;
      final int recoveredExpressionCapture_1297_13 = $i$f$mapToxx;
      return MeasureScope.layout$default(var1, var15, $i$f$mapTox, null, ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1297_0) -> io.intercom.android.sdk.survey.block.TableBlockKt$TableGrid$1$1.measure_3p2s80s$lambda$7(recoveredExpressionCapture_1297_0, recoveredExpressionCapture_1297_1, recoveredExpressionCapture_1297_2, recoveredExpressionCapture_1297_3, recoveredExpressionCapture_1297_4, recoveredExpressionCapture_1297_5, recoveredExpressionCapture_1297_6, recoveredExpressionCapture_1297_7, recoveredExpressionCapture_1297_8, recoveredExpressionCapture_1297_9, recoveredExpressionCapture_1297_10, recoveredExpressionCapture_1297_11, recoveredExpressionCapture_1297_12, recoveredExpressionCapture_1297_13, (androidx.compose.ui.layout.Placeable.PlacementScope) ((androidx.compose.ui.layout.Placeable.PlacementScope)recoveredExpressionArgument_1297_0))), 4, null);
   }

   private static final Unit measure_3p2s80s$lambda$7(
      ScrollState var0,
      List var1,
      int var2,
      int var3,
      List var4,
      int var5,
      List var6,
      int var7,
      int var8,
      List var9,
      List var10,
      int var11,
      Placeable var12,
      int var13,
      PlacementScope var14
   ) {
      Intrinsics.checkNotNullParameter(var14, "$this$layout");
      int var15 = var0.getValue();
      int[] var16 = new int[var1.size()];
      int var17 = 0;
      Iterable var18 = CollectionsKt.getIndices(var1);
      int var19 = 0;
      Iterator var20 = var18.iterator();

      while (var20.hasNext()) {
         int var21 = ((IntIterator)var20).nextInt();
         int var22 = var21;
         int var23 = 0;
         var16[var22] = var17;
         var17 += ((Number)var10.get(var22)).intValue();
      }

      int var24 = 1;

      for (int var27 = var1.size(); var24 < var27; var24++) {
         int var29 = var2;

         for (int var31 = 0; var31 < var3; var31++) {
            PlacementScope.place$default(var14, (Placeable)var4.get(var24 * var3 + var31), var29, var16[var24] + var5, 0.0F, 4, null);
            var29 += ((Number)var6.get(var31)).intValue() + var7;
         }
      }

      for (int var25 = 1; var25 < var8; var25++) {
         PlacementScope.place$default(var14, (Placeable)var9.get(var25), var2, var16[var25] + ((Number)var10.get(var25)).intValue() - var11, 0.0F, 4, null);
      }

      int var26 = var15;
      PlacementScope.place$default(var14, var12, 0, var26, 0.0F, 4, null);
      var19 = var2;

      for (int var30 = 0; var30 < var3; var30++) {
         PlacementScope.place$default(var14, (Placeable)var4.get(var30), var19, var26 + var5, 0.0F, 4, null);
         var19 += ((Number)var6.get(var30)).intValue() + var7;
      }

      if (var8 > 0) {
         PlacementScope.place$default(var14, (Placeable)var9.get(0), var2, var26 + var13 - var11, 0.0F, 4, null);
      }

      return Unit.INSTANCE;
   }
}
